--
-- PostgreSQL database dump
--

\restrict quSn0q0ENbhbWJveh4nJFt0J2QN19Rw2UEcyMDX3eP6GR1FWqTDmZ8jyoGVP3zr

-- Dumped from database version 16.13
-- Dumped by pg_dump version 16.13

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: addresses; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.addresses (
    id uuid NOT NULL,
    address_line text NOT NULL,
    city character varying(255) NOT NULL,
    country character varying(255) NOT NULL,
    district character varying(255) NOT NULL,
    full_name character varying(255) NOT NULL,
    is_default boolean,
    phone character varying(255) NOT NULL,
    title character varying(255) NOT NULL,
    user_id uuid NOT NULL
);


ALTER TABLE public.addresses OWNER TO baski_user;

--
-- Name: app_role_permissions; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.app_role_permissions (
    role_id uuid NOT NULL,
    permission_id uuid NOT NULL
);


ALTER TABLE public.app_role_permissions OWNER TO baski_user;

--
-- Name: app_roles; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.app_roles (
    id uuid NOT NULL,
    description character varying(255),
    is_active boolean,
    name character varying(255) NOT NULL
);


ALTER TABLE public.app_roles OWNER TO baski_user;

--
-- Name: brand_references; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.brand_references (
    id uuid NOT NULL,
    abbr character varying(255),
    active boolean,
    category character varying(255) NOT NULL,
    color character varying(255),
    description text,
    display_order integer,
    featured boolean,
    logo_url character varying(255),
    name character varying(255) NOT NULL,
    sector character varying(255) NOT NULL,
    show_text boolean
);


ALTER TABLE public.brand_references OWNER TO baski_user;

--
-- Name: campaigns; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.campaigns (
    id uuid NOT NULL,
    active boolean NOT NULL,
    background_color character varying(30),
    badge_color character varying(30),
    badge_text character varying(40),
    created_at timestamp(6) with time zone,
    cta_link character varying(500),
    cta_text character varying(60),
    description text,
    ends_at timestamp(6) with time zone,
    image_url character varying(500) NOT NULL,
    label character varying(200),
    mobile_image_url character varying(500),
    sort_order integer NOT NULL,
    starts_at timestamp(6) with time zone,
    title character varying(200) NOT NULL,
    updated_at timestamp(6) with time zone
);


ALTER TABLE public.campaigns OWNER TO baski_user;

--
-- Name: cart_items; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.cart_items (
    id uuid NOT NULL,
    declared_prints integer,
    file_original_name character varying(255),
    file_pages_count integer,
    files3key character varying(255),
    height_cm integer,
    options_json text,
    price_breakdown character varying(255),
    quantity integer NOT NULL,
    total_price numeric(10,2) NOT NULL,
    unit_price numeric(10,2) NOT NULL,
    width_cm integer,
    cart_id uuid NOT NULL,
    product_type_id uuid NOT NULL,
    created_at timestamp(6) without time zone NOT NULL
);


ALTER TABLE public.cart_items OWNER TO baski_user;

--
-- Name: carts; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.carts (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    expires_at timestamp(6) without time zone,
    updated_at timestamp(6) without time zone,
    user_id uuid NOT NULL
);


ALTER TABLE public.carts OWNER TO baski_user;

--
-- Name: catalog_attribute_options; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.catalog_attribute_options (
    id uuid NOT NULL,
    color_hex character varying(10),
    sort_order integer,
    value character varying(255) NOT NULL,
    attribute_id uuid NOT NULL,
    price_modifier numeric(5,3) DEFAULT 1.000 NOT NULL
);


ALTER TABLE public.catalog_attribute_options OWNER TO baski_user;

--
-- Name: catalog_attributes; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.catalog_attributes (
    id uuid NOT NULL,
    attr_key character varying(80) NOT NULL,
    input_type character varying(20) NOT NULL,
    label character varying(255) NOT NULL,
    required boolean,
    sort_order integer,
    category_id uuid NOT NULL
);


ALTER TABLE public.catalog_attributes OWNER TO baski_user;

--
-- Name: catalog_brands; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.catalog_brands (
    id uuid NOT NULL,
    active boolean,
    created_at timestamp(6) with time zone,
    description text,
    logo_url character varying(500),
    name character varying(255) NOT NULL,
    slug character varying(100) NOT NULL
);


ALTER TABLE public.catalog_brands OWNER TO baski_user;

--
-- Name: catalog_categories; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.catalog_categories (
    id uuid NOT NULL,
    active boolean,
    created_at timestamp(6) with time zone,
    icon character varying(255),
    name character varying(255) NOT NULL,
    slug character varying(100) NOT NULL,
    sort_order integer,
    tagline character varying(255),
    updated_at timestamp(6) with time zone,
    parent_id uuid
);


ALTER TABLE public.catalog_categories OWNER TO baski_user;

--
-- Name: catalog_order_files; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.catalog_order_files (
    id uuid NOT NULL,
    created_at timestamp(6) with time zone,
    file_size bigint NOT NULL,
    mime_type character varying(100),
    original_name character varying(255) NOT NULL,
    page_count integer,
    page_warning boolean,
    storage_path character varying(500) NOT NULL,
    stored_filename character varying(255) NOT NULL,
    order_id uuid NOT NULL
);


ALTER TABLE public.catalog_order_files OWNER TO baski_user;

--
-- Name: catalog_order_items; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.catalog_order_items (
    id uuid NOT NULL,
    attributes_snapshot text,
    category_id uuid,
    category_name character varying(200),
    category_slug character varying(200),
    main_image_url character varying(500),
    price_tl numeric(12,2),
    price_usd numeric(12,2) NOT NULL,
    product_id uuid,
    product_name character varying(200) NOT NULL,
    product_slug character varying(200) NOT NULL,
    tier_id uuid,
    tier_qty integer NOT NULL,
    order_id uuid NOT NULL
);


ALTER TABLE public.catalog_order_items OWNER TO baski_user;

--
-- Name: catalog_orders; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.catalog_orders (
    id uuid NOT NULL,
    city character varying(60),
    created_at timestamp(6) with time zone,
    customer_address text NOT NULL,
    customer_email character varying(100),
    customer_name character varying(100) NOT NULL,
    customer_phone character varying(30) NOT NULL,
    district character varying(60),
    notes text,
    order_number character varying(32) NOT NULL,
    status character varying(20) NOT NULL,
    subtotal_usd numeric(12,2),
    total_tl numeric(12,2),
    updated_at timestamp(6) with time zone,
    usd_kur_at_order numeric(10,4),
    user_id uuid,
    iyzico_conversation_data text,
    iyzico_payment_id character varying(64),
    payment_status character varying(20),
    coupon_code character varying(50),
    discount_amount_tl numeric(12,2),
    subtotal_tl numeric(12,2),
    CONSTRAINT catalog_orders_payment_status_check CHECK (((payment_status)::text = ANY ((ARRAY['PENDING'::character varying, 'PROCESSING'::character varying, 'PAID'::character varying, 'FAILED'::character varying, 'REFUNDED'::character varying])::text[]))),
    CONSTRAINT catalog_orders_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'CONFIRMED'::character varying, 'IN_PRODUCTION'::character varying, 'READY'::character varying, 'SHIPPED'::character varying, 'DELIVERED'::character varying, 'CANCELLED'::character varying])::text[])))
);


ALTER TABLE public.catalog_orders OWNER TO baski_user;

--
-- Name: catalog_product_attribute_values; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.catalog_product_attribute_values (
    id uuid NOT NULL,
    attribute_id uuid NOT NULL,
    option_id uuid NOT NULL,
    product_id uuid NOT NULL
);


ALTER TABLE public.catalog_product_attribute_values OWNER TO baski_user;

--
-- Name: catalog_product_images; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.catalog_product_images (
    id uuid NOT NULL,
    alt_text character varying(200),
    sort_order integer,
    url character varying(500) NOT NULL,
    product_id uuid NOT NULL
);


ALTER TABLE public.catalog_product_images OWNER TO baski_user;

--
-- Name: catalog_product_reviews; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.catalog_product_reviews (
    id uuid NOT NULL,
    anonymous boolean NOT NULL,
    approved boolean NOT NULL,
    comment text,
    created_at timestamp(6) with time zone NOT NULL,
    order_id uuid,
    rating integer NOT NULL,
    updated_at timestamp(6) with time zone,
    user_email character varying(255),
    user_id uuid NOT NULL,
    user_name character varying(255),
    product_id uuid NOT NULL
);


ALTER TABLE public.catalog_product_reviews OWNER TO baski_user;

--
-- Name: catalog_product_tiers; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.catalog_product_tiers (
    id uuid NOT NULL,
    price_usd numeric(12,2) NOT NULL,
    qty integer NOT NULL,
    sort_order integer,
    product_id uuid NOT NULL
);


ALTER TABLE public.catalog_product_tiers OWNER TO baski_user;

--
-- Name: catalog_products; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.catalog_products (
    id uuid NOT NULL,
    active boolean,
    badge character varying(50),
    created_at timestamp(6) with time zone,
    featured boolean,
    long_desc text,
    name character varying(255) NOT NULL,
    original_price numeric(12,2),
    short_desc character varying(500),
    slug character varying(120) NOT NULL,
    sort_order integer,
    updated_at timestamp(6) with time zone,
    brand_id uuid,
    category_id uuid NOT NULL
);


ALTER TABLE public.catalog_products OWNER TO baski_user;

--
-- Name: coupons; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.coupons (
    id uuid NOT NULL,
    active boolean,
    auto_issue_on_first_visit boolean,
    auto_issue_on_order_amount numeric(10,2),
    code character varying(50) NOT NULL,
    created_at timestamp(6) without time zone,
    current_usage integer,
    description text,
    discount_amount numeric(10,2),
    discount_percent numeric(5,2),
    end_date timestamp(6) without time zone,
    gift_amount numeric(10,2),
    max_usage integer,
    min_order_amount numeric(10,2),
    name character varying(255) NOT NULL,
    per_user_limit integer,
    start_date timestamp(6) without time zone,
    type character varying(20) NOT NULL,
    updated_at timestamp(6) without time zone,
    CONSTRAINT coupons_type_check CHECK (((type)::text = ANY ((ARRAY['PERCENT'::character varying, 'AMOUNT'::character varying, 'GIFT'::character varying])::text[])))
);


ALTER TABLE public.coupons OWNER TO baski_user;

--
-- Name: dealers; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.dealers (
    id uuid NOT NULL,
    address character varying(255) NOT NULL,
    city character varying(255),
    company_name character varying(255) NOT NULL,
    created_at timestamp(6) without time zone,
    credit_limit numeric(15,2),
    discount_rate numeric(5,2),
    district character varying(255),
    notes character varying(255),
    phone character varying(255) NOT NULL,
    rejection_reason character varying(255),
    status character varying(255),
    tax_number character varying(255) NOT NULL,
    tax_office character varying(255),
    updated_at timestamp(6) without time zone,
    user_id uuid,
    business_type character varying(255),
    estimated_monthly_revenue character varying(255),
    note character varying(255),
    website character varying(255),
    CONSTRAINT dealers_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying])::text[])))
);


ALTER TABLE public.dealers OWNER TO baski_user;

--
-- Name: files; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.files (
    id uuid NOT NULL,
    original_name character varying(255) NOT NULL,
    page_count integer,
    s3key character varying(255) NOT NULL,
    status character varying(255),
    uploaded_at timestamp(6) without time zone,
    order_item_id uuid NOT NULL,
    CONSTRAINT files_status_check CHECK (((status)::text = ANY ((ARRAY['LOCKED'::character varying, 'UNLOCKED'::character varying, 'REJECTED'::character varying])::text[])))
);


ALTER TABLE public.files OWNER TO baski_user;

--
-- Name: hero_slides; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.hero_slides (
    id uuid NOT NULL,
    active boolean NOT NULL,
    background_color character varying(30),
    created_at timestamp(6) with time zone,
    cta_link character varying(500),
    cta_text character varying(60),
    description text,
    ends_at timestamp(6) with time zone,
    image_url character varying(500) NOT NULL,
    label character varying(200),
    layout character varying(20),
    mobile_image_url character varying(500),
    sort_order integer NOT NULL,
    starts_at timestamp(6) with time zone,
    title character varying(200) NOT NULL,
    updated_at timestamp(6) with time zone,
    CONSTRAINT hero_slides_layout_check CHECK (((layout)::text = ANY ((ARRAY['SPLIT_LEFT'::character varying, 'SPLIT_RIGHT'::character varying, 'OVERLAY'::character varying, 'IMAGE_ONLY'::character varying])::text[])))
);


ALTER TABLE public.hero_slides OWNER TO baski_user;

--
-- Name: notifications; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.notifications (
    id uuid NOT NULL,
    channel character varying(255) NOT NULL,
    recipient character varying(255) NOT NULL,
    sent_at timestamp(6) without time zone,
    status character varying(255),
    order_id uuid NOT NULL
);


ALTER TABLE public.notifications OWNER TO baski_user;

--
-- Name: order_items; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.order_items (
    id uuid NOT NULL,
    height_cm integer,
    product_type character varying(255) NOT NULL,
    quantity integer NOT NULL,
    unit_price numeric(10,2) NOT NULL,
    width_cm integer,
    order_id uuid NOT NULL
);


ALTER TABLE public.order_items OWNER TO baski_user;

--
-- Name: order_status_history; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.order_status_history (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    note character varying(255),
    status character varying(255) NOT NULL,
    order_id uuid NOT NULL,
    CONSTRAINT order_status_history_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'PAID'::character varying, 'REVIEWING'::character varying, 'PRINTING'::character varying, 'SHIPPED'::character varying, 'COMPLETED'::character varying, 'CANCELLED'::character varying])::text[])))
);


ALTER TABLE public.order_status_history OWNER TO baski_user;

--
-- Name: orders; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.orders (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    declared_prints integer,
    pdf_page_count integer,
    shipping_address text NOT NULL,
    status character varying(255),
    total_price numeric(10,2) NOT NULL,
    user_id uuid NOT NULL,
    CONSTRAINT orders_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'PAID'::character varying, 'REVIEWING'::character varying, 'PRINTING'::character varying, 'SHIPPED'::character varying, 'COMPLETED'::character varying, 'CANCELLED'::character varying])::text[])))
);


ALTER TABLE public.orders OWNER TO baski_user;

--
-- Name: payments; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.payments (
    id uuid NOT NULL,
    amount numeric(10,2) NOT NULL,
    paid_at timestamp(6) without time zone,
    provider character varying(255),
    provider_ref character varying(255),
    status character varying(255),
    order_id uuid NOT NULL,
    CONSTRAINT payments_status_check CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'SUCCESS'::character varying, 'FAILED'::character varying, 'REFUNDED'::character varying])::text[])))
);


ALTER TABLE public.payments OWNER TO baski_user;

--
-- Name: permissions; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.permissions (
    id uuid NOT NULL,
    category character varying(255),
    code character varying(255) NOT NULL,
    label character varying(255) NOT NULL
);


ALTER TABLE public.permissions OWNER TO baski_user;

--
-- Name: pre_order_files; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.pre_order_files (
    id uuid NOT NULL,
    claimed_at timestamp(6) with time zone,
    claimed_by_order_id uuid,
    created_at timestamp(6) with time zone NOT NULL,
    file_size bigint NOT NULL,
    mime_type character varying(100),
    original_name character varying(500) NOT NULL,
    stored_path character varying(500) NOT NULL
);


ALTER TABLE public.pre_order_files OWNER TO baski_user;

--
-- Name: price_rules; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.price_rules (
    id uuid NOT NULL,
    base_price numeric(38,2),
    max_qty integer,
    min_qty integer,
    multiplier numeric(38,2),
    option_key character varying(255),
    option_value character varying(255),
    price_delta numeric(38,2),
    rule_type character varying(255) NOT NULL,
    unit_price numeric(38,2),
    product_type_id uuid NOT NULL
);


ALTER TABLE public.price_rules OWNER TO baski_user;

--
-- Name: product_configs; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.product_configs (
    id uuid NOT NULL,
    affects_price boolean,
    display_order integer,
    field_key character varying(255) NOT NULL,
    field_type character varying(255) NOT NULL,
    options text,
    required boolean,
    product_type_id uuid NOT NULL
);


ALTER TABLE public.product_configs OWNER TO baski_user;

--
-- Name: product_types; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.product_types (
    id uuid NOT NULL,
    description character varying(255),
    has_file boolean,
    is_active boolean,
    min_order integer,
    name character varying(255) NOT NULL,
    pricing_model character varying(255) NOT NULL,
    slug character varying(255) NOT NULL,
    unit character varying(255) NOT NULL,
    image_url character varying(255),
    badge character varying(255),
    featured boolean,
    original_price numeric(38,2)
);


ALTER TABLE public.product_types OWNER TO baski_user;

--
-- Name: system_settings; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.system_settings (
    key character varying(255) NOT NULL,
    description character varying(255),
    value text NOT NULL
);


ALTER TABLE public.system_settings OWNER TO baski_user;

--
-- Name: user_app_roles; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.user_app_roles (
    id uuid NOT NULL,
    assigned_at timestamp(6) without time zone,
    assigned_by character varying(255),
    app_role_id uuid NOT NULL,
    user_id uuid NOT NULL
);


ALTER TABLE public.user_app_roles OWNER TO baski_user;

--
-- Name: user_coupons; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.user_coupons (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    expires_at timestamp(6) without time zone,
    issued_at timestamp(6) without time zone,
    order_id uuid,
    source character varying(20) NOT NULL,
    used boolean,
    used_at timestamp(6) without time zone,
    user_id uuid NOT NULL,
    coupon_id uuid NOT NULL,
    CONSTRAINT user_coupons_source_check CHECK (((source)::text = ANY ((ARRAY['WELCOME'::character varying, 'GIFT'::character varying, 'PROMO'::character varying, 'MANUAL'::character varying])::text[])))
);


ALTER TABLE public.user_coupons OWNER TO baski_user;

--
-- Name: users; Type: TABLE; Schema: public; Owner: baski_user
--

CREATE TABLE public.users (
    id uuid NOT NULL,
    created_at timestamp(6) without time zone,
    email character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    password character varying(255) NOT NULL,
    phone character varying(255),
    role character varying(255) NOT NULL,
    email_verified boolean,
    google_id character varying(255),
    CONSTRAINT users_role_check CHECK (((role)::text = ANY ((ARRAY['CUSTOMER'::character varying, 'OPERATOR'::character varying, 'ADMIN'::character varying])::text[])))
);


ALTER TABLE public.users OWNER TO baski_user;

--
-- Data for Name: addresses; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.addresses (id, address_line, city, country, district, full_name, is_default, phone, title, user_id) FROM stdin;
f59e7694-aed1-42ca-b028-59ee4140cddf	Ataturk Cad. No:1	Istanbul	Türkiye	Kadikoy	Admin Test	t	05001234567	Ofis	c2a9af4c-0956-4e71-bde1-f8a8f9258586
d7f61615-398f-4aab-b457-2740814ac8d4	Uğur Mumcu\nŞeyh Şamil Cd. No:15	İstanbul	Türkiye	Kartal	test	t	05530214777	ev	b9b78f85-d48a-45ee-9db8-9b39781bde32
\.


--
-- Data for Name: app_role_permissions; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.app_role_permissions (role_id, permission_id) FROM stdin;
ab86f054-f176-43a3-a233-69d2897928cd	41708101-425f-4b6e-9260-7a6e484a0f56
ab86f054-f176-43a3-a233-69d2897928cd	b106de7b-69e4-458e-b173-706b7f200133
ab86f054-f176-43a3-a233-69d2897928cd	acb4b4d6-96ce-418a-8cee-cd53fd897797
ab86f054-f176-43a3-a233-69d2897928cd	6ed43423-604c-488c-ba97-db5fba5f1a9c
ab86f054-f176-43a3-a233-69d2897928cd	ef8f9d9e-ce11-464a-9215-f72417afc910
ab86f054-f176-43a3-a233-69d2897928cd	180988c6-db2a-4f97-b599-85125bdac6d0
ab86f054-f176-43a3-a233-69d2897928cd	5c4a50c4-f610-498c-a367-5258d41af6a6
ab86f054-f176-43a3-a233-69d2897928cd	ae87eeeb-9b2b-4679-bd2a-4d728d81344f
ab86f054-f176-43a3-a233-69d2897928cd	36392c6c-4235-406a-ba8d-48c638898cc9
ab86f054-f176-43a3-a233-69d2897928cd	6305a29f-1ae4-4ee4-b796-98cad99b6561
ab86f054-f176-43a3-a233-69d2897928cd	27173064-4934-4ca3-8f37-4a6d383f93c0
ab86f054-f176-43a3-a233-69d2897928cd	28d04e70-735b-4efa-a446-bcfed87f4d02
ab86f054-f176-43a3-a233-69d2897928cd	efb04aea-4485-41c4-8c77-00ca852d0003
ab86f054-f176-43a3-a233-69d2897928cd	040bef77-2083-441c-ad91-fb64b77e4e7d
ab86f054-f176-43a3-a233-69d2897928cd	abdbb250-1083-4eea-80a4-a7e351e83694
ab86f054-f176-43a3-a233-69d2897928cd	e1c7a7a3-f0e3-4047-a320-6f1b1a9576e9
ab86f054-f176-43a3-a233-69d2897928cd	f98acd04-729a-41b8-82b3-10ce8afc41a5
ab86f054-f176-43a3-a233-69d2897928cd	b94af777-a8c1-4b62-8f6a-b49dd8cf18c7
ab86f054-f176-43a3-a233-69d2897928cd	4dcca2db-d2dd-4577-895c-2e7ba95a5fd5
ab86f054-f176-43a3-a233-69d2897928cd	23142978-26c4-436a-ad95-ea09d5309e44
ab86f054-f176-43a3-a233-69d2897928cd	a8cde1c3-2788-4ef4-aab5-c43bf0be2ddb
a0bdaf1e-4386-485d-859c-1407813591a7	b106de7b-69e4-458e-b173-706b7f200133
\.


--
-- Data for Name: app_roles; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.app_roles (id, description, is_active, name) FROM stdin;
e5a63db2-3c9d-4e08-afc9-2ab7fb67e3e0	Sipariş yönetimi ve üretim takibi	t	Operatör
a0bdaf1e-4386-485d-859c-1407813591a7	Ödeme ve ciro raporları	t	Muhasebe
fa0e64d1-cbc8-4abf-a283-c824a1f5b5ef	Sadece üretim aşamasındaki siparişler	t	Üretim
ab86f054-f176-43a3-a233-69d2897928cd	Tüm yetkilere sahip	t	Admin
\.


--
-- Data for Name: brand_references; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.brand_references (id, abbr, active, category, color, description, display_order, featured, logo_url, name, sector, show_text) FROM stdin;
4468c276-c66b-4ed7-9b87-e138182ded81	\N	f	Zincir Market	#F4821F	\N	0	f	\N	cxcxccxcx	cvvcvc	\N
66dd8b05-89f3-4e92-bf95-908660adda2f	\N	f	Zincir Market	#F4821F	\N	0	f	\N	ggf	fggfgf	\N
6615e29d-e1ba-49dc-be2b-f6845f5c3904	HJ	f	Zincir Market	#F4821F	\N	0	f	\N	hjjh	Zincir Market	\N
6b69efaf-927c-47b5-a5b7-ef36fbc09d2c	\N	f	Zincir Market	#F4821F	\N	5	t	\N	dd	dffd	\N
c053c00b-abd5-4b72-b1c5-1fd25808c95a	H	f	Zincir Market	#F4821F	\N	3	f	\N	ghgh	hjjh	\N
e6c44dac-37d0-473a-98b2-81f046cf83ed	\N	t	Zincir Market	#F4821F	\N	0	f	https://markantalya.com/wp-content/uploads/2023/02/teknosa.jpg	Teknosa	Elektronik	t
\.


--
-- Data for Name: campaigns; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.campaigns (id, active, background_color, badge_color, badge_text, created_at, cta_link, cta_text, description, ends_at, image_url, label, mobile_image_url, sort_order, starts_at, title, updated_at) FROM stdin;
3767c06e-1721-448f-9d38-121dae9a01ff	t	#9333ea	#F4821F	\N	2026-06-01 09:49:35.597348+00	\N	\N	werweerwr	\N	http://localhost:8080/uploads/banner/a5e2d008-f917-492d-bd7a-f4e98a5e9e5e.webp	KAPMANYA	\N	0	\N	3 ADET YELKEN BAYRAK AL KART VİZİT 1 TL	2026-06-01 09:49:35.597348+00
\.


--
-- Data for Name: cart_items; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.cart_items (id, declared_prints, file_original_name, file_pages_count, files3key, height_cm, options_json, price_breakdown, quantity, total_price, unit_price, width_cm, cart_id, product_type_id, created_at) FROM stdin;
\.


--
-- Data for Name: carts; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.carts (id, created_at, expires_at, updated_at, user_id) FROM stdin;
9c070173-c049-4037-88f2-07ba841202d5	\N	2026-05-23 15:51:24.804787	2026-05-22 15:51:24.8332	c2a9af4c-0956-4e71-bde1-f8a8f9258586
67bce9d9-627d-4993-a092-814d5cbf8545	2026-05-29 22:00:36.676757	2026-05-30 22:00:36.660183	2026-05-29 22:00:36.676757	b9b78f85-d48a-45ee-9db8-9b39781bde32
\.


--
-- Data for Name: catalog_attribute_options; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.catalog_attribute_options (id, color_hex, sort_order, value, attribute_id, price_modifier) FROM stdin;
8cc47117-ed20-4dd8-b7e8-1e92a9e53039	\N	3	500	8811e952-4385-400e-a157-6e3b239a6f49	1.000
37e5f7ac-99c0-411a-8006-2c070b64b751	\N	1	5.5 x 8.5 cm (Standart)	00b63aa0-71fc-4e48-80e1-c69461ddd504	1.000
f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	\N	2	8.5 x 5.5 cm (Yatay)	00b63aa0-71fc-4e48-80e1-c69461ddd504	1.000
19093394-533f-4890-8856-eaba05ff6b21	\N	3	9 x 5 cm	00b63aa0-71fc-4e48-80e1-c69461ddd504	1.000
67480ace-94bf-4d99-b0bb-73460821fbc0	\N	4	8 x 5 cm	00b63aa0-71fc-4e48-80e1-c69461ddd504	1.000
35083070-9d84-499f-83ea-19db80862f39	\N	1	Tek Yön Baskı	a6f8a4df-8851-4680-829f-87986f7803de	1.000
bfd9bb29-f672-458f-b9b5-65e6359d253d	\N	2	Çift Yön Baskı	a6f8a4df-8851-4680-829f-87986f7803de	1.400
ec7c68f7-fa71-48fb-9b6b-8ae860df1492	\N	1	350g Mat Kuse	8811e952-4385-400e-a157-6e3b239a6f49	1.000
f05cceae-93c1-4205-8c9b-ddcd55ca4e83	\N	2	400g Mat Kuse	8811e952-4385-400e-a157-6e3b239a6f49	1.100
5dc33309-c210-428e-a2a3-0d909862125a	\N	3	350g Parlak Kuşe	8811e952-4385-400e-a157-6e3b239a6f49	1.050
7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	\N	4	400g Parlak Kuşe	8811e952-4385-400e-a157-6e3b239a6f49	1.150
1cc895fb-8487-466c-80cb-0b094c1bb1c0	\N	5	300g Bristol	8811e952-4385-400e-a157-6e3b239a6f49	1.250
85943178-63d2-4623-b71b-19e856a1a45f	\N	6	Kraft Kağıt	8811e952-4385-400e-a157-6e3b239a6f49	1.300
b5de8cf9-b7d4-4e59-be5f-1542ce035441	\N	1	Selefon Yok	f35c006f-3f41-4609-947b-331decc0123c	1.000
b7ca33eb-18ec-4407-99d9-778f4fca2d22	\N	2	Mat Selefon	f35c006f-3f41-4609-947b-331decc0123c	1.150
a60e91d0-59fd-42fa-93fe-2c88b3670a55	\N	3	Parlak Selefon	f35c006f-3f41-4609-947b-331decc0123c	1.150
22e0e47e-6b57-4cd4-8069-3718ccec56fb	\N	4	Soft Touch	f35c006f-3f41-4609-947b-331decc0123c	1.350
422670a0-7918-4f04-b554-43e89f2a4615	\N	1	Tek Yön Baskı	72528f26-c08d-4465-9eb7-6053695b93de	1.000
1a8bd646-5d18-4514-80a7-8c48cf85946f	\N	1	Tek Yön Baskı	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	1.000
d306e709-4634-49e6-9b33-d523d414b888	\N	2	Çift Yön Baskı	72528f26-c08d-4465-9eb7-6053695b93de	1.400
9423cb73-cc17-4808-99f9-61e504173f90	\N	2	Çift Yön Baskı	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	1.400
43ef505d-c60c-4749-8f9a-3a093f99dbb9	\N	1	Dikey	c1aa0331-352c-4219-af6d-a9161bfa0143	1.000
0f70ed44-5c38-42c8-a02e-6e088edf8f20	\N	2	Yatay	c1aa0331-352c-4219-af6d-a9161bfa0143	1.000
c82e4c46-6b83-4423-93d3-b8f7c13bb015	\N	1	8.5x5 cm	6cb3206f-8787-4761-a448-690f4c37cd09	1.000
ed6f2f53-68d6-4dd8-9c17-a29edd89b92c	\N	1	500 Mikron Buzlu PVC	50118dd8-633d-43ec-828a-97fd3fe0b7b8	1.000
0fc2909d-3db3-470a-8c84-48aaea57e4f0	\N	1	Tek Yön Baskı	9abea6d1-09f0-4ed7-927e-faef0e88a824	1.000
ea18e90d-1a8c-498e-8df4-dd3bbb335615	\N	1	4+0 CMYK	ab1c1763-7a7a-4b82-8e06-088bede34bc8	1.000
2467fa53-d031-46fe-b83d-f9a37ca4d723	\N	1	Lak Yok	cac9aa53-11d7-43ad-ac07-c63db02bc267	1.000
2a824ab6-6d7d-44cc-9148-3af158dbda70	\N	2	Tek Yön Kabartma Lak	cac9aa53-11d7-43ad-ac07-c63db02bc267	1.000
d96cccf3-eadb-4df7-b0b4-4750bb4879c8	\N	1	Oval	c6f53ceb-37ee-456b-90f2-35e5e57127b2	1.000
aa8bd074-8604-491d-93ed-0031888f00c6	\N	1	5+0 Cmk	8426e329-cd26-49a3-abb5-863940808283	1.000
d746761b-43b9-4053-a72b-7a2808da68bb	\N	2	5+5 Cmk	8426e329-cd26-49a3-abb5-863940808283	1.000
1c4ec33b-4854-47b3-ad54-daf42a013cc4	\N	1	350g Mat Kuşe	8811e952-4385-400e-a157-6e3b239a6f49	1.000
\.


