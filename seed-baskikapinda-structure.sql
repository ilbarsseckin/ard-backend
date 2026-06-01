-- ════════════════════════════════════════════════════════════
--  KATEGORİLERİ BASKIKAPINDA.COM YAPISIYLA EŞLEŞTİR
--  10 ana kategori + alt kategoriler
-- ════════════════════════════════════════════════════════════

BEGIN;

-- ─── 1) GEREKSIZ KATEGORİLERİ SİL ───────────────────────
-- Önce alt kategorileri sil (FK güvenliği)
DELETE FROM catalog_categories WHERE slug IN (
  'reklam-sticker','reklam-folyo-baski','reklam-vinil-baski','reklam-ozel-kesim','reklam-urun-etiketi',
  'fuar-yelken-bayrak','fuar-x-banner','fuar-orumcek-stand','fuar-branda','fuar-masa-ortusu',
  'cafe-menu','cafe-adisyon','cafe-masa-karti','cafe-amerikan-servis',
  'ambalaj-kargo-poseti','ambalaj-kraft-canta','ambalaj-karton-kutu',
  'ambalaj-paketleme-etiketi','ambalaj-gonderi-kutusu'
);

-- Sonra ana kategorileri sil
DELETE FROM catalog_categories WHERE slug IN (
  'reklam-urunleri','ambalaj-paketleme','fuar-organizasyon','cafe-restoran'
);


-- ─── 2) MEVCUT KATEGORİLERİ YENİDEN ADLANDIR ───────────
-- (FK'lar UUID üzerinden, slug değişimi ürünleri etkilemez — sadece URL değişir)
UPDATE catalog_categories SET name = 'El İlanı - Broşür',     slug = 'el-ilani-brosur'     WHERE slug = 'brosur';
UPDATE catalog_categories SET name = 'Bayrak Ürünleri',       slug = 'bayrak-urunleri',  parent_id = NULL  WHERE slug = 'bayrak';
UPDATE catalog_categories SET name = 'Kurumsal Ürünler',      slug = 'kurumsal-urunler'    WHERE slug = 'kurumsal-baski';
UPDATE catalog_categories SET name = 'Promosyon Ürünleri',    slug = 'promosyon-urunleri'  WHERE slug = 'promosyon';
UPDATE catalog_categories SET name = 'Tablolar',              slug = 'tablolar',  icon = '🖼️'  WHERE slug = 'foto-ozel-baski';


-- ─── 3) YENİ ANA KATEGORİLER (4 adet) ───────────────────
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, created_at, updated_at)
SELECT gen_random_uuid(), 'acil-baski', 'Acil Baskı', '⚡', 5, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = 'acil-baski');

INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, created_at, updated_at)
SELECT gen_random_uuid(), 'matbaa-urunleri', 'Matbaa Ürünleri', '📋', 55, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = 'matbaa-urunleri');

INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, created_at, updated_at)
SELECT gen_random_uuid(), 'dijital-baski-urunleri', 'Dijital Baskı Ürünleri', '🖨️', 75, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = 'dijital-baski-urunleri');

INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, created_at, updated_at)
SELECT gen_random_uuid(), 'emlak-urunleri', 'Emlak Ürünleri', '🏠', 95, true, NOW(), NOW()
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = 'emlak-urunleri');


-- ─── 4) SORT_ORDER TEMİZLİĞİ ────────────────────────────
UPDATE catalog_categories SET sort_order = 5  WHERE slug = 'acil-baski';
UPDATE catalog_categories SET sort_order = 10 WHERE slug = 'kartvizit';
UPDATE catalog_categories SET sort_order = 20 WHERE slug = 'el-ilani-brosur';
UPDATE catalog_categories SET sort_order = 30 WHERE slug = 'bayrak-urunleri';
UPDATE catalog_categories SET sort_order = 40 WHERE slug = 'kurumsal-urunler';
UPDATE catalog_categories SET sort_order = 50 WHERE slug = 'matbaa-urunleri';
UPDATE catalog_categories SET sort_order = 60 WHERE slug = 'promosyon-urunleri';
UPDATE catalog_categories SET sort_order = 70 WHERE slug = 'dijital-baski-urunleri';
UPDATE catalog_categories SET sort_order = 80 WHERE slug = 'tablolar';
UPDATE catalog_categories SET sort_order = 90 WHERE slug = 'emlak-urunleri';


