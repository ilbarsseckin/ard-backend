-- ════════════════════════════════════════════════════════════
--  KARTVİZİT KATEGORİSİ — ATTRIBUTE GENİŞLETMESİ
--  + 3 yeni attribute (Ebat, Baskı Yönü, Selefon)
--  + Kağıt'a 4 ek opsiyon
--  + Tüm kartvizit ürünlerine opsiyonları otomatik atama
--  Idempotent — birden fazla çalıştırılabilir
-- ════════════════════════════════════════════════════════════

BEGIN;

-- ────────────────────────────────────────────────────────────
-- 1) YENİ ATTRIBUTE'LAR
-- ────────────────────────────────────────────────────────────

-- Ebat
INSERT INTO catalog_attributes (id, attr_key, input_type, label, required, sort_order, category_id)
SELECT
  gen_random_uuid(),
  'ebat',
  COALESCE((SELECT input_type FROM catalog_attributes WHERE id = '8811e952-4385-400e-a157-6e3b239a6f49'), 'single_select'),
  'Ebat',
  true,
  1,
  'ac16bace-2752-493d-aeb6-3c6f9977a059'
WHERE NOT EXISTS (
  SELECT 1 FROM catalog_attributes
  WHERE attr_key = 'ebat' AND category_id = 'ac16bace-2752-493d-aeb6-3c6f9977a059'
);

-- Baskı Yönü
INSERT INTO catalog_attributes (id, attr_key, input_type, label, required, sort_order, category_id)
SELECT
  gen_random_uuid(),
  'baski_yonu',
  COALESCE((SELECT input_type FROM catalog_attributes WHERE id = '8811e952-4385-400e-a157-6e3b239a6f49'), 'single_select'),
  'Baskı Yönü',
  true,
  2,
  'ac16bace-2752-493d-aeb6-3c6f9977a059'
WHERE NOT EXISTS (
  SELECT 1 FROM catalog_attributes
  WHERE attr_key = 'baski_yonu' AND category_id = 'ac16bace-2752-493d-aeb6-3c6f9977a059'
);

-- Selefon
INSERT INTO catalog_attributes (id, attr_key, input_type, label, required, sort_order, category_id)
SELECT
  gen_random_uuid(),
  'selefon',
  COALESCE((SELECT input_type FROM catalog_attributes WHERE id = '8811e952-4385-400e-a157-6e3b239a6f49'), 'single_select'),
  'Selefon',
  false,
  4,
  'ac16bace-2752-493d-aeb6-3c6f9977a059'
WHERE NOT EXISTS (
  SELECT 1 FROM catalog_attributes
  WHERE attr_key = 'selefon' AND category_id = 'ac16bace-2752-493d-aeb6-3c6f9977a059'
);

-- Mevcut Kağıt sort_order = 3 olsun
UPDATE catalog_attributes
SET sort_order = 3
WHERE id = '8811e952-4385-400e-a157-6e3b239a6f49';


-- ────────────────────────────────────────────────────────────
-- 2) OPSİYONLAR
-- ────────────────────────────────────────────────────────────

-- Ebat opsiyonları
INSERT INTO catalog_attribute_options (id, value, sort_order, attribute_id)
SELECT
  gen_random_uuid(), v.value, v.so,
  (SELECT id FROM catalog_attributes WHERE attr_key = 'ebat' AND category_id = 'ac16bace-2752-493d-aeb6-3c6f9977a059')
FROM (VALUES
  ('5.5 x 8.5 cm (Standart)', 1),
  ('8.5 x 5.5 cm (Yatay)',    2),
  ('9 x 5 cm',                3),
  ('8 x 5 cm',                4)
) AS v(value, so)
WHERE NOT EXISTS (
  SELECT 1 FROM catalog_attribute_options
  WHERE attribute_id = (SELECT id FROM catalog_attributes WHERE attr_key = 'ebat' AND category_id = 'ac16bace-2752-493d-aeb6-3c6f9977a059')
    AND value = v.value
);

-- Baskı Yönü opsiyonları
INSERT INTO catalog_attribute_options (id, value, sort_order, attribute_id)
SELECT
  gen_random_uuid(), v.value, v.so,
  (SELECT id FROM catalog_attributes WHERE attr_key = 'baski_yonu' AND category_id = 'ac16bace-2752-493d-aeb6-3c6f9977a059')
FROM (VALUES
  ('Tek Yön Baskı',  1),
  ('Çift Yön Baskı', 2)
) AS v(value, so)
WHERE NOT EXISTS (
  SELECT 1 FROM catalog_attribute_options
  WHERE attribute_id = (SELECT id FROM catalog_attributes WHERE attr_key = 'baski_yonu' AND category_id = 'ac16bace-2752-493d-aeb6-3c6f9977a059')
    AND value = v.value
);