--
-- Data for Name: catalog_attributes; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.catalog_attributes (id, attr_key, input_type, label, required, sort_order, category_id) FROM stdin;
00b63aa0-71fc-4e48-80e1-c69461ddd504	ebat	select	Ebat	t	1	ac16bace-2752-493d-aeb6-3c6f9977a059
a6f8a4df-8851-4680-829f-87986f7803de	baski_yonu	select	Baskı Yönü	t	2	ac16bace-2752-493d-aeb6-3c6f9977a059
f35c006f-3f41-4609-947b-331decc0123c	selefon	select	Selefon	f	4	ac16bace-2752-493d-aeb6-3c6f9977a059
8811e952-4385-400e-a157-6e3b239a6f49	kagit	select	Kagit	t	3	ac16bace-2752-493d-aeb6-3c6f9977a059
c8e9b5f6-9c10-42c7-b435-a6097d7f34c5	olcu	select	Ölçü	t	1	ad720c31-b92b-4bba-8521-7a03c93324ca
72528f26-c08d-4465-9eb7-6053695b93de	baski_yonu	select	Baskı Yönü	t	2	ad720c31-b92b-4bba-8521-7a03c93324ca
9dcacff6-0b5f-42a4-b70e-543f6ae821f4	kagit	select	Kağıt	t	3	ad720c31-b92b-4bba-8521-7a03c93324ca
3e6d1fc9-9e95-4f72-bb07-c5f35e5ecf20	olcu	select	Ölçü	t	1	0d53bffa-cf96-4aa0-8e21-2278418ad97a
176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	baski_yonu	select	Baskı Yönü	t	2	0d53bffa-cf96-4aa0-8e21-2278418ad97a
cdffbfca-c554-404f-b37a-186d2c2d7b20	kagit	select	Kağıt	t	3	0d53bffa-cf96-4aa0-8e21-2278418ad97a
c1aa0331-352c-4219-af6d-a9161bfa0143	tasarim_yonu	select	Tasarım Yönü	t	1	961f50a7-1d5c-4c8a-854a-ba6620ab9074
6cb3206f-8787-4761-a448-690f4c37cd09	ebat	select	Ebat	t	2	961f50a7-1d5c-4c8a-854a-ba6620ab9074
50118dd8-633d-43ec-828a-97fd3fe0b7b8	malzeme	select	Malzeme	t	3	961f50a7-1d5c-4c8a-854a-ba6620ab9074
9abea6d1-09f0-4ed7-927e-faef0e88a824	baski_yonu	select	Baskı Yönü	t	4	961f50a7-1d5c-4c8a-854a-ba6620ab9074
ab1c1763-7a7a-4b82-8e06-088bede34bc8	baski_rengi	select	Baskı Rengi	t	5	961f50a7-1d5c-4c8a-854a-ba6620ab9074
cac9aa53-11d7-43ad-ac07-c63db02bc267	lak	select	Lak	f	6	961f50a7-1d5c-4c8a-854a-ba6620ab9074
c6f53ceb-37ee-456b-90f2-35e5e57127b2	kesim	select	Kesim	t	7	961f50a7-1d5c-4c8a-854a-ba6620ab9074
8426e329-cd26-49a3-abb5-863940808283	renk	select	Renk	f	0	ac16bace-2752-493d-aeb6-3c6f9977a059
\.


--
-- Data for Name: catalog_brands; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.catalog_brands (id, active, created_at, description, logo_url, name, slug) FROM stdin;
\.


--
-- Data for Name: catalog_categories; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.catalog_categories (id, active, created_at, icon, name, slug, sort_order, tagline, updated_at, parent_id) FROM stdin;
2783e48a-b502-454b-bffe-0af7301ec927	t	2026-05-27 16:14:59.736488+00	\N	Altın Yaldızlı Kartvizit	kartvizit-yaldiz	4	Altın Yaldızlı Kartvizit	2026-06-02 08:19:46.770157+00	ac16bace-2752-493d-aeb6-3c6f9977a059
6d5b06e4-572e-44da-88b2-460a4f49cb96	t	2026-06-02 08:57:37.189832+00	📄	Broşür & El İlanı	brosur	20	Tanıtım ve reklam baskıları	2026-06-02 08:57:37.189832+00	\N
43a784da-0d7b-40e8-bfef-7adb8ef96f21	t	2026-06-02 08:57:37.189832+00	✂️	Özel Kesim & Form	kartvizit-ozel	2	\N	2026-06-02 08:57:37.189832+00	ac16bace-2752-493d-aeb6-3c6f9977a059
0613535f-9f95-4160-883b-af5cc07ae7b2	t	2026-06-02 08:57:37.189832+00	🌿	Ekonomik Kartvizit	kartvizit-eko	3	\N	2026-06-02 08:57:37.189832+00	ac16bace-2752-493d-aeb6-3c6f9977a059
2e997fe3-7d4f-4c9a-92dd-0ce25256cbb8	t	2026-05-27 16:14:59.736488+00	📄	Amerikan Servis	brosur-amerikan-servis	4	\N	2026-05-27 16:14:59.736488+00	6d5b06e4-572e-44da-88b2-460a4f49cb96
b8d56b13-2748-44a6-899a-d173367b403d	t	2026-05-27 16:14:59.736488+00	📄	Katlamalı Broşür	brosur-katlamali	3	\N	2026-05-27 16:14:59.736488+00	6d5b06e4-572e-44da-88b2-460a4f49cb96
06938a6c-5437-4422-8a67-5c516fe91666	t	2026-05-27 16:14:59.736488+00	📄	El İlanı	brosur-el-ilani	1	\N	2026-05-27 16:14:59.736488+00	6d5b06e4-572e-44da-88b2-460a4f49cb96
edf39510-c96a-4075-97ab-9d29097a3553	t	2026-05-27 16:14:59.736488+00	📄	Ekonomik El İlanı	brosur-ekonomik-el-ilani	2	\N	2026-05-27 16:14:59.736488+00	6d5b06e4-572e-44da-88b2-460a4f49cb96
7fd43c70-ac25-43a8-997d-9bee5fcdc5f2	t	2026-05-27 16:14:59.736488+00	📄	Masa Sümeni	brosur-masa-sumeni	5	\N	2026-05-27 16:14:59.736488+00	6d5b06e4-572e-44da-88b2-460a4f49cb96
be969a50-86ce-4044-96e8-490b23552f7d	t	2026-05-27 16:14:59.736488+00	💳	Premium Kartvizit	kartvizit-premium	1	\N	2026-05-27 16:14:59.736488+00	ac16bace-2752-493d-aeb6-3c6f9977a059
961f50a7-1d5c-4c8a-854a-ba6620ab9074	t	2026-05-27 16:14:59.736488+00	💳	Şeffaf PVC Kartvizit	kartvizit-pvc	2	\N	2026-05-27 16:14:59.736488+00	ac16bace-2752-493d-aeb6-3c6f9977a059
68e80d8b-96ad-4e66-9828-e4b70a7a2ef6	t	2026-05-27 16:14:59.736488+00	💳	Kabartmalı Kartvizit	kartvizit-kabartma	3	\N	2026-05-27 16:14:59.736488+00	ac16bace-2752-493d-aeb6-3c6f9977a059
8836a040-5619-4e2e-a1c6-639cdf9783c5	t	2026-05-27 16:14:59.736488+00	💳	Soft Touch Kartvizit	kartvizit-soft-touch	5	\N	2026-05-27 16:14:59.736488+00	ac16bace-2752-493d-aeb6-3c6f9977a059
708668f4-698b-417b-97bf-0102423f593c	t	2026-05-27 16:14:59.736488+00	💳	Ekspres Kartvizit	kartvizit-ekspres	6	\N	2026-05-27 16:14:59.736488+00	ac16bace-2752-493d-aeb6-3c6f9977a059
a93c382e-d394-4e9b-928a-0f8b6097fce3	t	2026-05-27 16:14:59.736488+00	📋	Kartpostal	kurumsal-kartpostal	5	\N	2026-05-27 16:14:59.736488+00	ad720c31-b92b-4bba-8521-7a03c93324ca
190c3c41-e1be-496b-8fe6-d9c9dcde3d38	t	2026-05-27 16:14:59.736488+00	📋	Reçete Baskı	kurumsal-recete	3	\N	2026-05-27 16:14:59.736488+00	ad720c31-b92b-4bba-8521-7a03c93324ca
f6760942-5d2b-4d98-b934-7dc430984b0e	t	2026-05-27 16:14:59.736488+00	📋	Antetli Kağıt	kurumsal-antetli	1	\N	2026-05-27 16:14:59.736488+00	ad720c31-b92b-4bba-8521-7a03c93324ca
87df6959-6da6-4ca5-9077-8ba6e1c85333	t	2026-05-27 16:14:59.736488+00	📋	Sertifika	kurumsal-sertifika	2	\N	2026-05-27 16:14:59.736488+00	ad720c31-b92b-4bba-8521-7a03c93324ca
4a6850e6-86a5-4b8c-ab11-539228096a50	t	2026-05-27 16:14:59.736488+00	📋	Anket Formu	kurumsal-anket-formu	4	\N	2026-05-27 16:14:59.736488+00	ad720c31-b92b-4bba-8521-7a03c93324ca
3de0b0a2-693b-4d29-b588-1b9812db9e94	t	2026-05-27 16:14:59.736488+00	🎁	Islak Mendil	promosyon-islak-mendil	1	\N	2026-05-27 16:14:59.736488+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
dc7e1291-48e3-4cb5-a38d-7ab905aa5145	t	2026-05-27 16:14:59.736488+00	🎁	Baskılı Bardak	promosyon-baskili-bardak	2	\N	2026-05-27 16:14:59.736488+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
909aac03-267a-45e5-9b98-2fac2ba34a47	t	2026-05-27 16:14:59.736488+00	🎁	Bloknot	promosyon-bloknot	3	\N	2026-05-27 16:14:59.736488+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
93bfaf95-800a-4ae5-96ba-674d9bc0a8bb	t	2026-05-27 16:14:59.736488+00	🎁	Magnet	promosyon-magnet	4	\N	2026-05-27 16:14:59.736488+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
d3f8d09e-fa37-405f-bf7b-f3d3cd7bd424	t	2026-05-27 16:14:59.736488+00	🎁	Mousepad	promosyon-mousepad	5	\N	2026-05-27 16:14:59.736488+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
1b7361ef-5d50-48ac-b3a6-468ade1c7444	t	2026-05-27 16:14:59.736488+00	🎁	Takvim	promosyon-takvim	6	\N	2026-05-27 16:14:59.736488+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
5e46b861-9b7c-45bd-a40a-1f473b30dfe1	t	2026-05-27 16:14:59.736488+00	📷	Fotoğraf Baskı	foto-fotograf	2	\N	2026-05-27 16:14:59.736488+00	0d53bffa-cf96-4aa0-8e21-2278418ad97a
9baca02a-ad1f-4a0d-8145-0d3d95c12dba	t	2026-05-27 16:14:59.736488+00	📷	Kanvas Tablo	foto-kanvas-tablo	1	\N	2026-05-27 16:14:59.736488+00	0d53bffa-cf96-4aa0-8e21-2278418ad97a
0199dfc8-1809-4bd7-998f-1eca3d2db26c	t	2026-05-27 16:14:59.736488+00	📷	Puzzle Baskı	foto-puzzle	4	\N	2026-05-27 16:14:59.736488+00	0d53bffa-cf96-4aa0-8e21-2278418ad97a
ac6460b2-7caa-441e-bc72-7a933d4a3758	t	2026-05-27 16:14:59.736488+00	📷	Foto Kart	foto-kart	5	\N	2026-05-27 16:14:59.736488+00	0d53bffa-cf96-4aa0-8e21-2278418ad97a
b053f4b9-78e3-45a9-a536-b8ef844b5a4b	t	2026-05-27 16:14:59.736488+00	📷	Kupa Baskı	foto-kupa	3	\N	2026-05-27 16:14:59.736488+00	0d53bffa-cf96-4aa0-8e21-2278418ad97a
d7b967d1-80b4-46cd-93f8-e3f7f532b5b9	t	2026-05-26 21:15:51.692346+00	📕	Katalog	katalog	40	Ürün ve hizmet katalogları	2026-05-26 21:15:51.692346+00	6d5b06e4-572e-44da-88b2-460a4f49cb96
fe9fef34-dbfa-4314-8f65-2c1359352c58	t	2026-05-27 17:19:21.717588+00	📄	Kırımlı El İlanı	kirimli-el-ilani-brosur	12	\N	2026-05-27 17:19:21.717588+00	6d5b06e4-572e-44da-88b2-460a4f49cb96
5a4f40f1-cc9d-4e79-8045-b7fefbcd4537	t	2026-05-27 17:19:21.717588+00	📄	Kapı Askı Broşürü	kapi-aski-brosuru	11	\N	2026-05-27 17:19:21.717588+00	6d5b06e4-572e-44da-88b2-460a4f49cb96
ad720c31-b92b-4bba-8521-7a03c93324ca	t	2026-05-27 16:14:59.736488+00	🏢	Kurumsal Baskılar	kurumsal-urunler	40	\N	2026-05-27 16:14:59.736488+00	\N
0d53bffa-cf96-4aa0-8e21-2278418ad97a	t	2026-05-27 16:14:59.736488+00	🖼️	Tablo & Fotoğraf	tablolar	80	\N	2026-05-27 16:14:59.736488+00	\N
ac16bace-2752-493d-aeb6-3c6f9977a059	t	2026-05-22 14:18:52.552812+00	\N	Kartvizitler	kartvizit	10	\N	2026-05-22 14:18:52.552812+00	\N
13ff22a1-1856-4857-958d-b2244a2b2d40	t	2026-05-26 21:15:51.692346+00	🚩	Bayrak Ürünleri	bayrak-urunleri	30	Yelken, masa ve duvar bayrakları	2026-05-26 21:15:51.692346+00	\N
306c9f83-afcf-42a8-a75c-ca0c054e0e0c	t	2026-05-27 17:19:21.717588+00	📋	Matbaa Ürünleri	matbaa-urunleri	50	\N	2026-05-27 17:19:21.717588+00	\N
a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125	t	2026-05-26 21:15:51.692346+00	🎁	Promosyon Ürünleri	promosyon-urunleri	60	Bardak, kalem, anahtarlık, t-shirt	2026-05-26 21:15:51.692346+00	\N
8fd8fde8-3148-4260-8ca1-97450a9c89d5	t	2026-05-27 17:19:21.717588+00	🖨️	Dijital Baskı Ürünleri	dijital-baski-urunleri	70	\N	2026-05-27 17:19:21.717588+00	\N
b6da22c1-b04e-4dd8-bc19-7951e32f24e0	t	2026-05-27 17:19:21.717588+00	🏠	Emlak Ürünleri	emlak-urunleri	90	\N	2026-05-27 17:19:21.717588+00	\N
f179f36b-97ef-4553-8556-f3b723899a33	t	2026-05-26 21:15:51.692346+00	🖼️	Afiş	afis	20	Vinil ve kağıt afiş baskıları	2026-05-26 21:15:51.692346+00	8fd8fde8-3148-4260-8ca1-97450a9c89d5
e255e01e-c460-4c1b-8e8d-ac7badd16946	t	2026-05-26 21:15:51.692346+00	💌	Davetiye	davetiye	50	Özel gün davetiyeleri	2026-05-26 21:15:51.692346+00	ad720c31-b92b-4bba-8521-7a03c93324ca
99e901fd-eef9-44b6-874e-0f32eaf90b59	f	2026-05-26 21:15:51.692346+00	🎌	Roll-Up	roll-up	60	Roll-up ve display ürünleri	2026-05-26 21:23:02.386238+00	ad720c31-b92b-4bba-8521-7a03c93324ca
8e970dec-7525-4def-9d67-5c2ec3e74614	t	2026-05-27 17:19:21.717588+00	🚩	Masa Bayrağı	masa-bayragi	2	\N	2026-05-27 17:19:21.717588+00	13ff22a1-1856-4857-958d-b2244a2b2d40
94198885-8d1c-4989-bb5e-baa72b272522	t	2026-05-27 17:19:21.717588+00	🚩	Gönder Bayrağı	gonder-bayragi	3	\N	2026-05-27 17:19:21.717588+00	13ff22a1-1856-4857-958d-b2244a2b2d40
77f4b716-1b7c-471d-99cc-3b8203baae71	t	2026-05-27 17:19:21.717588+00	🚩	Gümüş Makam Bayrağı	gumus-makam-bayragi	1	\N	2026-05-27 17:19:21.717588+00	13ff22a1-1856-4857-958d-b2244a2b2d40
5e1630ff-a606-4e70-a6a3-a5ddd7827f25	t	2026-05-27 17:19:21.717588+00	🚩	Yelken Bayrak	yelken-bayrak	4	\N	2026-05-27 17:19:21.717588+00	13ff22a1-1856-4857-958d-b2244a2b2d40
41b8301e-26b2-4f11-9565-4d189c9e48e5	t	2026-05-27 17:19:21.717588+00	🚩	Kırlangıç Bayrak	kirlangic-bayrak	5	\N	2026-05-27 17:19:21.717588+00	13ff22a1-1856-4857-958d-b2244a2b2d40
53be3dba-d3cc-400f-9057-fe78598bfdf8	t	2026-05-27 17:19:21.717588+00	🏢	Kaşeler	kaseler	15	\N	2026-05-27 17:19:21.717588+00	ad720c31-b92b-4bba-8521-7a03c93324ca
663afecb-49b4-4b9e-a2fb-587e3a1acd85	t	2026-05-27 17:19:21.717588+00	🏢	İş Güvenlik Levhaları	is-guvenlik-levhalari	13	\N	2026-05-27 17:19:21.717588+00	ad720c31-b92b-4bba-8521-7a03c93324ca
059101b4-71ed-43b6-945d-992a1548e25e	t	2026-05-27 17:19:21.717588+00	🏢	Tabelalar	tabelalar	16	\N	2026-05-27 17:19:21.717588+00	ad720c31-b92b-4bba-8521-7a03c93324ca
c425677e-d5b9-40f3-8287-9b69c180ae63	t	2026-05-27 17:19:21.717588+00	🏢	Display Ürünleri	display-urunleri	12	\N	2026-05-27 17:19:21.717588+00	ad720c31-b92b-4bba-8521-7a03c93324ca
fdaef4e4-6b9b-40ed-b08b-55aa481e66be	t	2026-05-27 17:19:21.717588+00	🏢	Kapı İsimlikleri	kapi-isimlikleri	14	\N	2026-05-27 17:19:21.717588+00	ad720c31-b92b-4bba-8521-7a03c93324ca
2e42287e-4ef4-4a49-a56a-809f437dbf76	t	2026-05-27 17:19:21.717588+00	🏢	Dubalar	dubalar	10	\N	2026-05-27 17:19:21.717588+00	ad720c31-b92b-4bba-8521-7a03c93324ca
b5523675-7142-47c5-a2f0-3de89eb83f4b	t	2026-05-27 17:19:21.717588+00	🏢	Standlar	standlar	11	\N	2026-05-27 17:19:21.717588+00	ad720c31-b92b-4bba-8521-7a03c93324ca
c7384df9-6976-47f1-bc46-2c11a8c2f3be	t	2026-05-27 17:19:21.717588+00	📋	Çantalar	cantalar	4	\N	2026-05-27 17:19:21.717588+00	306c9f83-afcf-42a8-a75c-ca0c054e0e0c
ebb14f11-2ee7-40cd-845e-220a5c00ce27	t	2026-05-27 17:19:21.717588+00	📋	Zarflar	zarflar	2	\N	2026-05-27 17:19:21.717588+00	306c9f83-afcf-42a8-a75c-ca0c054e0e0c
6ae8201c-1f24-4964-a0cd-7cffff08b4be	t	2026-05-27 17:19:21.717588+00	📋	Form - Makbuz	form-makbuz	5	\N	2026-05-27 17:19:21.717588+00	306c9f83-afcf-42a8-a75c-ca0c054e0e0c
cca4ae1e-06bb-4a0b-8dae-7251d5f6acba	t	2026-05-27 17:19:21.717588+00	📋	Bloknotlar	bloknotlar	3	\N	2026-05-27 17:19:21.717588+00	306c9f83-afcf-42a8-a75c-ca0c054e0e0c
4e8d7f6d-435d-4983-a5ce-00b7c97897d0	t	2026-05-27 17:19:21.717588+00	📋	Etiketler	etiketler	1	\N	2026-05-27 17:19:21.717588+00	306c9f83-afcf-42a8-a75c-ca0c054e0e0c
babb3609-cc5f-4793-9cfe-cb7ae050c88b	t	2026-05-27 17:19:21.717588+00	🎁	Tişörtler	tisortler	17	\N	2026-05-27 17:19:21.717588+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
de83425b-50b7-4300-90cf-33b3920830da	t	2026-05-27 17:19:21.717588+00	🎁	Masa İsimlikleri	masa-isimlikleri	19	\N	2026-05-27 17:19:21.717588+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
ad5b0099-d0f2-4539-b5e3-2b5b93f038ac	t	2026-05-27 17:19:21.717588+00	🎁	Çakmaklar	cakmaklar	12	\N	2026-05-27 17:19:21.717588+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
028057ca-8cc9-4e10-8596-33b9d1abbd2c	t	2026-05-27 17:19:21.717588+00	🎁	Plaketler	plaketler	16	\N	2026-05-27 17:19:21.717588+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
ab5eef05-1f59-4731-a7fb-204367564c7b	t	2026-05-27 17:19:21.717588+00	🎁	Ajandalar	ajandalar	10	\N	2026-05-27 17:19:21.717588+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
127d2e22-4919-4b6f-91b9-06ffe8340155	t	2026-05-27 17:19:21.717588+00	🎁	Termoslar	termoslar	13	\N	2026-05-27 17:19:21.717588+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
a8564917-516e-427b-97e4-0ef08f36fc51	t	2026-05-27 17:19:21.717588+00	🎁	Powerbank	powerbank	20	\N	2026-05-27 17:19:21.717588+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
114983ba-6bba-4799-8b56-5f5945577ae5	t	2026-05-27 17:19:21.717588+00	🎁	Promosyon Paketleri	promosyon-paketleri	18	\N	2026-05-27 17:19:21.717588+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
6e897f17-c170-48cd-b49b-84a65ffed17c	t	2026-05-27 17:19:21.717588+00	🎁	Kalemler	kalemler	11	\N	2026-05-27 17:19:21.717588+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
1e2d4cb1-bbc7-4b02-a345-0d782fd78426	t	2026-05-27 17:19:21.717588+00	🎁	Anahtarlıklar	anahtarliklar	14	\N	2026-05-27 17:19:21.717588+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
555a2f06-5b50-46fd-96df-89b4b1227b60	t	2026-05-27 17:19:21.717588+00	🎁	VIP Setler	vip-setler	15	\N	2026-05-27 17:19:21.717588+00	a1ff67a1-e0b2-4a5d-81d7-dfd31cab7125
98f31fd9-b779-408e-b9fa-64a51a9d1215	t	2026-05-27 17:19:21.717588+00	🖨️	Folyolar	folyolar	2	\N	2026-05-27 17:19:21.717588+00	8fd8fde8-3148-4260-8ca1-97450a9c89d5
5d6ce25d-22eb-4fbe-aadd-75f49734c336	t	2026-05-27 17:19:21.717588+00	🖨️	Mat Folyo	mat-folyo	4	\N	2026-05-27 17:19:21.717588+00	8fd8fde8-3148-4260-8ca1-97450a9c89d5
8f6217d7-c331-4112-92b1-34ebd309dcd2	t	2026-05-27 17:19:21.717588+00	🖨️	Viniller	viniller	1	\N	2026-05-27 17:19:21.717588+00	8fd8fde8-3148-4260-8ca1-97450a9c89d5
216aa095-a2b2-4287-8bf0-3aa48cd1befb	t	2026-05-27 17:19:21.717588+00	🖨️	Şeffaf Folyo	seffaf-folyo	7	\N	2026-05-27 17:19:21.717588+00	8fd8fde8-3148-4260-8ca1-97450a9c89d5
a728f015-00b8-499f-a420-e2ce3dd0ff58	t	2026-05-27 17:19:21.717588+00	🖨️	Vinil Branda Afişler	vinil-branda-afisler	3	\N	2026-05-27 17:19:21.717588+00	8fd8fde8-3148-4260-8ca1-97450a9c89d5
0dc83881-acd8-4a59-973a-a26b9188fe45	t	2026-05-27 17:19:21.717588+00	🖨️	Mesh Delikli Vinil	mesh-delikli-vinil	5	\N	2026-05-27 17:19:21.717588+00	8fd8fde8-3148-4260-8ca1-97450a9c89d5
d5cc2218-1def-4e30-934a-f589b53a5d62	t	2026-05-27 17:19:21.717588+00	🖨️	Işıklı Vinil	isikli-vinil	6	\N	2026-05-27 17:19:21.717588+00	8fd8fde8-3148-4260-8ca1-97450a9c89d5
91f6da40-6558-488f-98ca-a412905f8578	t	2026-05-27 17:19:21.717588+00	🖼️	MDF Tablo	mdf-tablo	11	\N	2026-05-27 17:19:21.717588+00	0d53bffa-cf96-4aa0-8e21-2278418ad97a
dab555d3-3224-4863-9d1d-e4725f8600c8	t	2026-05-27 17:19:21.717588+00	🖼️	Dekoratif Tablolar	dekoratif-tablo	13	\N	2026-05-27 17:19:21.717588+00	0d53bffa-cf96-4aa0-8e21-2278418ad97a
939e7449-2abc-4920-9da0-8893d5645c78	t	2026-05-27 17:19:21.717588+00	🖼️	Atatürk Tabloları	ataturk-tablo	12	\N	2026-05-27 17:19:21.717588+00	0d53bffa-cf96-4aa0-8e21-2278418ad97a
f2272869-e576-4a72-b568-16875b3bb6d6	t	2026-05-27 17:19:21.717588+00	🏠	Emlak Tabelası	emlak-tabelasi	4	\N	2026-05-27 17:19:21.717588+00	b6da22c1-b04e-4dd8-bc19-7951e32f24e0
27d430ad-e9fb-4554-bbee-2b3551d059a7	t	2026-05-27 17:19:21.717588+00	🏠	Mesh Delikli Vinil Emlak Afişi	mesh-emlak-afisi	1	\N	2026-05-27 17:19:21.717588+00	b6da22c1-b04e-4dd8-bc19-7951e32f24e0
0e861770-c504-4b83-a1c6-7e2daed1c2e8	t	2026-05-27 17:19:21.717588+00	🏠	Vinil Branda Emlak Afişi	vinil-branda-emlak-afisi	2	\N	2026-05-27 17:19:21.717588+00	b6da22c1-b04e-4dd8-bc19-7951e32f24e0
c0f86c4c-4a3b-494a-aca2-7e2267b2a8ba	t	2026-05-27 17:19:21.717588+00	🏠	Emlak Kağıt Afişi	emlak-kagit-afisi	3	\N	2026-05-27 17:19:21.717588+00	b6da22c1-b04e-4dd8-bc19-7951e32f24e0
7119c09d-a9fd-41f5-a3b8-0ecc8eeed95e	t	2026-06-01 14:48:15.30916+00	\N	Standart Kartvizit	standart-kartvizit	0	\N	2026-06-01 14:48:15.309672+00	ac16bace-2752-493d-aeb6-3c6f9977a059
\.


--
-- Data for Name: catalog_order_files; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.catalog_order_files (id, created_at, file_size, mime_type, original_name, page_count, page_warning, storage_path, stored_filename, order_id) FROM stdin;
13dff614-a7ae-4c5d-b571-ebe9acd33f01	2026-05-26 08:40:15.485052+00	2888	application/pdf	Multibus_Fiyat_Teklifi.pdf	1	f	customer-designs/CAT-A2432A9A/e7852b2c-ea28-4b9a-bf2c-aaaacb523f24.pdf	e7852b2c-ea28-4b9a-bf2c-aaaacb523f24.pdf	ca56ffb3-fe9c-4c09-8160-c1c05dcb8146
c7e2da0b-243f-41e0-ac84-8fe1fdfa2fad	2026-05-26 08:40:22.456309+00	24209	application/pdf	Multibus_Fiyat_Teklifi_TR_Son.pdf	1	f	customer-designs/CAT-A2432A9A/c7973141-2dbb-4685-8e8a-0ef69e0a86e2.pdf	c7973141-2dbb-4685-8e8a-0ef69e0a86e2.pdf	ca56ffb3-fe9c-4c09-8160-c1c05dcb8146
\.


