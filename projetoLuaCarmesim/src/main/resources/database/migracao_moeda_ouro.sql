-- ============================================================
-- MIGRAÇÃO — moeda de ouro no inventário/sistema de economia
-- Rode isto se o banco já existe e você não quer recriar tudo.
-- ============================================================

DO $$
BEGIN
    ALTER TYPE tipo_item_enum ADD VALUE IF NOT EXISTS 'moeda';
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;
