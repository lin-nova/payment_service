INSERT INTO clients(uuid, username) VALUES ('067016f6-89a7-4570-8c33-7a476a44c3a6', 'client_1');
INSERT INTO clients(uuid, username) VALUES ('7a4e5998-6883-45f5-be64-8d6c3a097ec8', 'client_2');

INSERT INTO contracts(uuid, contract_number, client_id) VALUES ('a5bc1b00-f8c4-4700-bc2f-747994d3d610', '0001', '067016f6-89a7-4570-8c33-7a476a44c3a6');
INSERT INTO contracts(uuid, contract_number, client_id) VALUES ('7416f0e6-27c4-4fe6-b746-402f5f95076b', '0002', '7a4e5998-6883-45f5-be64-8d6c3a097ec8');


INSERT INTO payments(uuid, date, amount, type, contract_id, client_id) VALUES ('c1f1e8b4-8f3e-4d2a-9f3e-1a2b3c4d5e6f','2024-01-15',1500.00,'INCOMING', 'a5bc1b00-f8c4-4700-bc2f-747994d3d610', '067016f6-89a7-4570-8c33-7a476a44c3a6');
INSERT INTO payments(uuid, date, amount, type, contract_id, client_id) VALUES ('d221e8b4-8f3e-4d2a-9f3e-1a2b3c4d5e6f','2024-02-15',1501.00,'INCOMING', 'a5bc1b00-f8c4-4700-bc2f-747994d3d610', '067016f6-89a7-4570-8c33-7a476a44c3a6');