--
-- Data for Name: catalog_order_items; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.catalog_order_items (id, attributes_snapshot, category_id, category_name, category_slug, main_image_url, price_tl, price_usd, product_id, product_name, product_slug, tier_id, tier_qty, order_id) FROM stdin;
ae32d64c-3ffc-4300-a4ca-5b38232677d0	Kagit: 350g Mat Kuse	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	https://images.unsplash.com/photo-1572025442646-866d16c84a54?w=800	720.00	16.00	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	d5b3450a-1f59-4a5d-9c39-6d0be06f8999	500	b10a38f7-3ba2-4d14-a9a7-c1bccc9d874f
48973cd4-f39e-48ed-b13f-70b50c6481e8	Kagit: 350g Mat Kuse	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	https://images.unsplash.com/photo-1572025442646-866d16c84a54?w=800	900.00	20.00	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	62638e0d-6076-4622-a0bf-1a2d60170f07	1000	648c0957-670a-42bc-83d8-1f329933f5e2
837d579e-a715-4cba-b2d7-febe92e52511	Kagit: 350g Mat Kuse	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	https://images.unsplash.com/photo-1572025442646-866d16c84a54?w=800	720.00	16.00	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	d5b3450a-1f59-4a5d-9c39-6d0be06f8999	500	648c0957-670a-42bc-83d8-1f329933f5e2
f74b2182-533e-4cc2-8a92-346857ccb392	Kagit: 350g Mat Kuse	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	https://images.unsplash.com/photo-1572025442646-866d16c84a54?w=800	720.00	16.00	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	d5b3450a-1f59-4a5d-9c39-6d0be06f8999	500	0829cae9-467c-471a-ba3b-d8b4fb4af4c6
4e19da45-e40f-4e42-a5fd-b0da3c1be4be	Kagit: 400g Mat Kuse	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	https://images.unsplash.com/photo-1572025442646-866d16c84a54?w=800	720.00	16.00	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	d5b3450a-1f59-4a5d-9c39-6d0be06f8999	500	a9ded8d8-145a-46b9-adab-d9b045251516
ccf28604-7879-4b88-8a73-360221d7cfd2	Kagit: 350g Mat Kuse	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	https://images.unsplash.com/photo-1572025442646-866d16c84a54?w=800	720.00	16.00	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	d5b3450a-1f59-4a5d-9c39-6d0be06f8999	500	9527a82a-d756-4994-b219-2a207eb53f54
dae4bc30-e4cd-4060-982d-8aa65b992fca	Kagit: 350g Mat Kuse	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	https://d1x3eomzsc6lfz.cloudfront.net/baskiadam/images/products_gallery_images/standart-kartvizit90.jpg	720.00	16.00	b0c65d46-70f3-4e75-aef3-6e605818c174	kartvizit	kartvizit	6361cfbb-fca9-4cc6-9557-83d98c7016cf	100	c50870f7-d311-4100-94c1-581dfae8bfb5
fd253797-46f0-4c54-af0e-a04e9eb5cd1f	Kagit: 400g Mat Kuse	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	https://images.unsplash.com/photo-1572025442646-866d16c84a54?w=800	1620.00	36.00	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	5abd309c-096b-4128-9bab-8dc0437d13b2	2000	f3ae956a-2478-4cd7-bcc2-5b7d88b88e8c
9a0a8f76-3ad4-43d6-8d87-3df167eaf994	Kagit: 350g Mat Kuse	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	https://d1x3eomzsc6lfz.cloudfront.net/baskiadam/images/products_gallery_images/standart-kartvizit90.jpg	720.00	16.00	b0c65d46-70f3-4e75-aef3-6e605818c174	kartvizit	kartvizit	9f15a889-1e4a-4a1a-a2e3-080aade7f633	100	7b75311a-0db0-46b5-9883-1874b438f3f1
2234580f-c0df-4cd7-8f9b-adffc9c932fc	Kagit: 350g Mat Kuse	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	https://d1x3eomzsc6lfz.cloudfront.net/baskiadam/images/products_gallery_images/standart-kartvizit90.jpg	720.00	16.00	b0c65d46-70f3-4e75-aef3-6e605818c174	kartvizit	kartvizit	9f15a889-1e4a-4a1a-a2e3-080aade7f633	100	7b75311a-0db0-46b5-9883-1874b438f3f1
9836ca0b-55cc-4c34-bfd0-6012b479ab71	Kagit: 350g Mat Kuse	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	https://d1x3eomzsc6lfz.cloudfront.net/baskiadam/images/products_gallery_images/standart-kartvizit90.jpg	720.00	16.00	b0c65d46-70f3-4e75-aef3-6e605818c174	kartvizit	kartvizit	9f15a889-1e4a-4a1a-a2e3-080aade7f633	100	ca56ffb3-fe9c-4c09-8160-c1c05dcb8146
4fd35ae5-a3e6-4ad7-8bc0-6a0fab3df9c9	Kagit: 350g Mat Kuse	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	https://d1x3eomzsc6lfz.cloudfront.net/baskiadam/images/products_gallery_images/standart-kartvizit90.jpg	720.00	16.00	b0c65d46-70f3-4e75-aef3-6e605818c174	kartvizit	kartvizit	9f15a889-1e4a-4a1a-a2e3-080aade7f633	100	ca56ffb3-fe9c-4c09-8160-c1c05dcb8146
bbfdecb9-582a-4ef1-8d02-1eaf399ea3f5	Kagit: 400g Mat Kuse	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	https://images.unsplash.com/photo-1572025442646-866d16c84a54?w=800	720.00	16.00	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	d5b3450a-1f59-4a5d-9c39-6d0be06f8999	500	ca56ffb3-fe9c-4c09-8160-c1c05dcb8146
51e9ce9b-c8a2-4590-be14-1c1fe528ef54	Kagit: 350g Mat Kuse	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	https://d1x3eomzsc6lfz.cloudfront.net/baskiadam/images/products_gallery_images/standart-kartvizit90.jpg	720.00	16.00	b0c65d46-70f3-4e75-aef3-6e605818c174	kartvizit	kartvizit	9f15a889-1e4a-4a1a-a2e3-080aade7f633	100	ca56ffb3-fe9c-4c09-8160-c1c05dcb8146
bb1ca57f-e575-43a3-ac80-4670b03146bb	\N	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	https://images.unsplash.com/photo-1572025442646-866d16c84a54?w=800	2790.00	62.00	61ef4d08-595d-43ae-9b9a-7935b1e9c28c	Kraft Kartvizit	kraft-kartvizit	028a5593-d9ae-456a-bed4-adad0213fa9e	2500	b582379b-e9e0-49d0-a440-89feb4254341
0be264f3-5463-44b7-9eb6-b4336bb2dc38	Baskı Yönü: Çift Yön Baskı; Ebat: 8.5 x 5.5 cm (Yatay); Selefon: Mat Selefon; Kagit: 350g Mat Kuse	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	http://localhost:8080/uploads/product/dca88e62-2ddf-4f59-8ec1-634a629f618b.png	720.00	16.00	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	0536f7cc-a60a-462e-bb8c-ce966713c797	500	623f46bd-c2b4-45af-83b5-3c0404ec1bb4
3f1e81a4-0c9e-403a-b884-5ac17d2991ac	Ebat: 9 x 5 cm; Baskı Yönü: Çift Yön Baskı; Kagit: 400g Parlak Kuşe; Selefon: Mat Selefon	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	http://localhost:8080/uploads/product/dca88e62-2ddf-4f59-8ec1-634a629f618b.png	1620.00	36.00	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	3e1eb656-54b0-4ded-bf9c-94e72106f69a	2000	66a435be-4bd3-4839-850f-d89a788ceecb
a66f1d07-e41e-4fd8-9b17-1089ad9f11a7	Ebat: 5.5 x 8.5 cm (Standart); Baskı Yönü: Tek Yön Baskı; Kagit: 350g Mat Kuse; Selefon: Selefon Yok	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	http://localhost:8080/uploads/product/dca88e62-2ddf-4f59-8ec1-634a629f618b.png	720.00	16.00	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	0536f7cc-a60a-462e-bb8c-ce966713c797	500	da27f6ec-5486-4d30-b600-2a6cd635a86a
73570fe3-e618-4d39-89e7-b81b10f2a2b6	Ebat: 5.5 x 8.5 cm (Standart); Baskı Yönü: Tek Yön Baskı; Kagit: 350g Mat Kuse; Selefon: Selefon Yok	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	http://localhost:8080/uploads/product/dca88e62-2ddf-4f59-8ec1-634a629f618b.png	720.00	16.00	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	0536f7cc-a60a-462e-bb8c-ce966713c797	500	6feabc04-13d7-4cd0-b754-21319ce35db2
f39cef70-4110-4452-bec9-a04b8e2ef703	Kagit: 350g Mat Kuse; Baskı Yönü: Tek Yön Baskı; Ebat: 5.5 x 8.5 cm (Standart)	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	http://localhost:8080/uploads/product/dca88e62-2ddf-4f59-8ec1-634a629f618b.png	864.00	16.00	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	0536f7cc-a60a-462e-bb8c-ce966713c797	500	478370d2-e437-447c-aa62-d0c91ff6cc62
6993606b-e88f-444c-a171-f65d9fda3848	Ebat: 5.5 x 8.5 cm (Standart); Baskı Yönü: Tek Yön Baskı; Kagit: 350g Mat Kuse; Selefon: Selefon Yok	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	http://localhost:8080/uploads/product/dca88e62-2ddf-4f59-8ec1-634a629f618b.png	864.00	16.00	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	0536f7cc-a60a-462e-bb8c-ce966713c797	500	38115a26-f74d-4ead-80d5-af68160b78d6
0bec3fb7-b197-4246-956b-e47361341dc6	Ebat: 5.5 x 8.5 cm (Standart); Baskı Yönü: Tek Yön Baskı; Kagit: 350g Mat Kuse; Selefon: Mat Selefon	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	http://localhost:8080/uploads/product/dca88e62-2ddf-4f59-8ec1-634a629f618b.png	993.60	18.40	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	0536f7cc-a60a-462e-bb8c-ce966713c797	500	a051da28-bbe5-4368-bfd8-045d88fba3c7
70a98576-6045-4f63-81d9-1e28e8d06535	Baskı Yönü: Tek Yön Baskı; Kagit: 350g Mat Kuse; Selefon: Selefon Yok; Ebat: 5.5 x 8.5 cm (Standart)	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	http://localhost:8080/uploads/product/dca88e62-2ddf-4f59-8ec1-634a629f618b.png	1080.00	20.00	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	47d30155-cb8c-4e78-a5bb-530d5f70d5eb	1000	a051da28-bbe5-4368-bfd8-045d88fba3c7
ddb0820f-f9f8-402f-acc5-d9c597980b3b	Ebat: 5.5 x 8.5 cm (Standart); Baskı Yönü: Tek Yön Baskı; Kagit: 400g Mat Kuse; Selefon: Selefon Yok	ac16bace-2752-493d-aeb6-3c6f9977a059	Kartvizitler	kartvizit	http://localhost:8080/uploads/product/dca88e62-2ddf-4f59-8ec1-634a629f618b.png	950.40	17.60	db2838c4-395b-4b8f-930d-d278415c67b1	Standart Kartvizit	standart-kartvizit	0536f7cc-a60a-462e-bb8c-ce966713c797	500	a051da28-bbe5-4368-bfd8-045d88fba3c7
447ada38-fd0e-4956-8b33-6f218a9f682d	Ebat: 5.5 x 8.5 cm (Standart); Baskı Yönü: Çift Yön Baskı; Kagit: 400g Mat Kuse; Selefon: Parlak Selefon	7119c09d-a9fd-41f5-a3b8-0ecc8eeed95e	Standart Kartvizit	standart-kartvizit	http://localhost:8080/uploads/product/22b0c35c-ac6a-46b3-8a18-631b1c93d794.png	2295.22	42.50	c92b4f85-2ca3-4e87-8720-d86b68f5b93f	Standart Kartvizit	standart-kartvizit	a694ce5b-be55-4c46-8f1f-a492da382868	200	ff9a8746-42b9-4692-a760-ee19e39d87a5
\.


--
-- Data for Name: catalog_orders; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.catalog_orders (id, city, created_at, customer_address, customer_email, customer_name, customer_phone, district, notes, order_number, status, subtotal_usd, total_tl, updated_at, usd_kur_at_order, user_id, iyzico_conversation_data, iyzico_payment_id, payment_status, coupon_code, discount_amount_tl, subtotal_tl) FROM stdin;
b10a38f7-3ba2-4d14-a9a7-c1bccc9d874f	İstanbul	2026-05-25 19:27:47.529824+00	Uğur Mumcu\nŞeyh Şamil Cd. No:15	il@gmail.com	seckin ilbars	05530214776	Kartal	sfsdfsd	CAT-B85BED23	PENDING	16.00	720.00	2026-05-25 19:27:47.567952+00	45.0000	\N	\N	\N	\N	\N	\N	\N
648c0957-670a-42bc-83d8-1f329933f5e2	İstanbul	2026-05-25 19:50:03.210845+00	Uğur Mumcu\nŞeyh Şamil Cd. No:15	\N	seckin ilbars	05530214776	Kartal	\N	CAT-09A1A97D	PENDING	36.00	1620.00	2026-05-25 19:50:03.272409+00	45.0000	\N	\N	\N	PENDING	\N	\N	\N
0829cae9-467c-471a-ba3b-d8b4fb4af4c6	İstanbul	2026-05-25 19:53:45.272815+00	Uğur Mumcu\nŞeyh Şamil Cd. No:15	\N	seckin ilbars	054530214776	Kartal	\N	CAT-4FAA79BD	PENDING	16.00	720.00	2026-05-25 19:53:45.290843+00	45.0000	\N	\N	\N	PENDING	\N	\N	\N
a9ded8d8-145a-46b9-adab-d9b045251516	İstanbul	2026-05-25 19:56:11.739597+00	Uğur Mumcu\nŞeyh Şamil Cd. No:15	\N	seckin ilbars	05530214776	Kartal	\N	CAT-21CA9803	PENDING	16.00	720.00	2026-05-25 19:56:11.76203+00	45.0000	\N	\N	\N	PENDING	\N	\N	\N
9527a82a-d756-4994-b219-2a207eb53f54	İstanbul	2026-05-25 19:58:58.67681+00	Uğur Mumcu\nŞeyh Şamil Cd. No:15	\N	seckin ilbars	05530214776	Kartal	\N	CAT-89BA17EA	PENDING	16.00	720.00	2026-05-26 06:47:24.506695+00	45.0000	\N	\N	\N	PROCESSING	\N	\N	\N
c50870f7-d311-4100-94c1-581dfae8bfb5	İstanbul	2026-05-26 06:56:05.228404+00	Uğur Mumcu\nŞeyh Şamil Cd. No:15	\N	seckin ilbars	05530214776	Kartal	ffsdf	CAT-098E3522	PENDING	16.00	720.00	2026-05-26 06:56:05.259901+00	45.0000	\N	\N	\N	PENDING	\N	\N	\N
f3ae956a-2478-4cd7-bcc2-5b7d88b88e8c	İstanbul	2026-05-26 07:17:04.85736+00	Uğur Mumcu\nŞeyh Şamil Cd. No:15	\N	seckin ilbars	05530214776	Kartal	\N	CAT-B022BCFB	PENDING	36.00	1620.00	2026-05-26 07:17:04.871729+00	45.0000	\N	\N	\N	PENDING	\N	\N	\N
7b75311a-0db0-46b5-9883-1874b438f3f1	İstanbul	2026-05-26 08:34:43.225626+00	Uğur Mumcu\nŞeyh Şamil Cd. No:15	\N	seckin ilbars	05530214776	Kartal	\N	CAT-5512C96C	PENDING	32.00	1440.00	2026-05-26 08:34:43.327751+00	45.0000	\N	\N	\N	PENDING	\N	\N	\N
ff9a8746-42b9-4692-a760-ee19e39d87a5	Istanbul	2026-06-02 06:03:26.538788+00	Ataturk Cad. No:1	admin@baski.com	Admin Test	05530214776	Kadikoy	asdasdasdasdas	CAT-2C8EBAA7	PENDING	42.50	2295.22	2026-06-02 06:03:26.563128+00	45.0000	\N	\N	\N	PENDING	\N	0.00	\N
ca56ffb3-fe9c-4c09-8160-c1c05dcb8146	İstanbul	2026-05-26 08:39:54.183787+00	Uğur Mumcu\nŞeyh Şamil Cd. No:15	\N	seckin ilbars	05530214476	Kartal	Acele	CAT-A2432A9A	PENDING	64.00	2880.00	2026-05-26 08:40:57.245656+00	45.0000	\N	\N	\N	PROCESSING	\N	\N	\N
b582379b-e9e0-49d0-a440-89feb4254341	İstanbul	2026-05-26 21:11:57.243772+00	Uğur Mumcu\nŞeyh Şamil Cd. No:15	\N	seckin ilbars	05530214776	Kartal	dsfsdfsdfs	CAT-CB0EA54F	PENDING	62.00	2790.00	2026-05-26 21:11:57.278445+00	45.0000	\N	\N	\N	PENDING	\N	\N	\N
623f46bd-c2b4-45af-83b5-3c0404ec1bb4	İstanbul	2026-05-27 19:58:05.235618+00	Uğur Mumcu\nŞeyh Şamil Cd. No:15	\N	seckin ilbars	33	Kartal	asdad	CAT-848A66B4	PENDING	16.00	720.00	2026-05-27 19:58:05.292421+00	45.0000	\N	\N	\N	PENDING	\N	\N	\N
66a435be-4bd3-4839-850f-d89a788ceecb	İstanbul	2026-05-29 18:09:09.764576+00	uğurmumcu mahallesi,Şeyhşamil caddesi FeraLife sitesi b/20	\N	seçkin ilbars	05530214776	kartal	\N	CAT-21409773	PENDING	36.00	1620.00	2026-05-29 18:09:09.83735+00	45.0000	\N	\N	\N	PENDING	\N	\N	\N
da27f6ec-5486-4d30-b600-2a6cd635a86a	İstanbul	2026-05-29 18:10:41.003257+00	uğurmumcu mahallesi,Şeyhşamil caddesi FeraLife sitesi b/20	\N	seçkin ilbars	05530214776	kartal	\N	CAT-7C30B7B4	PENDING	16.00	720.00	2026-05-29 18:10:41.019079+00	45.0000	\N	\N	\N	PENDING	\N	\N	\N
6feabc04-13d7-4cd0-b754-21319ce35db2	İstanbul	2026-05-29 18:11:53.356652+00	uğurmumcu mahallesi,Şeyhşamil caddesi FeraLife sitesi b/20	\N	seçkin ilbars	05530214776	kartal	\N	CAT-6AF1C83A	PENDING	16.00	720.00	2026-05-29 18:11:53.369579+00	45.0000	\N	\N	\N	PENDING	\N	\N	\N
478370d2-e437-447c-aa62-d0c91ff6cc62	İstanbul	2026-05-29 18:32:34.779528+00	uğurmumcu mahallesi,Şeyhşamil caddesi FeraLife sitesi b/20	\N	seçkin ilbars	05530214776	kartal	\N	CAT-90E22FC7	PENDING	16.00	864.00	2026-05-29 18:32:34.814311+00	45.0000	\N	\N	\N	PENDING	\N	\N	\N
38115a26-f74d-4ead-80d5-af68160b78d6	İstanbul	2026-05-29 18:33:56.811295+00	Uğur Mumcu\nŞeyh Şamil Cd. No:15	\N	seckin ilbars	05530214776	Kartal	\N	CAT-CF8C39D2	PENDING	16.00	864.00	2026-05-29 18:33:56.838992+00	45.0000	\N	\N	\N	PENDING	\N	\N	\N
a051da28-bbe5-4368-bfd8-045d88fba3c7	İstanbul	2026-05-29 19:19:59.265741+00	Uğur Mumcu\nŞeyh Şamil Cd. No:15	test@gmail.com	test	05530214776	Kartal	\N	CAT-7599AA1E	PENDING	56.00	3024.00	2026-05-29 19:19:59.281373+00	45.0000	\N	\N	\N	PENDING	\N	\N	\N
\.


