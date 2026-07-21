-- Mock data for banking-platform-api
-- Assumes tables were already created by Hibernate (ddl-auto=update) and are empty.
-- Insert order respects foreign keys: rol -> profile -> app_user -> account -> beneficiary -> transaction.
-- Account balances are consistent with the transaction history below.

-- ============================================================
-- Roles
-- ============================================================
INSERT INTO rol (rol_id, name, description, add_date, add_user, deleted) VALUES
(1, 'ADMIN',  'System administrator with full access', NOW(), 'seed', 0),
(2, 'CLIENT', 'Regular banking client',                NOW(), 'seed', 0);

-- ============================================================
-- Profiles
-- ============================================================
INSERT INTO profile (profile_id, name, last_name, ci, mobile, address, status, add_date, add_user, deleted) VALUES
(1, 'Carlos', 'Mendoza',   '4587123', '+59171234567', 'Av. Ballivian #456, La Paz',        'ACTIVE', NOW(), 'seed', 0),
(2, 'Maria',  'Rojas',     '6234789', '+59176543210', 'Calle Sucre #123, Cochabamba',      'ACTIVE', NOW(), 'seed', 0),
(3, 'Jorge',  'Quispe',    '5891456', '+59170112233', 'Av. Blanco Galindo Km 4, Cochabamba','ACTIVE', NOW(), 'seed', 0),
(4, 'Lucia',  'Fernandez', '7456321', '+59169887766', 'Calle Bolivar #789, Santa Cruz',    'ACTIVE', NOW(), 'seed', 0);

-- ============================================================
-- Users (passwords are plain text, matching UserLoginUseCase)
-- ============================================================
INSERT INTO app_user (user_id, email, password, profile_id, rol_id, add_date, add_user, deleted) VALUES
(1, 'carlos.mendoza@bancouab.com', 'Admin2026', 1, 1, NOW(), 'seed', 0),
(2, 'maria.rojas@gmail.com',       'Maria2026', 2, 2, NOW(), 'seed', 0),
(3, 'jorge.quispe@gmail.com',      'Jorge2026', 3, 2, NOW(), 'seed', 0),
(4, 'lucia.fernandez@gmail.com',   'Lucia2026', 4, 2, NOW(), 'seed', 0);

-- ============================================================
-- Accounts (balance = deposits - withdrawals +/- transfers)
-- ============================================================
INSERT INTO account (account_id, account_number, currency, type, balance, status, user_id, add_date, add_user, deleted) VALUES
(1, '1000000101', 'BOB', 'SAVINGS',  3700.00, 'ACTIVE', 2, NOW(), 'seed', 0),
(2, '1000000102', 'USD', 'CHECKING', 1000.00, 'ACTIVE', 2, NOW(), 'seed', 0),
(3, '1000000201', 'BOB', 'SAVINGS',  3550.00, 'ACTIVE', 3, NOW(), 'seed', 0),
(4, '1000000301', 'BOB', 'SAVINGS',  4750.00, 'ACTIVE', 4, NOW(), 'seed', 0),
(5, '1000000302', 'BOB', 'CHECKING', 1200.00, 'ACTIVE', 4, NOW(), 'seed', 0);

-- ============================================================
-- Beneficiaries (a user saves another user's account)
-- ============================================================
INSERT INTO beneficiary (beneficiary_id, alias, description, user_id, account_id, add_date, add_user, deleted) VALUES
(1, 'Jorge Q.', 'Landlord - monthly rent',      2, 3, NOW(), 'seed', 0),
(2, 'Lucia F.', 'College friend',               3, 4, NOW(), 'seed', 0),
(3, 'Maria R.', 'Coworker - shared expenses',   4, 1, NOW(), 'seed', 0);

-- ============================================================
-- Transactions
-- DEPOSIT / WITHDRAWAL: source_account_id = own account, target NULL.
-- TRANSFER: source -> target.
-- Balance check:
--   acc 1: +5000 - 800 - 500 = 3700
--   acc 2: +1200 - 200       = 1000
--   acc 3: +3000 + 800 - 250 = 3550
--   acc 4: +4500 + 250       = 4750
--   acc 5: +1500 - 300       = 1200
-- ============================================================
INSERT INTO `transaction` (transaction_id, source_account_id, target_account_id, transaction_type, amount, transaction_date, description, reference, notes, status, currency, add_date, add_user, deleted) VALUES
(1,  1, NULL, 'DEPOSIT',    5000.00, '2026-07-01 09:15:00', 'Initial deposit',          'DEP-0001', NULL,                    'COMPLETED', 'BOB', NOW(), 'seed', 0),
(2,  2, NULL, 'DEPOSIT',    1200.00, '2026-07-01 10:30:00', 'Initial deposit',          'DEP-0002', NULL,                    'COMPLETED', 'USD', NOW(), 'seed', 0),
(3,  3, NULL, 'DEPOSIT',    3000.00, '2026-07-02 11:00:00', 'Salary deposit',           'DEP-0003', NULL,                    'COMPLETED', 'BOB', NOW(), 'seed', 0),
(4,  4, NULL, 'DEPOSIT',    4500.00, '2026-07-02 14:20:00', 'Initial deposit',          'DEP-0004', NULL,                    'COMPLETED', 'BOB', NOW(), 'seed', 0),
(5,  5, NULL, 'DEPOSIT',    1500.00, '2026-07-03 08:45:00', 'Savings transfer-in',      'DEP-0005', NULL,                    'COMPLETED', 'BOB', NOW(), 'seed', 0),
(6,  1, 3,    'TRANSFER',    800.00, '2026-07-05 16:10:00', 'Monthly rent',             'TRF-0001', 'July rent payment',     'COMPLETED', 'BOB', NOW(), 'seed', 0),
(7,  1, NULL, 'WITHDRAWAL',  500.00, '2026-07-08 12:05:00', 'ATM withdrawal',           'WTD-0001', NULL,                    'COMPLETED', 'BOB', NOW(), 'seed', 0),
(8,  3, 4,    'TRANSFER',    250.00, '2026-07-10 19:30:00', 'Dinner split',             'TRF-0002', NULL,                    'COMPLETED', 'BOB', NOW(), 'seed', 0),
(9,  5, NULL, 'WITHDRAWAL',  300.00, '2026-07-12 10:50:00', 'ATM withdrawal',           'WTD-0002', NULL,                    'COMPLETED', 'BOB', NOW(), 'seed', 0),
(10, 2, NULL, 'WITHDRAWAL',  200.00, '2026-07-15 15:40:00', 'Online purchase',          'WTD-0003', NULL,                    'COMPLETED', 'USD', NOW(), 'seed', 0);
