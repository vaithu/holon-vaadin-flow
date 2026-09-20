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