--
-- Data for Name: catalog_product_attribute_values; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.catalog_product_attribute_values (id, attribute_id, option_id, product_id) FROM stdin;
2c09f25d-4e5b-4af9-9e9a-c006069b3235	72528f26-c08d-4465-9eb7-6053695b93de	422670a0-7918-4f04-b554-43e89f2a4615	5c1c1bdc-91f1-4ddf-96b6-a3886c37e73c
d1b82380-b5ec-4f1f-a18c-a8c353cc5ae1	72528f26-c08d-4465-9eb7-6053695b93de	422670a0-7918-4f04-b554-43e89f2a4615	27217955-03b8-4a3f-b57e-bbff60ac1328
d64f3af6-c401-44c3-b69b-826cce6dd5d0	72528f26-c08d-4465-9eb7-6053695b93de	422670a0-7918-4f04-b554-43e89f2a4615	d32e70ec-9b9d-4209-b8a0-3dc47e66f473
07a70758-35f4-46fc-b82d-137686c3d434	72528f26-c08d-4465-9eb7-6053695b93de	422670a0-7918-4f04-b554-43e89f2a4615	0f113e07-2bd1-4bf3-90a4-f1bcfa491e72
6e95c878-a9da-4f31-bd3f-eaf034ebf20d	72528f26-c08d-4465-9eb7-6053695b93de	422670a0-7918-4f04-b554-43e89f2a4615	ff92cf7d-b1aa-4baa-831f-861da7a21015
e819896f-9a6d-439e-bcca-c760ae554e42	72528f26-c08d-4465-9eb7-6053695b93de	422670a0-7918-4f04-b554-43e89f2a4615	dbeb2ecb-012f-43f5-a5dc-6523433db614
18ad655a-389d-40a9-b80c-35453c18ccfe	72528f26-c08d-4465-9eb7-6053695b93de	422670a0-7918-4f04-b554-43e89f2a4615	d6b6b2bd-36dd-45b8-918b-2af953a8c2b3
ba433261-55fc-4372-a56f-9cbab180338d	72528f26-c08d-4465-9eb7-6053695b93de	422670a0-7918-4f04-b554-43e89f2a4615	fbabc552-7ac1-4d48-8f05-8dbd9841a931
06bfa1a3-20e5-4dfe-9ac8-436ad51348c7	72528f26-c08d-4465-9eb7-6053695b93de	422670a0-7918-4f04-b554-43e89f2a4615	7a991bcf-1c19-4725-a5c0-99cad0f76515
155eb0d9-cf65-415c-8a78-32ce913af569	72528f26-c08d-4465-9eb7-6053695b93de	422670a0-7918-4f04-b554-43e89f2a4615	93caa48c-b5d8-4289-96b8-e9b7ac2b8d9a
a83edd32-dcc6-49b5-a42d-b701fb72b86d	72528f26-c08d-4465-9eb7-6053695b93de	422670a0-7918-4f04-b554-43e89f2a4615	555185f3-fecb-44b9-90d1-c883fb78c45b
22bee4f7-683a-43e6-9b7f-226cd2848254	72528f26-c08d-4465-9eb7-6053695b93de	422670a0-7918-4f04-b554-43e89f2a4615	379f4020-e851-4a34-9703-83e337682ca6
4300a4e6-4a1c-419f-9ac3-00d24338ea82	72528f26-c08d-4465-9eb7-6053695b93de	422670a0-7918-4f04-b554-43e89f2a4615	9625f7c7-3058-4960-a42e-3b3648a5d1b4
e7c4a1e7-d0c3-4dce-bf6a-bd4b0f2a1d56	72528f26-c08d-4465-9eb7-6053695b93de	422670a0-7918-4f04-b554-43e89f2a4615	f506e467-1f89-4015-8f23-5f2a2aaca99c
7a7e3d63-13b4-4bde-b52d-465e94b1c1ba	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	1a8bd646-5d18-4514-80a7-8c48cf85946f	f1c87cd7-84f7-4b56-84b5-a5557835e192
0dcfc5cc-7377-4dec-b5fb-2f9787c88b3c	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	1a8bd646-5d18-4514-80a7-8c48cf85946f	f1480084-32f7-4cd0-9a1f-a66fbcd30ea0
5ced8944-6956-4e64-bade-b68033f22da8	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	1a8bd646-5d18-4514-80a7-8c48cf85946f	33f1bebe-3e36-4fd5-a623-ef092d18a638
11243394-c778-4f39-adf5-a28abe51f014	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	1a8bd646-5d18-4514-80a7-8c48cf85946f	bcc03a57-f819-447d-8fb7-cf3b1b6a74d1
35972a07-6c6e-4aed-80db-7f2e8a334d48	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	1a8bd646-5d18-4514-80a7-8c48cf85946f	889bc829-dcf8-4f8d-aba5-88ac06037a39
43402097-8f53-4be4-855f-badc120f5ee6	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	1a8bd646-5d18-4514-80a7-8c48cf85946f	99420ca6-a8ff-4aa2-bcde-03181677ba26
a2f553dc-9072-4e80-a847-853e7ec1680c	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	1a8bd646-5d18-4514-80a7-8c48cf85946f	d2461563-bae2-493c-9aec-9581f6e937a3
34b5d5ee-b61a-4a9d-8c9e-c5bde786b0fe	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	1a8bd646-5d18-4514-80a7-8c48cf85946f	b8ac33a9-1beb-4b18-98a8-b45d39259271
e7f8ae8c-78f8-4b84-8cdf-e27bed47bede	72528f26-c08d-4465-9eb7-6053695b93de	d306e709-4634-49e6-9b33-d523d414b888	5c1c1bdc-91f1-4ddf-96b6-a3886c37e73c
6580d506-c60e-42ab-879b-ac1eacd60605	72528f26-c08d-4465-9eb7-6053695b93de	d306e709-4634-49e6-9b33-d523d414b888	27217955-03b8-4a3f-b57e-bbff60ac1328
d7e713c6-2982-4bdf-a7e3-734b341dd944	72528f26-c08d-4465-9eb7-6053695b93de	d306e709-4634-49e6-9b33-d523d414b888	d32e70ec-9b9d-4209-b8a0-3dc47e66f473
ebb57776-8924-47ca-bd69-c9d9c5609d97	72528f26-c08d-4465-9eb7-6053695b93de	d306e709-4634-49e6-9b33-d523d414b888	0f113e07-2bd1-4bf3-90a4-f1bcfa491e72
a1b8e6ca-913e-4c7f-bfe5-3cd279fa52f9	72528f26-c08d-4465-9eb7-6053695b93de	d306e709-4634-49e6-9b33-d523d414b888	ff92cf7d-b1aa-4baa-831f-861da7a21015
38f15212-67ed-44ea-bc1a-733516a2050d	72528f26-c08d-4465-9eb7-6053695b93de	d306e709-4634-49e6-9b33-d523d414b888	dbeb2ecb-012f-43f5-a5dc-6523433db614
0a4897d2-a8b4-461b-abce-a80ffb401bb7	72528f26-c08d-4465-9eb7-6053695b93de	d306e709-4634-49e6-9b33-d523d414b888	d6b6b2bd-36dd-45b8-918b-2af953a8c2b3
05461504-27bf-4029-893d-a10610c8d3d3	72528f26-c08d-4465-9eb7-6053695b93de	d306e709-4634-49e6-9b33-d523d414b888	fbabc552-7ac1-4d48-8f05-8dbd9841a931
11241161-ebdf-4881-aa68-d334e6fc6b04	72528f26-c08d-4465-9eb7-6053695b93de	d306e709-4634-49e6-9b33-d523d414b888	7a991bcf-1c19-4725-a5c0-99cad0f76515
07beee40-2267-42da-a8ad-d8a150b17ab2	72528f26-c08d-4465-9eb7-6053695b93de	d306e709-4634-49e6-9b33-d523d414b888	93caa48c-b5d8-4289-96b8-e9b7ac2b8d9a
cfb82e56-7f99-42ab-8a0b-5733b487d0e5	72528f26-c08d-4465-9eb7-6053695b93de	d306e709-4634-49e6-9b33-d523d414b888	555185f3-fecb-44b9-90d1-c883fb78c45b
9c5a096e-4652-4a23-accd-6e6ec36087fd	72528f26-c08d-4465-9eb7-6053695b93de	d306e709-4634-49e6-9b33-d523d414b888	379f4020-e851-4a34-9703-83e337682ca6
e1fff724-6c92-44c2-928c-78ec03de91d9	72528f26-c08d-4465-9eb7-6053695b93de	d306e709-4634-49e6-9b33-d523d414b888	9625f7c7-3058-4960-a42e-3b3648a5d1b4
a8eb12a8-3d46-4c63-88b1-bb665c3065ff	72528f26-c08d-4465-9eb7-6053695b93de	d306e709-4634-49e6-9b33-d523d414b888	f506e467-1f89-4015-8f23-5f2a2aaca99c
e571ea1f-683a-402d-b1dd-3e60c5d224d0	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	9423cb73-cc17-4808-99f9-61e504173f90	f1c87cd7-84f7-4b56-84b5-a5557835e192
374a4441-d605-492c-a19c-8e1d8293f54a	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	9423cb73-cc17-4808-99f9-61e504173f90	f1480084-32f7-4cd0-9a1f-a66fbcd30ea0
aff23a87-825f-4ed9-bb0a-1ae13318ddba	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	9423cb73-cc17-4808-99f9-61e504173f90	33f1bebe-3e36-4fd5-a623-ef092d18a638
8be87d9e-78c6-4cfc-9a40-140981e8d6d8	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	9423cb73-cc17-4808-99f9-61e504173f90	bcc03a57-f819-447d-8fb7-cf3b1b6a74d1
c7834c84-a252-4998-9a38-23b6191b6a53	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	9423cb73-cc17-4808-99f9-61e504173f90	889bc829-dcf8-4f8d-aba5-88ac06037a39
2eee52f9-6f33-49c7-a6cd-d159d24d1e0e	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	9423cb73-cc17-4808-99f9-61e504173f90	99420ca6-a8ff-4aa2-bcde-03181677ba26
c73487af-1fde-48b4-98af-59bf635ce78a	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	9423cb73-cc17-4808-99f9-61e504173f90	d2461563-bae2-493c-9aec-9581f6e937a3
8809d65d-0e42-4321-86d8-7f8f951ad8d2	176daa1a-0316-4fbc-91c9-0f3eb5f29c5f	9423cb73-cc17-4808-99f9-61e504173f90	b8ac33a9-1beb-4b18-98a8-b45d39259271
8d7d4990-1733-4029-8139-6094c26d8dde	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	b69f701e-9fa2-437a-8db4-8d123f76bf94
d1c47fcb-61cb-4dcd-951e-1ad7d2d78dee	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	fbd34467-d662-4ae4-9c25-1c85a478243e
dec4f989-0404-4c17-a940-3c200935fe0e	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	b69f701e-9fa2-437a-8db4-8d123f76bf94
be657e04-0ecd-4deb-9a01-90ed0c5576c3	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	b69f701e-9fa2-437a-8db4-8d123f76bf94
47bf0d90-c06b-4a9f-b66b-fd95aad3442e	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	b69f701e-9fa2-437a-8db4-8d123f76bf94
c2c92acc-d12f-49ac-822c-ef9a32b86cc1	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	fbd34467-d662-4ae4-9c25-1c85a478243e
13353450-e16e-4f67-8f37-5d99ce1e8b14	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	fbd34467-d662-4ae4-9c25-1c85a478243e
1fc2c6ec-edca-47de-bc24-7708c16be86a	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	b69f701e-9fa2-437a-8db4-8d123f76bf94
7342f6be-d061-42ae-986d-547107db7878	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	fbd34467-d662-4ae4-9c25-1c85a478243e
f4a07799-339b-43fc-b39b-bb33d9ba98d1	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	fbd34467-d662-4ae4-9c25-1c85a478243e
aebc52f5-2cd8-428a-a6dc-a0f701740750	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	b69f701e-9fa2-437a-8db4-8d123f76bf94
b0d33bb8-a063-4378-8dc0-fcea60690a17	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	fbd34467-d662-4ae4-9c25-1c85a478243e
8b8d05fc-1307-4186-ac76-aefccc3763d6	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	b69f701e-9fa2-437a-8db4-8d123f76bf94
f8fb4242-da40-47fc-adb6-b92ed3c6e76e	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	fbd34467-d662-4ae4-9c25-1c85a478243e
5a8eb565-42c8-47ac-899e-3e7a6af6d1fb	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	b69f701e-9fa2-437a-8db4-8d123f76bf94
b6f052b5-3f82-4a7b-bbce-98cda7684619	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	b69f701e-9fa2-437a-8db4-8d123f76bf94
ea4fbf96-1697-429d-9dfa-cb011f847fcc	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	b69f701e-9fa2-437a-8db4-8d123f76bf94
20963637-e243-4c40-b3ae-99d3ee88b8f6	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	b69f701e-9fa2-437a-8db4-8d123f76bf94
75af6875-76bf-4fd9-8e8a-0bb35a818a4a	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	b69f701e-9fa2-437a-8db4-8d123f76bf94
3c3146e1-af94-41b9-bc9d-8ed1db322151	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	b69f701e-9fa2-437a-8db4-8d123f76bf94
6094ec36-920d-4b09-a5e9-add10c4a40b1	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	fbd34467-d662-4ae4-9c25-1c85a478243e
d0bc186f-78fe-444d-9d30-aa82e3f7e8e7	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	b69f701e-9fa2-437a-8db4-8d123f76bf94
6d9f3c81-3e72-4663-841b-f5b848cf5748	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	b69f701e-9fa2-437a-8db4-8d123f76bf94
bf575f8e-1689-4686-86c1-b7bf16db1028	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	b69f701e-9fa2-437a-8db4-8d123f76bf94
8325869d-83b5-4ceb-9180-77b3cad1ce15	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	b69f701e-9fa2-437a-8db4-8d123f76bf94
80eb0585-f094-4314-a67d-89346535293b	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	b69f701e-9fa2-437a-8db4-8d123f76bf94
4764d63d-e603-48fa-9ed2-15e9a4fd6ba1	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	fbd34467-d662-4ae4-9c25-1c85a478243e
2080093f-2e35-4a81-a2ee-70f2c9ddc3a4	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	fbd34467-d662-4ae4-9c25-1c85a478243e
2c5943b5-61d6-4b91-bb15-a55991df42b9	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	fbd34467-d662-4ae4-9c25-1c85a478243e
c3ba31a9-01fa-49d0-93fb-1ce913f169cd	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	fbd34467-d662-4ae4-9c25-1c85a478243e
98532d66-cb7f-4d2e-b972-bd763784a160	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	b69f701e-9fa2-437a-8db4-8d123f76bf94
b1a8eabc-2476-4552-8be9-cdf5e3c0c6ce	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	fbd34467-d662-4ae4-9c25-1c85a478243e
e6ff8db9-e628-40a0-9d7a-a0e7cae5ffbf	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	b69f701e-9fa2-437a-8db4-8d123f76bf94
25895013-b233-49d1-b441-670918752c65	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	fbd34467-d662-4ae4-9c25-1c85a478243e
7002a527-828c-4dfe-b66f-119d048630e4	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	fbd34467-d662-4ae4-9c25-1c85a478243e
5e3e90e6-19bf-4646-bdc8-f8644b6118bf	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	fbd34467-d662-4ae4-9c25-1c85a478243e
0aaf569d-30c3-4390-8e99-247745b0b8ad	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	fbd34467-d662-4ae4-9c25-1c85a478243e
323ba7d8-7db2-4be4-9014-42472a1d743f	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	fbd34467-d662-4ae4-9c25-1c85a478243e
0d2c4cdd-378b-4588-bcf0-0b4ad3543221	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	fbd34467-d662-4ae4-9c25-1c85a478243e
93b80d07-1d6a-4934-8e34-03f1bfe59095	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	fbd34467-d662-4ae4-9c25-1c85a478243e
02585a8b-e62d-407f-a0d9-2794bf01729e	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	116655bc-452a-4982-a7d2-f464752a949d
76760f0b-462a-45d3-900f-1d503d3353a6	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	a948564e-d2e4-406a-9f38-1973fc377675
66f60d02-30ca-4917-9e38-1f83b0ad28a4	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
1b715201-1828-47b8-ab55-02ceaab0b588	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
bb107acf-9367-49ab-b7b2-5dac0ac3bf2a	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
61ce7402-15af-4068-aaf9-beb7415cd49c	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	116655bc-452a-4982-a7d2-f464752a949d
5b51ba14-2d86-46c8-84a4-c22be6d7820a	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	1584ae3c-a607-42a3-a906-192ac2fc8bfe
4ffe99dc-ccad-4fd5-817f-38fc3daa773d	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
be4279e5-0024-425e-a29b-35d4b9147ca9	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
193435d5-2282-461e-928f-bf8f41783023	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	1584ae3c-a607-42a3-a906-192ac2fc8bfe
196641e5-9900-4e39-b1f3-9a09f9cad512	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	7bb29c68-6d79-46ba-a662-743f7cd78b1e
7d85121e-2a29-40e6-bb5c-9300d2b73fb0	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	c50afce6-a0bd-4914-b506-50e99a1faaf4
87d4d5a4-3213-4425-8275-41416d96f84a	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	c50afce6-a0bd-4914-b506-50e99a1faaf4
a3e977f1-48a3-4d1b-b106-437ece1f0d85	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	7c838a0f-3cf5-4087-9793-7ac22befe06f
1335f6d4-95b9-4371-90de-d380f7c09076	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	7c838a0f-3cf5-4087-9793-7ac22befe06f
4af8899a-2896-4a2e-8ab2-23e25e95580b	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
0166e075-c09d-41fd-90b8-6ddf65fa430d	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	2d10ce43-3226-43ed-9d4a-1b6781035b09
12907b66-ac9f-46dd-bf48-321992b3074d	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	a948564e-d2e4-406a-9f38-1973fc377675
0ab50809-0892-4d44-b739-5026fb3c0c17	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	7c838a0f-3cf5-4087-9793-7ac22befe06f
e8059921-c3a7-4314-8157-16f3f4afb75d	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	fa656ba8-e4bf-45f5-a776-e50bf8655260
5dc56f15-aff4-4850-ba24-8c963a7bd652	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	1584ae3c-a607-42a3-a906-192ac2fc8bfe
4b2f4967-d1fe-40d9-9247-fb7d99d1ce4e	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	faa09eab-a088-4178-ba38-bdfe1e84f067
3963cba1-613b-4648-a5c1-cf8e5b71effe	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
00380155-a9c8-4b91-8328-4cdfb49d1e1a	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	faa09eab-a088-4178-ba38-bdfe1e84f067
8dcedaa0-e2dd-4e00-ba95-464c47906442	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
b9dbb3b3-a1a8-41a1-af44-8f25c8e35841	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
27aadccd-1830-456c-8156-2bd90df56fa7	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	a948564e-d2e4-406a-9f38-1973fc377675
6d2bfa3f-869b-4b37-8309-d0c41ebb75c5	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	7c838a0f-3cf5-4087-9793-7ac22befe06f
99bb67d3-f87f-432e-a490-06399eca3fa5	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	fa656ba8-e4bf-45f5-a776-e50bf8655260
ca9c8567-a138-42be-b642-f6069f4cb2f8	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	116655bc-452a-4982-a7d2-f464752a949d
6a631787-b99a-4391-961f-b1179b0005ff	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	c8e2f9ae-385f-43c9-9ba7-e5416112975c
5ac6979f-525a-4959-ae2c-fc022056637b	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
3efb2bf6-91df-4efc-8120-eb49e022d51e	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	c50afce6-a0bd-4914-b506-50e99a1faaf4
0023ebf7-c26b-4b10-b192-e02fafa08a94	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
13b7a593-c93a-4121-a644-9ba4cf32f6d7	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	c50afce6-a0bd-4914-b506-50e99a1faaf4
8a686a73-eaa5-48e4-a6a1-7f5ff6aa6cdb	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
0c2be63a-ee52-4248-b727-5b3b4701ad5e	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	1584ae3c-a607-42a3-a906-192ac2fc8bfe
59e02047-cf4b-4734-9f27-f1bad049dfb6	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	faa09eab-a088-4178-ba38-bdfe1e84f067
65b7b453-e377-49cd-95c3-c1fb25a533c6	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	116655bc-452a-4982-a7d2-f464752a949d
5362322a-c25e-4e35-ad03-5ac6709beb96	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
84843c9b-15f2-466f-93b5-cc93e5f107f2	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	1584ae3c-a607-42a3-a906-192ac2fc8bfe
d9512d3b-e923-4b71-8597-8306d5b91ef1	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
fbd5ad49-b07e-4721-ac6b-c975c00dc1dd	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	7bb29c68-6d79-46ba-a662-743f7cd78b1e
e3d69644-aa87-4249-bcb6-520ed31a8d42	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	c8e2f9ae-385f-43c9-9ba7-e5416112975c
59175ed9-07a2-47cc-9275-f894008ac89f	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
81bac413-b44d-4cf1-8591-a3831a6c3298	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	a948564e-d2e4-406a-9f38-1973fc377675
20526182-d4c8-4c7e-9fe1-895274e2d3ba	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	a948564e-d2e4-406a-9f38-1973fc377675
59a5d92e-dc0d-4673-b708-9e4ea89719ec	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	116655bc-452a-4982-a7d2-f464752a949d
8379a39a-14a2-4863-aa88-7dddeb4b10ea	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	116655bc-452a-4982-a7d2-f464752a949d
eb5ded26-d656-45fd-8c8a-28c4fd2432ba	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	2d10ce43-3226-43ed-9d4a-1b6781035b09
7b2273c9-f808-450d-b4d8-3b05dcb29bc7	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	faa09eab-a088-4178-ba38-bdfe1e84f067
e90985a5-8f40-4a2b-a2de-e9816ea791b9	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
a4119267-d7b9-42a0-bb38-31e94fee5672	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	c50afce6-a0bd-4914-b506-50e99a1faaf4
c2ab5212-3bf2-488e-b94e-01d39b9e5959	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	7c838a0f-3cf5-4087-9793-7ac22befe06f
9a1ba384-0405-4817-ac43-1d76d0efeedc	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	faa09eab-a088-4178-ba38-bdfe1e84f067
c662689d-607c-44b1-83f7-777874a96532	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	7c838a0f-3cf5-4087-9793-7ac22befe06f
58f1f898-ef90-47ca-bd34-2753bfeeb1f0	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
34c3777e-897b-4609-b998-803d55351826	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	faa09eab-a088-4178-ba38-bdfe1e84f067
4022aa0f-2434-49c7-8058-2da993d722f1	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	7c838a0f-3cf5-4087-9793-7ac22befe06f
5c31d68f-27f3-457e-a0b8-254828ba9205	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	a948564e-d2e4-406a-9f38-1973fc377675
9ea44410-b307-4d41-bdfb-ec0b3a818a88	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
cbc7e172-de2b-4e32-b88f-4f6c221c57bc	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	1584ae3c-a607-42a3-a906-192ac2fc8bfe
cf5f4fa2-6b9e-450e-9107-35afbda05158	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
40373138-fd5f-4e6a-9efd-43e93f55ca09	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	1584ae3c-a607-42a3-a906-192ac2fc8bfe
ac1ad372-eb9a-4a37-898d-d899466c9f4a	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
ad6286ee-2bd1-4e65-8883-76e5976017b8	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	faa09eab-a088-4178-ba38-bdfe1e84f067
203e1bd5-2fee-4826-aab8-6873370760e0	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	a948564e-d2e4-406a-9f38-1973fc377675
b4bfbae7-d762-4c24-8f3d-94d212fc178d	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	7c838a0f-3cf5-4087-9793-7ac22befe06f
d35f4545-8e63-4ecc-9e62-54816ad68a98	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	c00fe121-4b31-42be-bedd-a7144085ed84
1334172a-f8c2-4122-a557-f4b17cac24d1	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
8d344be0-3859-4d83-ab9a-9eae420a6558	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	1584ae3c-a607-42a3-a906-192ac2fc8bfe
eb2e56dd-8615-4c9d-8644-65864d7ac6e8	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	7bb29c68-6d79-46ba-a662-743f7cd78b1e
288fd1d5-4bb7-4e69-a853-31e6006c4562	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	c50afce6-a0bd-4914-b506-50e99a1faaf4
02eb2f1f-69e0-4d8e-b909-9eef9f625cf7	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
d1a3fab5-7ff3-42f6-ad42-d1cf4cdaa1f3	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	7c838a0f-3cf5-4087-9793-7ac22befe06f
da6f7cb0-b515-4941-9376-18528dfc01ad	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	c50afce6-a0bd-4914-b506-50e99a1faaf4
9e162a06-8aa0-4cff-a6f2-33d196e80ca2	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
7b38748c-d14a-4ec1-9f14-4787fb956189	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
5e57487e-ab0f-4a9f-9dcb-82bd2dc3b706	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	a948564e-d2e4-406a-9f38-1973fc377675
454b9a3e-69b7-41de-894c-94dda4029d72	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	a948564e-d2e4-406a-9f38-1973fc377675
cf5600d8-a410-4a50-a04b-8fdbc1665386	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
5f34f666-064f-42ef-ac65-5891adca2c9c	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	116655bc-452a-4982-a7d2-f464752a949d
2fa7861e-ca39-403e-af8d-00d53ef108c2	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	1584ae3c-a607-42a3-a906-192ac2fc8bfe
7fcef570-0bfb-4b5b-a927-d181d09d619c	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	116655bc-452a-4982-a7d2-f464752a949d
b39eda2e-e893-4fe3-88d8-5e3b5b063145	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
76ee1bdc-1da6-4d0e-881b-de68321946ef	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	faa09eab-a088-4178-ba38-bdfe1e84f067
b36fd513-90bb-4a28-9600-8e58fba49bb2	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	c8e2f9ae-385f-43c9-9ba7-e5416112975c
d7a5e8af-b263-405f-803e-a7268d3185ff	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	7c838a0f-3cf5-4087-9793-7ac22befe06f
e5decf63-f960-47df-9594-20a1b7e02665	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	c50afce6-a0bd-4914-b506-50e99a1faaf4
d23ec609-93c2-4819-b3fa-5fae22d25efd	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	c50afce6-a0bd-4914-b506-50e99a1faaf4
ee767c5e-466e-4046-a922-6d8893d73b80	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	7c838a0f-3cf5-4087-9793-7ac22befe06f
781b859c-6dca-4e8f-bb9d-68a80e9c6a66	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
a558c5f1-ac5b-46c9-9d90-f08bf1f52cf7	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	a948564e-d2e4-406a-9f38-1973fc377675
ac69f70f-9995-4a50-ad57-bed9c5f31449	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
dd563346-f2af-494e-86b0-5b68fcea1179	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	c8e2f9ae-385f-43c9-9ba7-e5416112975c
61184105-f24f-4a8c-95e0-25a8bfe16c8c	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	faa09eab-a088-4178-ba38-bdfe1e84f067
867d07a6-493a-4531-afe3-2273d4e6bee9	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	116655bc-452a-4982-a7d2-f464752a949d
3d0dccd1-ad7e-4d3e-971c-cf20cb681bf1	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	a948564e-d2e4-406a-9f38-1973fc377675
62db81f3-adc2-4b4c-99f1-c78719de02f5	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	2d10ce43-3226-43ed-9d4a-1b6781035b09
9bea617b-b13b-4525-a663-63cbde569a52	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
345ae8e9-86e0-4c09-a76b-e108ddca78d2	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	c50afce6-a0bd-4914-b506-50e99a1faaf4
54b2c6f5-12dc-46c0-a8a8-37ffec66a647	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	116655bc-452a-4982-a7d2-f464752a949d
64f33710-00bc-4ae2-85ab-87f5646b3c73	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	1584ae3c-a607-42a3-a906-192ac2fc8bfe
5b9b0a6c-6277-484e-9d6a-ca89d2441dfe	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
b317b982-9933-497b-a3df-090383ec55ac	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
dd53ff37-6414-4d1f-8a3a-5ef15a31d79a	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
d5caa1ca-2f5b-4df9-b9ec-8ea0bce9a8c9	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	faa09eab-a088-4178-ba38-bdfe1e84f067
083d4874-f104-4e63-9690-20851bffae8c	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	1584ae3c-a607-42a3-a906-192ac2fc8bfe
b0fe947f-07a5-4a40-b82a-28f69a78bcbd	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	faa09eab-a088-4178-ba38-bdfe1e84f067
41c01d2c-d0b6-4030-85c7-a9fe4f41a7ab	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
ee5a7e3f-f544-488d-837c-e10fcda397ed	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
7b66e36b-a061-467c-bd57-24b07cce0a77	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	c00fe121-4b31-42be-bedd-a7144085ed84
1fe04c5c-6ce5-4650-a9b4-b6aacbd1dc56	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	116655bc-452a-4982-a7d2-f464752a949d
f476152d-7cff-4efc-a4a5-3638ce62a2b6	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
07be2ff1-9c54-4461-a8f6-7ee79961c474	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	c50afce6-a0bd-4914-b506-50e99a1faaf4
5417ff6c-87de-425a-b2db-fad61df31314	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	7bb29c68-6d79-46ba-a662-743f7cd78b1e
d7ac281a-ec0d-41af-91f8-3a8b9045440d	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
2da2e551-0a21-4047-bb26-b776a68c08f3	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	faa09eab-a088-4178-ba38-bdfe1e84f067
4dd90b93-9eff-4b85-af0d-f9ed3df04727	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
01c5b3e4-c32f-4c84-ba15-1aae7cb0d66e	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	1584ae3c-a607-42a3-a906-192ac2fc8bfe
476fc6a8-b205-4300-aed1-82d5e986e437	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
1448c9ea-a342-4b8b-9fb3-4497b8e1242b	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	fa656ba8-e4bf-45f5-a776-e50bf8655260
13e3031f-fabe-4122-a2cd-0e5648394a9a	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	7bb29c68-6d79-46ba-a662-743f7cd78b1e
a9d59912-f58f-49cc-805a-fe7a27da9962	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	fa656ba8-e4bf-45f5-a776-e50bf8655260
a69da0cc-9326-43a5-8502-4860d948f512	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	c00fe121-4b31-42be-bedd-a7144085ed84
4a7c5dea-753c-4ca7-95c6-a8548209b29f	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	c00fe121-4b31-42be-bedd-a7144085ed84
d1bb7643-571c-4fbd-a5b3-b9892cd43c31	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	c50afce6-a0bd-4914-b506-50e99a1faaf4
5f1907d8-cbf8-44ea-b82f-14c324525479	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	c00fe121-4b31-42be-bedd-a7144085ed84
c7f75991-56ce-4eff-bde4-82b34217bed7	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	c00fe121-4b31-42be-bedd-a7144085ed84
3f539443-2710-4bd2-a454-46b348774240	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	a948564e-d2e4-406a-9f38-1973fc377675
7b12e6aa-050c-4764-9879-467ace76c54e	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
834d0096-5d7f-4d08-80f6-6876cb383761	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
ff55c147-970e-4dee-984a-48fc8036f078	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	fa656ba8-e4bf-45f5-a776-e50bf8655260
065df265-f34d-450c-80dd-7e85b0d0c351	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	c50afce6-a0bd-4914-b506-50e99a1faaf4
ab64b7d2-bd9f-4bc6-bf19-b49952edb6b2	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	a948564e-d2e4-406a-9f38-1973fc377675
3134cd23-94fc-4951-a3fd-f3c1f27515db	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
a2d52ed4-546c-47f4-8aed-3b72c24f0226	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	fa656ba8-e4bf-45f5-a776-e50bf8655260
502fcb2b-68c3-4d54-af00-666139a4280b	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
02053b52-c309-4707-be6d-a9ccec488808	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	1584ae3c-a607-42a3-a906-192ac2fc8bfe
e28977e2-83b1-42c2-8e41-3fe192bc14d9	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
f3e92f77-9594-4814-a229-3aacaa30d154	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	2d10ce43-3226-43ed-9d4a-1b6781035b09
2dd14987-b4e0-4ffb-abd5-039f61698eff	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
e718a677-f9b5-4539-add7-87bd9b20c213	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
dcab521c-1fdf-4a56-a638-dd1600fa351b	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	c8e2f9ae-385f-43c9-9ba7-e5416112975c
a24432b0-8c15-4536-a9ad-164dc7f2904f	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	faa09eab-a088-4178-ba38-bdfe1e84f067
e16d22e4-f0eb-40e6-a9f5-bbdd1ba0dadb	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	c00fe121-4b31-42be-bedd-a7144085ed84
1ffcf93d-78ce-46be-b177-556d4aa45eb2	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	fa656ba8-e4bf-45f5-a776-e50bf8655260
a3e5a56a-4217-482d-bdce-df162ac38998	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	2d10ce43-3226-43ed-9d4a-1b6781035b09
e6c4a523-01e7-4028-b3aa-f5b5990c4645	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	c00fe121-4b31-42be-bedd-a7144085ed84
00bd7a26-f7b9-432a-bda1-12f6826ebcf7	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	7bb29c68-6d79-46ba-a662-743f7cd78b1e
864ca975-bc87-4462-bddb-f3750a2c584b	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	c00fe121-4b31-42be-bedd-a7144085ed84
bdb0c136-6944-46b9-98e9-32be419ca115	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	116655bc-452a-4982-a7d2-f464752a949d
8109d4d9-6878-45df-bb18-cf85f12f8993	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	7c838a0f-3cf5-4087-9793-7ac22befe06f
583152b6-167f-4822-8921-657366070344	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	7bb29c68-6d79-46ba-a662-743f7cd78b1e
05d34d26-4bb7-4ea5-804c-0d8347c716d5	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	c8e2f9ae-385f-43c9-9ba7-e5416112975c
b41c47e3-2f01-48b8-a140-22d23ad45998	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	c00fe121-4b31-42be-bedd-a7144085ed84
66b25244-04e1-4b19-90b2-4350c6169357	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	c00fe121-4b31-42be-bedd-a7144085ed84
2738e50c-6ece-440a-8f61-0b75df1a2db9	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	fa656ba8-e4bf-45f5-a776-e50bf8655260
cf3de683-9835-4706-a2b0-fa0d36ff7dfd	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	fa656ba8-e4bf-45f5-a776-e50bf8655260
236d6bf2-e3bb-42b2-b9e5-c48619f7a7c9	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
84abb7fd-c71b-4e6e-9a83-996fc612833e	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
533d9f68-4f7e-4b49-91e1-50f1d4ed260c	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
54239e00-9f8a-4d40-9c4b-0eff1fc0f608	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	fa656ba8-e4bf-45f5-a776-e50bf8655260
86840c7d-6c5f-45cb-9911-2ee6e17f38fa	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	c00fe121-4b31-42be-bedd-a7144085ed84
b2a4897a-4e39-46be-b195-3d5bc6df902e	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	fa656ba8-e4bf-45f5-a776-e50bf8655260
82fe2c3b-275a-4a3e-b667-7abecd1a9527	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	c00fe121-4b31-42be-bedd-a7144085ed84
984fb9bd-a76f-44be-b435-62b9d8ffda09	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
30d52038-16a7-4b48-b814-0c41b23cb029	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	c8e2f9ae-385f-43c9-9ba7-e5416112975c
5b1be238-4042-44dc-9407-295075ede0df	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
128b319b-c384-475d-b5a1-7847f219a5ca	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	116655bc-452a-4982-a7d2-f464752a949d
cc166191-585d-4259-a255-ea63b864cb73	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	fa656ba8-e4bf-45f5-a776-e50bf8655260
20c02bb0-a5e4-427f-9aaa-20e563174017	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	2d10ce43-3226-43ed-9d4a-1b6781035b09
e7f0de2d-91f2-4048-b0fb-51aa041b53e5	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
7ebf8656-e4e3-41e6-8025-e0c34f71b7f5	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
a9afa203-ac23-479f-a0c5-1ffc558f351c	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	fa656ba8-e4bf-45f5-a776-e50bf8655260
59be6ba1-4d6b-4b78-9b79-dfdddece4104	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	2d10ce43-3226-43ed-9d4a-1b6781035b09
803e36a5-f815-4abb-82a6-d5c509592897	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	7c838a0f-3cf5-4087-9793-7ac22befe06f
a375e3ae-28e7-4283-98b7-26f2d79ebf08	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
0143285d-3623-4e6b-b020-2fc13aafb08d	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	a948564e-d2e4-406a-9f38-1973fc377675
677d2768-328a-4160-a808-4825729dc9e2	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	c00fe121-4b31-42be-bedd-a7144085ed84
2780a615-e1e2-4c36-b07a-bd2318cd7a42	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	c8e2f9ae-385f-43c9-9ba7-e5416112975c
1087f31f-9614-4196-8bc3-043485c1cb2e	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
de11f446-f538-47c1-9328-9bfc043f270a	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	a948564e-d2e4-406a-9f38-1973fc377675
4fdcad2b-4008-436c-9480-509dd8f33751	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	c8e2f9ae-385f-43c9-9ba7-e5416112975c
4cea4b12-29d1-43e4-812f-cf086cb44396	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
7b4fc055-b078-48ea-bdd9-6a465d191ed9	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	1584ae3c-a607-42a3-a906-192ac2fc8bfe
3facf772-e8e1-4acf-90f8-6db9dfdef5ee	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	2d10ce43-3226-43ed-9d4a-1b6781035b09
c5295fce-5b76-4fee-b30d-bfea002ad47c	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	7c838a0f-3cf5-4087-9793-7ac22befe06f
e36bc40a-32e6-452c-aa5d-b8f3eb0757bf	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	fa656ba8-e4bf-45f5-a776-e50bf8655260
a16aae7d-823c-46de-a1fe-8ab00c76f7b1	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	7c838a0f-3cf5-4087-9793-7ac22befe06f
f75c80b7-0a66-488b-a2b0-34a56fd899ef	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	7bb29c68-6d79-46ba-a662-743f7cd78b1e
3803053c-2214-40fc-b49a-9b7f2f39e241	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
178b55c6-14b8-431c-a45e-a543e85147ca	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	1584ae3c-a607-42a3-a906-192ac2fc8bfe
6d1f106a-e90c-4442-82a5-bc0017d99798	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	116655bc-452a-4982-a7d2-f464752a949d
e6db7a9c-b25f-407a-8bbf-e68d9283447e	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	faa09eab-a088-4178-ba38-bdfe1e84f067
f683232b-97b8-4809-9ef5-6afc28457637	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	c8e2f9ae-385f-43c9-9ba7-e5416112975c
69fd3758-f37c-4e74-a2a0-7961165f0342	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	2d10ce43-3226-43ed-9d4a-1b6781035b09
bd7a031b-d2a1-431d-b48c-608df79ba631	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	116655bc-452a-4982-a7d2-f464752a949d
62a206c1-366a-4362-a021-bd413b07fae9	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	c8e2f9ae-385f-43c9-9ba7-e5416112975c
f1173fd6-43ce-436b-9a19-cebfa5dc6ed3	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	faa09eab-a088-4178-ba38-bdfe1e84f067
d66a3414-19b1-4c96-9c7b-832c03322782	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	7bb29c68-6d79-46ba-a662-743f7cd78b1e
24db2289-6505-4779-9805-6857f0175350	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
5527ce5c-dadd-4b42-81e1-2dd2e9636ad5	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	c50afce6-a0bd-4914-b506-50e99a1faaf4
16b32c08-1fc0-4a1f-9be6-c866a2c8f700	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	fa656ba8-e4bf-45f5-a776-e50bf8655260
b5ff7102-c563-4251-8b87-a8be09131904	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
41bea837-bd68-40a9-a9f3-950a15c63577	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	c8e2f9ae-385f-43c9-9ba7-e5416112975c
49f27144-d87e-4896-8248-4b2eb03d2366	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	c8e2f9ae-385f-43c9-9ba7-e5416112975c
4f1f6a1a-5956-4f62-a491-0459bc789b0d	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
1866312c-9a85-443d-9eda-94b4bb5a7576	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	c50afce6-a0bd-4914-b506-50e99a1faaf4
07a371d6-69cc-4c70-ac46-95553545b19a	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
652d1439-6254-4891-83bc-5d89c025f33f	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
3e1cf713-4065-4d66-9942-a8a8306b421d	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	a948564e-d2e4-406a-9f38-1973fc377675
64f93a13-f06e-4b41-83fa-33195dd97a2a	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
32985284-fc3e-4bc0-aeca-5a3cba6ed723	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	a948564e-d2e4-406a-9f38-1973fc377675
659507e1-dac6-4114-bf62-9eeb08f8bf73	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	c8e2f9ae-385f-43c9-9ba7-e5416112975c
2e8524d0-4b4e-4f90-9dc5-2200f0da9a3a	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	c8e2f9ae-385f-43c9-9ba7-e5416112975c
22465ee0-1929-4b13-b29e-b976c1887233	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	7c838a0f-3cf5-4087-9793-7ac22befe06f
da9998a7-6e84-4a36-a6fe-cd810c24d449	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
d897cb04-9aac-47ab-a9a6-4f039cd6548a	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	1584ae3c-a607-42a3-a906-192ac2fc8bfe
e3b41fe2-d002-4d70-bf06-b3f184f67500	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	7c838a0f-3cf5-4087-9793-7ac22befe06f
2fce67cd-24e0-4b9a-b0dc-3d3f91fabeb2	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	1584ae3c-a607-42a3-a906-192ac2fc8bfe
d226b5cc-0ecc-4417-9cad-d55e0191d08f	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
91710864-4d21-4ba5-bf2b-c53bdf93813a	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	c00fe121-4b31-42be-bedd-a7144085ed84
49546ef5-f8f9-413b-ad49-e055f2e03d00	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	c8e2f9ae-385f-43c9-9ba7-e5416112975c
78ec7c4e-6052-42af-8bbd-d089c0bb090d	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	116655bc-452a-4982-a7d2-f464752a949d
61e9dc0c-ce7a-434d-9a95-7386d02d4a24	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
170cea07-df55-4159-9b42-8083d02cc723	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	faa09eab-a088-4178-ba38-bdfe1e84f067
ad820f74-a3b0-4f50-953e-cadad41abd4c	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	fa656ba8-e4bf-45f5-a776-e50bf8655260
dd694424-e29b-4f45-990e-d46e041df2fa	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	116655bc-452a-4982-a7d2-f464752a949d
40f6d18a-daf5-4651-aa6a-dd4f60a2b8d1	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	faa09eab-a088-4178-ba38-bdfe1e84f067
dd773b84-67d5-4e37-94de-ecf7fb672b84	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
05f46616-c64c-4d1f-ac75-45d43c716f32	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	c8e2f9ae-385f-43c9-9ba7-e5416112975c
2b8bf854-761d-45b4-a957-c2ac79345068	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	c50afce6-a0bd-4914-b506-50e99a1faaf4
405e7915-411c-4bea-9b32-ca08aec7f04b	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	c00fe121-4b31-42be-bedd-a7144085ed84
929d8400-1372-45ed-aa88-49144a6afa93	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
92d2b2d7-d549-4e6d-bd38-2e98d38f9664	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	c8e2f9ae-385f-43c9-9ba7-e5416112975c
ddf2b6ec-d56a-4af3-8f01-47a17d736345	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	c50afce6-a0bd-4914-b506-50e99a1faaf4
2bca7d4a-558f-431d-b3cf-521c9a26400b	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	7c838a0f-3cf5-4087-9793-7ac22befe06f
25c3f168-5aea-405d-bf1e-c0eef2f76779	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	faa09eab-a088-4178-ba38-bdfe1e84f067
4b4bcff8-5c4c-44ca-b7cf-e24d2a425ae1	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	7bb29c68-6d79-46ba-a662-743f7cd78b1e
aa0d5755-0c79-4d08-9427-c307a25d37a9	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	2d10ce43-3226-43ed-9d4a-1b6781035b09
392b40b8-6d31-49e5-a552-beaf076d4ac3	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	2d10ce43-3226-43ed-9d4a-1b6781035b09
ea92b47d-3725-44b9-971b-3b262b5fb821	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	a948564e-d2e4-406a-9f38-1973fc377675
f3d68fc6-ae38-4cfe-b828-865020dfa394	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	c50afce6-a0bd-4914-b506-50e99a1faaf4
662f83a7-66a9-4eb3-91f9-fc374aac1736	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
cd60aa6f-ad9c-451e-896f-c7f67a228cf5	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
f090028b-a9e7-483b-a018-383234518ad2	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	c00fe121-4b31-42be-bedd-a7144085ed84
43c7380d-64f6-47c7-b7fb-77072dd6f2d6	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	1584ae3c-a607-42a3-a906-192ac2fc8bfe
516c97d1-509f-4327-a7f4-464297195049	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	7bb29c68-6d79-46ba-a662-743f7cd78b1e
a80b5046-c810-493a-b3c4-ee8ce1bb5c67	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	7bb29c68-6d79-46ba-a662-743f7cd78b1e
fcceb0d8-098e-4ff1-8591-682a9263af5c	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	c00fe121-4b31-42be-bedd-a7144085ed84
94c7f142-b46f-400a-9083-73c01f8b9162	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
f19dfb99-5954-4ea6-a77b-c7c0d2885630	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
438b0ef2-3ca0-4477-92d6-7f9a72a47fd6	8811e952-4385-400e-a157-6e3b239a6f49	ec7c68f7-fa71-48fb-9b6b-8ae860df1492	2d10ce43-3226-43ed-9d4a-1b6781035b09
fe80218f-b387-4b8c-af99-5c3224b90426	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	faa09eab-a088-4178-ba38-bdfe1e84f067
c9629879-89c4-4590-8a43-23336c8a51c6	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	a948564e-d2e4-406a-9f38-1973fc377675
dc595f06-788f-4ad6-b050-2843d97da592	f35c006f-3f41-4609-947b-331decc0123c	22e0e47e-6b57-4cd4-8069-3718ccec56fb	2d10ce43-3226-43ed-9d4a-1b6781035b09
c43f3c1c-62df-40a7-b698-4a1fe923e438	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
d4551471-514f-4521-a6e6-49d04b39ba54	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
7515ad8e-a410-4524-9a2d-76f4bb12c3b3	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	1584ae3c-a607-42a3-a906-192ac2fc8bfe
2ce61bfc-34f5-42fd-83d1-129cab303591	a6f8a4df-8851-4680-829f-87986f7803de	bfd9bb29-f672-458f-b9b5-65e6359d253d	7bb29c68-6d79-46ba-a662-743f7cd78b1e
9e7cd43c-658e-4ebf-9ec0-3f921be26b90	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	fa656ba8-e4bf-45f5-a776-e50bf8655260
0e0b7272-8114-4ced-a3fb-7433ef005e14	00b63aa0-71fc-4e48-80e1-c69461ddd504	67480ace-94bf-4d99-b0bb-73460821fbc0	7bb29c68-6d79-46ba-a662-743f7cd78b1e
b7a05676-9142-4d8a-ad1a-ca7d2b7b293e	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	c50afce6-a0bd-4914-b506-50e99a1faaf4
cfecb74f-d6e7-4d64-8c58-e96b15e9f145	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
4136f982-4024-4121-bc83-bcf6c0f56c92	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	116655bc-452a-4982-a7d2-f464752a949d
91c74e9c-057f-4f8c-aaf4-255c4e2f4600	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	fa656ba8-e4bf-45f5-a776-e50bf8655260
1d9a21b9-0283-49cb-b615-ef3a50ecaf5e	f35c006f-3f41-4609-947b-331decc0123c	a60e91d0-59fd-42fa-93fe-2c88b3670a55	2d10ce43-3226-43ed-9d4a-1b6781035b09
6d2f51ca-3182-4923-b05f-3adef75a6393	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	7bb29c68-6d79-46ba-a662-743f7cd78b1e
9948dde4-99d8-4995-8217-e80d9ca22518	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	116655bc-452a-4982-a7d2-f464752a949d
d1af7bf8-6f28-4f89-8934-05b7efe31b14	8811e952-4385-400e-a157-6e3b239a6f49	7f6a78b6-27e7-4e4f-aee6-8eb1c2673a08	c8e2f9ae-385f-43c9-9ba7-e5416112975c
c1ed4b55-65a5-406b-9d4f-8445501f9218	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
5fa4af04-72da-4216-8b01-230f0c1a2801	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	1584ae3c-a607-42a3-a906-192ac2fc8bfe
0389d5e1-e8c4-46cd-9033-dfde8b5f76c8	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
fb1c52a4-f1bf-420a-aea9-6b5921e28754	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	7bb29c68-6d79-46ba-a662-743f7cd78b1e
b115e20c-3378-49de-a610-7d7c119aa216	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	2d10ce43-3226-43ed-9d4a-1b6781035b09
3f174b2c-8ee2-4a98-9f8c-1e2f1069d834	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	2d10ce43-3226-43ed-9d4a-1b6781035b09
f420dd17-d839-4abe-88d3-538468ac9c82	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	a948564e-d2e4-406a-9f38-1973fc377675
840e2694-d536-4dea-aeba-f51b30b225fd	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	c00fe121-4b31-42be-bedd-a7144085ed84
fb13bcb7-a0a7-4e8a-8674-d50aa9619ad0	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	7bb29c68-6d79-46ba-a662-743f7cd78b1e
92d0b2c7-97f2-450f-b814-ba28f8c09ee6	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	7bb29c68-6d79-46ba-a662-743f7cd78b1e
244f5c1d-fb2f-4275-9171-b9d53d823fbe	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	c00fe121-4b31-42be-bedd-a7144085ed84
32e2c7b8-9eef-45d2-b554-a9b23f55bbbe	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	7c838a0f-3cf5-4087-9793-7ac22befe06f
437c48e3-a6f2-42e6-bea4-5c21ec1163cd	00b63aa0-71fc-4e48-80e1-c69461ddd504	19093394-533f-4890-8856-eaba05ff6b21	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
816467f2-b3e4-4856-ab58-00c8defb25fd	00b63aa0-71fc-4e48-80e1-c69461ddd504	f6cd3211-ca60-4815-a2cd-a9de1f9bd5c1	116655bc-452a-4982-a7d2-f464752a949d
7eb3673b-1395-4934-86b3-d78c1126381e	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
0050430b-64cf-4a74-a53f-e4b5fc5e53ef	8811e952-4385-400e-a157-6e3b239a6f49	8cc47117-ed20-4dd8-b7e8-1e92a9e53039	2d10ce43-3226-43ed-9d4a-1b6781035b09
c9804929-364c-4a12-aaa8-b0c026b2b90f	8811e952-4385-400e-a157-6e3b239a6f49	5dc33309-c210-428e-a2a3-0d909862125a	2d10ce43-3226-43ed-9d4a-1b6781035b09
80601fd6-2f70-4bd6-abb9-5d51d39896f1	00b63aa0-71fc-4e48-80e1-c69461ddd504	37e5f7ac-99c0-411a-8006-2c070b64b751	7bb29c68-6d79-46ba-a662-743f7cd78b1e
2ab1b5e9-8695-4656-9256-7f319e72c937	8811e952-4385-400e-a157-6e3b239a6f49	1c4ec33b-4854-47b3-ad54-daf42a013cc4	7bb29c68-6d79-46ba-a662-743f7cd78b1e
1a108a66-9192-4d66-86e0-e0a12da5afc7	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	c50afce6-a0bd-4914-b506-50e99a1faaf4
aff9457b-00f6-4d97-840b-3d64f209aa8b	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
628e1d7d-74d5-4589-8aff-c859664b4a33	8811e952-4385-400e-a157-6e3b239a6f49	85943178-63d2-4623-b71b-19e856a1a45f	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
912c701e-e80a-4562-97e8-2c2fffe60b3d	a6f8a4df-8851-4680-829f-87986f7803de	35083070-9d84-499f-83ea-19db80862f39	fa656ba8-e4bf-45f5-a776-e50bf8655260
f7c30d98-03fb-4132-b541-fe14fa21a39c	8811e952-4385-400e-a157-6e3b239a6f49	1cc895fb-8487-466c-80cb-0b094c1bb1c0	2d10ce43-3226-43ed-9d4a-1b6781035b09
04058f66-0ded-4ff7-819a-5bf74cdeb2f1	8811e952-4385-400e-a157-6e3b239a6f49	f05cceae-93c1-4205-8c9b-ddcd55ca4e83	fa656ba8-e4bf-45f5-a776-e50bf8655260
9619755e-f098-427b-b994-2ad55be41461	8426e329-cd26-49a3-abb5-863940808283	aa8bd074-8604-491d-93ed-0031888f00c6	2d10ce43-3226-43ed-9d4a-1b6781035b09
bc9a2180-2981-4fcd-84ed-bf257cd8579e	f35c006f-3f41-4609-947b-331decc0123c	b5de8cf9-b7d4-4e59-be5f-1542ce035441	7c838a0f-3cf5-4087-9793-7ac22befe06f
cf1e3d1a-b87a-43b4-bf20-c9ca586f4e61	8426e329-cd26-49a3-abb5-863940808283	d746761b-43b9-4053-a72b-7a2808da68bb	c8e2f9ae-385f-43c9-9ba7-e5416112975c
50a24e91-4590-4495-a97d-cf05c7c1fecb	f35c006f-3f41-4609-947b-331decc0123c	b7ca33eb-18ec-4407-99d9-778f4fca2d22	faa09eab-a088-4178-ba38-bdfe1e84f067
\.


