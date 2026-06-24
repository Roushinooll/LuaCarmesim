-- ============================================================
-- MIGRAÇÃO — sistema de caldeirão e craft de poções
-- Rode isto se o banco já existe e você não quer recriar tudo.
-- ============================================================

DO $$
BEGIN
    ALTER TYPE tipo_item_enum ADD VALUE IF NOT EXISTS 'ingrediente';
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;
