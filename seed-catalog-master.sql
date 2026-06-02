-- ════════════════════════════════════════════════════════════════════
--  KATALOG MASTER SEED  ·  seed-catalog-master.sql
-- --------------------------------------------------------------------
--  • Tamamen SLUG / KEY bazlı  → UUID hardcode YOK, her DB'de aynı çalışır
--  • IDEMPOTENT              → defalarca çalıştır, kopya satır oluşmaz
--  • Yeni ürün eklemek       = 4. bölümdeki VALUES listesine 1 satır
--  • Fiyat düzenlemek        = 5. bölümdeki taban-fiyat listesinde 1 sayı
--  • Resimler boş bırakılır  → admin panelden yüklenir (7. bölüm)
--  • Öznitelikler ürünlere   OTOMATİK atanır (kategoriye + üst kategoriye göre)
--
--  Çalıştırma:
--    docker exec -i ard-backend-db-1 psql -U baski_user -d baski_db < seed-catalog-master.sql
-- ════════════════════════════════════════════════════════════════════

BEGIN;

-- ────────────────────────────────────────────────────────────────────
-- 0) ŞEMA GÜVENCESİ  (kolon yoksa ekle — eski DB'lerle uyum)
-- ────────────────────────────────────────────────────────────────────
ALTER TABLE catalog_categories ADD COLUMN IF NOT EXISTS parent_id UUID;
CREATE INDEX IF NOT EXISTS idx_cat_cat_parent ON catalog_categories(parent_id);


-- ════════════════════════════════════════════════════════════════════
-- 1) ANA KATEGORİLER
--    slug | ad | ikon | alt-başlık | sıra
-- ════════════════════════════════════════════════════════════════════
INSERT INTO catalog_categories (id, slug, name, icon, tagline, sort_order, active, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, v.icon, v.tagline, v.so, true, NOW(), NOW()
FROM (VALUES
  ('kartvizit',       'Kartvizit',          '💳', 'Profesyonel kartvizit çeşitleri',  10),
  ('brosur',          'Broşür & El İlanı',  '📄', 'Tanıtım ve reklam baskıları',      20),
  ('kurumsal-baski',  'Kurumsal Baskılar',  '🏢', 'Antetli kağıt, zarf, dosya',       30)
) AS v(slug, name, icon, tagline, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories c WHERE c.slug = v.slug);


-- ════════════════════════════════════════════════════════════════════
-- 2) ALT KATEGORİLER  (üst kategoriye SLUG ile bağlanır)
--    slug | ad | ikon | üst-kategori-slug | sıra
-- ════════════════════════════════════════════════════════════════════
INSERT INTO catalog_categories (id, slug, name, icon, parent_id, sort_order, active, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, v.icon,
       (SELECT id FROM catalog_categories WHERE slug = v.parent),
       v.so, true, NOW(), NOW()
FROM (VALUES
  ('kartvizit-premium', 'Premium Kartvizit', '💎', 'kartvizit', 1),
  ('kartvizit-ozel',    'Özel Kesim & Form', '✂️', 'kartvizit', 2),
  ('kartvizit-eko',     'Ekonomik Kartvizit','🌿', 'kartvizit', 3)
) AS v(slug, name, icon, parent, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories c WHERE c.slug = v.slug);


-- ════════════════════════════════════════════════════════════════════
-- 3) ÖZNİTELİKLER (ATTRIBUTE)  — kategori SLUG + attr_key ile
--    kategori-slug | key | etiket | input-tipi | zorunlu | sıra
-- ════════════════════════════════════════════════════════════════════
INSERT INTO catalog_attributes (id, category_id, attr_key, label, input_type, required, sort_order)
SELECT gen_random_uuid(),
       (SELECT id FROM catalog_categories WHERE slug = v.cat),
       v.key, v.label, v.itype, v.req, v.so
FROM (VALUES
  ('kartvizit', 'ebat',       'Ebat',       'single_select', true,  1),
  ('kartvizit', 'kagit',      'Kağıt',      'single_select', true,  2),
  ('kartvizit', 'baski_yonu', 'Baskı Yönü', 'single_select', true,  3),
  ('kartvizit', 'selefon',    'Selefon',    'single_select', false, 4)
) AS v(cat, key, label, itype, req, so)
WHERE NOT EXISTS (
  SELECT 1 FROM catalog_attributes a
  WHERE a.attr_key = v.key
    AND a.category_id = (SELECT id FROM catalog_categories WHERE slug = v.cat)
);

-- 3b) ÖZNİTELİK OPSİYONLARI  — kategori-slug + attr_key + değer ile
--     kategori | key | değer | renk_hex | fiyat_çarpanı | sıra
INSERT INTO catalog_attribute_options (id, attribute_id, value, color_hex, price_modifier, sort_order)
SELECT gen_random_uuid(),
       (SELECT a.id FROM catalog_attributes a
          JOIN catalog_categories c ON c.id = a.category_id
         WHERE c.slug = v.cat AND a.attr_key = v.key),
       v.val, v.hex, v.pmod, v.so
