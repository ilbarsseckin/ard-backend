-- ════════════════════════════════════════════════════════════════════
--  KARTVİZİT — TEMİZ KURULUM  ·  seed-kartvizit-temiz.sql
-- --------------------------------------------------------------------
--  NE YAPAR:
--    1) SADECE kartvizit ürün katmanını siler (ürün + tier + resim +
--       öznitelik bağları). Kategoriler, öznitelikler, opsiyonlar KALIR.
--       Kartvizit DIŞINDAKİ kategorilerin ürünlerine DOKUNMAZ.
--    2) 15 kartvizit çeşidini gerçek alt kategori slug'larına kurar.
--    3) Fiyatları tek taban fiyattan (500 adet) otomatik 3 kademe yapar.
--    4) kartvizit özniteliklerini (renk/ebat/kağıt/baskı yönü/selefon)
--       her ürüne otomatik bağlar.
--    5) Resimler boş → admin panelden yüklenir.
--
--  Slug bazlı + idempotent. Tekrar çalıştırılabilir.
--  Çalıştırma:
--    docker exec -i ard-backend-db-1 psql -U baski_user -d baski_db < seed-kartvizit-temiz.sql
-- ════════════════════════════════════════════════════════════════════

BEGIN;

-- ════════════════════════════════════════════════════════════════════
-- 1) TEMİZLİK — sadece "kartvizit" + alt kategorilerindeki ürün katmanı
--    (FK güvenli sıra: önce çocuk tablolar, sonra ürün)
-- ════════════════════════════════════════════════════════════════════
CREATE TEMP TABLE _kv_prod ON COMMIT DROP AS
  SELECT p.id
  FROM catalog_products p
  JOIN catalog_categories c ON c.id = p.category_id
  WHERE c.slug = 'kartvizit'
     OR c.parent_id = (SELECT id FROM catalog_categories WHERE slug = 'kartvizit');

DELETE FROM catalog_product_attribute_values WHERE product_id IN (SELECT id FROM _kv_prod);
DELETE FROM catalog_product_images           WHERE product_id IN (SELECT id FROM _kv_prod);
DELETE FROM catalog_product_tiers            WHERE product_id IN (SELECT id FROM _kv_prod);
DELETE FROM catalog_products                 WHERE id         IN (SELECT id FROM _kv_prod);


