-- ════════════════════════════════════════════════════════════
--  MEGA KATALOG SEED
--  - parent_id kolonu ekle (hiyerarşik yapı için)
--  - 6 yeni ANA kategori
--  - 24 ALT kategori (her ana altında 4)
--  - Idempotent — birden fazla çalıştırılabilir
-- ════════════════════════════════════════════════════════════

BEGIN;

-- ────────────────────────────────────────────────────────────
-- 1) SCHEMA: parent_id kolonu (hiyerarşik kategoriler)
-- ────────────────────────────────────────────────────────────
ALTER TABLE catalog_categories ADD COLUMN IF NOT EXISTS parent_id UUID;
CREATE INDEX IF NOT EXISTS idx_cat_parent ON catalog_categories(parent_id);


-- ────────────────────────────────────────────────────────────
-- 2) ANA KATEGORİLER (6 yeni)
-- ────────────────────────────────────────────────────────────
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, created_at, updated_at)
SELECT gen_random_uuid(), 'kurumsal-baski', 'Kurumsal Baskılar', '🏢', 30, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = 'kurumsal-baski');

INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, created_at, updated_at)
SELECT gen_random_uuid(), 'reklam-urunleri', 'Reklam Ürünleri', '📢', 40, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = 'reklam-urunleri');

INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, created_at, updated_at)
SELECT gen_random_uuid(), 'ambalaj-paketleme', 'Ambalaj & Paketleme', '📦', 60, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = 'ambalaj-paketleme');

INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, created_at, updated_at)
SELECT gen_random_uuid(), 'fuar-organizasyon', 'Fuar & Organizasyon', '🎪', 70, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = 'fuar-organizasyon');

INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, created_at, updated_at)
SELECT gen_random_uuid(), 'cafe-restoran', 'Cafe & Restoran', '☕', 80, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = 'cafe-restoran');

INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, created_at, updated_at)
SELECT gen_random_uuid(), 'foto-ozel-baski', 'Fotoğraf & Özel Baskı', '📷', 90, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = 'foto-ozel-baski');


-- ────────────────────────────────────────────────────────────
-- 3) ALT KATEGORİLER
-- ────────────────────────────────────────────────────────────