FROM (VALUES
  -- Ebat
  ('kartvizit','ebat',       '5.5 x 8.5 cm (Standart)', NULL,      1.000, 1),
  ('kartvizit','ebat',       '9 x 5 cm',                NULL,      1.000, 2),
  ('kartvizit','ebat',       '8.5 x 5.5 cm (Yatay)',    NULL,      1.000, 3),
  -- Kağıt
  ('kartvizit','kagit',      '350g Mat Kuşe',           NULL,      1.000, 1),
  ('kartvizit','kagit',      '400g Parlak Kuşe',        NULL,      1.150, 2),
  ('kartvizit','kagit',      '300g Bristol',            NULL,      1.050, 3),
  ('kartvizit','kagit',      'Kraft Kağıt',             NULL,      1.100, 4),
  -- Baskı Yönü
  ('kartvizit','baski_yonu', 'Tek Yön Baskı',           NULL,      1.000, 1),
  ('kartvizit','baski_yonu', 'Çift Yön Baskı',          NULL,      1.200, 2),
  -- Selefon
  ('kartvizit','selefon',    'Selefon Yok',             NULL,      1.000, 1),
  ('kartvizit','selefon',    'Mat Selefon',             NULL,      1.100, 2),
  ('kartvizit','selefon',    'Parlak Selefon',          NULL,      1.100, 3),
  ('kartvizit','selefon',    'Soft Touch',              NULL,      1.250, 4)
) AS v(cat, key, val, hex, pmod, so)
WHERE NOT EXISTS (
  SELECT 1 FROM catalog_attribute_options o
  WHERE o.value = v.val
    AND o.attribute_id = (SELECT a.id FROM catalog_attributes a
                            JOIN catalog_categories c ON c.id = a.category_id
                           WHERE c.slug = v.cat AND a.attr_key = v.key)
);


-- ════════════════════════════════════════════════════════════════════
-- 4) ÜRÜNLER   ⭐  YENİ ÜRÜN = BURAYA 1 SATIR EKLE
--    slug | ad | kategori-slug | kısa açıklama | öne-çıkan | sıra
--    (Fiyat ve resim burada YOK — fiyat 5. bölümde, resim admin panelde)
-- ════════════════════════════════════════════════════════════════════
INSERT INTO catalog_products (id, slug, name, category_id, short_desc, featured, active, sort_order, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name,
       (SELECT id FROM catalog_categories WHERE slug = v.cat),
       v.descr, v.feat, true, v.so, NOW(), NOW()
FROM (VALUES
  ('sivama-kartvizit',          'Sıvama Kartvizit',          'kartvizit-premium', 'Sıvama tekniğiyle parlak yüzey, premium görünüm.', true,  10),
  ('3-katli-sandvic-kartvizit', '3 Katlı Sandviç Kartvizit', 'kartvizit-premium', '3 katmanlı kalın yapı, lüks ve dayanıklı dokunuş.', true,  11),
  ('gofreli-kartvizit',         'Gofreli Kartvizit',         'kartvizit-premium', 'Kabartma desenli, dokunsal etki yaratan kartvizit.',false, 12),
  ('altin-yaldizli-kartvizit',  'Altın Yaldızlı Kartvizit',  'kartvizit-premium', 'Altın yaldız detayları ile premium görünüm.',       true,  13),
  ('oval-kesim-kartvizit',      'Oval Kesim Kartvizit',      'kartvizit-ozel',    'Köşeleri oval kesim, modern ve şık silüet.',        false, 20),
  ('kare-kartvizit',            'Kare Kartvizit',            'kartvizit-ozel',    '9x9cm kare format, dikkat çekici tasarım.',         false, 21),
  ('katlamali-kartvizit',       'Katlamalı Kartvizit',       'kartvizit-ozel',    'Katlanabilir tasarım, içinde detay alanı.',         false, 22),
  ('standart-kartvizit',        'Standart Kartvizit',        'kartvizit-eko',     'Ekonomik, hızlı teslim standart kartvizit.',        false, 30),
  ('kraft-kartvizit',           'Kraft Kartvizit',           'kartvizit-eko',     'Kraft kağıt, doğal ve özgün görünüm. Eco friendly.',true,  31)
) AS v(slug, name, cat, descr, feat, so)
ON CONFLICT (slug) DO NOTHING;


-- ════════════════════════════════════════════════════════════════════
-- 5) FİYATLAR   ⭐  FİYAT DÜZENLEME SADECE BURADA
--    Her ürüne TEK taban fiyat ver (500 adet, USD).
--    500 / 1000 / 2500 kademeleri katsayı tablosuyla OTOMATİK hesaplanır.
-- ════════════════════════════════════════════════════════════════════
WITH taban_fiyat(slug, fiyat) AS (VALUES
  ('sivama-kartvizit',          22.00),
  ('3-katli-sandvic-kartvizit', 28.00),
  ('gofreli-kartvizit',         25.00),
  ('altin-yaldizli-kartvizit',  30.00),
  ('oval-kesim-kartvizit',      18.00),
  ('kare-kartvizit',            18.00),
  ('katlamali-kartvizit',       20.00),
  ('standart-kartvizit',        12.00),
  ('kraft-kartvizit',           16.00)
),
kademe(qty, carpan, so) AS (VALUES   -- adet kademeleri — istersen değiştir
  ( 500, 1.00, 1),
  (1000, 1.50, 2),
  (2500, 3.20, 3)
)
INSERT INTO catalog_product_tiers (id, product_id, qty, price_usd, sort_order)
SELECT gen_random_uuid(), p.id, k.qty, ROUND((t.fiyat * k.carpan)::numeric, 2), k.so
FROM taban_fiyat t
JOIN catalog_products p ON p.slug = t.slug
CROSS JOIN kademe k
WHERE NOT EXISTS (
  SELECT 1 FROM catalog_product_tiers x WHERE x.product_id = p.id AND x.qty = k.qty
);


