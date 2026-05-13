-- ============================================================
--  LUA CARMESIM — Script de criação do banco de dados
--  Projeto TLP2 | Heitor Tonani & Daniel Muniz
--  Adaptado para PostgreSQL (pgAdmin)
-- ============================================================

-- Execute conectado ao banco desejado no pgAdmin.
-- Caso queira criar o banco antes, rode manualmente:
-- CREATE DATABASE lua_carmesim
--     ENCODING 'UTF8'
--     LC_COLLATE 'pt_BR.UTF-8'
--     LC_CTYPE   'pt_BR.UTF-8';
-- Em seguida conecte nele e execute o restante deste script.

-- ============================================================
--  TIPOS ENUM
--  No PostgreSQL, ENUMs são tipos nomeados criados antes das
--  tabelas que os utilizam.
-- ============================================================

CREATE TYPE status_run_enum AS ENUM (
    'em_andamento',
    'morto',
    'vitoria',
    'retornou_via_porta_latao'
);

CREATE TYPE tipo_buff_enum AS ENUM (
    'combate',
    'alquimia',
    'exploracao',
    'dialogo',
    'sanidade'
);

CREATE TYPE tipo_item_enum AS ENUM (
    'revolver',
    'lamina',
    'talismo',
    'pocao',
    'reliquio'
);

CREATE TYPE tipo_ingrediente_enum AS ENUM (
    'erva',
    'fluido_biologico',
    'minerio',
    'parte_monstro',
    'item_npc'
);

-- ============================================================
--  FUNÇÃO AUXILIAR para atualização automática de timestamp
--  Substitui o ON UPDATE CURRENT_TIMESTAMP do MySQL.
-- ============================================================

CREATE OR REPLACE FUNCTION atualizar_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.atualizado_em = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ------------------------------------------------------------
-- JOGADOR
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS jogador (
    id_jogador      SERIAL          PRIMARY KEY,
    nome            VARCHAR(100)    NOT NULL,
    sequencia_atual INT             NOT NULL DEFAULT 10,
    sanidade_maxima INT             NOT NULL DEFAULT 100,
    sanidade_atual  INT             NOT NULL DEFAULT 100,
    criado_em       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_sanidade CHECK (sanidade_atual BETWEEN 0 AND sanidade_maxima),
    CONSTRAINT chk_sequencia CHECK (sequencia_atual BETWEEN 0 AND 10)
);

-- ------------------------------------------------------------
-- PROGRESSO
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS progresso (
    id_progresso    SERIAL              PRIMARY KEY,
    id_jogador      INT                 NOT NULL,
    sala_atual      INT                 NOT NULL DEFAULT 1,
    andar_atual     INT                 NOT NULL DEFAULT 1,
    status_run      status_run_enum     NOT NULL DEFAULT 'em_andamento',
    atualizado_em   TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_progresso_jogador
        FOREIGN KEY (id_jogador) REFERENCES jogador(id_jogador)
        ON DELETE CASCADE
);

-- Trigger que substitui o ON UPDATE CURRENT_TIMESTAMP do MySQL
CREATE OR REPLACE TRIGGER trg_progresso_atualizado_em
    BEFORE UPDATE ON progresso
    FOR EACH ROW
    EXECUTE FUNCTION atualizar_timestamp();

