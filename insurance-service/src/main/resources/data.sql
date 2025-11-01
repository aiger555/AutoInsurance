-- Table: clients
CREATE TABLE clients (
                         id UUID PRIMARY KEY,
                         full_name VARCHAR(255) NOT NULL,
                         date_of_birth DATE NOT NULL,
                         phone_number VARCHAR(50),
                         passport_number VARCHAR(50) UNIQUE NOT NULL,
                         pin VARCHAR(50) NOT NULL,
                         address TEXT,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: cars
CREATE TABLE cars (
                      id UUID PRIMARY KEY,
                      brand VARCHAR(100) NOT NULL,
                      model VARCHAR(100) NOT NULL,
                      manufacture_year INTEGER NOT NULL,
                      vin VARCHAR(50) UNIQUE NOT NULL,
                      license_plate VARCHAR(60) UNIQUE NOT NULL,
                      registration_authority VARCHAR(50),
                      registration_date DATE,
                      tech_passport_number VARCHAR(50),
                      engine_volume DOUBLE PRECISION,
                      max_allowed_weight DOUBLE PRECISION,
                      battery_capacity DOUBLE PRECISION,
                      passenger_capacity DOUBLE PRECISION,
                      vehicle_type VARCHAR(50) NOT NULL,
                      owner_id UUID NOT NULL,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                      CONSTRAINT fk_car_owner FOREIGN KEY (owner_id) REFERENCES clients(id) ON DELETE CASCADE
);

-- Table: insurance_policies
CREATE TABLE insurance_policies (
                                    policy_number VARCHAR(50) PRIMARY KEY,
                                    policy_type VARCHAR(20) NOT NULL,
                                    premium DECIMAL(15,2),
                                    start_date DATE NOT NULL,
                                    end_date DATE NOT NULL,
                                    status VARCHAR(20) DEFAULT 'ACTIVE',
                                    vehicle_owner_id UUID NOT NULL,
                                    car_id UUID NOT NULL,
                                    comissar_number VARCHAR(40),
                                    company_number VARCHAR(40),
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    CONSTRAINT fk_policy_vehicle_owner FOREIGN KEY (vehicle_owner_id) REFERENCES clients(id) ON DELETE CASCADE,
                                    CONSTRAINT fk_policy_car FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE
);

-- Table: drivers
CREATE TABLE drivers (
                         id UUID PRIMARY KEY,
                         policy_number VARCHAR(50) NOT NULL,
                         full_name VARCHAR(255) NOT NULL,
                         birth_date DATE NOT NULL,
                         license_number VARCHAR(50) NOT NULL,
                         driving_experience DATE NOT NULL,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT fk_driver_policy FOREIGN KEY (policy_number) REFERENCES insurance_policies(policy_number) ON DELETE CASCADE
);

-- Insert sample clients
INSERT INTO clients (id, full_name, date_of_birth, phone_number, passport_number, pin, address) VALUES
                                                                                                    ('550e8400-e29b-41d4-a716-446655440000', 'Иванов Иван Иванович', '1985-05-15', '+996555123456', 'AN1234567', '10123194567890', 'г. Бишкек, ул. Чуйкова 123'),
                                                                                                    ('550e8400-e29b-41d4-a716-446655440001', 'Петрова Мария Сергеевна', '1990-08-20', '+996555654321', 'AN7654321', '20105198765432', 'г. Бишкек, ул. Советская 45'),
                                                                                                    ('550e8400-e29b-41d4-a716-446655440002', 'Сидоров Алексей Викторович', '1978-11-25', '+996555112233', 'AN1122334', '10211198233445', 'г. Ош, ул. Ленина 67');

-- Insert sample cars
INSERT INTO cars (id, brand, model, manufacture_year, vin, license_plate, registration_authority, registration_date, tech_passport_number, engine_volume, vehicle_type, owner_id) VALUES
                                                                                                                                                                                      ('660e8400-e29b-41d4-a716-446655440000', 'Toyota', 'Camry', 2020, 'JTDKB20U303000001', '01KG123AB', 'ГУВД г. Бишкек', '2020-05-15', 'TP123456789', 2.5, 'PASSENGER_CAR', '550e8400-e29b-41d4-a716-446655440000'),
                                                                                                                                                                                      ('660e8400-e29b-41d4-a716-446655440001', 'Mercedes-Benz', 'E-Class', 2022, 'WDDHF8JB9EA123456', '01KG456CD', 'ГУВД г. Бишкек', '2022-03-20', 'TP987654321', 3.0, 'PASSENGER_CAR', '550e8400-e29b-41d4-a716-446655440001'),
                                                                                                                                                                                      ('660e8400-e29b-41d4-a716-446655440002', 'Kamaz', '65115', 2018, 'XLC65115012345678', '02OS789EF', 'ГУВД г. Ош', '2018-11-10', 'TP456789123', NULL, 'TRUCK', '550e8400-e29b-41d4-a716-446655440002');

-- Insert sample insurance policies
INSERT INTO insurance_policies (policy_number, policy_type, premium, start_date, end_date, vehicle_owner_id, car_id, comissar_number, company_number) VALUES
                                                                                                                                                          ('0505060', 'OSAGO', 4500.00, '2024-12-15', '2025-12-14', '550e8400-e29b-41d4-a716-446655440000', '660e8400-e29b-41d4-a716-446655440000', '+996558040425', '+996202565656'),
                                                                                                                                                          ('236', 'CASCO', 85000.00, '2024-12-16', '2025-12-15', '550e8400-e29b-41d4-a716-446655440001', '660e8400-e29b-41d4-a716-446655440001', '+996558040425', '+996202565656'),
                                                                                                                                                          ('0505061', 'OSAGO', 6800.00, '2024-12-17', '2025-12-16', '550e8400-e29b-41d4-a716-446655440002', '660e8400-e29b-41d4-a716-446655440002', '+996558040425', '+996202565656');

-- Insert sample drivers
INSERT INTO drivers (id, policy_number, full_name, birth_date, license_number, driving_experience) VALUES
                                                                                                       ('770e8400-e29b-41d4-a716-446655440000', '0505060', 'Иванов Иван Иванович', '1985-05-15', 'AA123456', '2014-05-15'),
                                                                                                       ('770e8400-e29b-41d4-a716-446655440001', '0505060', 'Петрова Мария Сергеевна', '1990-08-20', 'AB654321', '2019-08-20'),
                                                                                                       ('770e8400-e29b-41d4-a716-446655440002', '236', 'Петрова Мария Сергеевна', '1990-08-20', 'AC987654', '2016-03-10'),
                                                                                                       ('770e8400-e29b-41d4-a716-446655440003', '0505061', 'Сидоров Алексей Викторович', '1978-11-25', 'AD321987', '2009-11-25');