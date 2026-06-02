-- ════════════════════════════════════════════════════════════════════
--  KATEGORİ YAPISINI SADELEŞTİRME  ·  migrate-kategori-sadelestir.sql
-- --------------------------------------------------------------------
--  NE YAPAR (senin onayladığın temiz yapıya göre):
--    • brosur + el-ilani-brosur  → tek "Broşür & El İlanı" (brosur)
--    • kurumsal-baski + kurumsal-urunler → tek "Kurumsal Baskılar"
--    • fotograf-baski + tablolar → tek "Tablo & Fotoğraf"
--    • acil-baski ve tüm "hızlı-*" kategorileri SİLİNİR
--      (Ekspres artık kategori değil; ürünlerde "badge" alanı ile verilecek)
--    • Magnet sadece "Promosyon > Magnet"te kalır (UV ve matbaa magnet silinir)
--    • Tekrarlar silinir: katlamali-brosur (dup), canvas-tablo (dup)
--    • Kartvizit altındaki ürün-benzeri kategoriler silinir
--      (yaldizli-kartvizit, tuale-fantazi-kartvizit-bk, yaldizli-bristol-kartvizit,
--       ekonomik-tuale-kartvizit, tesekkur-not-karti → bunlar sonra ÜRÜN olacak)
--
--  GÜVENLİK:
--    • Öznitelikler korunur (öznitelik taşıyan hiçbir kategori silinmez).
--    • Silinen/birleşen kategorideki ürünler, hayatta kalan üst kategoriye TAŞINIR
--      (orphan kalmaz, FK güvenli).
--    • FK güvenli silme sırası: önce çocuklar, sonra boşalan üst kategoriler.
--
--  Çalıştırma (önce dev/yedek DB!):
--    docker exec -i ard-backend-db-1 psql -U baski_user -d baski_db < migrate-kategori-sadelestir.sql
-- ════════════════════════════════════════════════════════════════════

BEGIN;

-- ────────────────────────────────────────────────────────────────────
-- 1) BİRLEŞTİRME — birleşen üst kategorilerin çocuklarını kanonik üste taşı
-- ────────────────────────────────────────────────────────────────────
UPDATE catalog_categories SET parent_id = (SELECT id FROM catalog_categories WHERE slug='brosur')
  WHERE parent_id = (SELECT id FROM catalog_categories WHERE slug='el-ilani-brosur');

UPDATE catalog_categories SET parent_id = (SELECT id FROM catalog_categories WHERE slug='kurumsal-urunler')
  WHERE parent_id = (SELECT id FROM catalog_categories WHERE slug='kurumsal-baski');

UPDATE catalog_categories SET parent_id = (SELECT id FROM catalog_categories WHERE slug='tablolar')
  WHERE parent_id = (SELECT id FROM catalog_categories WHERE slug='fotograf-baski');

-- ────────────────────────────────────────────────────────────────────
-- 2) KANONİK KATEGORİLERİ YENİDEN ADLANDIR
-- ────────────────────────────────────────────────────────────────────
UPDATE catalog_categories SET name='Kurumsal Baskılar' WHERE slug='kurumsal-urunler';
UPDATE catalog_categories SET name='Tablo & Fotoğraf'  WHERE slug='tablolar';

-- ────────────────────────────────────────────────────────────────────
-- 3) ÜRÜNLERİ GÜVENE AL — silinecek kategorideki ürünleri hayatta kalan
--    en yakın üst kategoriye taşı (parent hayattaysa parent'a; yoksa
--    kartvizit'e güvenlik ağı). Adım 1 önce çalıştığı için broşür/kurumsal
--    çocukları zaten doğru üst kategoriye bağlı.
-- ────────────────────────────────────────────────────────────────────
WITH del(slug) AS (VALUES
  ('yaldizli-kartvizit'),('tuale-fantazi-kartvizit-bk'),('tesekkur-not-karti'),
  ('yaldizli-bristol-kartvizit'),('ekonomik-tuale-kartvizit'),
  ('acil-baski'),('hizli-sunum-dosyasi'),('hizli-diplomat-zarf'),('hizli-el-ilani-acil'),
  ('hizli-baski-kartvizit'),('hizli-el-ilani-brosur'),
  ('uv-baski-magnet'),('magnet-mat'),
  ('katlamali-brosur'),('canvas-tablo'),
  ('el-ilani-brosur'),('kurumsal-baski'),('fotograf-baski')
)
UPDATE catalog_products p
SET category_id = COALESCE(
  (SELECT par.id
     FROM catalog_categories cur
     JOIN catalog_categories par ON par.id = cur.parent_id
    WHERE cur.id = p.category_id
      AND par.slug NOT IN (SELECT slug FROM del)),
  (SELECT id FROM catalog_categories WHERE slug='kartvizit')
)
WHERE p.category_id IN (SELECT id FROM catalog_categories WHERE slug IN (SELECT slug FROM del));

-- ────────────────────────────────────────────────────────────────────
-- 4) SİLME — önce LEAF kategoriler (çocuğu kalmayanlar)
-- ────────────────────────────────────────────────────────────────────
DELETE FROM catalog_categories WHERE slug IN (
  -- kartvizit altı ürün-benzeri kategoriler
  'yaldizli-kartvizit','tuale-fantazi-kartvizit-bk','tesekkur-not-karti',
  'yaldizli-bristol-kartvizit','ekonomik-tuale-kartvizit',
  -- ekspres/hızlı kategoriler (artık badge)
  'hizli-sunum-dosyasi','hizli-diplomat-zarf','hizli-el-ilani-acil',
  'hizli-baski-kartvizit','hizli-el-ilani-brosur',
  -- fazlalık magnet kategorileri
  'uv-baski-magnet','magnet-mat',
  -- birebir tekrarlar
  'katlamali-brosur','canvas-tablo'
);

-- 4b) Şimdi boşalan ÜST kategoriler
DELETE FROM catalog_categories WHERE slug IN (
  'acil-baski','el-ilani-brosur','kurumsal-baski','fotograf-baski'
);

COMMIT;


-- ════════════════════════════════════════════════════════════════════
--  DOĞRULAMA
-- ════════════════════════════════════════════════════════════════════
-- Temiz ağaç
SELECT COALESCE(pp.name,'— ANA —') AS ust, c.name, c.slug
FROM catalog_categories c LEFT JOIN catalog_categories pp ON pp.id = c.parent_id
ORDER BY COALESCE(pp.sort_order, c.sort_order), c.parent_id NULLS FIRST, c.sort_order;

-- Öznitelikler hâlâ duruyor mu?
SELECT c.slug, COUNT(a.id) AS oznitelik
FROM catalog_attributes a JOIN catalog_categories c ON c.id = a.category_id
GROUP BY c.slug ORDER BY c.slug;

-- Orphan ürün var mı? (0 olmalı)
SELECT COUNT(*) AS orphan_urun FROM catalog_products p
LEFT JOIN catalog_categories c ON c.id = p.category_id WHERE c.id IS NULL;