-- ─── 5) MISFIT'LERİ YERİNE KOY ──────────────────────────
UPDATE catalog_categories SET parent_id = (SELECT id FROM catalog_categories WHERE slug = 'dijital-baski-urunleri')
WHERE slug = 'afis';

UPDATE catalog_categories SET parent_id = (SELECT id FROM catalog_categories WHERE slug = 'el-ilani-brosur')
WHERE slug = 'katalog';

UPDATE catalog_categories SET parent_id = (SELECT id FROM catalog_categories WHERE slug = 'kurumsal-urunler')
WHERE slug = 'davetiye';

UPDATE catalog_categories SET parent_id = (SELECT id FROM catalog_categories WHERE slug = 'kurumsal-urunler')
WHERE slug = 'roll-up';


-- ─── 6) ALT KATEGORİLER — BASKIKAPINDA'YA UYGUN ────────

-- Acil Baskı
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '⚡', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'acil-baski'), NOW(), NOW()
FROM (VALUES
  ('hizli-sunum-dosyasi',   'Hızlı Sunum Dosyası',     1),
  ('hizli-diplomat-zarf',   'Hızlı Diplomat Zarf',     2),
  ('hizli-el-ilani-acil',   'Hızlı El İlanı - Broşür', 3),
  ('hizli-baski-kartvizit', 'Hızlı Baskı Kartvizit',   4)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Kartvizit (eskiler kalır + yeniler eklenir)
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '💳', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'kartvizit'), NOW(), NOW()
FROM (VALUES
  ('tesekkur-not-karti',         'Teşekkür - Not Kartı',       10),
  ('yaldizli-bristol-kartvizit', 'Yaldızlı Bristol Kartvizit', 11),
  ('tuale-fantazi-kartvizit-bk', 'Tuale Fantazi Kartvizit',    12),
  ('yaldizli-kartvizit',         'Yaldızlı Kartvizit',         13),
  ('ekonomik-tuale-kartvizit',   'Ekonomik Tuale Kartvizit',   14)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- El İlanı - Broşür
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '📄', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'el-ilani-brosur'), NOW(), NOW()
FROM (VALUES
  ('hizli-el-ilani-brosur',  'Hızlı El İlanı - Broşür', 1),
  ('kapi-aski-brosuru',      'Kapı Askı Broşürü',        2),
  ('kirimli-el-ilani-brosur','Kırımlı El İlanı',         3),
  ('katlamali-brosur',       'Katlamalı Broşür',         4)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Bayrak Ürünleri
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '🚩', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'bayrak-urunleri'), NOW(), NOW()
FROM (VALUES
  ('gumus-makam-bayragi',  'Gümüş Makam Bayrağı', 1),
  ('masa-bayragi',         'Masa Bayrağı',         2),
  ('gonder-bayragi',       'Gönder Bayrağı',       3),
  ('yelken-bayrak',        'Yelken Bayrak',        4),
  ('kirlangic-bayrak',     'Kırlangıç Bayrak',     5)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Kurumsal Ürünler
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '🏢', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'kurumsal-urunler'), NOW(), NOW()
FROM (VALUES
  ('dubalar',               'Dubalar',               1),
  ('standlar',              'Standlar',              2),
  ('display-urunleri',      'Display Ürünleri',      3),
  ('is-guvenlik-levhalari', 'İş Güvenlik Levhaları', 4),
  ('kapi-isimlikleri',      'Kapı İsimlikleri',      5),
  ('kaseler',               'Kaşeler',               6),
  ('tabelalar',             'Tabelalar',             7)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Matbaa Ürünleri
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '📋', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'matbaa-urunleri'), NOW(), NOW()
FROM (VALUES
  ('etiketler',   'Etiketler',     1),
  ('zarflar',     'Zarflar',       2),
  ('bloknotlar',  'Bloknotlar',    3),
  ('cantalar',    'Çantalar',      4),
  ('form-makbuz', 'Form - Makbuz', 5),
  ('magnet-mat',  'Magnet',        6)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Promosyon Ürünleri (eskiler kalır + yeni anahtarlar eklenir)
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '🎁', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'promosyon-urunleri'), NOW(), NOW()
FROM (VALUES
  ('ajandalar',           'Ajandalar',           10),
  ('kalemler',            'Kalemler',            11),
  ('cakmaklar',           'Çakmaklar',           12),
  ('termoslar',           'Termoslar',           13),
  ('anahtarliklar',       'Anahtarlıklar',       14),
  ('vip-setler',          'VIP Setler',          15),
  ('plaketler',           'Plaketler',           16),
  ('tisortler',           'Tişörtler',           17),
  ('promosyon-paketleri', 'Promosyon Paketleri', 18),
  ('masa-isimlikleri',    'Masa İsimlikleri',    19),
  ('powerbank',           'Powerbank',           20)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Dijital Baskı Ürünleri
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '🖨️', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'dijital-baski-urunleri'), NOW(), NOW()
FROM (VALUES
  ('viniller',             'Viniller',              1),
  ('folyolar',             'Folyolar',              2),
  ('vinil-branda-afisler', 'Vinil Branda Afişler',  3),
  ('mat-folyo',            'Mat Folyo',             4),
  ('mesh-delikli-vinil',   'Mesh Delikli Vinil',    5),
  ('isikli-vinil',         'Işıklı Vinil',          6),
  ('seffaf-folyo',         'Şeffaf Folyo',          7),
  ('uv-baski-magnet',      'UV Baskı Magnet',       8)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Tablolar
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '🖼️', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'tablolar'), NOW(), NOW()
FROM (VALUES
  ('canvas-tablo',    'Canvas Tablo',       1),
  ('mdf-tablo',       'MDF Tablo',          2),
  ('ataturk-tablo',   'Atatürk Tabloları',  3),
  ('dekoratif-tablo', 'Dekoratif Tablolar', 4)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

-- Emlak Ürünleri
INSERT INTO catalog_categories (id, slug, name, icon, sort_order, active, parent_id, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name, '🏠', v.so, true,
  (SELECT id FROM catalog_categories WHERE slug = 'emlak-urunleri'), NOW(), NOW()
FROM (VALUES
  ('mesh-emlak-afisi',         'Mesh Delikli Vinil Emlak Afişi',    1),
  ('vinil-branda-emlak-afisi', 'Vinil Branda Emlak Afişi',           2),
  ('emlak-kagit-afisi',        'Emlak Kağıt Afişi',                  3),
  ('emlak-tabelasi',           'Emlak Tabelası',                     4)
) AS v(slug, name, so)
WHERE NOT EXISTS (SELECT 1 FROM catalog_categories WHERE slug = v.slug);

COMMIT;


-- ════════════════════════════════════════════════════════════
-- DOĞRULAMA
-- ════════════════════════════════════════════════════════════
SELECT
  CASE WHEN c.parent_id IS NULL THEN '⬛ ' || c.name ELSE '   ↳ ' || c.name END AS hiyerarsi,
  c.slug,
  c.sort_order
FROM catalog_categories c
LEFT JOIN catalog_categories p ON p.id = c.parent_id
WHERE c.active = true
ORDER BY COALESCE(p.sort_order, c.sort_order), c.parent_id NULLS FIRST, c.sort_order
LIMIT 80;