--
-- Data for Name: catalog_product_images; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.catalog_product_images (id, alt_text, sort_order, url, product_id) FROM stdin;
\.


--
-- Data for Name: catalog_product_reviews; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.catalog_product_reviews (id, anonymous, approved, comment, created_at, order_id, rating, updated_at, user_email, user_id, user_name, product_id) FROM stdin;
\.


--
-- Data for Name: catalog_product_tiers; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.catalog_product_tiers (id, price_usd, qty, sort_order, product_id) FROM stdin;
b6dccc1f-cda7-4be0-8608-7fee2d875032	32.00	500	3	96968a16-96f7-4647-bd50-4ec8dfe32ebf
71aca0c3-d93c-4f3b-88dc-6ba6f1cafd1a	17.60	250	2	96968a16-96f7-4647-bd50-4ec8dfe32ebf
0a0e976f-7df2-4224-b04d-f665075659e9	8.00	100	1	96968a16-96f7-4647-bd50-4ec8dfe32ebf
e4df9528-36b9-4989-8d8e-838732b24740	24.00	500	3	3c6b54cb-f751-40f9-a7c7-a7fa64713001
ec67c48d-53bf-41be-8f2b-ca419a7a7bc3	13.20	250	2	3c6b54cb-f751-40f9-a7c7-a7fa64713001
887c080f-bb3e-4884-80eb-86efbcce7719	6.00	100	1	3c6b54cb-f751-40f9-a7c7-a7fa64713001
b9ca5215-8e9c-498b-a78b-7f30628f6f20	48.00	500	3	204a4763-e4e6-46f7-a5cd-d51082ff28b2
16ef5d45-084a-4d50-b471-ee96fb36804f	26.40	250	2	204a4763-e4e6-46f7-a5cd-d51082ff28b2
85f7688f-05bc-4639-8339-0fb0ba5c0e87	12.00	100	1	204a4763-e4e6-46f7-a5cd-d51082ff28b2
0fedd194-80ec-442e-aa29-00869cb14287	40.00	500	3	5a4a8ef3-0189-4227-a5c8-af0abbb98610
71ceafbe-709e-4d7d-8c36-27ead9a921d7	22.00	250	2	5a4a8ef3-0189-4227-a5c8-af0abbb98610
a0aaf072-2d8e-4bed-a5b8-460ce01725ee	10.00	100	1	5a4a8ef3-0189-4227-a5c8-af0abbb98610
2c21780d-9d24-4734-83eb-526f895decdb	44.00	500	3	e3bf240f-f342-48bf-856d-d1c9f712d875
afe67d3b-34c3-4bf3-9592-afdf7ca5a3e1	24.20	250	2	e3bf240f-f342-48bf-856d-d1c9f712d875
6d964da9-bbbb-464a-aefe-401e9ad91687	11.00	100	1	e3bf240f-f342-48bf-856d-d1c9f712d875
cce5ab3a-c90b-454c-859a-4725e690fa2c	36.00	500	3	7d6c8738-455d-488c-a3fc-d5ecf745efa9
e2e22699-5211-4017-a951-a6df601eb3d8	19.80	250	2	7d6c8738-455d-488c-a3fc-d5ecf745efa9
2c6a430c-e015-4a8f-855e-b8d671818281	9.00	100	1	7d6c8738-455d-488c-a3fc-d5ecf745efa9
eb15b848-b1c9-4f97-849e-130b356f0f98	28.00	500	3	2f926ce2-242c-495d-983d-2c34a4e5e60d
ca5b1dba-8d0f-43a4-b496-863250220aff	15.40	250	2	2f926ce2-242c-495d-983d-2c34a4e5e60d
9187b5a6-4c41-48c1-844a-462bde425636	7.00	100	1	2f926ce2-242c-495d-983d-2c34a4e5e60d
03c48c9e-fee8-4813-b28b-bcfdf60e6fda	100.00	500	3	4c6e7f4a-9800-462f-a1a9-751129cc25be
4571af9a-f707-41e3-9871-698262d6fede	55.00	250	2	4c6e7f4a-9800-462f-a1a9-751129cc25be
274c1fa1-a306-4893-b333-18c522d43088	25.00	100	1	4c6e7f4a-9800-462f-a1a9-751129cc25be
84d9b182-1ba8-4eae-9be5-d86df911610b	180.00	5	3	04bce68a-3bce-4c0e-a1d5-6d3dcd09b053
d91bbdf0-0f13-4e42-bb99-336acc468c8a	112.50	3	2	04bce68a-3bce-4c0e-a1d5-6d3dcd09b053
ad131e88-1112-4298-8115-6d8517d5270e	45.00	1	1	04bce68a-3bce-4c0e-a1d5-6d3dcd09b053
ccba01a1-3c77-46d6-9de6-9c1c65960b4b	48.00	5	3	bb54d991-8205-4970-b0a7-a49473e7a4d6
c627fb9c-969a-4c2e-8586-653298ae20d5	30.00	3	2	bb54d991-8205-4970-b0a7-a49473e7a4d6
3e953077-029f-44b9-a87b-610b7c11703b	12.00	1	1	bb54d991-8205-4970-b0a7-a49473e7a4d6
a763e002-1b22-4cb4-8afb-cee510acfad4	120.00	5	3	720314fd-1abd-4c8a-a79a-592e5d6c8650
2fcd7d5b-7728-4ef7-aba8-a4cea73cfcae	75.00	3	2	720314fd-1abd-4c8a-a79a-592e5d6c8650
37f35f92-c8b7-403d-b0e4-479ec1c7e98c	30.00	1	1	720314fd-1abd-4c8a-a79a-592e5d6c8650
5c3d22ca-721c-4296-a19a-34f20784bce4	56.00	5	3	a2f7ea19-abe4-4998-a223-993b2a5d3090
552a8a53-e1e9-455b-a03c-38ee58b70bcc	35.00	3	2	a2f7ea19-abe4-4998-a223-993b2a5d3090
940ab879-8ee8-4afa-a483-01345c6e3cb6	14.00	1	1	a2f7ea19-abe4-4998-a223-993b2a5d3090
c7af8470-0ad9-4d6c-b0ab-81472cd96e82	72.00	5	3	b9ad2e9e-c746-45b0-8d29-2f23bd5697c7
540cf376-a1e8-47bf-9c3a-5e55b7bd458d	45.00	3	2	b9ad2e9e-c746-45b0-8d29-2f23bd5697c7
c2f3c0f8-3c04-44e3-ac5f-a512f10096b7	18.00	1	1	b9ad2e9e-c746-45b0-8d29-2f23bd5697c7
67a9eb03-70b0-4fe2-93c3-be390e7afcc8	60.00	500	3	dbeb2ecb-012f-43f5-a5dc-6523433db614
1915c93c-f8eb-456c-a4d0-13388e6c03f9	33.00	250	2	dbeb2ecb-012f-43f5-a5dc-6523433db614
27ec95b8-03c8-439b-9ecc-48f9331c55dd	15.00	100	1	dbeb2ecb-012f-43f5-a5dc-6523433db614
f852f20d-e973-4fd0-ac52-db739f5a86b2	72.00	500	3	d32e70ec-9b9d-4209-b8a0-3dc47e66f473
41965fce-8cd1-4787-b52e-087a010740b9	39.60	250	2	d32e70ec-9b9d-4209-b8a0-3dc47e66f473
4db96b65-885d-49a6-8501-3b1968a49928	18.00	100	1	d32e70ec-9b9d-4209-b8a0-3dc47e66f473
712f4e85-7fbf-48c0-9d14-0115fe6b3e06	56.00	500	3	9625f7c7-3058-4960-a42e-3b3648a5d1b4
3d5b6d62-9445-4a3f-b033-30f2681ac3f7	30.80	250	2	9625f7c7-3058-4960-a42e-3b3648a5d1b4
402183d4-3339-4b30-af21-7053dd56a741	14.00	100	1	9625f7c7-3058-4960-a42e-3b3648a5d1b4
bed73abb-22c8-49aa-935f-7741662e62d6	48.00	500	3	379f4020-e851-4a34-9703-83e337682ca6
0124d0f6-0cf9-4dc1-9b61-d16ccc9facc6	26.40	250	2	379f4020-e851-4a34-9703-83e337682ca6
498a87da-537c-4ad0-9081-fa7fcb6f4780	12.00	100	1	379f4020-e851-4a34-9703-83e337682ca6
c36b4586-e61a-4ef0-9b68-b6d419ce18a6	40.00	500	3	ff92cf7d-b1aa-4baa-831f-861da7a21015
8eb9513e-96ec-4302-bad0-348491afafbc	22.00	250	2	ff92cf7d-b1aa-4baa-831f-861da7a21015
7018c15c-cfe2-4bea-ac18-6408c2e1ad12	10.00	100	1	ff92cf7d-b1aa-4baa-831f-861da7a21015
0db0778b-5f84-484c-a00a-7b88a6b750d4	80.00	5	3	d6b6b2bd-36dd-45b8-918b-2af953a8c2b3
2227e2eb-f141-4afc-a173-51e714e116e6	50.00	3	2	d6b6b2bd-36dd-45b8-918b-2af953a8c2b3
10245a98-7d52-4600-a73a-f8a5384cbff0	20.00	1	1	d6b6b2bd-36dd-45b8-918b-2af953a8c2b3
55021328-8c45-4dd9-910e-f917588c8756	160.00	5	3	fbabc552-7ac1-4d48-8f05-8dbd9841a931
22b29245-fe98-4efa-a373-ee856c558f2a	100.00	3	2	fbabc552-7ac1-4d48-8f05-8dbd9841a931
ad26e296-64b4-4de9-8117-0f65df56d04e	40.00	1	1	fbabc552-7ac1-4d48-8f05-8dbd9841a931
69191bfd-76a9-4db2-be29-4e363a41e47a	140.00	5	3	5c1c1bdc-91f1-4ddf-96b6-a3886c37e73c
931a24c4-108c-49ed-a501-6ae194844f28	87.50	3	2	5c1c1bdc-91f1-4ddf-96b6-a3886c37e73c
3a1aef9f-a93a-4743-9415-0d12f96a9a68	35.00	1	1	5c1c1bdc-91f1-4ddf-96b6-a3886c37e73c
aa0187af-5a16-4aed-b9d8-958d90de5ba6	32.00	5	3	555185f3-fecb-44b9-90d1-c883fb78c45b
aa56cf2a-9961-4710-8fb3-e26a39d5d3b3	20.00	3	2	555185f3-fecb-44b9-90d1-c883fb78c45b
17bc9414-5d38-4f10-be7e-5061bea55ef4	8.00	1	1	555185f3-fecb-44b9-90d1-c883fb78c45b
791194d2-c298-4424-a050-a3b1d314cf28	36.00	5	3	7a991bcf-1c19-4725-a5c0-99cad0f76515
47f71a28-6f54-4322-9af6-ebd33b2ff22d	22.50	3	2	7a991bcf-1c19-4725-a5c0-99cad0f76515
6414dc4c-4be0-417b-8bf2-39ab0fd03411	9.00	1	1	7a991bcf-1c19-4725-a5c0-99cad0f76515
81f9772e-1300-4073-9c9a-12b1875d885f	24.00	5	3	93caa48c-b5d8-4289-96b8-e9b7ac2b8d9a
e8e698dd-caec-4124-8dd6-94bdd60cb11f	15.00	3	2	93caa48c-b5d8-4289-96b8-e9b7ac2b8d9a
e3b5ac51-14d0-4d85-8693-644b88659f0c	6.00	1	1	93caa48c-b5d8-4289-96b8-e9b7ac2b8d9a
bb76ea46-b25f-4b82-8e5d-40e5e757e389	100.00	5	3	0f113e07-2bd1-4bf3-90a4-f1bcfa491e72
31aa592f-e99c-42bc-a27d-80464e17e339	62.50	3	2	0f113e07-2bd1-4bf3-90a4-f1bcfa491e72
b1fc8fd5-e4d5-4a3a-a466-95d31deed13b	25.00	1	1	0f113e07-2bd1-4bf3-90a4-f1bcfa491e72
ec9f7182-c321-443d-9f50-d6b3b797ec8a	52.00	500	3	27217955-03b8-4a3f-b57e-bbff60ac1328
f1c60f60-ab9f-4f1b-bdca-48f3af304757	28.60	250	2	27217955-03b8-4a3f-b57e-bbff60ac1328
3e72d740-73b4-4a27-b191-4483c1eb292b	13.00	100	1	27217955-03b8-4a3f-b57e-bbff60ac1328
cbdc84b1-d0bf-4e8a-bfd7-bc41f6f25259	88.00	5	3	f506e467-1f89-4015-8f23-5f2a2aaca99c
481facc0-9c98-4cad-a72d-aff72fdb10ee	55.00	3	2	f506e467-1f89-4015-8f23-5f2a2aaca99c
e382798c-782b-4e39-931b-d2569cea1e85	22.00	1	1	f506e467-1f89-4015-8f23-5f2a2aaca99c
b2514af3-fd31-4864-a75c-b4f8d44e90c7	32.00	500	3	9e29855f-90c3-4e56-8236-4de364524522
f1cf7bdf-4f2e-49e8-949b-96dc989d2eca	17.60	250	2	9e29855f-90c3-4e56-8236-4de364524522
2fdf2eb6-4437-472e-bf1e-8722083ad7bc	8.00	100	1	9e29855f-90c3-4e56-8236-4de364524522
f2c3f77b-9311-46b5-936c-1c00299f2f7d	40.00	500	3	7b66d82d-b943-465b-9767-58c44bc23f5e
84ca1dcb-e88e-4873-a73c-e3dc17854ef9	22.00	250	2	7b66d82d-b943-465b-9767-58c44bc23f5e
4fce588d-b51f-4ad7-b9a8-1729d76958cd	10.00	100	1	7b66d82d-b943-465b-9767-58c44bc23f5e
810295e6-afa8-47bd-b15a-781afaf9dbd6	48.00	500	3	cf7b4ed5-182d-4049-97b9-9fbb3baa9e4b
85b113a7-5489-4089-927f-dedbd6554018	26.40	250	2	cf7b4ed5-182d-4049-97b9-9fbb3baa9e4b
d7847cca-12a4-4ee6-8f56-22b0a6b0d5f0	12.00	100	1	cf7b4ed5-182d-4049-97b9-9fbb3baa9e4b
8903f131-8b5a-4216-963d-16875198f30a	16.00	250	3	763507aa-9d97-4d28-9065-385e68b9fe13
d24d1bac-8342-46e6-b940-571213a84111	7.20	100	2	763507aa-9d97-4d28-9065-385e68b9fe13
15b3bbbe-fd49-4417-9583-0b8d87ce9740	4.00	50	1	763507aa-9d97-4d28-9065-385e68b9fe13
ce426902-cdf7-457d-8102-e70cdf970776	56.00	500	3	76421511-87ff-4ace-be67-d578b6271acf
79ba3a81-646f-4f8e-a413-e66ad14e9a5d	30.80	250	2	76421511-87ff-4ace-be67-d578b6271acf
d66a4d2d-24c7-4003-b5bd-544d367075f2	14.00	100	1	76421511-87ff-4ace-be67-d578b6271acf
3b9b0426-ab8b-4f2a-b709-2071169e2221	8.00	250	3	83303a4f-d1f6-45b8-b33c-3a9744b36221
a4aad929-5df2-40b5-b671-7d70d23f9451	3.60	100	2	83303a4f-d1f6-45b8-b33c-3a9744b36221
ec58f0a2-6aac-4a7a-b2fc-d16aa62b23a7	2.00	50	1	83303a4f-d1f6-45b8-b33c-3a9744b36221
e9f63dd5-aefe-4023-8804-defb3476dd6f	12.00	250	3	dd052ed1-646a-4dd4-bf19-6a24eef5e2af
19421c32-5351-46f1-8cb3-2e123f3ddf9a	5.40	100	2	dd052ed1-646a-4dd4-bf19-6a24eef5e2af
f022f176-b5be-4132-9314-f97dfc02fa0b	3.00	50	1	dd052ed1-646a-4dd4-bf19-6a24eef5e2af
8c8fa642-440d-4648-90ae-320d4f64b7ae	12.00	250	3	8688f2dc-c168-42b0-a950-f999c383712f
9059ce8c-4b6b-4ebd-9795-4dd0547e5210	5.40	100	2	8688f2dc-c168-42b0-a950-f999c383712f
7a6db9ee-5511-41c2-b672-93f8cf4f5576	3.00	50	1	8688f2dc-c168-42b0-a950-f999c383712f
8806da37-3640-43f8-9463-23c4cb5d7fc2	8.00	250	3	97cfc740-1f60-40cc-b3fb-e572ff01a96f
22453aaa-ef94-4c17-8d94-1382ff67f76a	3.60	100	2	97cfc740-1f60-40cc-b3fb-e572ff01a96f
100a270e-725f-4e5e-8c00-d7cc56fa7408	2.00	50	1	97cfc740-1f60-40cc-b3fb-e572ff01a96f
6e7881bc-6705-41f3-b494-952cc4c49c9e	16.00	250	3	ce7cf5a5-65b3-4a06-948b-3255c002eab4
9b01776c-a5e4-422f-b83f-2f32ab8adaa9	7.20	100	2	ce7cf5a5-65b3-4a06-948b-3255c002eab4
6d1aebee-07d9-4bae-8ccc-15dd08f6cb7f	4.00	50	1	ce7cf5a5-65b3-4a06-948b-3255c002eab4
f24d898a-a188-4147-8d25-1ac288fc2c20	20.00	250	3	bd6e9414-9a75-43b2-9419-b1b88b7c5880
97dc8614-7505-4471-b232-049984ad9f70	9.00	100	2	bd6e9414-9a75-43b2-9419-b1b88b7c5880
2ce54a65-03c7-46b5-8a06-88c8c7a3b319	5.00	50	1	bd6e9414-9a75-43b2-9419-b1b88b7c5880
ec0cf1b2-c7f8-470e-8233-53baf420d62c	24.00	250	3	744af4b7-c027-45a7-a3b3-2a20a9bb67cd
5f684142-693c-43da-84fb-93209db51820	10.80	100	2	744af4b7-c027-45a7-a3b3-2a20a9bb67cd
2559101d-05c8-474d-85c2-bfcfcd2c31b4	6.00	50	1	744af4b7-c027-45a7-a3b3-2a20a9bb67cd
dd134e0b-d18e-4894-a444-0acbf12010d5	8.00	250	3	1334b694-251c-44b3-9a65-c00fefc6af43
747cc8ae-af56-4b60-94fc-8a30d220611b	3.60	100	2	1334b694-251c-44b3-9a65-c00fefc6af43
d563588c-3f38-494c-8bdb-5d93569210f1	2.00	50	1	1334b694-251c-44b3-9a65-c00fefc6af43
5ef3c407-7364-43a3-a59a-d846063db2ad	8.00	250	3	82b26352-23ab-4b79-9232-d1d0d62fe418
63634b20-aabb-4abd-a8ae-713af3044cd2	3.60	100	2	82b26352-23ab-4b79-9232-d1d0d62fe418
70b5c1ee-dbc9-4561-862e-05344e2b937e	70.40	2500	3	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
fd6bba50-bd40-4363-9f88-aeb8dc5785fb	33.00	1000	2	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
f0a07733-2ff9-45d5-8ee4-e693a3ec4341	22.00	500	1	163c30ac-b337-47c9-bd13-7f1b0a0cb0fa
23cdcd6c-9f33-4f05-b109-845d8c2b7f19	89.60	2500	3	2d10ce43-3226-43ed-9d4a-1b6781035b09
25421978-722b-43bd-86d4-db03646dc652	42.00	1000	2	2d10ce43-3226-43ed-9d4a-1b6781035b09
980f7159-d1fd-4738-91bc-9b90dcfaf1fc	28.00	500	1	2d10ce43-3226-43ed-9d4a-1b6781035b09
457a23cd-d589-43c2-930e-09d8c7fd79ce	70.40	2500	3	7c838a0f-3cf5-4087-9793-7ac22befe06f
0ca1039f-77b5-4e3d-b7d1-53fb81300024	33.00	1000	2	7c838a0f-3cf5-4087-9793-7ac22befe06f
95684465-b7fd-4497-897b-43a6e9afcacd	22.00	500	1	7c838a0f-3cf5-4087-9793-7ac22befe06f
c5b807c2-9b60-4d87-8367-61655e34f3a2	80.00	2500	3	c00fe121-4b31-42be-bedd-a7144085ed84
014fecaf-6e89-4a98-b14f-91e126603439	37.50	1000	2	c00fe121-4b31-42be-bedd-a7144085ed84
829a15f2-669a-43c4-9ffb-64066f7c02e1	25.00	500	1	c00fe121-4b31-42be-bedd-a7144085ed84
5230e1ad-b4c9-4245-b16d-67e5958aad3b	83.20	2500	3	faa09eab-a088-4178-ba38-bdfe1e84f067
82255769-6605-4bd9-8c74-e10f24c85451	39.00	1000	2	faa09eab-a088-4178-ba38-bdfe1e84f067
bd3b29ff-71fd-4d87-b71b-bc6fe28c3ca4	26.00	500	1	faa09eab-a088-4178-ba38-bdfe1e84f067
ae24af24-8ec1-403d-ac9a-e885ffa0bbc3	96.00	2500	3	c8e2f9ae-385f-43c9-9ba7-e5416112975c
8dbb59a7-4577-4b71-a13d-177328e4d7e2	45.00	1000	2	c8e2f9ae-385f-43c9-9ba7-e5416112975c
39436edf-7956-48e1-9982-3bb00be3d3ad	30.00	500	1	c8e2f9ae-385f-43c9-9ba7-e5416112975c
6d97b7f6-d304-4cd3-a9b5-68814dee61b2	76.80	2500	3	c50afce6-a0bd-4914-b506-50e99a1faaf4
203fac76-a465-48f7-b693-d9b607553ea1	36.00	1000	2	c50afce6-a0bd-4914-b506-50e99a1faaf4
dcca84df-eca5-4f0c-b814-03d7adc8c94d	24.00	500	1	c50afce6-a0bd-4914-b506-50e99a1faaf4
9a76257a-40e5-4cf5-b192-7b4eced36e5c	57.60	2500	3	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
0749d909-213e-422e-99da-c4cdb09c87d6	27.00	1000	2	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
8003d820-bf58-4a41-8b01-0519c6963f4c	18.00	500	1	4cd1ad0a-b8c8-4d4d-b635-2c736de928f8
7836bc0c-0b80-4838-9ed3-2b4fa84f5868	64.00	2500	3	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
cd11ef1a-a861-48d8-9fd3-7018c6d0cdb8	30.00	1000	2	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
d51781f5-d923-4807-a59d-38fe4560f60f	20.00	500	1	b2ecc9d8-31b8-414e-ba8e-fc7281149fc7
e04ff82a-2acb-4c98-b854-c3d27822df4b	60.80	2500	3	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
9bc3d60c-4522-413a-903a-8e04f9056479	28.50	1000	2	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
96b57363-44f5-42b8-a849-95a78323812d	19.00	500	1	6e6fba56-6b89-43a7-9bcf-f0bd42acba9c
29b0ba4b-a9aa-4b5f-87c4-b5528c307af1	64.00	2500	3	7bb29c68-6d79-46ba-a662-743f7cd78b1e
e42f5bbc-a2af-42ac-a6e8-053a51a4cd2a	30.00	1000	2	7bb29c68-6d79-46ba-a662-743f7cd78b1e
40ed0f3b-3d8c-4f11-820d-641c24caa902	20.00	500	1	7bb29c68-6d79-46ba-a662-743f7cd78b1e
c0943272-eb30-46b6-a108-bc7527b63d8a	64.00	2500	3	1584ae3c-a607-42a3-a906-192ac2fc8bfe
2c782d6b-297e-433a-ad45-09df98da9cfa	30.00	1000	2	1584ae3c-a607-42a3-a906-192ac2fc8bfe
551f5b75-831d-4ee6-9c12-70797bebe851	20.00	500	1	1584ae3c-a607-42a3-a906-192ac2fc8bfe
10b2ac71-b84d-450b-8049-446b3f2d2ec4	80.00	2500	3	a948564e-d2e4-406a-9f38-1973fc377675
cc48e773-825a-4970-8200-ce3384fa08e3	37.50	1000	2	a948564e-d2e4-406a-9f38-1973fc377675
199b6aa3-df31-49c4-892f-499996a994cd	25.00	500	1	a948564e-d2e4-406a-9f38-1973fc377675
1feceeab-d5f3-4b82-96fa-039eb334cf37	96.00	2500	3	116655bc-452a-4982-a7d2-f464752a949d
fb173e16-9fdb-4fb2-a0be-d3015cc40f8e	45.00	1000	2	116655bc-452a-4982-a7d2-f464752a949d
cbca4836-dc6c-431b-ad31-c9d2cf6a300a	30.00	500	1	116655bc-452a-4982-a7d2-f464752a949d
76976a59-3da4-4610-84d5-2e30897ce3f1	60.80	2500	3	fa656ba8-e4bf-45f5-a776-e50bf8655260
9d459d8c-7af7-478e-950e-722897de8d6a	28.50	1000	2	fa656ba8-e4bf-45f5-a776-e50bf8655260
8c8e84c9-3e7e-47fb-b442-73d2faf7430b	19.00	500	1	fa656ba8-e4bf-45f5-a776-e50bf8655260
6f93df56-133e-4df1-8a12-312c65acb8df	2.00	50	1	82b26352-23ab-4b79-9232-d1d0d62fe418
91b277c2-dde5-4881-ab31-69e9f8c8263c	32.00	250	3	657fb80e-07af-4db2-8176-6c13a8a1b0a1
236900db-a948-4375-9c30-63983e97c6d3	14.40	100	2	657fb80e-07af-4db2-8176-6c13a8a1b0a1
fa0e0bb0-2732-43c8-8950-cfeeb45b84aa	8.00	50	1	657fb80e-07af-4db2-8176-6c13a8a1b0a1
aee753b5-dbc8-4add-8269-e41e6efb9ed6	8.00	250	3	b7dde16a-5767-485d-a581-a57b6fde3043
8d8aba02-9df2-4448-9dd2-a77f3424f21d	3.60	100	2	b7dde16a-5767-485d-a581-a57b6fde3043
fb386b8b-a1e0-4ac0-b3d2-67dbd50529d5	2.00	50	1	b7dde16a-5767-485d-a581-a57b6fde3043
16d67259-384d-486b-a44d-542cb982105e	60.00	250	3	adec4528-0e01-4628-9272-bc786c70d96f
25378280-4ca3-4e1d-82a4-8d7a4857921b	27.00	100	2	adec4528-0e01-4628-9272-bc786c70d96f
f9502ccc-2d8c-4410-8cfa-eb78ed141885	15.00	50	1	adec4528-0e01-4628-9272-bc786c70d96f
7853ea84-b65d-4a00-a69f-2a61d0aaf8ea	48.00	250	3	b8247ee9-c10e-4853-9060-5a45eb09b3fe
3418b056-5079-470c-ae76-18667d869cbf	21.60	100	2	b8247ee9-c10e-4853-9060-5a45eb09b3fe
f1ae6121-a2cb-4be6-9076-a9ac880b9555	12.00	50	1	b8247ee9-c10e-4853-9060-5a45eb09b3fe
4c8f9046-04f5-4e70-84d3-560e3dcbe3f9	24.00	250	3	559b51f9-0ff2-4e14-b439-7a7cc3f72d7e
0cd6a53f-9910-417a-a2bf-82d8787b230b	10.80	100	2	559b51f9-0ff2-4e14-b439-7a7cc3f72d7e
7c0cfd64-57e0-4b8c-aec0-20df0fecbafb	6.00	50	1	559b51f9-0ff2-4e14-b439-7a7cc3f72d7e
31d7d088-f70f-4d46-bc06-cb90ecfcd16e	40.00	250	3	4cd68003-0fff-4594-9d2f-91eed3bd5514
f6f4b3b7-8d9f-4cde-b662-5c623106d03e	18.00	100	2	4cd68003-0fff-4594-9d2f-91eed3bd5514
39ab6d25-dbe4-46f8-b245-ec8a9c40500d	10.00	50	1	4cd68003-0fff-4594-9d2f-91eed3bd5514
dfa3595f-ce5b-4a27-9b43-cc0fc9fb5a1b	20.00	250	3	ce2527bf-569c-41cc-a6de-997605b1d40d
e2bc78eb-918a-4a8a-99f0-8107561c719f	9.00	100	2	ce2527bf-569c-41cc-a6de-997605b1d40d
20a6dac2-90d2-471f-8018-38e946c7e831	5.00	50	1	ce2527bf-569c-41cc-a6de-997605b1d40d
4d07a4c7-0850-4d83-b6d7-2958e4587782	48.00	250	3	610bc72f-818a-4e73-8702-53586a0de19a
09fb223d-4afc-42ed-9cec-93f6b722212c	21.60	100	2	610bc72f-818a-4e73-8702-53586a0de19a
489a74ea-4894-4d60-990f-5646eb4e68ae	12.00	50	1	610bc72f-818a-4e73-8702-53586a0de19a
b397b8ba-314a-4535-839e-aee408494ff0	40.00	5	3	331c165c-cb29-4e7d-bd8c-6659da4ad7d5
b7b051c5-3647-472b-8bbf-1cb975ca3e50	25.00	3	2	331c165c-cb29-4e7d-bd8c-6659da4ad7d5
d195e776-f9a9-439f-89e3-38cda87bf01a	10.00	1	1	331c165c-cb29-4e7d-bd8c-6659da4ad7d5
1b3c8314-8f40-4427-a7ae-aa8a3a35fb4d	36.00	5	3	86793400-2856-4837-bae8-e5aaf26fdc69
5cdb2473-a6bf-4fd5-868b-99fe401bcb76	22.50	3	2	86793400-2856-4837-bae8-e5aaf26fdc69
6f703f4a-54c6-4af0-aea5-eed332bfd38a	9.00	1	1	86793400-2856-4837-bae8-e5aaf26fdc69
9325df83-bbe8-4810-b8ec-5d63162a3eb9	48.00	5	3	ea68cd2a-5e5b-437a-926d-9e110bb88058
7c1ecd07-3065-4f47-9751-abdbd9f8b927	30.00	3	2	ea68cd2a-5e5b-437a-926d-9e110bb88058
f2564a04-ac5d-4950-97dd-9fbf2763c0c2	12.00	1	1	ea68cd2a-5e5b-437a-926d-9e110bb88058
0c85b3fa-a740-4089-801a-971de1652922	44.00	5	3	3efde787-c772-477d-ae03-d2a5fd49868b
29ef8e99-c434-4834-9b2d-552d43b93f9f	27.50	3	2	3efde787-c772-477d-ae03-d2a5fd49868b
8ff4e902-58ba-4cd6-889a-d635c86d5375	11.00	1	1	3efde787-c772-477d-ae03-d2a5fd49868b
c52cf867-abbd-4a9a-82df-edac8bb10b4a	52.00	5	3	8bcc3b47-be5e-4b70-93fc-6e5c0e38dba3
4a6b6404-01f3-44f4-9fbc-dcac17f7d9a6	32.50	3	2	8bcc3b47-be5e-4b70-93fc-6e5c0e38dba3
efe21735-c03e-44c6-85fb-2e7c2f4e75e4	13.00	1	1	8bcc3b47-be5e-4b70-93fc-6e5c0e38dba3
af8f7f68-b3c7-41d1-ac4e-6c186aedeaa9	64.00	5	3	d3aaf05d-fca8-4037-a196-8826d2921eb5
2563e3d9-6e17-4626-a6d6-9f608f493159	40.00	3	2	d3aaf05d-fca8-4037-a196-8826d2921eb5
adefa655-eb27-4413-b583-ff0b7f758107	16.00	1	1	d3aaf05d-fca8-4037-a196-8826d2921eb5
8f690338-23b4-4f11-ad12-d32373a3c17d	44.00	5	3	7fc39472-cf1c-4a28-97f2-2345c9a62f3c
c52658b0-785b-4228-aebf-703bc98c56e1	27.50	3	2	7fc39472-cf1c-4a28-97f2-2345c9a62f3c
63261742-6b31-4880-9173-7e91091f4760	11.00	1	1	7fc39472-cf1c-4a28-97f2-2345c9a62f3c
d5d60371-3a9e-4aa7-a3c7-2f98f2ef843a	32.00	5	3	9fea8b2c-682e-443d-a292-23c06eaeb3ea
587c9afd-816a-444b-963d-6ed97948bfa4	20.00	3	2	9fea8b2c-682e-443d-a292-23c06eaeb3ea
915107ab-23b6-4885-9b88-1cc4284abc1f	8.00	1	1	9fea8b2c-682e-443d-a292-23c06eaeb3ea
12d46ba6-572e-4332-b944-d356d455f4d3	100.00	5	3	f1480084-32f7-4cd0-9a1f-a66fbcd30ea0
88f77341-b2bb-40b0-99f6-6715de5860ae	62.50	3	2	f1480084-32f7-4cd0-9a1f-a66fbcd30ea0
a40c0d2f-4283-455e-9e3e-2f8e29f5b1cd	25.00	1	1	f1480084-32f7-4cd0-9a1f-a66fbcd30ea0
2c8986f4-fac1-4b56-8ffd-26e7c1fc9781	32.00	250	3	d2461563-bae2-493c-9aec-9581f6e937a3
3b847caa-02f8-4cd1-8907-e9a52610e83e	14.40	100	2	d2461563-bae2-493c-9aec-9581f6e937a3
82ba1567-08a6-4fde-bee4-a4fae98dedf4	8.00	50	1	d2461563-bae2-493c-9aec-9581f6e937a3
81835856-a52f-480a-8ed8-c982a19d92fa	20.00	250	3	99420ca6-a8ff-4aa2-bcde-03181677ba26
6f2fd2b0-87ca-493c-b058-883a720e345f	9.00	100	2	99420ca6-a8ff-4aa2-bcde-03181677ba26
de03adb1-5abc-4b71-986f-c791cad7b955	5.00	50	1	99420ca6-a8ff-4aa2-bcde-03181677ba26
6309f4f9-e535-4d32-85ba-bf40f3ee81b5	28.00	250	3	889bc829-dcf8-4f8d-aba5-88ac06037a39
671118d8-ea8c-48e3-800f-ff0a8ccb5ec2	12.60	100	2	889bc829-dcf8-4f8d-aba5-88ac06037a39
ec556056-64bf-471b-9f40-288977dbe269	7.00	50	1	889bc829-dcf8-4f8d-aba5-88ac06037a39
293cdd37-7f1b-4829-826c-b4af95a8a52d	12.00	250	3	33f1bebe-3e36-4fd5-a623-ef092d18a638
f7080a15-a4d5-418a-bebc-a67908c0a346	5.40	100	2	33f1bebe-3e36-4fd5-a623-ef092d18a638
2eb8c8ea-8753-4e16-96ac-6329fe5c0262	3.00	50	1	33f1bebe-3e36-4fd5-a623-ef092d18a638
1841b3d4-ae4b-4a48-95fb-966fa724ff90	88.00	5	3	f1c87cd7-84f7-4b56-84b5-a5557835e192
af13a007-dd88-4cd0-8cf3-905af43693d0	55.00	3	2	f1c87cd7-84f7-4b56-84b5-a5557835e192
095be41c-45b6-433d-8eee-206027a2d6b7	22.00	1	1	f1c87cd7-84f7-4b56-84b5-a5557835e192
0535b5f2-c2bf-425b-84fa-bfe84e2c59eb	112.00	5	3	bcc03a57-f819-447d-8fb7-cf3b1b6a74d1
c6675768-7c41-4e0a-aec3-3929c95cf0a8	70.00	3	2	bcc03a57-f819-447d-8fb7-cf3b1b6a74d1
cd9c1b3d-025e-4133-8ca1-d21c9649e168	28.00	1	1	bcc03a57-f819-447d-8fb7-cf3b1b6a74d1
952b68cc-430a-4d64-81bf-b411cae3287d	96.00	5	3	b8ac33a9-1beb-4b18-98a8-b45d39259271
a5d0f348-26a5-4461-95ad-e908ee645f6d	60.00	3	2	b8ac33a9-1beb-4b18-98a8-b45d39259271
926ea3d4-5be2-4645-bf55-4fa76e42486b	24.00	1	1	b8ac33a9-1beb-4b18-98a8-b45d39259271
ec2f3344-050e-4ac8-be29-6024e2591086	56.00	5	3	5a2d6cfa-bc9d-48fc-a2a0-ac853dc1933e
b863a16f-920e-4517-8764-8ee2dae8d064	35.00	3	2	5a2d6cfa-bc9d-48fc-a2a0-ac853dc1933e
c2edae9d-fa37-4432-8a4b-f2188509fcb9	14.00	1	1	5a2d6cfa-bc9d-48fc-a2a0-ac853dc1933e
067cb737-516b-49ef-8d37-47707ed43025	52.00	5	3	876c23a5-9d73-4e40-be72-bf379d69c21a
6a4d4a73-3a63-48ae-bd5b-2abfd7a2c764	32.50	3	2	876c23a5-9d73-4e40-be72-bf379d69c21a
d1f8d6c8-263e-4cd9-902c-77d84dec2f8b	13.00	1	1	876c23a5-9d73-4e40-be72-bf379d69c21a
d751e0dc-7f01-4750-8d6f-9588a352c8e1	24.00	5	3	e372bb93-00f6-46cb-baa4-9e0e820b2e92
35926f18-2684-40da-816f-cc3376aa9047	15.00	3	2	e372bb93-00f6-46cb-baa4-9e0e820b2e92
e7032aa1-0c01-4079-880c-cd85c5069c22	6.00	1	1	e372bb93-00f6-46cb-baa4-9e0e820b2e92
ed9079c7-15be-49ae-b49d-5b6e80994494	80.00	5	3	9de8cdb2-10de-4075-81e6-9a09ab326040
46cde5af-60ce-46e0-b901-69a7b8878db8	50.00	3	2	9de8cdb2-10de-4075-81e6-9a09ab326040
2d63bdb3-0c31-4ae1-b00e-6d627695303b	20.00	1	1	9de8cdb2-10de-4075-81e6-9a09ab326040
89f59f3a-e633-4880-a058-b4ecb4161ec8	12.00	500	1	fbd34467-d662-4ae4-9c25-1c85a478243e
2c885edf-b831-461c-b85b-f4dc3f0cffcf	14.00	500	1	b69f701e-9fa2-437a-8db4-8d123f76bf94
cb194de0-fede-4ac4-bc95-5614c8a08ac5	18.00	1000	2	fbd34467-d662-4ae4-9c25-1c85a478243e
537c62f2-cb15-4048-9fda-ee6fa08ee470	21.00	1000	2	b69f701e-9fa2-437a-8db4-8d123f76bf94
cb957203-ed0c-4a7f-90bf-5b635c253f8b	38.40	2500	3	fbd34467-d662-4ae4-9c25-1c85a478243e
3a1a0eab-cef6-4c2e-a14f-995e1ecd9fde	44.80	2500	3	b69f701e-9fa2-437a-8db4-8d123f76bf94
\.


