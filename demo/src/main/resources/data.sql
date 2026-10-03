-- Demo seed data, loaded on startup (see spring.sql.init.mode / spring.jpa.defer-datasource-initialization
-- in application.properties). Runs after Hibernate creates the schema (ddl-auto=create-drop).

-- ── Products ─────────────────────────────────────────────────────────────────
INSERT INTO product (name, category, price, active, created_date) VALUES
    ('Wireless Mouse Pro',          'Electronics',     29.99,  TRUE, CURRENT_DATE),
    ('Ergonomic Office Chair',      'Furniture',       249.50, TRUE, CURRENT_DATE),
    ('Noise-Cancelling Headset',    'Audio',           89.00,  TRUE, CURRENT_DATE),
    ('LED Desk Lamp',               'Lighting',        34.90,  TRUE, CURRENT_DATE),
    ('Multi-Port USB-C Hub',        'Accessories',     19.99,  TRUE, CURRENT_DATE),
    ('Portable SSD 1TB',            'Storage',         109.00, TRUE, CURRENT_DATE),
    ('Mesh WiFi-6 Router',          'Networking',      159.99, TRUE, CURRENT_DATE),
    ('Productivity Suite License',  'Software',        49.00,  TRUE, CURRENT_DATE),
    ('Mechanical Keyboard TKL',     'Peripherals',      99.90, TRUE, CURRENT_DATE),
    ('Recycled Notebook Pack',      'Office Supplies',   6.50, FALSE, CURRENT_DATE);

-- ── Customer detail data ────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS customer_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    order_number VARCHAR(40) NOT NULL,
    order_date VARCHAR(30) NOT NULL,
    status VARCHAR(40) NOT NULL,
    amount VARCHAR(40) NOT NULL
);
CREATE TABLE IF NOT EXISTS customer_invoice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    invoice_number VARCHAR(40) NOT NULL,
    due_date VARCHAR(30) NOT NULL,
    status VARCHAR(40) NOT NULL,
    balance VARCHAR(40) NOT NULL
);
CREATE TABLE IF NOT EXISTS customer_file (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(20) NOT NULL,
    uploaded VARCHAR(30) NOT NULL,
    file_size VARCHAR(30) NOT NULL
);
CREATE TABLE IF NOT EXISTS customer_activity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    event_time VARCHAR(30) NOT NULL,
    actor VARCHAR(120) NOT NULL,
    description VARCHAR(255) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    detail VARCHAR(255)
);

INSERT INTO customer_order (customer_id, order_number, order_date, status, amount) VALUES
    (1, 'SO-2026-0011', '14 Sep 2026', 'In fulfilment', '€89.97'),
    (1, 'SO-2026-0012', '02 Sep 2026', 'Confirmed', '€59.98'),
    (2, 'SO-2026-0021', '11 Sep 2026', 'Confirmed', '€249.50');
INSERT INTO customer_invoice (customer_id, invoice_number, due_date, status, balance) VALUES
    (1, 'INV-2026-0001', '28 Sep 2026', 'Open', '€1,799.40'),
    (1, 'INV-2026-0031', '12 Oct 2026', 'Scheduled', '€29.99'),
    (2, 'INV-2026-0002', '30 Sep 2026', 'Open', '€249.50');
INSERT INTO customer_file (customer_id, file_name, file_type, uploaded, file_size) VALUES
    (1, 'Master agreement C-2026-0001.pdf', 'PDF', '12 Sep 2026', '412 KB'),
    (1, 'NDA + DPA bundle.pdf', 'PDF', '08 Sep 2026', '2.1 MB'),
    (1, 'Account forecast.xlsx', 'XLSX', '01 Sep 2026', '84 KB'),
    (2, 'Master agreement C-2026-0002.pdf', 'PDF', '10 Sep 2026', '398 KB');
INSERT INTO customer_activity (customer_id, event_time, actor, description, severity, detail) VALUES
    (1, '2h ago', 'System', 'SEPA DD scheduled', 'SUCCESS', '€1,799.40 open'),
    (1, 'Yesterday', 'System', 'Order shipped partial', 'INFO', NULL),
    (1, '4 days ago', 'System', 'Dunning +7 sent', 'WARNING', NULL),
    (2, 'Yesterday', 'System', 'Invoice issued', 'INFO', NULL);

-- ── Chat channels ────────────────────────────────────────────────────────────
INSERT INTO chat_room (id, name, description, type, is_private) VALUES
    ('general',      'general',      'General discussion', 'CHANNEL', false),
    ('random',       'random',       'Off-topic',           'CHANNEL', false),
    ('engineering',  'engineering',  'Tech talk',            'CHANNEL', false),
    ('design',       'design',       'Design decisions',     'CHANNEL', false),
    ('announcements','announcements','Company news',         'CHANNEL', false);

-- ── Chat memberships (all demo users join every default channel) ────────────
INSERT INTO chat_room_member (id, user_id, room_id, joined_at, role)
SELECT RANDOM_UUID(), u.user_id, r.id, CURRENT_TIMESTAMP, 'MEMBER'
FROM (VALUES ('alice'), ('bob'), ('carol'), ('dave'), ('eve')) AS u(user_id)
CROSS JOIN chat_room r;
