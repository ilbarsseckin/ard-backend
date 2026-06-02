-- Standart + Ekspres kartvizit ürünleri (idempotent)
INSERT INTO catalog_products (id, slug, name, category_id, short_desc, featured, active, sort_order, created_at, updated_at)
SELECT gen_random_uuid(), v.slug, v.name,
       (SELECT id FROM catalog_categories WHERE slug = v.cat),
       v.descr, false, true, v.so, NOW(), NOW()
FROM (VALUES
  ('standart-kartvizit-urun', 'Standart Kartvizit', 'standart-kartvizit', 'Ekonomik, hızlı teslim standart kartvizit.', 5),
  ('ekspres-kartvizit',       'Ekspres Kartvizit',  'kartvizit-ekspres', 'Aynı/sonraki gün teslim kartvizit.',         8)
) AS v(slug, name, cat, descr, so)
ON CONFLICT (slug) DO NOTHING;

-- Fiyat kademeleri (500/1000/2500) — standart 12$, ekspres 14$ taban
WITH taban(slug, fiyat) AS (VALUES ('standart-kartvizit-urun', 12.00), ('ekspres-kartvizit', 14.00)),
     kademe(qty, carpan, so) AS (VALUES (500,1.00,1),(1000,1.50,2),(2500,3.20,3))
INSERT INTO catalog_product_tiers (id, product_id, qty, price_usd, sort_order)
SELECT gen_random_uuid(), p.id, k.qty, ROUND((t.fiyat * k.carpan)::numeric, 2), k.so
FROM taban t JOIN catalog_products p ON p.slug = t.slug CROSS JOIN kademe k
WHERE NOT EXISTS (SELECT 1 FROM catalog_product_tiers x WHERE x.product_id = p.id AND x.qty = k.qty);

-- kartvizit özniteliklerini bağla (renk/ebat/kağıt/baskı yönü/selefon)
INSERT INTO catalog_product_attribute_values (id, product_id, attribute_id, option_id)
SELECT gen_random_uuid(), p.id, o.attribute_id, o.id
FROM catalog_products p
JOIN catalog_attributes a ON a.category_id = (SELECT id FROM catalog_categories WHERE slug='kartvizit')
JOIN catalog_attribute_options o ON o.attribute_id = a.id
WHERE p.slug IN ('standart-kartvizit-urun', 'ekspres-kartvizit')
  AND NOT EXISTS (SELECT 1 FROM catalog_product_attribute_values pav
                  WHERE pav.product_id = p.id AND pav.option_id = o.id);