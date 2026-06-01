-- ════════════════════════════════════════════════════════════
--  KUPON BAŞLANGIÇ VERİLERİ
--  Hibernate tabloları otomatik oluşturacak (ddl-auto: update)
--  Önce backend'i restart et, sonra bu SQL'i çalıştır
-- ════════════════════════════════════════════════════════════

-- 1) HOŞGELDİN KUPONU — İlk ziyarette welcome dialog'da gösterilir
INSERT INTO coupons (
    id, code, name, description, type,
    discount_amount, min_order_amount,
    max_usage, current_usage, per_user_limit,
    start_date, end_date, active,
    auto_issue_on_first_visit, auto_issue_on_order_amount,
    created_at, updated_at
) VALUES (
    gen_random_uuid(), 'HOSGELDIN100', 'Hoş Geldin Kuponu',
    'Yeni üyelere özel ₺100 indirim. Minimum 500 TL alışverişte geçerlidir.',
    'AMOUNT',
    100.00, 500.00,
    NULL, 0, 1,
    NULL, NULL, true,
    true, NULL,
    NOW(), NOW()
)
ON CONFLICT (code) DO NOTHING;


-- 2) HEDIYE KUPONU — 5000+ siparişlerde otomatik verilen 1000 TL kupon
INSERT INTO coupons (
    id, code, name, description, type,
    gift_amount, min_order_amount,
    max_usage, current_usage, per_user_limit,
    start_date, end_date, active,
    auto_issue_on_first_visit, auto_issue_on_order_amount,
    created_at, updated_at
) VALUES (
    gen_random_uuid(), 'HEDIYE1000', '5000 TL ve Üzeri Hediye',
    '5000 TL ve üzeri alışverişlerinizde sonraki siparişinizde kullanabileceğiniz ₺1000 hediye kuponu.',
    'GIFT',
    1000.00, 1000.00,
    NULL, 0, 1,
    NULL, NULL, true,
    false, 5000.00,
    NOW(), NOW()
)
ON CONFLICT (code) DO NOTHING;


-- 3) % İNDİRİM ÖRNEĞİ — Tüm sepete %10 indirim
INSERT INTO coupons (
    id, code, name, description, type,
    discount_percent, min_order_amount,
    max_usage, current_usage, per_user_limit,
    start_date, end_date, active,
    auto_issue_on_first_visit,
    created_at, updated_at
) VALUES (
    gen_random_uuid(), 'INDIRIM10', '%10 İndirim',
    'Tüm ürünlerde %10 indirim. Minimum 300 TL.',
    'PERCENT',
    10.00, 300.00,
    100, 0, 1,
    NULL, NULL, true,
    false,
    NOW(), NOW()
)
ON CONFLICT (code) DO NOTHING;


-- Doğrulama
SELECT code, name, type, discount_percent, discount_amount, gift_amount,
       min_order_amount, auto_issue_on_first_visit, auto_issue_on_order_amount, active
FROM coupons ORDER BY created_at DESC;
