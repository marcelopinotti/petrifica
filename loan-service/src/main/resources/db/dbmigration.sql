CREATE EXTENSION IF NOT EXISTS postgis;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE customers (
                           id UUID PRIMARY KEY,
                           keycloak_id VARCHAR(255) UNIQUE,
                           customer_type VARCHAR(20) NOT NULL,
                           full_name VARCHAR(255) NOT NULL,
                           email VARCHAR(255) UNIQUE NOT NULL,
                           cpf VARCHAR(11) UNIQUE,
                           cnpj VARCHAR(14) UNIQUE,
                           monthly_income NUMERIC(15,2),
                           created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE customer_addresses (
                                    id UUID PRIMARY KEY,
                                    customer_id UUID NOT NULL REFERENCES customers(id),
                                    cep VARCHAR(8),
                                    street VARCHAR(255),
                                    city VARCHAR(100),
                                    state VARCHAR(2),
                                    location GEOGRAPHY(POINT,4326),
                                    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE devices (
                         id UUID PRIMARY KEY,
                         device_fingerprint VARCHAR(255) UNIQUE NOT NULL,
                         user_agent TEXT,
                         platform VARCHAR(100),
                         created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE ip_addresses (
                              id UUID PRIMARY KEY,
                              ip VARCHAR(45) UNIQUE NOT NULL,
                              country VARCHAR(100),
                              state VARCHAR(100),
                              city VARCHAR(100),
                              isp VARCHAR(255),
                              is_vpn BOOLEAN DEFAULT FALSE,
                              is_proxy BOOLEAN DEFAULT FALSE,
                              is_datacenter BOOLEAN DEFAULT FALSE,
                              location GEOGRAPHY(POINT,4326),
                              created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE loans (
                       id UUID PRIMARY KEY,
                       customer_id UUID NOT NULL REFERENCES customers(id),
                       device_id UUID REFERENCES devices(id),
                       requested_amount NUMERIC(15,2) NOT NULL,
                       approved_amount NUMERIC(15,2),
                       installments INT,
                       interest_rate NUMERIC(5,2),
                       declared_income NUMERIC(15,2),
                       reason VARCHAR(50),
                       status VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP DEFAULT NOW(),
                       updated_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE status_history (
                                id UUID PRIMARY KEY,
                                loan_id UUID NOT NULL REFERENCES loans(id),
                                status VARCHAR(50) NOT NULL,
                                changed_at TIMESTAMP DEFAULT NOW(),
                                notes TEXT
);

CREATE TABLE fraud_analysis (
                                id UUID PRIMARY KEY,
                                loan_id UUID NOT NULL UNIQUE REFERENCES loans(id),
                                customer_id UUID NOT NULL REFERENCES customers(id),
                                total_rules INT NOT NULL,
                                passed_rules INT NOT NULL,
                                failed_rules INT NOT NULL,
                                inconclusive_rules INT NOT NULL,
                                pass_percentage NUMERIC(5,2) NOT NULL,
                                verdict VARCHAR(50) NOT NULL,
                                rejection_reason TEXT,
                                notes TEXT,
                                analyzed_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE fraud_rule_results (
                                    id UUID PRIMARY KEY,
                                    fraud_analysis_id UUID NOT NULL REFERENCES fraud_analysis(id),
                                    rule_name VARCHAR(150) NOT NULL,
                                    result VARCHAR(30) NOT NULL,
                                    message TEXT,
                                    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE location_evidences (
                                    id UUID PRIMARY KEY,
                                    loan_id UUID NOT NULL REFERENCES loans(id),
                                    ip_address_id UUID REFERENCES ip_addresses(id),
                                    gps_location GEOGRAPHY(POINT,4326),
                                    gps_accuracy_meters NUMERIC(10,2),
                                    distance_ip_to_address_meters NUMERIC(12,2),
                                    distance_gps_to_address_meters NUMERIC(12,2),
                                    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_customer_addresses_location
    ON customer_addresses
    USING GIST(location);

CREATE INDEX idx_ip_addresses_location
    ON ip_addresses
    USING GIST(location);

CREATE INDEX idx_location_evidences_gps_location
    ON location_evidences
    USING GIST(gps_location);

CREATE INDEX idx_loans_customer_id
    ON loans(customer_id);

CREATE INDEX idx_loans_status
    ON loans(status);

CREATE INDEX idx_fraud_analysis_verdict
    ON fraud_analysis(verdict);

CREATE INDEX idx_fraud_rule_results_analysis
    ON fraud_rule_results(fraud_analysis_id);