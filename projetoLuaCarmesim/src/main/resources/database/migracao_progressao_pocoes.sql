-- ============================================================
-- MIGRAÇÃO: PROGRESSÃO DE POÇÕES DO JOGADOR
-- Rode este SQL uma vez no banco que já existe.
-- ============================================================

ALTER TABLE jogador
ADD COLUMN IF NOT EXISTS caminho_atual VARCHAR(50);

-- Opcional para testes: resetar um jogador específico.
-- Troque o nome antes de rodar.
-- UPDATE jogador
-- SET sequencia_atual = 10,
--     caminho_atual = NULL
-- WHERE nome = 'SEU_NOME_AQUI';
