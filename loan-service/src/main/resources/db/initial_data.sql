INSERT INTO customers (
    id,
    keycloak_id,
    customer_type,
    full_name,
    email,
    cpf,
    cnpj,
    monthly_income,
    created_at
) VALUES
      (
          '11111111-1111-1111-1111-111111111111',
          'keycloak-joao',
          'PF',
          'Joao Silva',
          'joao@email.com',
          '12345678900',
          NULL,
          10000.00,
          NOW()
      ),
      (
          '22222222-2222-2222-2222-222222222222',
          'keycloak-empresa',
          'PJ',
          'Empresa XPTO LTDA',
          'contato@xpto.com',
          NULL,
          '12345678000199',
          50000.00,
          NOW()
      );

INSERT INTO customer_addresses (
    id,
    customer_id,
    cep,
    street,
    city,
    state,
    location,
    created_at
) VALUES
      (
          'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',
          '11111111-1111-1111-1111-111111111111',
          '01001000',
          'Praça da Sé',
          'São Paulo',
          'SP',
          ST_SetSRID(ST_MakePoint(-46.633308, -23.550520), 4326)::geography,
          NOW()
      ),
      (
          'bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb',
          '22222222-2222-2222-2222-222222222222',
          '20040002',
          'Rua da Assembleia',
          'Rio de Janeiro',
          'RJ',
          ST_SetSRID(ST_MakePoint(-43.178240, -22.906847), 4326)::geography,
          NOW()
      );

INSERT INTO devices (
    id,
    device_fingerprint,
    user_agent,
    platform,
    created_at
) VALUES
      (
          '33333333-3333-3333-3333-333333333333',
          'device-fingerprint-joao-001',
          'Mozilla/5.0 Chrome',
          'WEB',
          NOW()
      ),
      (
          '44444444-4444-4444-4444-444444444444',
          'device-fingerprint-xpto-001',
          'Mozilla/5.0 Safari',
          'MOBILE',
          NOW()
      );

INSERT INTO ip_addresses (
    id,
    ip,
    country,
    state,
    city,
    isp,
    is_vpn,
    is_proxy,
    is_datacenter,
    location,
    created_at
) VALUES
      (
          '55555555-5555-5555-5555-555555555555',
          '177.10.20.30',
          'BR',
          'SP',
          'São Paulo',
          'Vivo',
          false,
          false,
          false,
          ST_SetSRID(ST_MakePoint(-46.633308, -23.550520), 4326)::geography,
          NOW()
      ),
      (
          '66666666-6666-6666-6666-666666666666',
          '200.100.50.25',
          'BR',
          'CE',
          'Fortaleza',
          'Claro',
          true,
          false,
          false,
          ST_SetSRID(ST_MakePoint(-38.526669, -3.731862), 4326)::geography,
          NOW()
      );

INSERT INTO loans (
    id,
    customer_id,
    device_id,
    requested_amount,
    approved_amount,
    installments,
    interest_rate,
    declared_income,
    reason,
    status,
    created_at,
    updated_at
) VALUES
      (
          '77777777-7777-7777-7777-777777777777',
          '11111111-1111-1111-1111-111111111111',
          '33333333-3333-3333-3333-333333333333',
          3000.00,
          3000.00,
          12,
          2.50,
          10000.00,
          'HOME',
          'APPROVED',
          NOW(),
          NOW()
      ),
      (
          '88888888-8888-8888-8888-888888888888',
          '22222222-2222-2222-2222-222222222222',
          '44444444-4444-4444-4444-444444444444',
          80000.00,
          NULL,
          60,
          3.20,
          50000.00,
          'OTHER',
          'MANUAL_REVIEW',
          NOW(),
          NOW()
      );

