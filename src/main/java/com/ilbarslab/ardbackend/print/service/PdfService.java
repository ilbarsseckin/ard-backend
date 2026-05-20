package com.ilbarslab.ardbackend.print.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@Slf4j
public class PdfService {

    private static final long MAX_FILE_SIZE = 100 * 1024 * 1024; // 100 MB

    public int countPages(MultipartFile file) throws IOException {
        validateFile(file);
        try (PDDocument doc = Loader.loadPDF(file.getBytes())) {
            int pages = doc.getNumberOfPages();
            log.info("PDF sayfa sayısı: {} — dosya: {}", pages, file.getOriginalFilename());
            return pages;
        }
    }

    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Dosya boş veya yüklenmemiş");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("Dosya boyutu 100 MB'ı aşamaz");
        }
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        boolean validType = contentType != null && (
                contentType.equals("application/pdf") ||
                        contentType.equals("application/x-pdf")
        );
        boolean validExt = filename != null && (
                filename.toLowerCase().endsWith(".pdf") ||
                        filename.toLowerCase().endsWith(".ai") ||
                        filename.toLowerCase().endsWith(".eps")
        );

        if (!validType && !validExt) {
            throw new RuntimeException("Sadece PDF, AI veya EPS dosyası yükleyebilirsiniz");
        }
    }

    public String buildWarningMessage(int pageCount, int declaredPrints) {
        if (pageCount > declaredPrints) {
            return String.format(
                    "PDF'inizde %d sayfa tespit edildi ancak %d baskı beyan ettiniz. " +
                            "Eğer %d ayrı baskı istiyorsanız lütfen beyan sayısını güncelleyin. " +
                            "Operatörümüz dosyanızı inceleyecek ve gerekirse sizinle iletişime geçecektir.",
                    pageCount, declaredPrints, pageCount
            );
        }
        return null;
    }
}
