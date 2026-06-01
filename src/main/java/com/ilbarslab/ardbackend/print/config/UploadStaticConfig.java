package com.ilbarslab.ardbackend.print.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * /uploads/** path'inden disk üzerindeki uploadDir'i serve eder.
 *
 * Eğer projende zaten bir WebConfig veya WebMvcConfigurer implementasyonu varsa,
 * bu sınıfı eklemek yerine mevcut sınıfa addResourceHandlers metodunu ekle:
 *
 *   @Override
 *   public void addResourceHandlers(ResourceHandlerRegistry registry) {
 *       String location = "file:" + Paths.get(uploadDir).toAbsolutePath().toString() + "/";
 *       registry.addResourceHandler("/uploads/**")
 *               .addResourceLocations(location)
 *               .setCachePeriod(3600);
 *   }
 */
@Configuration
public class UploadStaticConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = "file:" + Paths.get(uploadDir).toAbsolutePath().toString() + "/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)
                .setCachePeriod(3600);
    }
}
