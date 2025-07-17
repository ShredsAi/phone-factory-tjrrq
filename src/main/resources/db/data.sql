-- Sample purchase order for testing
INSERT INTO purchase_orders (order_id, supplier_id, order_date, status, total_amount, currency, expected_delivery_date)
VALUES
    ('PO-2024-001', 'SUP-001', CURRENT_TIMESTAMP, 'DRAFT', 4500.00, 'USD', CURRENT_TIMESTAMP + INTERVAL '7 days')
ON CONFLICT (order_id) DO NOTHING;

-- Sample order line item for testing
INSERT INTO order_line_items (line_item_id, purchase_order_id, material_id, quantity, unit_price, currency, line_total, requested_delivery_date, notes)
VALUES
    (gen_random_uuid(), 'PO-2024-001', 'MTL-001', 100.00, 45.00, 'USD', 4500.00, CURRENT_TIMESTAMP + INTERVAL '7 days', 'Initial test line')
ON CONFLICT (line_item_id) DO NOTHING;

-- Sample approval workflow for testing
INSERT INTO approval_workflows (workflow_id, purchase_order_id, current_approval_level, current_approver_id, required_approval_level, workflow_status)
VALUES
    (gen_random_uuid(), 'PO-2024-001', 'SUPERVISOR', 'USR-001', 'SUPERVISOR', 'PENDING')
ON CONFLICT (purchase_order_id) DO NOTHING;