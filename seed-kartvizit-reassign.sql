-- ════════════════════════════════════════════════════════════
--  KARTVIZIT ÜRÜN TEMİZLİĞİ + SUB-CAT ATAMALARI
-- ════════════════════════════════════════════════════════════

BEGIN;

-- ─── 1) TEST ÇÖPLERİNİ SİL ─────────────────────────────
DELETE FROM catalog_product_attribute_values WHERE product_id IN (
  SELECT id FROM catalog_products WHERE slug IN ('assas','dfdfdfdf','sdsds','kartvizit','sfsdfsdfsd')
);

DELETE FROM catalog_products WHERE slug IN ('assas','dfdfdfdf','sdsds','kartvizit','sfsdfsdfsd');


-- ─── 2) EXISTING SUB-CAT'LERE EŞLEŞENLERİ TAŞI ───────
-- Standart
UPDATE catalog_products SET category_id = (SELECT id FROM catalog_categories WHERE slug = 'kartvizit-standart')
WHERE slug = 'standart-kartvizit';

-- Altın Yaldız
UPDATE catalog_products SET category_id = (SELECT id FROM catalog_categories WHERE slug = 'kartvizit-yaldiz')
WHERE slug = 'altin-yaldizli-kartvizit';

-- PVC
UPDATE catalog_products SET category_id = (SELECT id FROM catalog_categories WHERE slug = 'kartvizit-pvc')
WHERE slug IN ('pvc-kapli-kartvizit', 'seffaf-kartvizit');

-- Kabartma
UPDATE catalog_products SET category_id = (SELECT id FROM catalog_categories WHERE slug = 'kartvizit-kabartma')
WHERE slug = 'kabartma-lakli-kartvizit';

-- Soft Touch
UPDATE catalog_products SET category_id = (SELECT id FROM catalog_categories WHERE slug = 'kartvizit-soft-touch')
WHERE slug = 'softtouch-kartvizit';

-- Tuale Fantazi
UPDATE catalog_products SET category_id = (SELECT id FROM catalog_categories WHERE slug = 'tuale-fantazi-kartvizit-bk')
WHERE slug = 'tuale-fantazi-kartvizit';

-- Premium → Sıvama/Gofreli/Katlamalı/3-Katlı (premium tarzı işlemler)
UPDATE catalog_products SET category_id = (SELECT id FROM catalog_categories WHERE slug = 'kartvizit-premium')
WHERE slug IN ('sivama-kartvizit', 'gofreli-kartvizit', 'katlamali-kartvizit', '3-katli-sandvic-kartvizit');


-- ─── 3) DOĞRULAMA ─────────────────────────────────────
COMMIT;

SELECT c.slug AS kategori_slug, c.name AS kategori_adi, COUNT(p.id) AS urun_sayisi
FROM catalog_categories c
LEFT JOIN catalog_products p ON p.category_id = c.id
WHERE c.slug = 'kartvizit' OR c.parent_id = (SELECT id FROM catalog_categories WHERE slug = 'kartvizit')
GROUP BY c.slug, c.name, c.sort_order
ORDER BY c.sort_order;