--
-- Data for Name: catalog_products; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.catalog_products (id, active, badge, created_at, featured, long_desc, name, original_price, short_desc, slug, sort_order, updated_at, brand_id, category_id) FROM stdin;
76421511-87ff-4ace-be67-d578b6271acf	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Form - Makbuz	\N	Form - Makbuz — profesyonel baskı.	form-makbuz-urun	100	2026-06-02 09:52:14.645762+00	\N	6ae8201c-1f24-4964-a0cd-7cffff08b4be
83303a4f-d1f6-45b8-b33c-3a9744b36221	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Islak Mendil	\N	Islak Mendil — profesyonel baskı.	islak-mendil	100	2026-06-02 09:52:14.645762+00	\N	3de0b0a2-693b-4d29-b588-1b9812db9e94
dd052ed1-646a-4dd4-bf19-6a24eef5e2af	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Baskılı Bardak	\N	Baskılı Bardak — profesyonel baskı.	baskili-bardak	100	2026-06-02 09:52:14.645762+00	\N	dc7e1291-48e3-4cb5-a38d-7ab905aa5145
8688f2dc-c168-42b0-a950-f999c383712f	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Bloknot	\N	Bloknot — profesyonel baskı.	promo-bloknot	100	2026-06-02 09:52:14.645762+00	\N	909aac03-267a-45e5-9b98-2fac2ba34a47
97cfc740-1f60-40cc-b3fb-e572ff01a96f	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Magnet	\N	Magnet — profesyonel baskı.	promo-magnet	100	2026-06-02 09:52:14.645762+00	\N	93bfaf95-800a-4ae5-96ba-674d9bc0a8bb
ce7cf5a5-65b3-4a06-948b-3255c002eab4	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Mousepad	\N	Mousepad — profesyonel baskı.	mousepad	100	2026-06-02 09:52:14.645762+00	\N	d3f8d09e-fa37-405f-bf7b-f3d3cd7bd424
bd6e9414-9a75-43b2-9419-b1b88b7c5880	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Takvim	\N	Takvim — profesyonel baskı.	takvim	100	2026-06-02 09:52:14.645762+00	\N	1b7361ef-5d50-48ac-b3a6-468ade1c7444
744af4b7-c027-45a7-a3b3-2a20a9bb67cd	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Ajanda	\N	Ajanda — profesyonel baskı.	ajanda	100	2026-06-02 09:52:14.645762+00	\N	ab5eef05-1f59-4731-a7fb-204367564c7b
1334b694-251c-44b3-9a65-c00fefc6af43	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Kalem	\N	Kalem — profesyonel baskı.	kalem	100	2026-06-02 09:52:14.645762+00	\N	6e897f17-c170-48cd-b49b-84a65ffed17c
82b26352-23ab-4b79-9232-d1d0d62fe418	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Çakmak	\N	Çakmak — profesyonel baskı.	cakmak	100	2026-06-02 09:52:14.645762+00	\N	ad5b0099-d0f2-4539-b5e3-2b5b93f038ac
657fb80e-07af-4db2-8176-6c13a8a1b0a1	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Termos	\N	Termos — profesyonel baskı.	termos	100	2026-06-02 09:52:14.645762+00	\N	127d2e22-4919-4b6f-91b9-06ffe8340155
b7dde16a-5767-485d-a581-a57b6fde3043	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Anahtarlık	\N	Anahtarlık — profesyonel baskı.	anahtarlik	100	2026-06-02 09:52:14.645762+00	\N	1e2d4cb1-bbc7-4b02-a345-0d782fd78426
adec4528-0e01-4628-9272-bc786c70d96f	t	\N	2026-06-02 09:52:14.645762+00	f	\N	VIP Set	\N	VIP Set — profesyonel baskı.	vip-set	100	2026-06-02 09:52:14.645762+00	\N	555a2f06-5b50-46fd-96df-89b4b1227b60
b8247ee9-c10e-4853-9060-5a45eb09b3fe	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Plaket	\N	Plaket — profesyonel baskı.	plaket	100	2026-06-02 09:52:14.645762+00	\N	028057ca-8cc9-4e10-8596-33b9d1abbd2c
559b51f9-0ff2-4e14-b439-7a7cc3f72d7e	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Tişört	\N	Tişört — profesyonel baskı.	tisort	100	2026-06-02 09:52:14.645762+00	\N	babb3609-cc5f-4793-9cfe-cb7ae050c88b
4cd68003-0fff-4594-9d2f-91eed3bd5514	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Promosyon Paketi	\N	Promosyon Paketi — profesyonel baskı.	promosyon-paketi	100	2026-06-02 09:52:14.645762+00	\N	114983ba-6bba-4799-8b56-5f5945577ae5
ce2527bf-569c-41cc-a6de-997605b1d40d	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Masa İsimliği	\N	Masa İsimliği — profesyonel baskı.	masa-isimligi	100	2026-06-02 09:52:14.645762+00	\N	de83425b-50b7-4300-90cf-33b3920830da
610bc72f-818a-4e73-8702-53586a0de19a	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Powerbank	\N	Powerbank — profesyonel baskı.	powerbank-urun	100	2026-06-02 09:52:14.645762+00	\N	a8564917-516e-427b-97e4-0ef08f36fc51
331c165c-cb29-4e7d-bd8c-6659da4ad7d5	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Vinil	\N	Vinil — profesyonel baskı.	vinil	100	2026-06-02 09:52:14.645762+00	\N	8f6217d7-c331-4112-92b1-34ebd309dcd2
86793400-2856-4837-bae8-e5aaf26fdc69	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Folyo	\N	Folyo — profesyonel baskı.	folyo	100	2026-06-02 09:52:14.645762+00	\N	98f31fd9-b779-408e-b9fa-64a51a9d1215
ea68cd2a-5e5b-437a-926d-9e110bb88058	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Vinil Branda Afiş	\N	Vinil Branda Afiş — profesyonel baskı.	vinil-branda-afis	100	2026-06-02 09:52:14.645762+00	\N	a728f015-00b8-499f-a420-e2ce3dd0ff58
3efde787-c772-477d-ae03-d2a5fd49868b	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Mat Folyo	\N	Mat Folyo — profesyonel baskı.	mat-folyo-urun	100	2026-06-02 09:52:14.645762+00	\N	5d6ce25d-22eb-4fbe-aadd-75f49734c336
8bcc3b47-be5e-4b70-93fc-6e5c0e38dba3	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Mesh Delikli Vinil	\N	Mesh Delikli Vinil — profesyonel baskı.	mesh-vinil	100	2026-06-02 09:52:14.645762+00	\N	0dc83881-acd8-4a59-973a-a26b9188fe45
d3aaf05d-fca8-4037-a196-8826d2921eb5	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Işıklı Vinil	\N	Işıklı Vinil — profesyonel baskı.	isikli-vinil-urun	100	2026-06-02 09:52:14.645762+00	\N	d5cc2218-1def-4e30-934a-f589b53a5d62
7fc39472-cf1c-4a28-97f2-2345c9a62f3c	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Şeffaf Folyo	\N	Şeffaf Folyo — profesyonel baskı.	seffaf-folyo-urun	100	2026-06-02 09:52:14.645762+00	\N	216aa095-a2b2-4287-8bf0-3aa48cd1befb
9fea8b2c-682e-443d-a292-23c06eaeb3ea	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Afiş	\N	Afiş — profesyonel baskı.	afis-urun	100	2026-06-02 09:52:14.645762+00	\N	f179f36b-97ef-4553-8556-f3b723899a33
f1480084-32f7-4cd0-9a1f-a66fbcd30ea0	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Kanvas Tablo	\N	Kanvas Tablo — profesyonel baskı.	kanvas-tablo	100	2026-06-02 09:52:14.645762+00	\N	9baca02a-ad1f-4a0d-8145-0d3d95c12dba
d2461563-bae2-493c-9aec-9581f6e937a3	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Fotoğraf Baskı	\N	Fotoğraf Baskı — profesyonel baskı.	fotograf-baski-urun	100	2026-06-02 09:52:14.645762+00	\N	5e46b861-9b7c-45bd-a40a-1f473b30dfe1
99420ca6-a8ff-4aa2-bcde-03181677ba26	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Kupa Baskı	\N	Kupa Baskı — profesyonel baskı.	kupa-baski	100	2026-06-02 09:52:14.645762+00	\N	b053f4b9-78e3-45a9-a536-b8ef844b5a4b
889bc829-dcf8-4f8d-aba5-88ac06037a39	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Puzzle Baskı	\N	Puzzle Baskı — profesyonel baskı.	puzzle-baski	100	2026-06-02 09:52:14.645762+00	\N	0199dfc8-1809-4bd7-998f-1eca3d2db26c
33f1bebe-3e36-4fd5-a623-ef092d18a638	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Foto Kart	\N	Foto Kart — profesyonel baskı.	foto-kart-urun	100	2026-06-02 09:52:14.645762+00	\N	ac6460b2-7caa-441e-bc72-7a933d4a3758
f1c87cd7-84f7-4b56-84b5-a5557835e192	t	\N	2026-06-02 09:52:14.645762+00	f	\N	MDF Tablo	\N	MDF Tablo — profesyonel baskı.	mdf-tablo-urun	100	2026-06-02 09:52:14.645762+00	\N	91f6da40-6558-488f-98ca-a412905f8578
163c30ac-b337-47c9-bd13-7f1b0a0cb0fa	t	\N	2026-06-02 09:29:23.63945+00	t	\N	Sıvama Kartvizit	\N	Sıvama tekniğiyle parlak yüzey, premium görünüm.	sivama-kartvizit	10	2026-06-02 09:29:23.63945+00	\N	be969a50-86ce-4044-96e8-490b23552f7d
2d10ce43-3226-43ed-9d4a-1b6781035b09	t	\N	2026-06-02 09:29:23.63945+00	t	\N	3 Katlı Sandviç Kartvizit	\N	3 katmanlı kalın yapı, lüks ve dayanıklı dokunuş.	3-katli-sandvic-kartvizit	11	2026-06-02 09:29:23.63945+00	\N	be969a50-86ce-4044-96e8-490b23552f7d
7c838a0f-3cf5-4087-9793-7ac22befe06f	t	\N	2026-06-02 09:29:23.63945+00	f	\N	Tuale Fantazi Kartvizit	\N	Tuale dokulu özel kağıt, sanatsal ve özgün etki.	tuale-fantazi-kartvizit	12	2026-06-02 09:29:23.63945+00	\N	be969a50-86ce-4044-96e8-490b23552f7d
c00fe121-4b31-42be-bedd-a7144085ed84	t	\N	2026-06-02 09:29:23.63945+00	f	\N	Gofreli Kartvizit	\N	Kabartma desenli, dokunsal etki yaratan kartvizit.	gofreli-kartvizit	20	2026-06-02 09:29:23.63945+00	\N	68e80d8b-96ad-4e66-9828-e4b70a7a2ef6
faa09eab-a088-4178-ba38-bdfe1e84f067	t	\N	2026-06-02 09:29:23.63945+00	t	\N	Kabartma Laklı Kartvizit	\N	Selektif lak kabartma, modern ve şık tasarım.	kabartma-lakli-kartvizit	21	2026-06-02 09:29:23.63945+00	\N	68e80d8b-96ad-4e66-9828-e4b70a7a2ef6
c8e2f9ae-385f-43c9-9ba7-e5416112975c	t	\N	2026-06-02 09:29:23.63945+00	t	\N	Altın Yaldızlı Kartvizit	\N	Altın yaldız detayları ile premium görünüm.	altin-yaldizli-kartvizit	30	2026-06-02 09:29:23.63945+00	\N	2783e48a-b502-454b-bffe-0af7301ec927
c50afce6-a0bd-4914-b506-50e99a1faaf4	t	\N	2026-06-02 09:29:23.63945+00	f	\N	Yumuşak Dokulu Kartvizit	\N	SoftTouch teknolojisi, yumuşak ve premium hisli yüzey.	softtouch-kartvizit	40	2026-06-02 09:29:23.63945+00	\N	8836a040-5619-4e2e-a1c6-639cdf9783c5
4cd1ad0a-b8c8-4d4d-b635-2c736de928f8	t	\N	2026-06-02 09:29:23.63945+00	f	\N	Kare Kartvizit	\N	9x9cm kare format, dikkat çekici alternatif tasarım.	kare-kartvizit	50	2026-06-02 09:29:23.63945+00	\N	43a784da-0d7b-40e8-bfef-7adb8ef96f21
b2ecc9d8-31b8-414e-ba8e-fc7281149fc7	t	\N	2026-06-02 09:29:23.63945+00	f	\N	Katlamalı Kartvizit	\N	Katlanabilir tasarım, içinde detay sunma alanı.	katlamali-kartvizit	51	2026-06-02 09:29:23.63945+00	\N	43a784da-0d7b-40e8-bfef-7adb8ef96f21
6e6fba56-6b89-43a7-9bcf-f0bd42acba9c	t	\N	2026-06-02 09:29:23.63945+00	f	\N	Oval Kesim Kartvizit	\N	Köşeleri oval kesim, modern ve şık silüet.	oval-kesim-kartvizit	52	2026-06-02 09:29:23.63945+00	\N	43a784da-0d7b-40e8-bfef-7adb8ef96f21
7bb29c68-6d79-46ba-a662-743f7cd78b1e	t	\N	2026-06-02 09:29:23.63945+00	f	\N	İki Kenar Oval Kartvizit	\N	Üst ve alt kenarları oval, dikkat çekici form.	iki-kenar-oval-kartvizit	53	2026-06-02 09:29:23.63945+00	\N	43a784da-0d7b-40e8-bfef-7adb8ef96f21
1584ae3c-a607-42a3-a906-192ac2fc8bfe	t	\N	2026-06-02 09:29:23.63945+00	f	\N	Takvimli Kartvizit	\N	Arkasında takvim baskı, uzun süreli akılda kalıcılık.	takvimli-kartvizit	54	2026-06-02 09:29:23.63945+00	\N	43a784da-0d7b-40e8-bfef-7adb8ef96f21
a948564e-d2e4-406a-9f38-1973fc377675	t	\N	2026-06-02 09:29:23.63945+00	f	\N	PVC Kaplı Kartvizit	\N	PVC kaplama, su geçirmez ve uzun ömürlü kullanım.	pvc-kapli-kartvizit	60	2026-06-02 09:29:23.63945+00	\N	961f50a7-1d5c-4c8a-854a-ba6620ab9074
116655bc-452a-4982-a7d2-f464752a949d	t	\N	2026-06-02 09:29:23.63945+00	t	\N	Şeffaf Kartvizit	\N	Şeffaf plastik malzeme, lüks ve dikkat çekici.	seffaf-kartvizit	61	2026-06-02 09:29:23.63945+00	\N	961f50a7-1d5c-4c8a-854a-ba6620ab9074
fa656ba8-e4bf-45f5-a776-e50bf8655260	t	\N	2026-06-02 09:29:23.63945+00	t	\N	Kraft Kartvizit	\N	Kraft kağıt, doğal ve özgün görünüm. Eco friendly.	kraft-kartvizit	70	2026-06-02 09:29:23.63945+00	\N	0613535f-9f95-4160-883b-af5cc07ae7b2
bcc03a57-f819-447d-8fb7-cf3b1b6a74d1	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Atatürk Tablosu	\N	Atatürk Tablosu — profesyonel baskı.	ataturk-tablo-urun	100	2026-06-02 09:52:14.645762+00	\N	939e7449-2abc-4920-9da0-8893d5645c78
b8ac33a9-1beb-4b18-98a8-b45d39259271	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Dekoratif Tablo	\N	Dekoratif Tablo — profesyonel baskı.	dekoratif-tablo-urun	100	2026-06-02 09:52:14.645762+00	\N	dab555d3-3224-4863-9d1d-e4725f8600c8
5a2d6cfa-bc9d-48fc-a2a0-ac853dc1933e	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Mesh Emlak Afişi	\N	Mesh Emlak Afişi — profesyonel baskı.	mesh-emlak-afisi-urun	100	2026-06-02 09:52:14.645762+00	\N	27d430ad-e9fb-4554-bbee-2b3551d059a7
876c23a5-9d73-4e40-be72-bf379d69c21a	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Vinil Branda Emlak Afişi	\N	Vinil Branda Emlak Afişi — profesyonel baskı.	vinil-emlak-afisi	100	2026-06-02 09:52:14.645762+00	\N	0e861770-c504-4b83-a1c6-7e2daed1c2e8
e372bb93-00f6-46cb-baa4-9e0e820b2e92	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Kağıt Emlak Afişi	\N	Kağıt Emlak Afişi — profesyonel baskı.	kagit-emlak-afisi	100	2026-06-02 09:52:14.645762+00	\N	c0f86c4c-4a3b-494a-aca2-7e2267b2a8ba
9de8cdb2-10de-4075-81e6-9a09ab326040	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Emlak Tabelası	\N	Emlak Tabelası — profesyonel baskı.	emlak-tabelasi-urun	100	2026-06-02 09:52:14.645762+00	\N	f2272869-e576-4a72-b568-16875b3bb6d6
fbd34467-d662-4ae4-9c25-1c85a478243e	t	\N	2026-06-02 10:51:57.143448+00	f	\N	Standart Kartvizit	\N	Ekonomik, hızlı teslim standart kartvizit.	standart-kartvizit-urun	5	2026-06-02 10:51:57.143448+00	\N	7119c09d-a9fd-41f5-a3b8-0ecc8eeed95e
b69f701e-9fa2-437a-8db4-8d123f76bf94	t	\N	2026-06-02 10:51:57.143448+00	f	\N	Ekspres Kartvizit	\N	Aynı/sonraki gün teslim kartvizit.	ekspres-kartvizit	8	2026-06-02 10:51:57.143448+00	\N	708668f4-698b-417b-97bf-0102423f593c
96968a16-96f7-4647-bd50-4ec8dfe32ebf	t	\N	2026-06-02 09:52:14.645762+00	f	\N	El İlanı	\N	El İlanı — profesyonel baskı.	el-ilani	100	2026-06-02 09:52:14.645762+00	\N	06938a6c-5437-4422-8a67-5c516fe91666
3c6b54cb-f751-40f9-a7c7-a7fa64713001	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Ekonomik El İlanı	\N	Ekonomik El İlanı — profesyonel baskı.	ekonomik-el-ilani	100	2026-06-02 09:52:14.645762+00	\N	edf39510-c96a-4075-97ab-9d29097a3553
204a4763-e4e6-46f7-a5cd-d51082ff28b2	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Katlamalı Broşür	\N	Katlamalı Broşür — profesyonel baskı.	katlamali-brosur-urun	100	2026-06-02 09:52:14.645762+00	\N	b8d56b13-2748-44a6-899a-d173367b403d
5a4a8ef3-0189-4227-a5c8-af0abbb98610	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Amerikan Servis	\N	Amerikan Servis — profesyonel baskı.	amerikan-servis	100	2026-06-02 09:52:14.645762+00	\N	2e997fe3-7d4f-4c9a-92dd-0ce25256cbb8
e3bf240f-f342-48bf-856d-d1c9f712d875	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Masa Sümeni	\N	Masa Sümeni — profesyonel baskı.	masa-sumeni	100	2026-06-02 09:52:14.645762+00	\N	7fd43c70-ac25-43a8-997d-9bee5fcdc5f2
7d6c8738-455d-488c-a3fc-d5ecf745efa9	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Kapı Askı Broşürü	\N	Kapı Askı Broşürü — profesyonel baskı.	kapi-aski-brosuru-urun	100	2026-06-02 09:52:14.645762+00	\N	5a4f40f1-cc9d-4e79-8045-b7fefbcd4537
2f926ce2-242c-495d-983d-2c34a4e5e60d	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Kırımlı El İlanı	\N	Kırımlı El İlanı — profesyonel baskı.	kirimli-el-ilani	100	2026-06-02 09:52:14.645762+00	\N	fe9fef34-dbfa-4314-8f65-2c1359352c58
4c6e7f4a-9800-462f-a1a9-751129cc25be	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Katalog Baskı	\N	Katalog Baskı — profesyonel baskı.	katalog-urun	100	2026-06-02 09:52:14.645762+00	\N	d7b967d1-80b4-46cd-93f8-e3f7f532b5b9
04bce68a-3bce-4c0e-a1d5-6d3dcd09b053	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Gümüş Makam Bayrağı	\N	Gümüş Makam Bayrağı — profesyonel baskı.	makam-bayragi	100	2026-06-02 09:52:14.645762+00	\N	77f4b716-1b7c-471d-99cc-3b8203baae71
bb54d991-8205-4970-b0a7-a49473e7a4d6	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Masa Bayrağı	\N	Masa Bayrağı — profesyonel baskı.	masa-bayragi-urun	100	2026-06-02 09:52:14.645762+00	\N	8e970dec-7525-4def-9d67-5c2ec3e74614
720314fd-1abd-4c8a-a79a-592e5d6c8650	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Gönder Bayrağı	\N	Gönder Bayrağı — profesyonel baskı.	gonder-bayragi-urun	100	2026-06-02 09:52:14.645762+00	\N	94198885-8d1c-4989-bb5e-baa72b272522
a2f7ea19-abe4-4998-a223-993b2a5d3090	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Yelken Bayrak	\N	Yelken Bayrak — profesyonel baskı.	yelken-bayrak-urun	100	2026-06-02 09:52:14.645762+00	\N	5e1630ff-a606-4e70-a6a3-a5ddd7827f25
b9ad2e9e-c746-45b0-8d29-2f23bd5697c7	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Kırlangıç Bayrak	\N	Kırlangıç Bayrak — profesyonel baskı.	kirlangic-bayrak-urun	100	2026-06-02 09:52:14.645762+00	\N	41b8301e-26b2-4f11-9565-4d189c9e48e5
dbeb2ecb-012f-43f5-a5dc-6523433db614	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Antetli Kağıt	\N	Antetli Kağıt — profesyonel baskı.	antetli-kagit	100	2026-06-02 09:52:14.645762+00	\N	f6760942-5d2b-4d98-b934-7dc430984b0e
d32e70ec-9b9d-4209-b8a0-3dc47e66f473	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Sertifika	\N	Sertifika — profesyonel baskı.	sertifika	100	2026-06-02 09:52:14.645762+00	\N	87df6959-6da6-4ca5-9077-8ba6e1c85333
9625f7c7-3058-4960-a42e-3b3648a5d1b4	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Reçete Baskı	\N	Reçete Baskı — profesyonel baskı.	recete-baski	100	2026-06-02 09:52:14.645762+00	\N	190c3c41-e1be-496b-8fe6-d9c9dcde3d38
379f4020-e851-4a34-9703-83e337682ca6	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Anket Formu	\N	Anket Formu — profesyonel baskı.	anket-formu	100	2026-06-02 09:52:14.645762+00	\N	4a6850e6-86a5-4b8c-ab11-539228096a50
ff92cf7d-b1aa-4baa-831f-861da7a21015	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Kartpostal	\N	Kartpostal — profesyonel baskı.	kartpostal	100	2026-06-02 09:52:14.645762+00	\N	a93c382e-d394-4e9b-928a-0f8b6097fce3
d6b6b2bd-36dd-45b8-918b-2af953a8c2b3	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Duba	\N	Duba — profesyonel baskı.	duba	100	2026-06-02 09:52:14.645762+00	\N	2e42287e-4ef4-4a49-a56a-809f437dbf76
fbabc552-7ac1-4d48-8f05-8dbd9841a931	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Stand	\N	Stand — profesyonel baskı.	stand	100	2026-06-02 09:52:14.645762+00	\N	b5523675-7142-47c5-a2f0-3de89eb83f4b
5c1c1bdc-91f1-4ddf-96b6-a3886c37e73c	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Display	\N	Display — profesyonel baskı.	display	100	2026-06-02 09:52:14.645762+00	\N	c425677e-d5b9-40f3-8287-9b69c180ae63
555185f3-fecb-44b9-90d1-c883fb78c45b	t	\N	2026-06-02 09:52:14.645762+00	f	\N	İş Güvenlik Levhası	\N	İş Güvenlik Levhası — profesyonel baskı.	is-guvenlik-levhasi	100	2026-06-02 09:52:14.645762+00	\N	663afecb-49b4-4b9e-a2fb-587e3a1acd85
7a991bcf-1c19-4725-a5c0-99cad0f76515	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Kapı İsimliği	\N	Kapı İsimliği — profesyonel baskı.	kapi-isimligi	100	2026-06-02 09:52:14.645762+00	\N	fdaef4e4-6b9b-40ed-b08b-55aa481e66be
93caa48c-b5d8-4289-96b8-e9b7ac2b8d9a	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Kaşe	\N	Kaşe — profesyonel baskı.	kase	100	2026-06-02 09:52:14.645762+00	\N	53be3dba-d3cc-400f-9057-fe78598bfdf8
0f113e07-2bd1-4bf3-90a4-f1bcfa491e72	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Tabela	\N	Tabela — profesyonel baskı.	tabela	100	2026-06-02 09:52:14.645762+00	\N	059101b4-71ed-43b6-945d-992a1548e25e
27217955-03b8-4a3f-b57e-bbff60ac1328	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Davetiye	\N	Davetiye — profesyonel baskı.	davetiye-urun	100	2026-06-02 09:52:14.645762+00	\N	e255e01e-c460-4c1b-8e8d-ac7badd16946
f506e467-1f89-4015-8f23-5f2a2aaca99c	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Roll-Up Banner	\N	Roll-Up Banner — profesyonel baskı.	roll-up-urun	100	2026-06-02 09:52:14.645762+00	\N	99e901fd-eef9-44b6-874e-0f32eaf90b59
9e29855f-90c3-4e56-8236-4de364524522	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Etiket	\N	Etiket — profesyonel baskı.	etiket	100	2026-06-02 09:52:14.645762+00	\N	4e8d7f6d-435d-4983-a5ce-00b7c97897d0
7b66d82d-b943-465b-9767-58c44bc23f5e	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Zarf	\N	Zarf — profesyonel baskı.	zarf	100	2026-06-02 09:52:14.645762+00	\N	ebb14f11-2ee7-40cd-845e-220a5c00ce27
cf7b4ed5-182d-4049-97b9-9fbb3baa9e4b	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Bloknot	\N	Bloknot — profesyonel baskı.	bloknot	100	2026-06-02 09:52:14.645762+00	\N	cca4ae1e-06bb-4a0b-8dae-7251d5f6acba
763507aa-9d97-4d28-9065-385e68b9fe13	t	\N	2026-06-02 09:52:14.645762+00	f	\N	Çanta	\N	Çanta — profesyonel baskı.	canta	100	2026-06-02 09:52:14.645762+00	\N	c7384df9-6976-47f1-bc46-2c11a8c2f3be
\.


