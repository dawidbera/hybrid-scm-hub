-- Initial data for Hybrid-Cloud SCM Hub
-- Products
INSERT INTO products (id, sku, name, description, base_price) VALUES 
(gen_random_uuid(), 'LAP-001', 'High-End Laptop', 'Professional grade workstation', 1200.00),
(gen_random_uuid(), 'MOU-002', 'Wireless Mouse', 'Ergonomic optical mouse', 25.50),
(gen_random_uuid(), 'MON-003', '4K Monitor', '32-inch ultra-wide display', 450.00)
ON CONFLICT (sku) DO NOTHING;

-- Warehouses
INSERT INTO warehouses (id, name, location) VALUES 
(gen_random_uuid(), 'Central Warehouse', 'London, UK'),
(gen_random_uuid(), 'North Branch', 'New York, US'),
(gen_random_uuid(), 'West Hub', 'Chicago, US')
ON CONFLICT DO NOTHING;
