INSERT INTO address (id, building_number, city, street, country_code, postal_code, latitude, longitude)
VALUES
    (1, '123', 'Test City 1', 'Test Street 1', 'CZ', '12345', 40.7128, -74.0060),
    (2, '456', 'Test City 2', 'Test Street 2', 'SK', '67890', 34.0522, -118.2437),
    (3, '789', 'Test City 3', 'Test Street 3', 'CZ', '54321', 51.5074, -0.1278),
    (4, '12', null, null, 'CZ', '54321', null, null);

INSERT INTO contracting_authority (id, contracting_authority_name, address_id, url)
VALUES
    (1, 'Authority 1', 1, 'http://authority1.example.com'),
    (2, 'Authority 2', 2, 'http://authority2.example.com');

INSERT INTO company (id, company_name, address_id, organisation_id)
VALUES
    (1,'Company A', 1, 'ORG001'),
    (2,'Company B', 2, 'ORG002'),
    (3,'Company C', 3, 'ORG003'),
    (4,'Company D', 1, 'ORG004'),
    (5,'Company E', 4, 'ORG005');

INSERT INTO procurement (id,procurement_name, supplier_id, contracting_authority_id, contract_price, place_of_performance, date_of_publication, system_number)
VALUES
    (1,'Procurement 1', 1, 1, 50000.00, 'Test Place 1', '2023-09-15', 'SYS001'),
    (2,'Procurement 2', 2, 2, 120000.50, 'Test Place 2', '2023-09-10', 'SYS002'),
    (3,'Procurement 3', 3, 1, 75000.75, 'Test Place 3', '2023-08-15', 'SYS003'),
    (4,'Procurement 4', 5, 2, 5000.00, 'Test Place 3', '2023-08-15', 'SYS004');

INSERT INTO offer (id, price, procurement_id, company_id)
VALUES
    (1, 50000.00, 1, 1),
    (2, 120000.50, 2, 2),
    (3, 75000.75, 3, 3),
    (4, 5000.00, 4, 5),
    (5, 10.00, 1, 2),
    (6, 12.00, 1, 4);
