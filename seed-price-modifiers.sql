-- ════════════════════════════════════════════════════════════
--  PRICE MODIFIER ÖZELLİĞİ EKLEME
--  - catalog_attribute_options tablosuna price_modifier kolonu
--  - Mantıklı default değerler ata
-- ════════════════════════════════════════════════════════════

BEGIN;

-- 1) Kolonu ekle (idempotent)
ALTER TABLE catalog_attribute_options
ADD COLUMN IF NOT EXISTS price_modifier NUMERIC(5,3) NOT NULL DEFAULT 1.000;

-- 2) Çarpan değerlerini set et
-- ─── BASKI YÖNÜ ───
UPDATE catalog_attribute_options
SET price_modifier = 1.000
WHERE value = 'Tek Yön Baskı';

UPDATE catalog_attribute_options
SET price_modifier = 1.400
WHERE value = 'Çift Yön Baskı';

-- ─── KAĞIT ───
UPDATE catalog_attribute_options
SET price_modifier = 1.000
WHERE value = '350g Mat Kuse';

UPDATE catalog_attribute_options
SET price_modifier = 1.100
WHERE value = '400g Mat Kuse';

UPDATE catalog_attribute_options
SET price_modifier = 1.050
WHERE value = '350g Parlak Kuşe';

UPDATE catalog_attribute_options
SET price_modifier = 1.150
WHERE value = '400g Parlak Kuşe';

UPDATE catalog_attribute_options
SET price_modifier = 1.250
WHERE value = '300g Bristol';

UPDATE catalog_attribute_options
SET price_modifier = 1.300
WHERE value = 'Kraft Kağıt';

-- ─── SELEFON ───
UPDATE catalog_attribute_options
SET price_modifier = 1.000
WHERE value = 'Selefon Yok';

UPDATE catalog_attribute_options
SET price_modifier = 1.150
WHERE value = 'Mat Selefon';

UPDATE catalog_attribute_options
SET price_modifier = 1.150
WHERE value = 'Parlak Selefon';

UPDATE catalog_attribute_options
SET price_modifier = 1.350
WHERE value = 'Soft Touch';

-- ─── EBAT ───
-- Standart boyutlar fiyat etkilemiyor, hepsi 1.000

COMMIT;

-- Doğrulama
SELECT a.label, o.value, o.price_modifier
FROM catalog_attribute_options o
JOIN catalog_attributes a ON a.id = o.attribute_id
WHERE a.category_id = 'ac16bace-2752-493d-aeb6-3c6f9977a059'
ORDER BY a.sort_order, o.sort_order;
