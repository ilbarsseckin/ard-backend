package com.ilbarslab.ardbackend.print.config;

import com.ilbarslab.ardbackend.print.entity.AppRole;
import com.ilbarslab.ardbackend.print.entity.Permission;
import com.ilbarslab.ardbackend.print.repository.AppRoleRepository;
import com.ilbarslab.ardbackend.print.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class PermissionInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final AppRoleRepository appRoleRepository;

    @Override
    public void run(String... args) {
        // Referans izni yoksa ekle (mevcut sistemlere migration için)
        if (!permissionRepository.existsByCode("referans.yonet")) {
            permissionRepository.save(Permission.builder()
                    .code("referans.yonet")
                    .label("Referans ekle/düzenle/sil")
                    .category("Referans")
                    .build());
            log.info("referans.yonet izni eklendi.");
        }

        if (permissionRepository.count() > 1) {
            log.info("İzinler zaten yüklü.");
            return;
        }

        log.info("İzinler yükleniyor...");

        List<Object[]> perms = List.of(
                // Sipariş izinleri
                new Object[]{"siparis.goruntule",    "Siparişleri görüntüle",   "Sipariş"},
                new Object[]{"siparis.durum_guncelle","Sipariş durumu güncelle", "Sipariş"},
                new Object[]{"siparis.onayla",        "Sipariş onayla",          "Sipariş"},
                new Object[]{"siparis.reddet",        "Sipariş reddet",          "Sipariş"},
                new Object[]{"siparis.baskiya_gonder","Baskıya gönder",          "Sipariş"},
                new Object[]{"siparis.kargola",       "Kargoya ver",             "Sipariş"},
                new Object[]{"siparis.tamamla",       "Siparişi tamamla",        "Sipariş"},
                // Ödeme izinleri
                new Object[]{"odeme.goruntule",       "Ödemeleri görüntüle",     "Ödeme"},
                new Object[]{"odeme.rapor",           "Ödeme raporu al",         "Ödeme"},
                new Object[]{"odeme.iade",            "İade işlemi yap",         "Ödeme"},
                // Ürün izinleri
                new Object[]{"urun.goruntule",        "Ürünleri görüntüle",      "Ürün"},
                new Object[]{"urun.duzenle",          "Ürün ekle/düzenle",       "Ürün"},
                new Object[]{"urun.sil",              "Ürün sil/pasif yap",      "Ürün"},
                new Object[]{"urun.fiyat_guncelle",   "Fiyat güncelle",          "Ürün"},
                new Object[]{"urun.import",           "Excel ile toplu yükle",   "Ürün"},
                // Kullanıcı izinleri
                new Object[]{"kullanici.goruntule",   "Kullanıcıları görüntüle", "Kullanıcı"},
                new Object[]{"kullanici.duzenle",     "Kullanıcı düzenle",       "Kullanıcı"},
                new Object[]{"kullanici.rol_ver",     "Kullanıcıya rol ata",     "Kullanıcı"},
                // Rapor izinleri
                new Object[]{"rapor.ciro",            "Ciro raporunu gör",       "Rapor"},
                new Object[]{"rapor.gunluk",          "Günlük rapor",            "Rapor"},
                new Object[]{"rapor.musteri",         "Müşteri raporu",          "Rapor"},
                // Referans izinleri
                new Object[]{"referans.yonet",        "Referans ekle/düzenle/sil","Referans"}
        );

        List<Permission> savedPerms = perms.stream().map(p ->
                permissionRepository.save(Permission.builder()
                        .code((String) p[0])
                        .label((String) p[1])
                        .category((String) p[2])
                        .build())
        ).toList();

        // Operatör rolü
        Set<Permission> operatorPerms = new HashSet<>();
        savedPerms.stream()
                .filter(p -> p.getCode().startsWith("siparis.") || p.getCode().equals("urun.goruntule"))
                .forEach(operatorPerms::add);

        appRoleRepository.save(AppRole.builder()
                .name("Operatör")
                .description("Sipariş yönetimi ve üretim takibi")
                .permissions(operatorPerms)
                .isActive(true)
                .build());

        // Muhasebe rolü
        Set<Permission> muhasebePerms = new HashSet<>();
        savedPerms.stream()
                .filter(p -> p.getCode().startsWith("odeme.") || p.getCode().startsWith("rapor.") || p.getCode().equals("siparis.goruntule"))
                .forEach(muhasebePerms::add);

        appRoleRepository.save(AppRole.builder()
                .name("Muhasebe")
                .description("Ödeme ve ciro raporları")
                .permissions(muhasebePerms)
                .isActive(true)
                .build());

        // Üretim rolü
        Set<Permission> uretimPerms = new HashSet<>();
        savedPerms.stream()
                .filter(p -> List.of("siparis.goruntule", "siparis.baskiya_gonder", "siparis.tamamla").contains(p.getCode()))
                .forEach(uretimPerms::add);

        appRoleRepository.save(AppRole.builder()
                .name("Üretim")
                .description("Sadece üretim aşamasındaki siparişler")
                .permissions(uretimPerms)
                .isActive(true)
                .build());

        // Pazarlama rolü — referans yönetimi dahil
        Set<Permission> pazarlamaPerms = new HashSet<>();
        savedPerms.stream()
                .filter(p -> p.getCode().equals("referans.yonet") || p.getCode().startsWith("rapor."))
                .forEach(pazarlamaPerms::add);

        appRoleRepository.save(AppRole.builder()
                .name("Pazarlama")
                .description("Referans yönetimi ve raporlar")
                .permissions(pazarlamaPerms)
                .isActive(true)
                .build());

        // Admin rolü — tüm izinler
        appRoleRepository.save(AppRole.builder()
                .name("Admin")
                .description("Tüm yetkilere sahip")
                .permissions(new HashSet<>(savedPerms))
                .isActive(true)
                .build());

        log.info("{} izin ve 5 varsayılan rol yüklendi.", savedPerms.size());
    }
}