--
-- Data for Name: coupons; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.coupons (id, active, auto_issue_on_first_visit, auto_issue_on_order_amount, code, created_at, current_usage, description, discount_amount, discount_percent, end_date, gift_amount, max_usage, min_order_amount, name, per_user_limit, start_date, type, updated_at) FROM stdin;
60c48c9b-d742-4546-aeb3-81826d5673f4	t	t	\N	HOSGELDIN100	2026-05-27 20:00:40.549097	0	Yeni üyelere özel ₺100 indirim. Minimum 500 TL alışverişte geçerlidir.	100.00	\N	\N	\N	\N	500.00	Hoş Geldin Kuponu	1	\N	AMOUNT	2026-05-27 20:00:40.549097
f81936b5-8632-45d5-b78a-f95c9a6d2ee3	t	f	5000.00	HEDIYE1000	2026-05-27 20:00:40.557293	0	5000 TL ve üzeri alışverişlerinizde sonraki siparişinizde kullanabileceğiniz ₺1000 hediye kuponu.	\N	\N	\N	1000.00	\N	1000.00	5000 TL ve Üzeri Hediye	1	\N	GIFT	2026-05-27 20:00:40.557293
f5613149-8fd7-453b-8482-a63643ea409d	t	f	\N	INDIRIM10	2026-05-27 20:00:40.561437	0	Tüm ürünlerde %10 indirim. Minimum 300 TL.	\N	10.00	\N	\N	100	300.00	%10 İndirim	1	\N	PERCENT	2026-05-27 20:00:40.561437
\.


--
-- Data for Name: dealers; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.dealers (id, address, city, company_name, created_at, credit_limit, discount_rate, district, notes, phone, rejection_reason, status, tax_number, tax_office, updated_at, user_id, business_type, estimated_monthly_revenue, note, website) FROM stdin;
\.


--
-- Data for Name: files; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.files (id, original_name, page_count, s3key, status, uploaded_at, order_item_id) FROM stdin;
\.


--
-- Data for Name: hero_slides; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.hero_slides (id, active, background_color, created_at, cta_link, cta_text, description, ends_at, image_url, label, layout, mobile_image_url, sort_order, starts_at, title, updated_at) FROM stdin;
1775b562-9b8b-4c5d-bbbc-60c8c8d10445	f	#fef3c7	2026-05-26 17:47:15.30608+00	/urun/standart-kartvizit	İncele	1000 adet sadece 850 TL	\N	http://localhost:8080/uploads/hero/ffd4ebc8-48c4-433c-b67a-dbb05e711578.png	%50 İNDİRİM	IMAGE_ONLY	\N	0	\N	Standart Kartvizit	2026-06-01 12:01:22.044411+00
d51eee94-6e2b-4197-9efc-5bb2a74665be	t	#fef3c7	2026-05-29 17:56:42.837937+00	/urunler	Promosyon	\N	\N	http://localhost:8080/uploads/hero/5d62147a-1425-4838-be30-5303d76d36a7.png	\N	IMAGE_ONLY	\N	0	\N	genel	2026-06-01 12:01:28.956615+00
b069c7b2-ee61-4b20-b51c-c52d32be1cff	t	#ccfbf1	2026-05-29 18:43:54.58223+00	/katalog/kartvizit	\N	\N	\N	http://localhost:8080/uploads/hero/de93d682-a8d3-427f-ab69-d5e217d909da.png	\N	IMAGE_ONLY	\N	0	\N	kartvizit	2026-06-01 12:01:34.75395+00
b44c4142-a4ce-4277-99f2-3a91eb6a0643	t	#fef3c7	2026-05-29 18:58:41.288167+00	/katalog/promosyon-urunleri	\N	\N	\N	http://localhost:8080/uploads/hero/08d7a933-69db-4eb4-a310-314c3a368afb.png	\N	IMAGE_ONLY	\N	0	\N	promosyon	2026-06-01 12:01:43.125527+00
8da5a778-ebce-4820-b16e-588bad6822a2	t	#fef3c7	2026-05-30 20:58:37.765926+00	/dijital-baski-urunleri	\N	\N	\N	http://localhost:8080/uploads/hero/de424d81-811f-4d0f-94f3-299792487c59.png	\N	IMAGE_ONLY	\N	0	\N	vinil	2026-06-01 12:01:57.856974+00
14461727-519a-4866-bb2c-366bd5038e22	t	#fef3c7	2026-05-30 06:55:40.771971+00	/urun/yelken-bayrak	İncele	\N	\N	http://localhost:8080/uploads/hero/ed4c55b3-ba6e-4f3f-860f-424f491fc557.png	\N	IMAGE_ONLY	http://localhost:8080/uploads/hero/5307ce24-9c12-486c-981b-d9af93bb4ac9.png	0	\N	yelkenbayrak	2026-06-01 12:02:53.096371+00
\.


--
-- Data for Name: notifications; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.notifications (id, channel, recipient, sent_at, status, order_id) FROM stdin;
\.


--
-- Data for Name: order_items; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.order_items (id, height_cm, product_type, quantity, unit_price, width_cm, order_id) FROM stdin;
f2ec50bd-d25f-4147-9ab6-b972d025caa3	50	buyuk-format-vinil	2	92.50	100	dce85c16-a4ac-49fc-acf5-f98f7fbc5728
6bc803ee-7870-48a7-9975-fe97674c8cd5	50	buyuk-format-vinil	2	92.50	100	587073ca-a60c-40f3-bcdd-30766735e7e6
078d2583-474a-43c5-8acf-9f90dd456486	50	buyuk-format-vinil	2	92.50	100	4e653d18-8ba6-4b37-9873-1b81eb949ef6
cbae5de6-199a-4830-a1ff-caf8231e2521	7	buyuk-format-branda	2	12.00	3	a5e07398-0077-4d49-824a-55ea5a1549a8
a1eb3e47-eb81-4fdc-91c7-1e041dead925	500	buyuk-format-vinil-baski	1000	1850.00	200	a5e07398-0077-4d49-824a-55ea5a1549a8
236a8e71-6a91-425a-8821-8ed6dbeb3dee	2	tabela-forex	1	12.00	11	a5e07398-0077-4d49-824a-55ea5a1549a8
d5018df6-1407-4222-8534-59cc42b7af36	\N	promosyon-kupa	1	45.00	\N	fb7865fe-e21d-41ca-8451-c10d6238cfbf
387587ce-7ca8-45a2-a08b-654cc45d399d	\N	promosyon-bez-canta	1	35.00	\N	d3196c34-9c84-4a4b-8065-21848e2b52bc
61ed8286-1fc4-4b50-9003-4bf15745ab8e	2	tabela-forex	1	12.00	3	d3196c34-9c84-4a4b-8065-21848e2b52bc
c859ed65-106e-47bf-b946-79f6408290bf	\N	promosyon-kupa	1	45.00	\N	e4535a7e-8465-4dde-aa57-1f9eda22bebe
9dde983c-c617-4b4b-8ea5-4ec143ee1d18	\N	promosyon-kupa	1	45.00	\N	e4535a7e-8465-4dde-aa57-1f9eda22bebe
5fbc96db-e870-455f-8aae-3e290763ad70	\N	promosyon-bez-canta	1	35.00	\N	e4535a7e-8465-4dde-aa57-1f9eda22bebe
5d0d2f33-fe40-4a32-a5c4-589b0d15795a	\N	promosyon-bez-canta	1	35.00	\N	e4535a7e-8465-4dde-aa57-1f9eda22bebe
e4ae3c82-f883-4392-87e4-a7e46e01ea83	50	tabela-forex	1	12.00	10	eceafd77-0170-4228-9149-0c8b35e0d03f
ef9e4f4d-c90e-4a11-951d-0080d549f552	50	tabela-forex	1	12.00	10	eceafd77-0170-4228-9149-0c8b35e0d03f
89336644-fe96-4dce-9883-d587b9601d87	\N	promosyon-kupa	1	45.00	\N	77678308-3be1-40de-8336-3ba2e1ff70ff
d2e01ebf-6289-48e0-a354-0937aad96675	\N	promosyon-kupa	1	45.00	\N	77678308-3be1-40de-8336-3ba2e1ff70ff
e557208a-4b83-4a6a-9ff6-a8847a24954f	\N	promosyon-kupa	1	45.00	\N	9be267fd-8fbe-4506-bc3f-d988f3bb579f
321e340e-538b-4ca2-92b5-bb10ad162362	\N	promosyon-kupa	1	45.00	\N	9be267fd-8fbe-4506-bc3f-d988f3bb579f
469da0f1-9bf7-4820-bb25-e503b1647b41	50	tabela-forex	1	60.00	100	0fe95dc1-3a1b-4c72-9730-5231e9e1e44c
887b1418-4a1d-474c-aa53-29c1f2cee13b	50	tabela-forex	1	60.00	100	0fe95dc1-3a1b-4c72-9730-5231e9e1e44c
dec90b83-593a-4306-a6ea-8547f943e570	\N	promosyon-kupa	1	45.00	\N	c7f68167-a2c7-4b77-8431-f8c1a9cb2dbf
e9cf3216-2cb0-4382-a735-781af643fb29	\N	promosyon-kupa	1	45.00	\N	c7f68167-a2c7-4b77-8431-f8c1a9cb2dbf
0a6d59aa-bb77-4c71-94a5-086839cbcd72	\N	promosyon-kupa	1	45.00	\N	d3d5c2e7-629f-4c84-ab7f-82380694c86e
14a516b6-b952-4e91-af85-bc151cec9a34	\N	promosyon-kupa	1	45.00	\N	d3d5c2e7-629f-4c84-ab7f-82380694c86e
f711b0c5-f393-431d-9ee5-618a8c3349b3	100	buyuk-format-vinil-baski	1	72.00	50	0f0cb41f-a87d-464b-becf-54b8cda80ffa
1f71508a-6c2c-4820-80c5-e08dbf6e4c51	100	buyuk-format-vinil-baski	1	72.00	50	0f0cb41f-a87d-464b-becf-54b8cda80ffa
\.


--
-- Data for Name: order_status_history; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.order_status_history (id, created_at, note, status, order_id) FROM stdin;
f841eb89-ed68-4677-9907-9550fea9e797	2026-05-19 17:42:16.955591	Sipariş onaylandı, incelemeye alındı	REVIEWING	4e653d18-8ba6-4b37-9873-1b81eb949ef6
38db518b-9902-47d2-8ef1-70671b71e7e2	2026-05-19 17:42:23.608809	Sipariş onaylandı, incelemeye alındı	REVIEWING	4e653d18-8ba6-4b37-9873-1b81eb949ef6
93cae277-20ce-484b-a32d-a0f47ddcac1c	2026-05-20 08:43:23.508605	Baskıya gönderildi	PRINTING	4e653d18-8ba6-4b37-9873-1b81eb949ef6
d9c41e68-e12d-493f-a6ac-4c3e8bdcc751	2026-05-20 08:43:35.849309	Kargoya verildi	SHIPPED	4e653d18-8ba6-4b37-9873-1b81eb949ef6
5bdacfc1-2120-4def-a2e5-321d082ebaba	2026-05-20 08:43:38.02388	Sipariş tamamlandı	COMPLETED	4e653d18-8ba6-4b37-9873-1b81eb949ef6
\.


--
-- Data for Name: orders; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.orders (id, created_at, declared_prints, pdf_page_count, shipping_address, status, total_price, user_id) FROM stdin;
dce85c16-a4ac-49fc-acf5-f98f7fbc5728	2026-05-19 15:10:11.279266	1	0	Admin Test, Ataturk Cad. No:1 Kadikoy/Istanbul Türkiye	PENDING	185.00	c2a9af4c-0956-4e71-bde1-f8a8f9258586
587073ca-a60c-40f3-bcdd-30766735e7e6	2026-05-19 15:46:28.867238	1	0	Admin Test, Ataturk Cad. No:1 Kadikoy/Istanbul Türkiye	PENDING	185.00	c2a9af4c-0956-4e71-bde1-f8a8f9258586
4e653d18-8ba6-4b37-9873-1b81eb949ef6	2026-05-19 17:11:29.376051	1	0	Admin Test, Ataturk Cad. No:1 Kadikoy/Istanbul Türkiye	COMPLETED	185.00	c2a9af4c-0956-4e71-bde1-f8a8f9258586
a5e07398-0077-4d49-824a-55ea5a1549a8	2026-05-21 18:04:24.567384	5	0	Admin Test, Ataturk Cad. No:1 Kadikoy/Istanbul Türkiye	PENDING	1850036.00	c2a9af4c-0956-4e71-bde1-f8a8f9258586
fb7865fe-e21d-41ca-8451-c10d6238cfbf	2026-05-21 18:04:36.767846	1	0	Admin Test, Ataturk Cad. No:1 Kadikoy/Istanbul Türkiye	PENDING	45.00	c2a9af4c-0956-4e71-bde1-f8a8f9258586
d3196c34-9c84-4a4b-8065-21848e2b52bc	2026-05-21 18:05:14.392022	2	0	Admin Test, Ataturk Cad. No:1 Kadikoy/Istanbul Türkiye	PENDING	47.00	c2a9af4c-0956-4e71-bde1-f8a8f9258586
e4535a7e-8465-4dde-aa57-1f9eda22bebe	2026-05-22 08:41:59.666643	4	0	Admin Test, Ataturk Cad. No:1 Kadikoy/Istanbul Türkiye	PENDING	160.00	c2a9af4c-0956-4e71-bde1-f8a8f9258586
eceafd77-0170-4228-9149-0c8b35e0d03f	2026-05-22 08:42:25.080261	2	0	Admin Test, Ataturk Cad. No:1 Kadikoy/Istanbul Türkiye	PENDING	24.00	c2a9af4c-0956-4e71-bde1-f8a8f9258586
77678308-3be1-40de-8336-3ba2e1ff70ff	2026-05-22 08:50:46.18578	2	0	Admin Test, Ataturk Cad. No:1 Kadikoy/Istanbul Türkiye	PENDING	90.00	c2a9af4c-0956-4e71-bde1-f8a8f9258586
9be267fd-8fbe-4506-bc3f-d988f3bb579f	2026-05-22 08:51:13.195915	2	0	Admin Test, Ataturk Cad. No:1 Kadikoy/Istanbul Türkiye	PENDING	90.00	c2a9af4c-0956-4e71-bde1-f8a8f9258586
0fe95dc1-3a1b-4c72-9730-5231e9e1e44c	2026-05-22 10:04:30.337683	2	0	Admin Test, Ataturk Cad. No:1 Kadikoy/Istanbul Türkiye	PENDING	120.00	c2a9af4c-0956-4e71-bde1-f8a8f9258586
c7f68167-a2c7-4b77-8431-f8c1a9cb2dbf	2026-05-22 10:43:43.797407	2	0	Admin Test, Ataturk Cad. No:1 Kadikoy/Istanbul Türkiye	PENDING	90.00	c2a9af4c-0956-4e71-bde1-f8a8f9258586
d3d5c2e7-629f-4c84-ab7f-82380694c86e	2026-05-22 10:52:42.48916	2	0	Admin Test, Ataturk Cad. No:1 Kadikoy/Istanbul Türkiye	PENDING	90.00	c2a9af4c-0956-4e71-bde1-f8a8f9258586
0f0cb41f-a87d-464b-becf-54b8cda80ffa	2026-05-22 15:51:35.991737	2	0	Admin Test, Ataturk Cad. No:1 Kadikoy/Istanbul Türkiye	PENDING	144.00	c2a9af4c-0956-4e71-bde1-f8a8f9258586
\.


--
-- Data for Name: payments; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.payments (id, amount, paid_at, provider, provider_ref, status, order_id) FROM stdin;
d3cd95e2-7b85-446b-ae5e-da9699ea8db0	185.00	\N	iyzico	\N	PENDING	dce85c16-a4ac-49fc-acf5-f98f7fbc5728
875b41d3-c018-4549-a665-c5c13ce0f88d	185.00	\N	iyzico	\N	PENDING	587073ca-a60c-40f3-bcdd-30766735e7e6
e7edb928-6fc9-4815-9f5c-532d9f2a91ad	185.00	\N	iyzico	\N	PENDING	4e653d18-8ba6-4b37-9873-1b81eb949ef6
6f80cc49-8249-49a3-857e-64d5da8da3ac	1850036.00	\N	iyzico	\N	PENDING	a5e07398-0077-4d49-824a-55ea5a1549a8
9256dcbd-f14c-43ae-85aa-9c9fa4d86066	45.00	\N	iyzico	\N	PENDING	fb7865fe-e21d-41ca-8451-c10d6238cfbf
26c4fa5d-3757-4917-9e42-901d3664c13d	47.00	\N	iyzico	\N	PENDING	d3196c34-9c84-4a4b-8065-21848e2b52bc
29038148-bf9c-4437-b1d5-45c794b32ff3	160.00	\N	iyzico	\N	PENDING	e4535a7e-8465-4dde-aa57-1f9eda22bebe
5166af3b-38e2-43dd-aeb1-797ca880c798	24.00	\N	iyzico	\N	PENDING	eceafd77-0170-4228-9149-0c8b35e0d03f
0040d8c1-8ddf-4cc7-be86-a63d64ae8d21	90.00	\N	iyzico	\N	PENDING	77678308-3be1-40de-8336-3ba2e1ff70ff
c3c69d7e-b79a-4c6c-8c87-b82f12f36a28	90.00	\N	iyzico	\N	PENDING	9be267fd-8fbe-4506-bc3f-d988f3bb579f
9b7aa288-63a3-49bb-bc2a-85874e304637	120.00	\N	iyzico	\N	PENDING	0fe95dc1-3a1b-4c72-9730-5231e9e1e44c
964cbc09-cf8a-4a61-8f2b-004a9525f3ad	90.00	\N	iyzico	\N	PENDING	c7f68167-a2c7-4b77-8431-f8c1a9cb2dbf
f5df4d6c-4815-4c2a-adf0-1c621e3ab64e	90.00	\N	iyzico	\N	PENDING	d3d5c2e7-629f-4c84-ab7f-82380694c86e
09bd0f08-85ab-4ee9-8f8f-e9d348692a7b	144.00	\N	iyzico	\N	PENDING	0f0cb41f-a87d-464b-becf-54b8cda80ffa
\.


