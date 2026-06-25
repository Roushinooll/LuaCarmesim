




DO $$
BEGIN
    ALTER TYPE tipo_item_enum ADD VALUE IF NOT EXISTS 'ingrediente';
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;