INSERT INTO status_history (
    id,
    loan_id,
    status,
    changed_at,
    notes
) VALUES
      (
          '99999999-9999-9999-9999-999999999999',
          '77777777-7777-7777-7777-777777777777',
          'PENDING',
          NOW(),
          'Empréstimo criado'
      ),
      (
          '99999999-9999-9999-9999-999999999998',
          '77777777-7777-7777-7777-777777777777',
          'APPROVED',
          NOW(),
          'Aprovado pelo antifraude'
      ),
      (
          '99999999-9999-9999-9999-999999999997',
          '88888888-8888-8888-8888-888888888888',
          'MANUAL_REVIEW',
          NOW(),
          'Enviado para análise manual'
      );

INSERT INTO fraud_analysis (
    id,
    loan_id,
    customer_id,
    total_rules,
    passed_rules,
    failed_rules,
    inconclusive_rules,
    pass_percentage,
    verdict,
    rejection_reason,
    notes,
    analyzed_at
) VALUES
      (
          '12121212-1212-1212-1212-121212121212',
          '77777777-7777-7777-7777-777777777777',
          '11111111-1111-1111-1111-111111111111',
          10,
          9,
          1,
          0,
          90.00,
          'APPROVED',
          NULL,
          'Mais de 80% das regras passaram',
          NOW()
      ),
      (
          '13131313-1313-1313-1313-131313131313',
          '88888888-8888-8888-8888-888888888888',
          '22222222-2222-2222-2222-222222222222',
          10,
          7,
          3,
          0,
          70.00,
          'MANUAL_REVIEW',
          NULL,
          'Menos de 80% das regras passaram',
          NOW()
      );

INSERT INTO fraud_rule_results (
    id,
    fraud_analysis_id,
    rule_name,
    result,
    message,
    created_at
) VALUES
      (
          '14141414-1414-1414-1414-141414141414',
          '12121212-1212-1212-1212-121212121212',
          'CPF_VALID',
          'PASSOU',
          'CPF possui dígito verificador válido',
          NOW()
      ),
      (
          '15151515-1515-1515-1515-151515151515',
          '12121212-1212-1212-1212-121212121212',
          'EMAIL_NOT_LEAKED',
          'PASSOU',
          'Email não encontrado em vazamentos',
          NOW()
      ),
      (
          '16161616-1616-1616-1616-161616161616',
          '12121212-1212-1212-1212-121212121212',
          'IP_STATE_MATCH_ADDRESS',
          'PASSOU',
          'UF do IP compatível com UF do CEP informado',
          NOW()
      ),
      (
          '17171717-1717-1717-1717-171717171717',
          '13131313-1313-1313-1313-131313131313',
          'IP_STATE_MATCH_ADDRESS',
          'FALHOU',
          'IP localizado em UF diferente do endereço informado',
          NOW()
      ),
      (
          '18181818-1818-1818-1818-181818181818',
          '13131313-1313-1313-1313-131313131313',
          'VPN_DETECTED',
          'FALHOU',
          'IP identificado como VPN ou proxy',
          NOW()
      ),
      (
          '19191919-1919-1919-1919-191919191919',
          '13131313-1313-1313-1313-131313131313',
          'CNPJ_ACTIVE',
          'PASSOU',
          'CNPJ localizado e ativo na consulta externa',
          NOW()
      );

INSERT INTO location_evidences (
    id,
    loan_id,
    ip_address_id,
    gps_location,
    gps_accuracy_meters,
    distance_ip_to_address_meters,
    distance_gps_to_address_meters,
    created_at
) VALUES
      (
          '20202020-2020-2020-2020-202020202020',
          '77777777-7777-7777-7777-777777777777',
          '55555555-5555-5555-5555-555555555555',
          ST_SetSRID(ST_MakePoint(-46.633500, -23.550700), 4326)::geography,
          20.00,
          35.00,
          28.00,
          NOW()
      ),
      (
          '21212121-2121-2121-2121-212121212121',
          '88888888-8888-8888-8888-888888888888',
          '66666666-6666-6666-6666-666666666666',
          ST_SetSRID(ST_MakePoint(-43.178240, -22.906847), 4326)::geography,
          30.00,
          2200000.00,
          40.00,
          NOW()
      );