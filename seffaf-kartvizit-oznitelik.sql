-- Şeffaf Kartvizit öznitelikleri
-- Lak = opsiyonel (required=false), diğerleri = zorunlu (required=true)
-- Tekrar çalıştırılabilir: önce bu kategorinin mevcut özniteliklerini temizler.

DO $$
DECLARE
  cat_id UUID;
  a_id   UUID;
BEGIN
  SELECT id INTO cat_id
  FROM catalog_categories
  WHERE lower(name) LIKE '%şeffaf%' AND lower(name) LIKE '%kartvizit%'
  LIMIT 1;

  IF cat_id IS NULL THEN
    RAISE NOTICE 'Şeffaf Kartvizit kategorisi bulunamadı — isim kontrol et.';
    RETURN;
  END IF;

  -- Temizlik (idempotent)
  DELETE FROM catalog_product_attribute_values
    WHERE attribute_id IN (SELECT id FROM catalog_attributes WHERE category_id = cat_id);
  DELETE FROM catalog_attribute_options
    WHERE attribute_id IN (SELECT id FROM catalog_attributes WHERE category_id = cat_id);
  DELETE FROM catalog_attributes WHERE category_id = cat_id;

  -- 1) Tasarım Yönü (zorunlu)
  INSERT INTO catalog_attributes (id, category_id, attr_key, label, input_type, required, sort_order)
    VALUES (gen_random_uuid(), cat_id, 'tasarim_yonu', 'Tasarım Yönü', 'select', true, 1)
    RETURNING id INTO a_id;
  INSERT INTO catalog_attribute_options (id, attribute_id, value, sort_order, price_modifier) VALUES
    (gen_random_uuid(), a_id, 'Dikey', 1, 1.0),
    (gen_random_uuid(), a_id, 'Yatay', 2, 1.0);

  -- 2) Ebat (zorunlu)
  INSERT INTO catalog_attributes (id, category_id, attr_key, label, input_type, required, sort_order)
    VALUES (gen_random_uuid(), cat_id, 'ebat', 'Ebat', 'select', true, 2)
    RETURNING id INTO a_id;
  INSERT INTO catalog_attribute_options (id, attribute_id, value, sort_order, price_modifier) VALUES
    (gen_random_uuid(), a_id, '8.5x5 cm', 1, 1.0);

  -- 3) Malzeme (zorunlu)
  INSERT INTO catalog_attributes (id, category_id, attr_key, label, input_type, required, sort_order)
    VALUES (gen_random_uuid(), cat_id, 'malzeme', 'Malzeme', 'select', true, 3)
    RETURNING id INTO a_id;
  INSERT INTO catalog_attribute_options (id, attribute_id, value, sort_order, price_modifier) VALUES
    (gen_random_uuid(), a_id, '500 Mikron Buzlu PVC', 1, 1.0);

  -- 4) Baskı Yönü (zorunlu)
  INSERT INTO catalog_attributes (id, category_id, attr_key, label, input_type, required, sort_order)
    VALUES (gen_random_uuid(), cat_id, 'baski_yonu', 'Baskı Yönü', 'select', true, 4)
    RETURNING id INTO a_id;
  INSERT INTO catalog_attribute_options (id, attribute_id, value, sort_order, price_modifier) VALUES
    (gen_random_uuid(), a_id, 'Tek Yön Baskı', 1, 1.0);

  -- 5) Baskı Rengi (zorunlu)
  INSERT INTO catalog_attributes (id, category_id, attr_key, label, input_type, required, sort_order)
    VALUES (gen_random_uuid(), cat_id, 'baski_rengi', 'Baskı Rengi', 'select', true, 5)
    RETURNING id INTO a_id;
  INSERT INTO catalog_attribute_options (id, attribute_id, value, sort_order, price_modifier) VALUES
    (gen_random_uuid(), a_id, '4+0 CMYK', 1, 1.0);

  -- 6) Lak (OPSİYONEL)
  INSERT INTO catalog_attributes (id, category_id, attr_key, label, input_type, required, sort_order)
    VALUES (gen_random_uuid(), cat_id, 'lak', 'Lak', 'select', false, 6)
    RETURNING id INTO a_id;
  INSERT INTO catalog_attribute_options (id, attribute_id, value, sort_order, price_modifier) VALUES
    (gen_random_uuid(), a_id, 'Lak Yok', 1, 1.0),
    (gen_random_uuid(), a_id, 'Tek Yön Kabartma Lak', 2, 1.0);

  -- 7) Kesim (zorunlu)
  INSERT INTO catalog_attributes (id, category_id, attr_key, label, input_type, required, sort_order)
    VALUES (gen_random_uuid(), cat_id, 'kesim', 'Kesim', 'select', true, 7)
    RETURNING id INTO a_id;
  INSERT INTO catalog_attribute_options (id, attribute_id, value, sort_order, price_modifier) VALUES
    (gen_random_uuid(), a_id, 'Oval', 1, 1.0);

  RAISE NOTICE 'Şeffaf Kartvizit öznitelikleri kuruldu (kategori: %)', cat_id;
END $$;