--
-- Data for Name: permissions; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.permissions (id, category, code, label) FROM stdin;
040bef77-2083-441c-ad91-fb64b77e4e7d	Sipariş	siparis.goruntule	Siparişleri görüntüle
28d04e70-735b-4efa-a446-bcfed87f4d02	Sipariş	siparis.durum_guncelle	Sipariş durumu güncelle
b106de7b-69e4-458e-b173-706b7f200133	Sipariş	siparis.onayla	Sipariş onayla
4dcca2db-d2dd-4577-895c-2e7ba95a5fd5	Sipariş	siparis.reddet	Sipariş reddet
180988c6-db2a-4f97-b599-85125bdac6d0	Sipariş	siparis.baskiya_gonder	Baskıya gönder
abdbb250-1083-4eea-80a4-a7e351e83694	Sipariş	siparis.kargola	Kargoya ver
acb4b4d6-96ce-418a-8cee-cd53fd897797	Sipariş	siparis.tamamla	Siparişi tamamla
36392c6c-4235-406a-ba8d-48c638898cc9	Ödeme	odeme.goruntule	Ödemeleri görüntüle
27173064-4934-4ca3-8f37-4a6d383f93c0	Ödeme	odeme.rapor	Ödeme raporu al
efb04aea-4485-41c4-8c77-00ca852d0003	Ödeme	odeme.iade	İade işlemi yap
e1c7a7a3-f0e3-4047-a320-6f1b1a9576e9	Ürün	urun.goruntule	Ürünleri görüntüle
41708101-425f-4b6e-9260-7a6e484a0f56	Ürün	urun.duzenle	Ürün ekle/düzenle
6ed43423-604c-488c-ba97-db5fba5f1a9c	Ürün	urun.sil	Ürün sil/pasif yap
a8cde1c3-2788-4ef4-aab5-c43bf0be2ddb	Ürün	urun.fiyat_guncelle	Fiyat güncelle
ef8f9d9e-ce11-464a-9215-f72417afc910	Ürün	urun.import	Excel ile toplu yükle
f98acd04-729a-41b8-82b3-10ce8afc41a5	Kullanıcı	kullanici.goruntule	Kullanıcıları görüntüle
5c4a50c4-f610-498c-a367-5258d41af6a6	Kullanıcı	kullanici.duzenle	Kullanıcı düzenle
ae87eeeb-9b2b-4679-bd2a-4d728d81344f	Kullanıcı	kullanici.rol_ver	Kullanıcıya rol ata
b94af777-a8c1-4b62-8f6a-b49dd8cf18c7	Rapor	rapor.ciro	Ciro raporunu gör
6305a29f-1ae4-4ee4-b796-98cad99b6561	Rapor	rapor.gunluk	Günlük rapor
23142978-26c4-436a-ad95-ea09d5309e44	Rapor	rapor.musteri	Müşteri raporu
a147dc71-1bd8-4d9e-8537-595467242d30	Referans	referans.yonet	Referans ekle/düzenle/sil
\.


--
-- Data for Name: pre_order_files; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.pre_order_files (id, claimed_at, claimed_by_order_id, created_at, file_size, mime_type, original_name, stored_path) FROM stdin;
5f1e1e89-dfd3-4feb-b03f-7cecd274c850	2026-05-29 18:09:09.730214+00	66a435be-4bd3-4839-850f-d89a788ceecb	2026-05-29 18:08:11.557444+00	2888	application/pdf	Multibus_Fiyat_Teklifi.pdf	C:\\ard\\ard-backend\\uploads\\design\\5f1e1e89-dfd3-4feb-b03f-7cecd274c850.pdf
a56de09d-e716-499b-9c59-5206663c5c9b	2026-05-29 18:10:40.994882+00	da27f6ec-5486-4d30-b600-2a6cd635a86a	2026-05-29 18:10:03.74666+00	1189949	application/pdf	MLŞ-185 DIAFONBOX_Kullanım Kılavuzu (2).pdf	C:\\ard\\ard-backend\\uploads\\design\\a56de09d-e716-499b-9c59-5206663c5c9b.pdf
1a063eb0-bfa3-4083-95f7-dab66b12ff12	2026-05-29 18:11:53.35274+00	6feabc04-13d7-4cd0-b754-21319ce35db2	2026-05-29 18:11:31.839797+00	2540043	image/png	0d704fef-fbf9-41f9-b68f-dcf82dcf3bca.png	C:\\ard\\ard-backend\\uploads\\design\\1a063eb0-bfa3-4083-95f7-dab66b12ff12.png
73ab8294-de1e-4d7f-a3aa-0d49352ed8d5	2026-05-29 18:32:34.741564+00	478370d2-e437-447c-aa62-d0c91ff6cc62	2026-05-29 18:32:11.781119+00	306213	image/jpeg	2023_07_web-offset-printing-machine.jpg	C:\\ard\\ard-backend\\uploads\\design\\73ab8294-de1e-4d7f-a3aa-0d49352ed8d5.jpg
28212ddc-2e43-46b1-946c-09585e99a98f	2026-05-29 18:33:56.787515+00	38115a26-f74d-4ead-80d5-af68160b78d6	2026-05-29 18:33:13.924478+00	2097651	image/png	ChatGPT Image 26 May 2026 16_57_36.png	C:\\ard\\ard-backend\\uploads\\design\\28212ddc-2e43-46b1-946c-09585e99a98f.png
b7ca1892-1863-4be3-9b50-09eb2733079a	\N	\N	2026-05-29 18:52:51.704441+00	2708329	image/png	0ee81ec9-e95b-4143-b02a-f81b780218ed.png	C:\\ard\\ard-backend\\uploads\\design\\b7ca1892-1863-4be3-9b50-09eb2733079a.png
7c4846a2-a069-4204-81bb-11bae2379656	2026-05-29 19:19:59.255939+00	a051da28-bbe5-4368-bfd8-045d88fba3c7	2026-05-29 18:34:28.618075+00	2097651	image/png	ChatGPT Image 26 May 2026 16_57_36.png	C:\\ard\\ard-backend\\uploads\\design\\7c4846a2-a069-4204-81bb-11bae2379656.png
93f08652-05c5-417a-a138-2a81ba40f797	2026-05-29 19:19:59.255939+00	a051da28-bbe5-4368-bfd8-045d88fba3c7	2026-05-29 19:01:08.071699+00	1947107	image/png	ChatGPT Image 29 May 2026 21_55_49.png	C:\\ard\\ard-backend\\uploads\\design\\93f08652-05c5-417a-a138-2a81ba40f797.png
5b0fb296-41b5-4aaf-920e-4d8f1440c9a9	2026-05-29 19:19:59.255939+00	a051da28-bbe5-4368-bfd8-045d88fba3c7	2026-05-29 19:19:45.039471+00	1749820	image/png	baskiurunleri-showcase.png	C:\\ard\\ard-backend\\uploads\\design\\5b0fb296-41b5-4aaf-920e-4d8f1440c9a9.png
98fd4651-1256-4504-9aab-a6c0f19d87db	\N	\N	2026-06-02 05:58:26.0649+00	69020	application/pdf	PDKS_Azure_Gecis_Rehberi.pdf	C:\\ard\\ard-backend\\uploads\\design\\98fd4651-1256-4504-9aab-a6c0f19d87db.pdf
99b8e626-f5ff-4766-bae9-a0d8de4bab3c	\N	\N	2026-06-02 06:01:48.974117+00	1100312	application/pdf	Adsız tasarım.pdf	C:\\ard\\ard-backend\\uploads\\design\\99b8e626-f5ff-4766-bae9-a0d8de4bab3c.pdf
7b5d5985-a875-4d46-aa1b-9339398c18f4	2026-06-02 06:03:26.520156+00	ff9a8746-42b9-4692-a760-ee19e39d87a5	2026-06-02 06:03:06.257345+00	754937	application/pdf	Beyaz ve Mavi Sade Pilates Eğitimi Instagram Gönderisi (1).pdf	C:\\ard\\ard-backend\\uploads\\design\\7b5d5985-a875-4d46-aa1b-9339398c18f4.pdf
\.


--
-- Data for Name: price_rules; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.price_rules (id, base_price, max_qty, min_qty, multiplier, option_key, option_value, price_delta, rule_type, unit_price, product_type_id) FROM stdin;
4eae9a02-c02c-4187-a4cc-0f10d8c06ca5	100.00	\N	1	\N	\N	\N	\N	AREA_BASED	\N	7b4a1463-b298-4e8e-8fc5-03b5ff2bfa6b
e1aea76b-9cae-444c-b980-040eb516fa70	3.20	\N	1	\N	\N	\N	\N	AREA_BASED	\N	0dc9ff09-3a78-4ddc-abb2-8bdc1bafe58e
f9bace09-4e52-4de4-af57-67c9456fb4cf	3.50	\N	1	\N	\N	\N	\N	AREA_BASED	\N	761fbdcb-563a-4738-9218-02b4a97a18bb
\.


--
-- Data for Name: product_configs; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.product_configs (id, affects_price, display_order, field_key, field_type, options, required, product_type_id) FROM stdin;
\.


--
-- Data for Name: product_types; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.product_types (id, description, has_file, is_active, min_order, name, pricing_model, slug, unit, image_url, badge, featured, original_price) FROM stdin;
7b4a1463-b298-4e8e-8fc5-03b5ff2bfa6b	asdas	t	t	1	fsdfsdf	AREA_BASED	buyuk-format-fsdfsdfds	m2	https://artemizreklam.com/wp-content/uploads/2021/03/vinil-baski-600x450.jpg	YENİ	t	125.00
0dc9ff09-3a78-4ddc-abb2-8bdc1bafe58e	Super Vinil Baskı	t	t	1	Vinil Baskı	AREA_BASED	buyuk-format-vinil-baski	m2	https://www.poshreklam.com/wp-content/uploads/2025/07/posh-reklam-vinil-baski-banner.webp		t	0.00
761fbdcb-563a-4738-9218-02b4a97a18bb	FOLYO BASKI	t	t	1	Folyo Baskı	AREA_BASED	buyuk-format-folyo-baski	m2	https://www.dinamiktanitim.com/image/cache/catalog/dijital-baski/1-Sinif-Dijital-Baski-Makineleri-450x450-500x515.jpg	YENİ	t	0.00
\.


--
-- Data for Name: system_settings; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.system_settings (key, description, value) FROM stdin;
\.


--
-- Data for Name: user_app_roles; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.user_app_roles (id, assigned_at, assigned_by, app_role_id, user_id) FROM stdin;
02315e54-d123-45e9-997d-1ebac3c8ba4a	2026-05-20 10:43:08.239564	admin@baski.com	e5a63db2-3c9d-4e08-afc9-2ab7fb67e3e0	c2a9af4c-0956-4e71-bde1-f8a8f9258586
93503184-b52b-4a72-85e9-af5a786d01f4	2026-05-20 10:43:19.328474	admin@baski.com	ab86f054-f176-43a3-a233-69d2897928cd	c2a9af4c-0956-4e71-bde1-f8a8f9258586
8f8e5202-ff04-4bd1-b9ac-45e7ead067cd	2026-05-20 14:32:10.524221	admin@baski.com	fa0e64d1-cbc8-4abf-a283-c824a1f5b5ef	8a4ac4fd-f4dd-4032-83b2-72ffe913377f
\.


--
-- Data for Name: user_coupons; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.user_coupons (id, created_at, expires_at, issued_at, order_id, source, used, used_at, user_id, coupon_id) FROM stdin;
\.


--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: baski_user
--

COPY public.users (id, created_at, email, name, password, phone, role, email_verified, google_id) FROM stdin;
7b8a427a-cf5b-400a-868a-c62c6fb30c99	2026-05-19 21:39:29.80454	ilbarsseckin@gmail.com	seçkin ilbars	$2a$10$/Q5bOasIpH.EClgOd2bCqeTKpVMSUBYxuTAHBqDg/o4Cuqytv2AwW	05530214776	CUSTOMER	\N	\N
c2a9af4c-0956-4e71-bde1-f8a8f9258586	2026-05-19 13:26:58.825019	admin@baski.com	Admin	$2a$10$/cJFoXPHcLcQV46xRpDUxu6Qs8vuAPjcrnKtPOUJyYfZ.N5TvP3rS	05001234567	ADMIN	\N	\N
d524f0ed-f101-4a09-ada5-033b7dc062e1	2026-05-20 11:02:18.472145	arif@gmail.com	arif	$2a$10$.EytYLqld6foWFMGDx5kyuuAHViqg9xTZuumGuKAEuJ3sTxrqAXcu		OPERATOR	\N	\N
8a4ac4fd-f4dd-4032-83b2-72ffe913377f	2026-05-20 10:59:18.839038	ayse@gmail.com	ayse	$2a$10$KkpDk95eXLAStVP2TkjCF./ikuD4jTDiw3EKtpB.rlygC.NAgLwC6		OPERATOR	\N	\N
b9b78f85-d48a-45ee-9db8-9b39781bde32	2026-05-29 21:54:39.94968	test@gmail.com	test	$2a$10$uW5ld8pBt4Z1X0OFowMfkOrJ5XcqyOXNB6V/aE4CAXLPc82q5fTSq	05530214776	CUSTOMER	f	\N
\.


--
-- Name: addresses addresses_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.addresses
    ADD CONSTRAINT addresses_pkey PRIMARY KEY (id);


--
-- Name: app_role_permissions app_role_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.app_role_permissions
    ADD CONSTRAINT app_role_permissions_pkey PRIMARY KEY (role_id, permission_id);


--
-- Name: app_roles app_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.app_roles
    ADD CONSTRAINT app_roles_pkey PRIMARY KEY (id);


--
-- Name: brand_references brand_references_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.brand_references
    ADD CONSTRAINT brand_references_pkey PRIMARY KEY (id);


--
-- Name: campaigns campaigns_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.campaigns
    ADD CONSTRAINT campaigns_pkey PRIMARY KEY (id);


--
-- Name: cart_items cart_items_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.cart_items
    ADD CONSTRAINT cart_items_pkey PRIMARY KEY (id);


--
-- Name: carts carts_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.carts
    ADD CONSTRAINT carts_pkey PRIMARY KEY (id);


--
-- Name: catalog_attribute_options catalog_attribute_options_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_attribute_options
    ADD CONSTRAINT catalog_attribute_options_pkey PRIMARY KEY (id);


--
-- Name: catalog_attributes catalog_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_attributes
    ADD CONSTRAINT catalog_attributes_pkey PRIMARY KEY (id);


--
-- Name: catalog_brands catalog_brands_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_brands
    ADD CONSTRAINT catalog_brands_pkey PRIMARY KEY (id);


--
-- Name: catalog_categories catalog_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_categories
    ADD CONSTRAINT catalog_categories_pkey PRIMARY KEY (id);


--
-- Name: catalog_order_files catalog_order_files_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_order_files
    ADD CONSTRAINT catalog_order_files_pkey PRIMARY KEY (id);


--
-- Name: catalog_order_items catalog_order_items_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_order_items
    ADD CONSTRAINT catalog_order_items_pkey PRIMARY KEY (id);


--
-- Name: catalog_orders catalog_orders_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_orders
    ADD CONSTRAINT catalog_orders_pkey PRIMARY KEY (id);


--
-- Name: catalog_product_attribute_values catalog_product_attribute_values_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_product_attribute_values
    ADD CONSTRAINT catalog_product_attribute_values_pkey PRIMARY KEY (id);


--
-- Name: catalog_product_images catalog_product_images_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_product_images
    ADD CONSTRAINT catalog_product_images_pkey PRIMARY KEY (id);


--
-- Name: catalog_product_reviews catalog_product_reviews_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_product_reviews
    ADD CONSTRAINT catalog_product_reviews_pkey PRIMARY KEY (id);


--
-- Name: catalog_product_tiers catalog_product_tiers_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_product_tiers
    ADD CONSTRAINT catalog_product_tiers_pkey PRIMARY KEY (id);


--
-- Name: catalog_products catalog_products_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_products
    ADD CONSTRAINT catalog_products_pkey PRIMARY KEY (id);


--
-- Name: coupons coupons_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.coupons
    ADD CONSTRAINT coupons_pkey PRIMARY KEY (id);


--
-- Name: dealers dealers_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.dealers
    ADD CONSTRAINT dealers_pkey PRIMARY KEY (id);


--
-- Name: files files_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.files
    ADD CONSTRAINT files_pkey PRIMARY KEY (id);


--
-- Name: hero_slides hero_slides_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.hero_slides
    ADD CONSTRAINT hero_slides_pkey PRIMARY KEY (id);


--
-- Name: catalog_brands idx_cat_brand_slug; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_brands
    ADD CONSTRAINT idx_cat_brand_slug UNIQUE (slug);


--
-- Name: catalog_categories idx_cat_cat_slug; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_categories
    ADD CONSTRAINT idx_cat_cat_slug UNIQUE (slug);


--
-- Name: catalog_products idx_cat_prod_slug; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_products
    ADD CONSTRAINT idx_cat_prod_slug UNIQUE (slug);


--
-- Name: catalog_orders idx_catorder_number; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_orders
    ADD CONSTRAINT idx_catorder_number UNIQUE (order_number);


--
-- Name: coupons idx_coupon_code; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.coupons
    ADD CONSTRAINT idx_coupon_code UNIQUE (code);


--
-- Name: notifications notifications_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);


--
-- Name: order_items order_items_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.order_items
    ADD CONSTRAINT order_items_pkey PRIMARY KEY (id);


--
-- Name: order_status_history order_status_history_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.order_status_history
    ADD CONSTRAINT order_status_history_pkey PRIMARY KEY (id);


--
-- Name: orders orders_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT orders_pkey PRIMARY KEY (id);


--
-- Name: payments payments_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT payments_pkey PRIMARY KEY (id);


--
-- Name: permissions permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.permissions
    ADD CONSTRAINT permissions_pkey PRIMARY KEY (id);


--
-- Name: pre_order_files pre_order_files_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.pre_order_files
    ADD CONSTRAINT pre_order_files_pkey PRIMARY KEY (id);


--
-- Name: price_rules price_rules_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.price_rules
    ADD CONSTRAINT price_rules_pkey PRIMARY KEY (id);


--
-- Name: product_configs product_configs_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.product_configs
    ADD CONSTRAINT product_configs_pkey PRIMARY KEY (id);


--
-- Name: product_types product_types_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.product_types
    ADD CONSTRAINT product_types_pkey PRIMARY KEY (id);


--
-- Name: system_settings system_settings_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.system_settings
    ADD CONSTRAINT system_settings_pkey PRIMARY KEY (key);


--
-- Name: dealers uk10jndvam70sjubvckk4l6cvxr; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.dealers
    ADD CONSTRAINT uk10jndvam70sjubvckk4l6cvxr UNIQUE (user_id);


--
-- Name: files uk2bsy7ojkfd5129barybedxjrd; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.files
    ADD CONSTRAINT uk2bsy7ojkfd5129barybedxjrd UNIQUE (order_item_id);


--
-- Name: dealers uk40654vo0wa4g02l8ltqpayvs0; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.dealers
    ADD CONSTRAINT uk40654vo0wa4g02l8ltqpayvs0 UNIQUE (tax_number);


--
-- Name: users uk6dotkott2kjsp8vw4d0m25fb7; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk6dotkott2kjsp8vw4d0m25fb7 UNIQUE (email);


--
-- Name: permissions uk7lcb6glmvwlro3p2w2cewxtvd; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.permissions
    ADD CONSTRAINT uk7lcb6glmvwlro3p2w2cewxtvd UNIQUE (code);


--
-- Name: product_types uk9abi23631rfwuaml6m9a0pjok; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.product_types
    ADD CONSTRAINT uk9abi23631rfwuaml6m9a0pjok UNIQUE (slug);


--
-- Name: app_roles ukfvrw9klein793jl7h2qug4a5t; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.app_roles
    ADD CONSTRAINT ukfvrw9klein793jl7h2qug4a5t UNIQUE (name);


--
-- Name: user_app_roles user_app_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.user_app_roles
    ADD CONSTRAINT user_app_roles_pkey PRIMARY KEY (id);


--
-- Name: user_coupons user_coupons_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.user_coupons
    ADD CONSTRAINT user_coupons_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: idx_campaign_active; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_campaign_active ON public.campaigns USING btree (active);


--
-- Name: idx_campaign_sort; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_campaign_sort ON public.campaigns USING btree (sort_order);


--
-- Name: idx_cat_attr_cat; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_cat_attr_cat ON public.catalog_attributes USING btree (category_id);


--
-- Name: idx_cat_attr_opt_attr; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_cat_attr_opt_attr ON public.catalog_attribute_options USING btree (attribute_id);


--
-- Name: idx_cat_cat_parent; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_cat_cat_parent ON public.catalog_categories USING btree (parent_id);


--
-- Name: idx_cat_img_prod; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_cat_img_prod ON public.catalog_product_images USING btree (product_id);


--
-- Name: idx_cat_parent; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_cat_parent ON public.catalog_categories USING btree (parent_id);


--
-- Name: idx_cat_pav_attr; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_cat_pav_attr ON public.catalog_product_attribute_values USING btree (attribute_id);


--
-- Name: idx_cat_pav_prod; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_cat_pav_prod ON public.catalog_product_attribute_values USING btree (product_id);


--
-- Name: idx_cat_prod_brand; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_cat_prod_brand ON public.catalog_products USING btree (brand_id);


--
-- Name: idx_cat_prod_cat; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_cat_prod_cat ON public.catalog_products USING btree (category_id);


--
-- Name: idx_cat_tier_prod; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_cat_tier_prod ON public.catalog_product_tiers USING btree (product_id);


--
-- Name: idx_catfile_created; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_catfile_created ON public.catalog_order_files USING btree (created_at);


--
-- Name: idx_catfile_order; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_catfile_order ON public.catalog_order_files USING btree (order_id);


--
-- Name: idx_catorder_created; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_catorder_created ON public.catalog_orders USING btree (created_at);


--
-- Name: idx_catorder_payment; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_catorder_payment ON public.catalog_orders USING btree (payment_status);


--
-- Name: idx_catorder_status; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_catorder_status ON public.catalog_orders USING btree (status);


--
-- Name: idx_catorder_user; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_catorder_user ON public.catalog_orders USING btree (user_id);


--
-- Name: idx_catorderitem_order; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_catorderitem_order ON public.catalog_order_items USING btree (order_id);


--
-- Name: idx_coupon_active; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_coupon_active ON public.coupons USING btree (active);


--
-- Name: idx_hero_active; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_hero_active ON public.hero_slides USING btree (active);


--
-- Name: idx_hero_sort; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_hero_sort ON public.hero_slides USING btree (sort_order);


--
-- Name: idx_review_approved; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_review_approved ON public.catalog_product_reviews USING btree (approved);


--
-- Name: idx_review_product; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_review_product ON public.catalog_product_reviews USING btree (product_id);


--
-- Name: idx_review_user; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_review_user ON public.catalog_product_reviews USING btree (user_id);


--
-- Name: idx_user_coupon_coupon; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_user_coupon_coupon ON public.user_coupons USING btree (coupon_id);


--
-- Name: idx_user_coupon_used; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_user_coupon_used ON public.user_coupons USING btree (used);


--
-- Name: idx_user_coupon_user; Type: INDEX; Schema: public; Owner: baski_user
--

CREATE INDEX idx_user_coupon_user ON public.user_coupons USING btree (user_id);


--
-- Name: addresses fk1fa36y2oqhao3wgg2rw1pi459; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.addresses
    ADD CONSTRAINT fk1fa36y2oqhao3wgg2rw1pi459 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: catalog_product_reviews fk2kgl0mo84s9ojf49gy64x33km; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_product_reviews
    ADD CONSTRAINT fk2kgl0mo84s9ojf49gy64x33km FOREIGN KEY (product_id) REFERENCES public.catalog_products(id);


--
-- Name: orders fk32ql8ubntj5uh44ph9659tiih; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.orders
    ADD CONSTRAINT fk32ql8ubntj5uh44ph9659tiih FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: price_rules fk6kjo14mp38w3vg9mtihp6qm8o; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.price_rules
    ADD CONSTRAINT fk6kjo14mp38w3vg9mtihp6qm8o FOREIGN KEY (product_type_id) REFERENCES public.product_types(id);


--
-- Name: notifications fk6og1jgdhfyqm6mk8v6a1qxias; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.notifications
    ADD CONSTRAINT fk6og1jgdhfyqm6mk8v6a1qxias FOREIGN KEY (order_id) REFERENCES public.orders(id);


--
-- Name: catalog_products fk71llqey9drw2ddppk1x5d7y8a; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_products
    ADD CONSTRAINT fk71llqey9drw2ddppk1x5d7y8a FOREIGN KEY (category_id) REFERENCES public.catalog_categories(id);


--
-- Name: user_app_roles fk7887shsgpv00sxotrp5e40s4; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.user_app_roles
    ADD CONSTRAINT fk7887shsgpv00sxotrp5e40s4 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: payments fk81gagumt0r8y3rmudcgpbk42l; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.payments
    ADD CONSTRAINT fk81gagumt0r8y3rmudcgpbk42l FOREIGN KEY (order_id) REFERENCES public.orders(id);


--
-- Name: catalog_product_images fk8cen2hupbgmww0k9ktx855cfy; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_product_images
    ADD CONSTRAINT fk8cen2hupbgmww0k9ktx855cfy FOREIGN KEY (product_id) REFERENCES public.catalog_products(id);


--
-- Name: catalog_product_attribute_values fk92fl2875p0i2bgmt4mytslds7; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_product_attribute_values
    ADD CONSTRAINT fk92fl2875p0i2bgmt4mytslds7 FOREIGN KEY (option_id) REFERENCES public.catalog_attribute_options(id);


--
-- Name: catalog_product_tiers fk9a812dlsy06g8w4up2h7ja3cl; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_product_tiers
    ADD CONSTRAINT fk9a812dlsy06g8w4up2h7ja3cl FOREIGN KEY (product_id) REFERENCES public.catalog_products(id);


--
-- Name: user_coupons fk9oi3p5xyfe4j32xs54nn7mi20; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.user_coupons
    ADD CONSTRAINT fk9oi3p5xyfe4j32xs54nn7mi20 FOREIGN KEY (coupon_id) REFERENCES public.coupons(id);


--
-- Name: carts fkb5o626f86h46m4s7ms6ginnop; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.carts
    ADD CONSTRAINT fkb5o626f86h46m4s7ms6ginnop FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: product_configs fkbesof4ivunprgtgl02m8ot8ji; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.product_configs
    ADD CONSTRAINT fkbesof4ivunprgtgl02m8ot8ji FOREIGN KEY (product_type_id) REFERENCES public.product_types(id);


--
-- Name: order_items fkbioxgbv59vetrxe0ejfubep1w; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.order_items
    ADD CONSTRAINT fkbioxgbv59vetrxe0ejfubep1w FOREIGN KEY (order_id) REFERENCES public.orders(id);


--
-- Name: catalog_attributes fkcnoaqdir9tqw97rx9qai3a8x; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_attributes
    ADD CONSTRAINT fkcnoaqdir9tqw97rx9qai3a8x FOREIGN KEY (category_id) REFERENCES public.catalog_categories(id);


--
-- Name: catalog_product_attribute_values fkdtpgrpnj7p8r7jbd7g8wff2re; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_product_attribute_values
    ADD CONSTRAINT fkdtpgrpnj7p8r7jbd7g8wff2re FOREIGN KEY (attribute_id) REFERENCES public.catalog_attributes(id);


--
-- Name: cart_items fkf9owekfxuecvej3oog3tfbl78; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.cart_items
    ADD CONSTRAINT fkf9owekfxuecvej3oog3tfbl78 FOREIGN KEY (product_type_id) REFERENCES public.product_types(id);


--
-- Name: catalog_products fkhshn9rn4ctcb47abmqbaqtx87; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_products
    ADD CONSTRAINT fkhshn9rn4ctcb47abmqbaqtx87 FOREIGN KEY (brand_id) REFERENCES public.catalog_brands(id);


--
-- Name: catalog_product_attribute_values fkk8skj7um9456efqgey4fs59yg; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_product_attribute_values
    ADD CONSTRAINT fkk8skj7um9456efqgey4fs59yg FOREIGN KEY (product_id) REFERENCES public.catalog_products(id);


--
-- Name: app_role_permissions fkkbwih5u1ia34o15953kubu94f; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.app_role_permissions
    ADD CONSTRAINT fkkbwih5u1ia34o15953kubu94f FOREIGN KEY (permission_id) REFERENCES public.permissions(id);


--
-- Name: catalog_order_items fkku4k5sde1qrwxk4wxd4gt3twj; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_order_items
    ADD CONSTRAINT fkku4k5sde1qrwxk4wxd4gt3twj FOREIGN KEY (order_id) REFERENCES public.catalog_orders(id);


--
-- Name: catalog_categories fkmausmraxpfw3ir4m4b0xsvtry; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_categories
    ADD CONSTRAINT fkmausmraxpfw3ir4m4b0xsvtry FOREIGN KEY (parent_id) REFERENCES public.catalog_categories(id);


--
-- Name: user_app_roles fkmrtxog42fhl4hjl6uuyhtbotd; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.user_app_roles
    ADD CONSTRAINT fkmrtxog42fhl4hjl6uuyhtbotd FOREIGN KEY (app_role_id) REFERENCES public.app_roles(id);


--
-- Name: files fkmuh938t60lw4df8ggbs4v6qrd; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.files
    ADD CONSTRAINT fkmuh938t60lw4df8ggbs4v6qrd FOREIGN KEY (order_item_id) REFERENCES public.order_items(id);


--
-- Name: order_status_history fknmcbg3mmbt8wfva97ra40nmp3; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.order_status_history
    ADD CONSTRAINT fknmcbg3mmbt8wfva97ra40nmp3 FOREIGN KEY (order_id) REFERENCES public.orders(id);


--
-- Name: cart_items fkpcttvuq4mxppo8sxggjtn5i2c; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.cart_items
    ADD CONSTRAINT fkpcttvuq4mxppo8sxggjtn5i2c FOREIGN KEY (cart_id) REFERENCES public.carts(id);


--
-- Name: catalog_attribute_options fkqajudtehmp5jc8okn86few4xf; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_attribute_options
    ADD CONSTRAINT fkqajudtehmp5jc8okn86few4xf FOREIGN KEY (attribute_id) REFERENCES public.catalog_attributes(id);


--
-- Name: dealers fkqoq67umfy4ce8rtk8872opdpp; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.dealers
    ADD CONSTRAINT fkqoq67umfy4ce8rtk8872opdpp FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: app_role_permissions fksj8wgtocscsk3cv3d7pngtv1s; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.app_role_permissions
    ADD CONSTRAINT fksj8wgtocscsk3cv3d7pngtv1s FOREIGN KEY (role_id) REFERENCES public.app_roles(id);


--
-- Name: catalog_order_files fktmdge251d2drsqvloc6ehw823; Type: FK CONSTRAINT; Schema: public; Owner: baski_user
--

ALTER TABLE ONLY public.catalog_order_files
    ADD CONSTRAINT fktmdge251d2drsqvloc6ehw823 FOREIGN KEY (order_id) REFERENCES public.catalog_orders(id);


--
-- PostgreSQL database dump complete
--

\unrestrict quSn0q0ENbhbWJveh4nJFt0J2QN19Rw2UEcyMDX3eP6GR1FWqTDmZ8jyoGVP3zr

