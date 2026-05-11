ALTER TABLE company_submitted_documents
ADD COLUMN IF NOT EXISTS reviewed_at TIMESTAMP;

ALTER TABLE company_submitted_documents
ADD COLUMN IF NOT EXISTS reviewed_by UUID;

ALTER TABLE company_submitted_documents
ADD COLUMN IF NOT EXISTS replaced_by_document_id UUID;

ALTER TABLE company_submitted_documents
ADD COLUMN IF NOT EXISTS deletion_reason TEXT;

ALTER TABLE company_submitted_documents
DROP CONSTRAINT IF EXISTS fk_company_submitted_documents_reviewed_by;

ALTER TABLE company_submitted_documents
ADD CONSTRAINT fk_company_submitted_documents_reviewed_by
FOREIGN KEY (reviewed_by)
REFERENCES users(id);

ALTER TABLE company_submitted_documents
DROP CONSTRAINT IF EXISTS fk_company_submitted_documents_replaced_by;

ALTER TABLE company_submitted_documents
ADD CONSTRAINT fk_company_submitted_documents_replaced_by
FOREIGN KEY (replaced_by_document_id)
REFERENCES company_submitted_documents(id);

CREATE INDEX IF NOT EXISTS idx_company_submitted_documents_reviewed_by
ON company_submitted_documents(reviewed_by);

CREATE INDEX IF NOT EXISTS idx_company_submitted_documents_replaced_by
ON company_submitted_documents(replaced_by_document_id);

CREATE INDEX IF NOT EXISTS idx_company_submitted_documents_convenio_type_status
ON company_submitted_documents(convenio_id, document_type, status);