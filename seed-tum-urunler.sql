-- ════════════════════════════════════════════════════════════════════
--  TÜM KATEGORİLERE ÜRÜN DOLDURMA  ·  seed-tum-urunler.sql
-- --------------------------------------------------------------------
--  • Her alt kategoriye 1 başlangıç ürünü kurar (kartvizit HARİÇ —
--    onu seed-kartvizit-temiz.sql ile zaten kurduk).
--  • Fiyatlar PLACEHOLDER — taban fiyattan 3 kademe otomatik hesaplanır.
--    Ürün tipine göre profil:
--      'yuksek' → 100/250/500 adet   (broşür, antetli, etiket...)
--      'orta'   → 50/100/250 adet    (promosyon ürünleri)
--      'dusuk'  → 1/3/5 adet         (bayrak, tablo, afiş, tabela...)
--  • Resimler boş → admin panelden.
--  • Slug bazlı + idempotent. Kategori yoksa o ürün atlanır (hata vermez).
--  • Backend recursive fix ile birlikte: ana kategoriye tıklayınca
--    alt kategorilerdeki bu ürünler de görünür.
--
--  ⭐ DÜZENLEME: ürün eklemek/fiyat değiştirmek için aşağıdaki _seed
--     bloğundaki satırları düzenle (slug | ad | alt-kategori | profil | taban-fiyat).
--
--  Çalıştırma:
--    docker exec -i ard-backend-db-1 psql -U baski_user -d baski_db < seed-tum-urunler.sql
-- ════════════════════════════════════════════════════════════════════

BEGIN;