-- ------------------------------------------------------------
-- RANKING
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ranking (
    id_ranking          SERIAL      PRIMARY KEY,
    id_jogador          INT         NOT NULL,
    total_salas_zeradas INT         NOT NULL DEFAULT 0,
    melhor_sequencia    INT         NOT NULL DEFAULT 10,
    data_recorde        TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_ranking_jogador
        FOREIGN KEY (id_jogador) REFERENCES jogador(id_jogador)
        ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- BUFF_PERMANENTE
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS buff_permanente (
    id_buff     SERIAL          PRIMARY KEY,
    id_jogador  INT             NOT NULL,
    nome_buff   VARCHAR(100)    NOT NULL,
    tipo        tipo_buff_enum  NOT NULL,
    valor       INT             NOT NULL DEFAULT 0,
    descricao   VARCHAR(255),

    CONSTRAINT fk_buff_jogador
        FOREIGN KEY (id_jogador) REFERENCES jogador(id_jogador)
        ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- ITEM_ESPECIAL
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS item_especial (
    id_item     SERIAL          PRIMARY KEY,
    id_jogador  INT             NOT NULL,
    nome_item   VARCHAR(100)    NOT NULL,
    tipo_item   tipo_item_enum  NOT NULL,
    efeito      VARCHAR(255),
    permanente  BOOLEAN         NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_item_jogador
        FOREIGN KEY (id_jogador) REFERENCES jogador(id_jogador)
        ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- FORMULA_POCAO
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS formula_pocao (
    id_formula          SERIAL          PRIMARY KEY,
    nome_pocao          VARCHAR(150)    NOT NULL,
    nivel_sequencia     INT             NOT NULL,
    efeito_principal    VARCHAR(255)    NOT NULL,
    descricao           TEXT,

    CONSTRAINT chk_nivel_formula CHECK (nivel_sequencia BETWEEN 0 AND 9)
);

-- ------------------------------------------------------------
-- INGREDIENTE_FORMULA
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ingrediente_formula (
    id_ingrediente      SERIAL                  PRIMARY KEY,
    id_formula          INT                     NOT NULL,
    nome_ingrediente    VARCHAR(150)            NOT NULL,
    tipo_ingrediente    tipo_ingrediente_enum   NOT NULL,
    quantidade          INT                     NOT NULL DEFAULT 1,

    CONSTRAINT fk_ingrediente_formula
        FOREIGN KEY (id_formula) REFERENCES formula_pocao(id_formula)
        ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- JOGADOR_FORMULA (tabela associativa)
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS jogador_formula (
    id_jogador      INT         NOT NULL,
    id_formula      INT         NOT NULL,
    aprendida_em    TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id_jogador, id_formula),

    CONSTRAINT fk_jogfor_jogador
        FOREIGN KEY (id_jogador) REFERENCES jogador(id_jogador)
        ON DELETE CASCADE,
    CONSTRAINT fk_jogfor_formula
        FOREIGN KEY (id_formula) REFERENCES formula_pocao(id_formula)
        ON DELETE CASCADE
);

-- ============================================================
--  DADOS DE EXEMPLO
-- ============================================================

INSERT INTO jogador (nome, sequencia_atual, sanidade_maxima, sanidade_atual)
VALUES ('Protagonista', 9, 100, 85);

INSERT INTO progresso (id_jogador, sala_atual, andar_atual, status_run)
VALUES (1, 3, 1, 'em_andamento');

INSERT INTO ranking (id_jogador, total_salas_zeradas, melhor_sequencia)
VALUES (1, 7, 9);

INSERT INTO buff_permanente (id_jogador, nome_buff, tipo, valor, descricao)
VALUES (1, 'Mente Forjada', 'sanidade', 15,
        'Cada morte ensinou a resistir um pouco mais às alucinações.');

INSERT INTO item_especial (id_jogador, nome_item, tipo_item, efeito, permanente)
VALUES (1, 'Revólver do Carcereiro', 'revolver',
        'Causa dano extra contra Beyonders de sequência superior.', TRUE);

INSERT INTO formula_pocao (nome_pocao, nivel_sequencia, efeito_principal, descricao)
VALUES ('Poção Noturna de Sequência 9', 9,
        'Desperta a percepção oculta do usuário',
        'O primeiro passo para se tornar um Beyonder. Ingredientes básicos, mas o ritual é crítico.');

INSERT INTO ingrediente_formula (id_formula, nome_ingrediente, tipo_ingrediente, quantidade)
VALUES
    (1, 'Erva da Lua Carmesim',             'erva',              3),
    (1, 'Fluido Cerebral de Rato das Ruas', 'fluido_biologico',  1),
    (1, 'Pó de Âmbar Negro',               'minerio',           2);

INSERT INTO jogador_formula (id_jogador, id_formula)
VALUES (1, 1);

-- ============================================================
--  FIM DO SCRIPT
-- ============================================================