-- ════════════════════════════════════════════════════════════════════
-- 2) ÜRÜNLER   ⭐  YENİ ÜRÜN = BURAYA 1 SATIR
--    slug | ad | alt-kategori-slug | kısa açıklama | öne-çıkan | sıra
-- ════════════════════════════════════════════════════════════════════
INSERT INTO catalog_products (id, slug, name, category_id, short_desc, featured, active, sort_order, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name,
       (SELECT id FROM catalog_categories WHERE slug = v.cat),
       v.descr, v.feat, true, v.so, NOW(), NOW()
FROM (VALUES
  ('sivama-kartvizit',          'Sıvama Kartvizit',           'kartvizit-premium',    'Sıvama tekniğiyle parlak yüzey, premium görünüm.',     true,  10),
  ('3-katli-sandvic-kartvizit', '3 Katlı Sandviç Kartvizit',  'kartvizit-premium',    '3 katmanlı kalın yapı, lüks ve dayanıklı dokunuş.',    true,  11),
  ('tuale-fantazi-kartvizit',   'Tuale Fantazi Kartvizit',    'kartvizit-premium',    'Tuale dokulu özel kağıt, sanatsal ve özgün etki.',     false, 12),
  ('gofreli-kartvizit',         'Gofreli Kartvizit',          'kartvizit-kabartma',   'Kabartma desenli, dokunsal etki yaratan kartvizit.',   false, 20),
  ('kabartma-lakli-kartvizit',  'Kabartma Laklı Kartvizit',   'kartvizit-kabartma',   'Selektif lak kabartma, modern ve şık tasarım.',        true,  21),
  ('altin-yaldizli-kartvizit',  'Altın Yaldızlı Kartvizit',   'kartvizit-yaldiz',     'Altın yaldız detayları ile premium görünüm.',          true,  30),
  ('softtouch-kartvizit',       'Yumuşak Dokulu Kartvizit',   'kartvizit-soft-touch', 'SoftTouch teknolojisi, yumuşak ve premium hisli yüzey.',false, 40),
  ('kare-kartvizit',            'Kare Kartvizit',             'kartvizit-ozel',       '9x9cm kare format, dikkat çekici alternatif tasarım.', false, 50),
  ('katlamali-kartvizit',       'Katlamalı Kartvizit',        'kartvizit-ozel',       'Katlanabilir tasarım, içinde detay sunma alanı.',      false, 51),
  ('oval-kesim-kartvizit',      'Oval Kesim Kartvizit',       'kartvizit-ozel',       'Köşeleri oval kesim, modern ve şık silüet.',           false, 52),
  ('iki-kenar-oval-kartvizit',  'İki Kenar Oval Kartvizit',   'kartvizit-ozel',       'Üst ve alt kenarları oval, dikkat çekici form.',       false, 53),
  ('takvimli-kartvizit',        'Takvimli Kartvizit',         'kartvizit-ozel',       'Arkasında takvim baskı, uzun süreli akılda kalıcılık.',false, 54),
  ('pvc-kapli-kartvizit',       'PVC Kaplı Kartvizit',        'kartvizit-pvc',        'PVC kaplama, su geçirmez ve uzun ömürlü kullanım.',    false, 60),
  ('seffaf-kartvizit',          'Şeffaf Kartvizit',           'kartvizit-pvc',        'Şeffaf plastik malzeme, lüks ve dikkat çekici.',       true,  61),
  ('kraft-kartvizit',           'Kraft Kartvizit',            'kartvizit-eko',        'Kraft kağıt, doğal ve özgün görünüm. Eco friendly.',   true,  70)
) AS v(slug, name, cat, descr, feat, so)
ON CONFLICT (slug) DO NOTHING;


-- ════════════════════════════════════════════════════════════════════
-- 3) FİYATLAR   ⭐  FİYAT DÜZENLEME SADECE BURADA
--    Ürün başına TEK taban fiyat (500 adet USD). 500/1000/2500 otomatik.
-- ════════════════════════════════════════════════════════════════════
WITH taban_fiyat(slug, fiyat) AS (VALUES
  ('sivama-kartvizit',          22.00),
  ('3-katli-sandvic-kartvizit', 28.00),
  ('tuale-fantazi-kartvizit',   22.00),
  ('gofreli-kartvizit',         25.00),
  ('kabartma-lakli-kartvizit',  26.00),
  ('altin-yaldizli-kartvizit',  30.00),
  ('softtouch-kartvizit',       24.00),
  ('kare-kartvizit',            18.00),
  ('katlamali-kartvizit',       20.00),
  ('oval-kesim-kartvizit',      19.00),
  ('iki-kenar-oval-kartvizit',  20.00),
  ('takvimli-kartvizit',        20.00),
  ('pvc-kapli-kartvizit',       25.00),
  ('seffaf-kartvizit',          30.00),
  ('kraft-kartvizit',           19.00)
),
kademe(qty, carpan, so) AS (VALUES
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
-- 4) ÖZNİTELİK BAĞLAMA (OTOMATİK)
--    Her kartvizit ürünü, ÜST "kartvizit" kategorisinin TÜM öznitelik
--    opsiyonlarını alır (renk, ebat, kağıt, baskı yönü, selefon).
--    Böylece tüm kartvizit çeşitleri aynı, tutarlı seçeneklere sahip olur.
-- ════════════════════════════════════════════════════════════════════
INSERT INTO catalog_product_attribute_values (id, product_id, attribute_id, option_id)
SELECT gen_random_uuid(), p.id, o.attribute_id, o.id
FROM catalog_products p
JOIN catalog_categories c ON c.id = p.category_id
JOIN catalog_attributes a
  ON a.category_id = (SELECT id FROM catalog_categories WHERE slug = 'kartvizit')
JOIN catalog_attribute_options o ON o.attribute_id = a.id
WHERE (c.slug = 'kartvizit'
       OR c.parent_id = (SELECT id FROM catalog_categories WHERE slug = 'kartvizit'))
  AND NOT EXISTS (
    SELECT 1 FROM catalog_product_attribute_values pav
    WHERE pav.product_id = p.id AND pav.option_id = o.id
  );


-- ════════════════════════════════════════════════════════════════════
-- 5) RESİMLER — boş. Admin panel → Katalog → Ürünler → [ürün] → Görseller.
-- ════════════════════════════════════════════════════════════════════

COMMIT;


-- ════════════════════════════════════════════════════════════════════
--  DOĞRULAMA
-- ════════════════════════════════════════════════════════════════════
SELECT
  cat.name AS alt_kategori,
  p.name   AS urun,
  (SELECT string_agg(t.qty || '=' || t.price_usd, ' · ' ORDER BY t.sort_order)
     FROM catalog_product_tiers t WHERE t.product_id = p.id) AS fiyatlar,
  (SELECT COUNT(*) FROM catalog_product_attribute_values pav WHERE pav.product_id = p.id) AS opsiyon,
  (SELECT COUNT(*) FROM catalog_product_images img WHERE img.product_id = p.id) AS resim
FROM catalog_products p
JOIN catalog_categories cat ON cat.id = p.category_id
WHERE cat.slug = 'kartvizit'
   OR cat.parent_id = (SELECT id FROM catalog_categories WHERE slug = 'kartvizit')
ORDER BY cat.sort_order, p.sort_order;