CREATE TEMP TABLE _seed(slug text, name text, cat text, profil text, fiyat numeric) ON COMMIT DROP;
INSERT INTO _seed VALUES
  -- ── Broşür & El İlanı ──
  ('el-ilani',              'El İlanı',              'brosur-el-ilani',          'yuksek',  8),
  ('ekonomik-el-ilani',     'Ekonomik El İlanı',     'brosur-ekonomik-el-ilani', 'yuksek',  6),
  ('katlamali-brosur-urun', 'Katlamalı Broşür',      'brosur-katlamali',         'yuksek', 12),
  ('amerikan-servis',       'Amerikan Servis',       'brosur-amerikan-servis',   'yuksek', 10),
  ('masa-sumeni',           'Masa Sümeni',           'brosur-masa-sumeni',       'yuksek', 11),
  ('kapi-aski-brosuru-urun','Kapı Askı Broşürü',     'kapi-aski-brosuru',        'yuksek',  9),
  ('kirimli-el-ilani',      'Kırımlı El İlanı',      'kirimli-el-ilani-brosur',  'yuksek',  7),
  ('katalog-urun',          'Katalog Baskı',         'katalog',                  'yuksek', 25),
  -- ── Bayrak Ürünleri ──
  ('makam-bayragi',         'Gümüş Makam Bayrağı',   'gumus-makam-bayragi',      'dusuk',  45),
  ('masa-bayragi-urun',     'Masa Bayrağı',          'masa-bayragi',             'dusuk',  12),
  ('gonder-bayragi-urun',   'Gönder Bayrağı',        'gonder-bayragi',           'dusuk',  30),
  ('yelken-bayrak-urun',    'Yelken Bayrak',         'yelken-bayrak',            'dusuk',  14),
  ('kirlangic-bayrak-urun', 'Kırlangıç Bayrak',      'kirlangic-bayrak',         'dusuk',  18),
  -- ── Kurumsal Baskılar ──
  ('antetli-kagit',         'Antetli Kağıt',         'kurumsal-antetli',         'yuksek', 15),
  ('sertifika',             'Sertifika',             'kurumsal-sertifika',       'yuksek', 18),
  ('recete-baski',          'Reçete Baskı',          'kurumsal-recete',          'yuksek', 14),
  ('anket-formu',           'Anket Formu',           'kurumsal-anket-formu',     'yuksek', 12),
  ('kartpostal',            'Kartpostal',            'kurumsal-kartpostal',      'yuksek', 10),
  ('duba',                  'Duba',                  'dubalar',                  'dusuk',  20),
  ('stand',                 'Stand',                 'standlar',                 'dusuk',  40),
  ('display',               'Display',               'display-urunleri',         'dusuk',  35),
  ('is-guvenlik-levhasi',   'İş Güvenlik Levhası',   'is-guvenlik-levhalari',    'dusuk',   8),
  ('kapi-isimligi',         'Kapı İsimliği',         'kapi-isimlikleri',         'dusuk',   9),
  ('kase',                  'Kaşe',                  'kaseler',                  'dusuk',   6),
  ('tabela',                'Tabela',                'tabelalar',                'dusuk',  25),
  ('davetiye-urun',         'Davetiye',              'davetiye',                 'yuksek', 13),
  ('roll-up-urun',          'Roll-Up Banner',        'roll-up',                  'dusuk',  22),
  -- ── Matbaa Ürünleri ──
  ('etiket',                'Etiket',                'etiketler',                'yuksek',  8),
  ('zarf',                  'Zarf',                  'zarflar',                  'yuksek', 10),
  ('bloknot',               'Bloknot',               'bloknotlar',               'yuksek', 12),
  ('canta',                 'Çanta',                 'cantalar',                 'orta',    4),
  ('form-makbuz-urun',      'Form - Makbuz',         'form-makbuz',              'yuksek', 14),
  -- ── Promosyon Ürünleri ──
  ('islak-mendil',          'Islak Mendil',          'promosyon-islak-mendil',   'orta',    2),
  ('baskili-bardak',        'Baskılı Bardak',        'promosyon-baskili-bardak', 'orta',    3),
  ('promo-bloknot',         'Bloknot',               'promosyon-bloknot',        'orta',    3),
  ('promo-magnet',          'Magnet',                'promosyon-magnet',         'orta',    2),
  ('mousepad',              'Mousepad',              'promosyon-mousepad',       'orta',    4),
  ('takvim',                'Takvim',                'promosyon-takvim',         'orta',    5),
  ('ajanda',                'Ajanda',                'ajandalar',                'orta',    6),
  ('kalem',                 'Kalem',                 'kalemler',                 'orta',    2),
  ('cakmak',                'Çakmak',                'cakmaklar',                'orta',    2),
  ('termos',                'Termos',                'termoslar',                'orta',    8),
  ('anahtarlik',            'Anahtarlık',            'anahtarliklar',            'orta',    2),
  ('vip-set',               'VIP Set',               'vip-setler',               'orta',   15),
  ('plaket',                'Plaket',                'plaketler',                'orta',   12),
  ('tisort',                'Tişört',                'tisortler',                'orta',    6),
  ('promosyon-paketi',      'Promosyon Paketi',      'promosyon-paketleri',      'orta',   10),
  ('masa-isimligi',         'Masa İsimliği',         'masa-isimlikleri',         'orta',    5),
  ('powerbank-urun',        'Powerbank',             'powerbank',                'orta',   12),
  -- ── Dijital Baskı Ürünleri ──
  ('vinil',                 'Vinil',                 'viniller',                 'dusuk',  10),
  ('folyo',                 'Folyo',                 'folyolar',                 'dusuk',   9),
  ('vinil-branda-afis',     'Vinil Branda Afiş',     'vinil-branda-afisler',     'dusuk',  12),
  ('mat-folyo-urun',        'Mat Folyo',             'mat-folyo',                'dusuk',  11),
  ('mesh-vinil',            'Mesh Delikli Vinil',    'mesh-delikli-vinil',       'dusuk',  13),
  ('isikli-vinil-urun',     'Işıklı Vinil',          'isikli-vinil',             'dusuk',  16),
  ('seffaf-folyo-urun',     'Şeffaf Folyo',          'seffaf-folyo',             'dusuk',  11),
  ('afis-urun',             'Afiş',                  'afis',                     'dusuk',   8),
  -- ── Tablo & Fotoğraf ──
  ('kanvas-tablo',          'Kanvas Tablo',          'foto-kanvas-tablo',        'dusuk',  25),
  ('fotograf-baski-urun',   'Fotoğraf Baskı',        'foto-fotograf',            'orta',    8),
  ('kupa-baski',            'Kupa Baskı',            'foto-kupa',                'orta',    5),
  ('puzzle-baski',          'Puzzle Baskı',          'foto-puzzle',              'orta',    7),
  ('foto-kart-urun',        'Foto Kart',             'foto-kart',                'orta',    3),
  ('mdf-tablo-urun',        'MDF Tablo',             'mdf-tablo',                'dusuk',  22),
  ('ataturk-tablo-urun',    'Atatürk Tablosu',       'ataturk-tablo',            'dusuk',  28),
  ('dekoratif-tablo-urun',  'Dekoratif Tablo',       'dekoratif-tablo',          'dusuk',  24),
  -- ── Emlak Ürünleri ──
  ('mesh-emlak-afisi-urun', 'Mesh Emlak Afişi',      'mesh-emlak-afisi',         'dusuk',  14),
  ('vinil-emlak-afisi',     'Vinil Branda Emlak Afişi','vinil-branda-emlak-afisi','dusuk', 13),
  ('kagit-emlak-afisi',     'Kağıt Emlak Afişi',     'emlak-kagit-afisi',        'dusuk',   6),
  ('emlak-tabelasi-urun',   'Emlak Tabelası',        'emlak-tabelasi',           'dusuk',  20);