-- Kartvizit alt kategorileri
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '💳', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'kartvizit'), NOW(), NOW()
FROM (VALUES
  ('kartvizit-premium',    'Premium Kartvizit',    1),
  ('kartvizit-pvc',        'Şeffaf PVC Kartvizit', 2),
  ('kartvizit-kabartma',   'Kabartmalı Kartvizit', 3),
  ('kartvizit-yaldiz',     'Altın Yaldızlı Kartvizit', 4),
  ('kartvizit-soft-touch', 'Soft Touch Kartvizit', 5),
  ('kartvizit-ekspres',    'Ekspres Kartvizit',    6)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Broşür alt kategorileri
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '📄', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'brosur'), NOW(), NOW()
FROM (VALUES
  ('brosur-el-ilani',          'El İlanı',           1),
  ('brosur-ekonomik-el-ilani', 'Ekonomik El İlanı',  2),
  ('brosur-katlamali',         'Katlamalı Broşür',   3),
  ('brosur-amerikan-servis',   'Amerikan Servis',    4),
  ('brosur-masa-sumeni',       'Masa Sümeni',        5)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Kurumsal Baskılar alt kategorileri
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '📋', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'kurumsal-baski'), NOW(), NOW()
FROM (VALUES
  ('kurumsal-antetli',     'Antetli Kağıt',      1),
  ('kurumsal-sertifika',   'Sertifika',          2),
  ('kurumsal-recete',      'Reçete Baskı',       3),
  ('kurumsal-anket-formu', 'Anket Formu',        4),
  ('kurumsal-kartpostal',  'Kartpostal',         5)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Reklam Ürünleri alt kategorileri
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '🎯', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'reklam-urunleri'), NOW(), NOW()
FROM (VALUES
  ('reklam-sticker',        'Sticker / Etiket',     1),
  ('reklam-folyo-baski',    'Folyo Baskı',          2),
  ('reklam-vinil-baski',    'Vinil Baskı',          3),
  ('reklam-ozel-kesim',     'Özel Kesim Etiket',    4),
  ('reklam-urun-etiketi',   'Ürün Etiketi',         5)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Promosyon alt kategorileri
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '🎁', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'promosyon'), NOW(), NOW()
FROM (VALUES
  ('promosyon-islak-mendil',  'Islak Mendil',     1),
  ('promosyon-baskili-bardak','Baskılı Bardak',   2),
  ('promosyon-bloknot',       'Bloknot',          3),
  ('promosyon-magnet',        'Magnet',           4),
  ('promosyon-mousepad',      'Mousepad',         5),
  ('promosyon-takvim',        'Takvim',           6)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Ambalaj alt kategorileri
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '📦', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'ambalaj-paketleme'), NOW(), NOW()
FROM (VALUES
  ('ambalaj-kargo-poseti',     'Kargo Poşeti',         1),
  ('ambalaj-kraft-canta',      'Kraft Çanta',          2),
  ('ambalaj-karton-kutu',      'Karton Kutu',          3),
  ('ambalaj-paketleme-etiketi','Paketleme Etiketi',    4),
  ('ambalaj-gonderi-kutusu',   'Gönderi Kutusu',       5)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Fuar & Organizasyon alt kategorileri
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '🎪', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'fuar-organizasyon'), NOW(), NOW()
FROM (VALUES
  ('fuar-yelken-bayrak', 'Yelken Bayrak',    1),
  ('fuar-x-banner',      'X Banner',         2),
  ('fuar-orumcek-stand', 'Örümcek Stand',    3),
  ('fuar-branda',        'Branda',           4),
  ('fuar-masa-ortusu',   'Masa Örtüsü',      5)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Cafe & Restoran alt kategorileri
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '☕', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'cafe-restoran'), NOW(), NOW()
FROM (VALUES
  ('cafe-menu',          'Menü Baskı',       1),
  ('cafe-adisyon',       'Adisyon',          2),
  ('cafe-masa-karti',    'Masa Kartı',       3),
  ('cafe-amerikan-servis','Amerikan Servis (Cafe)', 4)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Fotoğraf & Özel Baskı alt kategorileri
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '📷', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'foto-ozel-baski'), NOW(), NOW()
FROM (VALUES
  ('foto-kanvas-tablo', 'Kanvas Tablo',     1),
  ('foto-fotograf',     'Fotoğraf Baskı',   2),
  ('foto-kupa',         'Kupa Baskı',       3),
  ('foto-puzzle',       'Puzzle Baskı',     4),
  ('foto-kart',         'Foto Kart',        5)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);


-- ────────────────────────────────────────────────────────────
-- 4) YENİ ANA KATEGORİLERE BASE ATTRIBUTE'LAR
--    (Ölçü, Baskı Yönü, Kağıt — her birine eklensin)
-- ────────────────────────────────────────────────────────────

-- "Kurumsal Baskılar" için Ölçü + Baskı Yönü + Kağıt
DO $$
DECLARE cat_id UUID;
BEGIN
  SELECT id INTO cat_id FROM catalog_categories WHERE slug = 'kurumsal-baski';
  IF NOT EXISTS (SELECT 1 FROM catalog_attributes WHERE attr_key = 'olcu' AND category_id = cat_id) THEN
    INSERT INTO catalog_attributes (id, attr_key, input_type, label, required, sort_order, category_id)
    VALUES (gen_random_uuid(), 'olcu', 'select', 'Ölçü', true, 1, cat_id);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM catalog_attributes WHERE attr_key = 'baski_yonu' AND category_id = cat_id) THEN
    INSERT INTO catalog_attributes (id, attr_key, input_type, label, required, sort_order, category_id)
    VALUES (gen_random_uuid(), 'baski_yonu', 'select', 'Baskı Yönü', true, 2, cat_id);
  END IF;
  IF NOT EXISTS (SELECT 1 FROM catalog_attributes WHERE attr_key = 'kagit' AND category_id = cat_id) THEN
    INSERT INTO catalog_attributes (id, attr_key, input_type, label, required, sort_order, category_id)
    VALUES (gen_random_uuid(), 'kagit', 'select', 'Kağıt', true, 3, cat_id);
  END IF;
END $$;