-- ════════════════════════════════════════════════════════════════════
-- 6) ÜRÜN ↔ ÖZNİTELİK EŞLEŞTİRMESİ  (OTOMATİK — elle uğraşma yok)
--    Kuralı: bir ürün, KENDİ kategorisinin + ÜST kategorisinin tüm
--    öznitelik opsiyonlarını otomatik alır. Böylece alt kategori
--    ürünleri de "kartvizit" özniteliklerini (ebat, kağıt...) devralır.
-- ════════════════════════════════════════════════════════════════════
INSERT INTO catalog_product_attribute_values (id, product_id, attribute_id, option_id)
SELECT gen_random_uuid(), p.id, o.attribute_id, o.id
FROM catalog_products p
JOIN catalog_categories pc ON pc.id = p.category_id
JOIN catalog_attributes a
  ON a.category_id = pc.id
  OR a.category_id = pc.parent_id           -- üst kategori öznitelikleri de
JOIN catalog_attribute_options o ON o.attribute_id = a.id
WHERE NOT EXISTS (
  SELECT 1 FROM catalog_product_attribute_values pav
  WHERE pav.product_id = p.id AND pav.option_id = o.id
);


-- ════════════════════════════════════════════════════════════════════
-- 7) RESİMLER  — kasıtlı olarak BOŞ.
--    Admin panel → Katalog → Ürünler → [ürün] → Görseller'den yükle.
--
--    (İSTEĞE BAĞLI) Her ürüne geçici placeholder eklemek istersen
--    aşağıdaki bloğun yorumunu kaldır:
-- ────────────────────────────────────────────────────────────────────
-- INSERT INTO catalog_product_images (id, product_id, url, alt_text, sort_order)
-- SELECT gen_random_uuid(), p.id, '/placeholder-product.png', p.name, 0
-- FROM catalog_products p
-- WHERE NOT EXISTS (SELECT 1 FROM catalog_product_images i WHERE i.product_id = p.id);


COMMIT;


-- ════════════════════════════════════════════════════════════════════
--  DOĞRULAMA SORGULARI  (COMMIT sonrası — sadece okur, değiştirmez)
-- ════════════════════════════════════════════════════════════════════

-- 7a) Kategori ağacı
SELECT COALESCE(pp.name, '— ANA —') AS ust_kategori, c.name, c.slug, c.sort_order AS sira
FROM catalog_categories c
LEFT JOIN catalog_categories pp ON pp.id = c.parent_id
ORDER BY COALESCE(pp.sort_order, c.sort_order), c.parent_id NULLS FIRST, c.sort_order;

-- 7b) Ürün özeti: kategori + fiyat kademeleri + öznitelik sayısı + resim sayısı
SELECT
  p.name AS urun,
  cat.name AS kategori,
  (SELECT string_agg(t.qty || ' adet=' || t.price_usd || '$', '  ·  ' ORDER BY t.sort_order)
     FROM catalog_product_tiers t WHERE t.product_id = p.id) AS fiyatlar,
  (SELECT COUNT(*) FROM catalog_product_attribute_values pav WHERE pav.product_id = p.id) AS oznitelik_opsiyon,
  (SELECT COUNT(*) FROM catalog_product_images img WHERE img.product_id = p.id) AS resim
FROM catalog_products p
JOIN catalog_categories cat ON cat.id = p.category_id
ORDER BY cat.sort_order, p.sort_order;