-- ── 1) ÜRÜNLER ── (kategorisi var olanlar; yoksa atlanır)
INSERT INTO catalog_products (id, slug, name, category_id, short_desc, featured, active, sort_order, created_at, updated_at)
SELECT gen_random_uuid(), s.slug, s.name,
       (SELECT id FROM catalog_categories WHERE slug = s.cat),
       s.name || ' — profesyonel baskı.', false, true, 100, NOW(), NOW()
FROM _seed s
WHERE EXISTS (SELECT 1 FROM catalog_categories WHERE slug = s.cat)
ON CONFLICT (slug) DO NOTHING;

-- ── 2) FİYAT KADEMELERİ ── (taban × profil katsayısı, otomatik)
INSERT INTO catalog_product_tiers (id, product_id, qty, price_usd, sort_order)
SELECT gen_random_uuid(), p.id, pr.qty, ROUND((s.fiyat * pr.carpan)::numeric, 2), pr.so
FROM _seed s
JOIN catalog_products p ON p.slug = s.slug
JOIN (VALUES
  ('yuksek',100,1.0,1),('yuksek',250,2.2,2),('yuksek',500,4.0,3),
  ('orta',   50,1.0,1),('orta',  100,1.8,2),('orta',  250,4.0,3),
  ('dusuk',   1,1.0,1),('dusuk',   3,2.5,2),('dusuk',   5,4.0,3)
) AS pr(profil, qty, carpan, so) ON pr.profil = s.profil
WHERE NOT EXISTS (SELECT 1 FROM catalog_product_tiers x WHERE x.product_id = p.id AND x.qty = pr.qty);

-- ── 3) ÖZNİTELİK ── ürün, ait olduğu ana kategorinin özniteliklerini alır
--    (sadece öznitelik tanımlı kategoriler için — kurumsal, tablo vb.)
INSERT INTO catalog_product_attribute_values (id, product_id, attribute_id, option_id)
SELECT gen_random_uuid(), p.id, o.attribute_id, o.id
FROM catalog_products p
JOIN catalog_categories c ON c.id = p.category_id
JOIN catalog_attributes a ON a.category_id = COALESCE(c.parent_id, c.id)
JOIN catalog_attribute_options o ON o.attribute_id = a.id
WHERE p.slug IN (SELECT slug FROM _seed)
  AND NOT EXISTS (
    SELECT 1 FROM catalog_product_attribute_values pav
    WHERE pav.product_id = p.id AND pav.option_id = o.id
  );

COMMIT;


-- ════════════════════════════════════════════════════════════════════
--  DOĞRULAMA — ana kategori başına ürün sayısı (alt kategoriler dahil)
-- ════════════════════════════════════════════════════════════════════
SELECT ana.name AS ana_kategori,
       COUNT(DISTINCT p.id) AS urun_sayisi
FROM catalog_categories ana
LEFT JOIN catalog_categories alt ON alt.parent_id = ana.id
LEFT JOIN catalog_products p ON p.category_id = ana.id OR p.category_id = alt.id
WHERE ana.parent_id IS NULL
GROUP BY ana.name, ana.sort_order
ORDER BY ana.sort_order;