-- Aynısını diğer 5 yeni kategori için
DO $$
DECLARE cat_slug TEXT;
DECLARE cat_id UUID;
BEGIN
  FOR cat_slug IN SELECT unnest(ARRAY[
    'reklam-urunleri', 'ambalaj-paketleme', 'fuar-organizasyon',
    'cafe-restoran', 'foto-ozel-baski'
  ]) LOOP
    SELECT id INTO cat_id FROM catalog_categories WHERE slug = cat_slug;
    IF cat_id IS NULL THEN CONTINUE; END IF;

    IF NOT EXISTS (SELECT 1 FROM catalog_attributes WHERE attr_key = 'olcu' AND category_id = cat_id) THEN
      INSERT INTO catalog_attributes (id, attr_key, input_type, label, required, sort_order, category_id)
      VALUES (gen_random_uuid(), 'olcu', 'select', 'Ölçü', true, 1, cat_id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM catalog_attributes WHERE attr_key = 'baski_yonu' AND category_id = cat_id) THEN
      INSERT INTO catalog_attributes (id, attr_key, input_type, label, required, sort_order, category_id)
      VALUES (gen_random_uuid(), 'baski_yonu', 'select', 'Baskı Yönü', true, 2, cat_id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM catalog_attributes WHERE attr_key = 'kagit' AND category_id = cat_id) THEN
      INSERT INTO catalog_attributes (id, attr_key, input_type, label, required, sort_order, category_id)
      VALUES (gen_random_uuid(), 'kagit', 'select', 'Kağıt', true, 3, cat_id);
    END IF;
  END LOOP;
END $$;


-- ────────────────────────────────────────────────────────────
-- 5) BAZE OPSİYONLAR (Tek Yön / Çift Yön — tüm yeni baski_yonu için)
-- ────────────────────────────────────────────────────────────
INSERT INTO catalog_attribute_options (id, value, sort_order, attribute_id, price_modifier)
SELECT gen_random_uuid(), 'Tek Yön Baskı', 1, a.id, 1.000
FROM catalog_attributes a
JOIN catalog_categories c ON c.id = a.category_id
WHERE a.attr_key = 'baski_yonu'
  AND c.slug IN ('kurumsal-baski', 'reklam-urunleri', 'ambalaj-paketleme',
                 'fuar-organizasyon', 'cafe-restoran', 'foto-ozel-baski')
  AND NOT EXISTS (
    SELECT 1 FROM catalog_attribute_options o WHERE o.attribute_id = a.id AND o.value = 'Tek Yön Baskı'
  );

INSERT INTO catalog_attribute_options (id, value, sort_order, attribute_id, price_modifier)
SELECT gen_random_uuid(), 'Çift Yön Baskı', 2, a.id, 1.400
FROM catalog_attributes a
JOIN catalog_categories c ON c.id = a.category_id
WHERE a.attr_key = 'baski_yonu'
  AND c.slug IN ('kurumsal-baski', 'reklam-urunleri', 'ambalaj-paketleme',
                 'fuar-organizasyon', 'cafe-restoran', 'foto-ozel-baski')
  AND NOT EXISTS (
    SELECT 1 FROM catalog_attribute_options o WHERE o.attribute_id = a.id AND o.value = 'Çift Yön Baskı'
  );


COMMIT;


-- ════════════════════════════════════════════════════════════
-- DOĞRULAMA
-- ════════════════════════════════════════════════════════════

-- Ana kategoriler + sub-categoory sayısı
SELECT
  COALESCE(p.name, c.name) AS ana_kategori,
  COUNT(c2.id) AS alt_kategori_sayisi
FROM catalog_categories c
LEFT JOIN catalog_categories p ON p.id = c.parent_id
LEFT JOIN catalog_categories c2 ON c2.parent_id = c.id
WHERE c.parent_id IS NULL
GROUP BY c.id, c.name, p.name
ORDER BY c.sort_order, c.name;

-- Yeni kategorilerin attribute durumu
SELECT c.name AS kategori, a.label AS attribute, COUNT(o.id) AS opsiyon
FROM catalog_categories c
JOIN catalog_attributes a ON a.category_id = c.id
LEFT JOIN catalog_attribute_options o ON o.attribute_id = a.id
WHERE c.slug IN ('kurumsal-baski', 'reklam-urunleri', 'ambalaj-paketleme',
                 'fuar-organizasyon', 'cafe-restoran', 'foto-ozel-baski')
GROUP BY c.name, a.label, a.sort_order
ORDER BY c.name, a.sort_order;