-- Selefon opsiyonları
INSERT INTO catalog_attribute_options (id, value, sort_order, attribute_id)
SELECT
  gen_random_uuid(), v.value, v.so,
  (SELECT id FROM catalog_attributes WHERE attr_key = 'selefon' AND category_id = 'ac16bace-2752-493d-aeb6-3c6f9977a059')
FROM (VALUES
  ('Selefon Yok',    1),
  ('Mat Selefon',    2),
  ('Parlak Selefon', 3),
  ('Soft Touch',     4)
) AS v(value, so)
WHERE NOT EXISTS (
  SELECT 1 FROM catalog_attribute_options
  WHERE attribute_id = (SELECT id FROM catalog_attributes WHERE attr_key = 'selefon' AND category_id = 'ac16bace-2752-493d-aeb6-3c6f9977a059')
    AND value = v.value
);

-- Kağıt'a ek opsiyonlar
INSERT INTO catalog_attribute_options (id, value, sort_order, attribute_id)
SELECT
  gen_random_uuid(), v.value, v.so, '8811e952-4385-400e-a157-6e3b239a6f49'::uuid
FROM (VALUES
  ('350g Parlak Kuşe', 3),
  ('400g Parlak Kuşe', 4),
  ('300g Bristol',     5),
  ('Kraft Kağıt',      6)
) AS v(value, so)
WHERE NOT EXISTS (
  SELECT 1 FROM catalog_attribute_options
  WHERE attribute_id = '8811e952-4385-400e-a157-6e3b239a6f49'
    AND value = v.value
);


-- ────────────────────────────────────────────────────────────
-- 3) TÜM KARTVİZİT ÜRÜNLERİNE OPSİYONLARI ATA
--    (idempotent — sadece eksik olanları ekler)
-- ────────────────────────────────────────────────────────────

-- Ebat + Baskı Yönü + Selefon opsiyonları
INSERT INTO catalog_product_attribute_values (id, attribute_id, option_id, product_id)
SELECT gen_random_uuid(), o.attribute_id, o.id, p.id
FROM catalog_products p
CROSS JOIN catalog_attribute_options o
WHERE p.category_id = 'ac16bace-2752-493d-aeb6-3c6f9977a059'
  AND o.attribute_id IN (
    SELECT id FROM catalog_attributes
    WHERE category_id = 'ac16bace-2752-493d-aeb6-3c6f9977a059'
      AND attr_key IN ('ebat', 'baski_yonu', 'selefon')
  )
  AND NOT EXISTS (
    SELECT 1 FROM catalog_product_attribute_values pav
    WHERE pav.product_id = p.id AND pav.option_id = o.id
  );

-- Yeni Kağıt opsiyonları
INSERT INTO catalog_product_attribute_values (id, attribute_id, option_id, product_id)
SELECT gen_random_uuid(), '8811e952-4385-400e-a157-6e3b239a6f49'::uuid, o.id, p.id
FROM catalog_products p
CROSS JOIN catalog_attribute_options o
WHERE p.category_id = 'ac16bace-2752-493d-aeb6-3c6f9977a059'
  AND o.attribute_id = '8811e952-4385-400e-a157-6e3b239a6f49'
  AND o.value IN ('350g Parlak Kuşe', '400g Parlak Kuşe', '300g Bristol', 'Kraft Kağıt')
  AND NOT EXISTS (
    SELECT 1 FROM catalog_product_attribute_values pav
    WHERE pav.product_id = p.id AND pav.option_id = o.id
  );

COMMIT;


-- ════════════════════════════════════════════════════════════
-- DOĞRULAMA SORGULARI
-- ════════════════════════════════════════════════════════════

-- Kartvizit kategorisinin attribute'ları
SELECT a.label, a.attr_key, a.sort_order, a.required, COUNT(o.id) AS opsiyon_sayisi
FROM catalog_attributes a
LEFT JOIN catalog_attribute_options o ON o.attribute_id = a.id
WHERE a.category_id = 'ac16bace-2752-493d-aeb6-3c6f9977a059'
GROUP BY a.id, a.label, a.attr_key, a.sort_order, a.required
ORDER BY a.sort_order;

-- Bir örnek ürünün attribute durumu
SELECT p.name, a.label, COUNT(o.id) AS opsiyon_sayisi
FROM catalog_products p
LEFT JOIN catalog_product_attribute_values pav ON pav.product_id = p.id
LEFT JOIN catalog_attributes a ON a.id = pav.attribute_id
LEFT JOIN catalog_attribute_options o ON o.id = pav.option_id
WHERE p.slug = 'standart-kartvizit'
GROUP BY p.name, a.label, a.sort_order
ORDER BY a.sort_order;
