
BEGIN;

INSERT INTO public.tariffs (name, status, billing_step_minutes, free_minutes, hour_price, created, changed)
VALUES
    ('STANDARD',      'ACTIVE', 30, 10, 2.50, now(), now()),
    ('ELECTRIC_FAST', 'ACTIVE', 15,  5, 3.80, now(), now())
ON CONFLICT (name) DO NOTHING;

WITH std_tariff AS (
    SELECT id FROM public.tariffs WHERE name = 'STANDARD'
)
INSERT INTO public.parking_lots (tariff_id, address, name, status, created, changed)
VALUES
    ((SELECT id FROM std_tariff), 'Минск, ул. Ленина, 1',        'Central Lot',   'ACTIVE',      now(), now()),
    ((SELECT id FROM std_tariff), 'Минск, ул. Притыцкого, 12',  'West Lot',      'ACTIVE',      now(), now()),
    ((SELECT id FROM std_tariff), 'Минск, пр-т Победителей, 9', 'Riverside Lot', 'MAINTENANCE', now(), now())
ON CONFLICT (address) DO NOTHING;


WITH lots AS (
    SELECT id FROM public.parking_lots
),
gen AS (
    SELECT l.id AS parking_lot_id,
           gs.n AS number,
           CASE WHEN gs.n <= 15 THEN 0 ELSE 1 END AS level,
           CASE
               WHEN gs.n IN (3,4,5) THEN 'DISABLED'
               WHEN gs.n IN (10,11,12,13) THEN 'ELECTRIC'
               ELSE 'STANDARD'
           END AS type,
           'AVAILABLE' AS status
    FROM lots l
    CROSS JOIN LATERAL (SELECT generate_series(1, 30) AS n) gs
)
INSERT INTO public.spots (parking_lot_id, number, level, type, status, created, changed)
SELECT parking_lot_id, number, level, type, status, now(), now()
FROM gen
ON CONFLICT (parking_lot_id, number) DO NOTHING;


INSERT INTO public.users (first_name, second_name, email, status, disabled_permit, created, changed)
VALUES
    ('Иван',   'Иванов',   'ivan@example.com',   'ACTIVE',  false, now(), now()),
    ('Анна',   'Сидорова', 'anna@example.com',   'ACTIVE',  true,  now(), now()),
    ('Пётр',   'Петров',   'petr@example.com',   'ACTIVE',  false, now(), now()),
    ('Ольга',  'Кузнецова','olga@example.com',   'BLOCKED', false, now(), now())
ON CONFLICT (email) DO NOTHING;

WITH u AS (
    SELECT id, email FROM public.users
    WHERE email IN ('ivan@example.com','anna@example.com','petr@example.com','olga@example.com')
)
INSERT INTO public.security (username, password, role, user_id)
SELECT
    split_part(email, '@', 1) AS username,
    '$2a$10$7EqJtq98hPqEX7fNZaFWoOaQ8f5bC1u6GqZ6uZ2G1u8G9Q5k3e6yK' AS password,
    CASE
        WHEN email = 'ivan@example.com' THEN 'ADMIN'
        ELSE 'USER'
    END AS role,
    id AS user_id
FROM u
ON CONFLICT (username) DO NOTHING;

WITH u_ivan AS (SELECT id FROM public.users WHERE email = 'ivan@example.com'),
     u_anna AS (SELECT id FROM public.users WHERE email = 'anna@example.com'),
     u_petr AS (SELECT id FROM public.users WHERE email = 'petr@example.com')
INSERT INTO public.vehicles (user_id, plate_number, type, created, changed)
VALUES
    ((SELECT id FROM u_ivan), 'A123BC-7', 'CAR',          now(), now()),
    ((SELECT id FROM u_ivan), 'E777AA-5', 'ELECTRIC_CAR', now(), now()),
    ((SELECT id FROM u_anna), 'M555TT-2', 'MOTORCYCLE',   now(), now()),
    ((SELECT id FROM u_petr), 'T999TT-7', 'TRUCK',       now(), now())
ON CONFLICT (plate_number) DO NOTHING;

WITH v1 AS (SELECT id FROM public.vehicles WHERE plate_number = 'A123BC-7'),
     v2 AS (SELECT id FROM public.vehicles WHERE plate_number = 'E777AA-5'),
     s1 AS (SELECT id FROM public.spots ORDER BY id ASC LIMIT 1),
     s2 AS (SELECT id FROM public.spots ORDER BY id ASC OFFSET 5 LIMIT 1)
INSERT INTO public.reservations (spot_id, vehicle_id, start_time, end_time, status, created, changed)
VALUES
    ((SELECT id FROM s1), (SELECT id FROM v1), now() + interval '1 hour',  now() + interval '3 hours', 'ACTIVE',  now(), now()),
    ((SELECT id FROM s2), (SELECT id FROM v2), now() - interval '4 hours', now() - interval '2 hours', 'EXPIRED', now(), now())
ON CONFLICT DO NOTHING;

WITH v1 AS (SELECT id FROM public.vehicles WHERE plate_number = 'A123BC-7'),
     v2 AS (SELECT id FROM public.vehicles WHERE plate_number = 'E777AA-5'),
     s3 AS (SELECT id FROM public.spots ORDER BY id ASC OFFSET 10 LIMIT 1),
     s4 AS (SELECT id FROM public.spots ORDER BY id ASC OFFSET 15 LIMIT 1),
     r_active AS (SELECT id FROM public.reservations WHERE status='ACTIVE' LIMIT 1)
INSERT INTO public.parking_sessions
    (spot_id, vehicle_id, start_time, end_time, status, reservation_id, total_cost)
VALUES
    ((SELECT id FROM s3), (SELECT id FROM v1), now() - interval '20 minutes', NULL, 'ACTIVE',   NULL,        NULL),
    ((SELECT id FROM s4), (SELECT id FROM v2), now() - interval '3 hours',    now() - interval '1 hour', 'FINISHED',
        (SELECT id FROM r_active), 9.40)
ON CONFLICT DO NOTHING;

COMMIT;
