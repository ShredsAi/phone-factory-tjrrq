-- Purchase Orders table
CREATE TABLE IF NOT EXISTS purchase_orders (
    order_id VARCHAR(50) PRIMARY KEY,
    supplier_id VARCHAR(50) NOT NULL,
    order_date TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(30) NOT NULL,
    total_amount DECIMAL(19,4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    expected_delivery_date TIMESTAMP WITH TIME ZONE,
    payment_terms TEXT,
    delivery_conditions TEXT,
    contractual_obligations TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Order Line Items table
CREATE TABLE IF NOT EXISTS order_line_items (
    line_item_id UUID PRIMARY KEY,
    purchase_order_id VARCHAR(50) NOT NULL,
    material_id VARCHAR(50) NOT NULL,
    quantity DECIMAL(18,4) NOT NULL,
    unit_price DECIMAL(19,4) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    line_total DECIMAL(19,4) NOT NULL,
    requested_delivery_date TIMESTAMP WITH TIME ZONE,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_line_items_purchase_order
        FOREIGN KEY (purchase_order_id)
        REFERENCES purchase_orders(order_id)
        ON DELETE CASCADE
);

-- Approval Workflows table
CREATE TABLE IF NOT EXISTS approval_workflows (
    workflow_id UUID PRIMARY KEY,
    purchase_order_id VARCHAR(50) NOT NULL UNIQUE,
    current_approval_level VARCHAR(30),
    current_approver_id VARCHAR(50),
    required_approval_level VARCHAR(30) NOT NULL,
    workflow_status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_approval_workflows_purchase_order
        FOREIGN KEY (purchase_order_id)
        REFERENCES purchase_orders(order_id)
        ON DELETE CASCADE
);

-- Approval Steps table
CREATE TABLE IF NOT EXISTS approval_steps (
    step_id UUID PRIMARY KEY,
    workflow_id UUID NOT NULL,
    approval_level VARCHAR(30) NOT NULL,
    approver_id VARCHAR(50) NOT NULL,
    decision VARCHAR(20) NOT NULL,
    comments TEXT,
    decision_timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_approval_steps_workflow
        FOREIGN KEY (workflow_id)
        REFERENCES approval_workflows(workflow_id)
        ON DELETE CASCADE
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_purchase_orders_status ON purchase_orders(status);
CREATE INDEX IF NOT EXISTS idx_purchase_orders_supplier ON purchase_orders(supplier_id);
CREATE INDEX IF NOT EXISTS idx_order_line_items_material ON order_line_items(material_id);
CREATE INDEX IF NOT EXISTS idx_approval_workflows_status ON approval_workflows(workflow_status);
CREATE INDEX IF NOT EXISTS idx_approval_steps_approver ON approval_steps(approver_id);