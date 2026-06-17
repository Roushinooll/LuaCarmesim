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
    'ingrediente',
    'reliquio',
    'moeda'
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
    caminho_atual   VARCHAR(50),
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
VALUES ('Protagonista', 10, 100, 85);

INSERT INTO progresso (id_jogador, sala_atual, andar_atual, status_run)
VALUES (1, 3, 1, 'em_andamento');

INSERT INTO ranking (id_jogador, total_salas_zeradas, melhor_sequencia)
VALUES (1, 7, 10);

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
-- RECEITAS COMPLETAS DAS POÇÕES DE SEQUÊNCIA
-- Baseadas no documento de design do Lua Carmesim
-- ============================================================

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_formula_nome'
    ) THEN
        ALTER TABLE formula_pocao
        ADD CONSTRAINT uq_formula_nome UNIQUE (nome_pocao);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_ingrediente_formula_nome'
    ) THEN
        ALTER TABLE ingrediente_formula
        ADD CONSTRAINT uq_ingrediente_formula_nome UNIQUE (id_formula, nome_ingrediente);
    END IF;
END $$;

INSERT INTO formula_pocao (nome_pocao, nivel_sequencia, efeito_principal, descricao)
VALUES
('Vidente', 9, 'Sensibilidade espiritual e percepção oculta.', 'Inaugura a sensibilidade espiritual, permitindo reconhecer entidades, vestígios ocultos e ameaças latentes.'),
('Palhaço', 8, 'Agilidade, evasão e ações caóticas.', 'Refina drasticamente a agilidade, os mecanismos de evasão e a natureza caótica das ações em combate.'),
('Mágico', 7, 'Prodígios místicos menores e ilusões.', 'Habilita a execução de prodígios místicos menores, projeções ilusórias e alteração da matéria física.'),
('Sem Rosto', 6, 'Mimetismo, disfarce e novas opções de diálogo.', 'Proporciona mimetismo de formas, ludíbrio de adversários e acesso a ramos dialógicos inéditos com NPCs.'),
('Marionetista', 5, 'Controle de inimigos debilitados.', 'Permite subjugação de inimigos debilitados, controle de receptáculos e distorção da vontade alheia.'),

('Criminoso', 9, 'Discrição, resistência física e intimidação.', 'Amplia a discrição, a tolerância ao sofrimento físico e a imposição de terror sobre humanos.'),
('Anjo Sem Asas', 8, 'Aura hostil, defesa física e coerção.', 'Concede uma aura de hostilidade absoluta, reforçando a couraça física e a coerção psicológica.'),
('Assassino em Série', 7, 'Golpes letais e abate furtivo.', 'Otimiza golpes letais, letalidade contra alvos avariados e ritos de abate furtivo.'),
('Diabo', 6, 'Corrupção e energia brutal.', 'Libera artes da corrupção, energias deletérias e o fomento a impulsos brutais.'),
('Apóstolo do Desejo', 5, 'Indução à demência e domínio por pavor.', 'Permite a gestão das paixões, a indução à demência e a vassalagem pelo pavor ou cobiça.'),

('Caçador', 9, 'Rastreio, perícia bélica e ataques elementares.', 'Incrementa a perícia bélica, artes de rastreio e força de ataques elementares.'),
('Provocador', 8, 'Indução de fúria ou pânico.', 'Garante a indução de fúria ou pânico, forçando adversários a cometerem erros táticos sob pressão.'),
('Piromaníaco', 7, 'Manipulação de chamas e combustão.', 'Desbloqueia manipulação de chamas, combustão persistente e detonações de alta volatilidade.'),
('Conspirador', 6, 'Estratégia, armadilhas e leitura de intenções.', 'Refina o intelecto estratégico, o uso de armadilhas e a percepção das intenções do opressor.'),
('Ceifador', 5, 'Execução precisa de ciclos de vida debilitados.', 'Consagra o jogador como executor irreprimível, capaz de findar ciclos de vida debilitados com precisão.'),

('Bardo', 9, 'Manipulação sonora e oratória.', 'Habilita a manipulação sonora e oratória para revigoração pessoal ou erosão da moral inimiga.'),
('Suplicante da Luz', 8, 'Defesa espiritual e restauração.', 'Proporciona restabelecimento da essência, defesa espiritual e salvaguarda contra a mácula.'),
('Sumo Sacerdote do Sol', 7, 'Radiação ofensiva e expurgo.', 'Canaliza radiação ofensiva, ritos de expurgo e dano severo contra entidades das trevas.'),
('Notário', 6, 'Dogmas místicos e restrição do agressor.', 'Estabelece dogmas místicos em batalha, reforçando juramentos e restringindo a mobilidade do agressor.'),
('Sacerdote da Luz', 5, 'Purificação avançada e aniquilação solar.', 'Otorga o apogeu da purificação, terapias avançadas e aniquilação solar de entidades corrompidas.')
ON CONFLICT (nome_pocao) DO UPDATE SET
    nivel_sequencia = EXCLUDED.nivel_sequencia,
    efeito_principal = EXCLUDED.efeito_principal,
    descricao = EXCLUDED.descricao;

INSERT INTO ingrediente_formula (id_formula, nome_ingrediente, tipo_ingrediente, quantidade)
SELECT fp.id_formula, dados.nome_ingrediente, dados.tipo_ingrediente::tipo_ingrediente_enum, dados.quantidade
FROM formula_pocao fp
JOIN (
    VALUES
    ('Vidente', 'Erva de Névoa Prateada', 'erva', 3),
    ('Vidente', 'Fragmento de Vidro Manchado', 'minerio', 2),
    ('Vidente', 'Olho Seco de Corvo Urbano', 'parte_monstro', 1),
    ('Vidente', 'Orvalho da Meia-Noite', 'erva', 1),

    ('Palhaço', 'Erva de Névoa Prateada', 'erva', 4),
    ('Palhaço', 'Fragmento de Vidro Manchado', 'minerio', 3),
    ('Palhaço', 'Olho Seco de Corvo Urbano', 'parte_monstro', 2),
    ('Palhaço', 'Máscara Rachada de Artista de Rua', 'item_npc', 1),

    ('Mágico', 'Fragmento de Vidro Manchado', 'minerio', 4),
    ('Mágico', 'Orvalho da Meia-Noite', 'erva', 3),
    ('Mágico', 'Máscara Rachada de Artista de Rua', 'item_npc', 2),
    ('Mágico', 'Carta de Tarô Queimada', 'item_npc', 1),

    ('Sem Rosto', 'Máscara Rachada de Artista de Rua', 'item_npc', 3),
    ('Sem Rosto', 'Carta de Tarô Queimada', 'item_npc', 2),
    ('Sem Rosto', 'Lágrima de Espelho Vivo', 'minerio', 2),
    ('Sem Rosto', 'Pele Preservada de Doppelgänger', 'parte_monstro', 1),

    ('Marionetista', 'Carta de Tarô Queimada', 'item_npc', 3),
    ('Marionetista', 'Pele Preservada de Doppelgänger', 'parte_monstro', 2),
    ('Marionetista', 'Coração de Marionete Humana', 'parte_monstro', 1),
    ('Marionetista', 'Fio de Prata Lunar', 'minerio', 1),

    ('Criminoso', 'Sangue Coagulado', 'fluido_biologico', 3),
    ('Criminoso', 'Faca Enferrujada', 'item_npc', 2),
    ('Criminoso', 'Dente de Rato Gigante', 'parte_monstro', 1),
    ('Criminoso', 'Moeda Roubada de Cadáver', 'item_npc', 1),

    ('Anjo Sem Asas', 'Sangue Coagulado', 'fluido_biologico', 4),
    ('Anjo Sem Asas', 'Faca Enferrujada', 'item_npc', 3),
    ('Anjo Sem Asas', 'Moeda Roubada de Cadáver', 'item_npc', 2),
    ('Anjo Sem Asas', 'Pena Negra Queimada', 'parte_monstro', 1),

    ('Assassino em Série', 'Sangue Coagulado', 'fluido_biologico', 4),
    ('Assassino em Série', 'Pena Negra Queimada', 'parte_monstro', 3),
    ('Assassino em Série', 'Lâmina Marcada por Assassinato', 'item_npc', 2),
    ('Assassino em Série', 'Diário de Assassinato Incompleto', 'item_npc', 1),

    ('Diabo', 'Pena Negra Queimada', 'parte_monstro', 3),
    ('Diabo', 'Diário de Assassinato Incompleto', 'item_npc', 2),
    ('Diabo', 'Sangue Negro', 'fluido_biologico', 2),
    ('Diabo', 'Chifre de Demônio Urbano', 'parte_monstro', 1),

    ('Apóstolo do Desejo', 'Sangue Negro', 'fluido_biologico', 3),
    ('Apóstolo do Desejo', 'Chifre de Demônio Urbano', 'parte_monstro', 2),
    ('Apóstolo do Desejo', 'Coração Pulsante de Desejo', 'parte_monstro', 1),
    ('Apóstolo do Desejo', 'Fragmento de Tentação Carmesim', 'minerio', 1),

    ('Caçador', 'Garra de Cão Mutado', 'parte_monstro', 3),
    ('Caçador', 'Ponta de Flecha Enferrujada', 'item_npc', 2),
    ('Caçador', 'Olho de Coruja Noturna', 'parte_monstro', 1),
    ('Caçador', 'Sangue de Presa', 'fluido_biologico', 1),

    ('Provocador', 'Garra de Cão Mutado', 'parte_monstro', 4),
    ('Provocador', 'Ponta de Flecha Enferrujada', 'item_npc', 3),
    ('Provocador', 'Sangue de Presa', 'fluido_biologico', 2),
    ('Provocador', 'Língua de Bandido Insultador', 'parte_monstro', 1),

    ('Piromaníaco', 'Ponta de Flecha Enferrujada', 'item_npc', 4),
    ('Piromaníaco', 'Língua de Bandido Insultador', 'parte_monstro', 3),
    ('Piromaníaco', 'Óleo Carmesim', 'fluido_biologico', 2),
    ('Piromaníaco', 'Glândula de Fogo Interno', 'parte_monstro', 1),

    ('Conspirador', 'Língua de Bandido Insultador', 'parte_monstro', 3),
    ('Conspirador', 'Glândula de Fogo Interno', 'parte_monstro', 2),
    ('Conspirador', 'Olho de Espião Morto', 'parte_monstro', 2),
    ('Conspirador', 'Mapa Vivo da Cidade', 'item_npc', 1),

    ('Ceifador', 'Glândula de Fogo Interno', 'parte_monstro', 3),
    ('Ceifador', 'Mapa Vivo da Cidade', 'item_npc', 2),
    ('Ceifador', 'Foice Ritualística Banhada em Sangue', 'item_npc', 1),
    ('Ceifador', 'Fragmento da Sombra da Morte', 'minerio', 1),

    ('Bardo', 'Corda de Violino Rompida', 'item_npc', 3),
    ('Bardo', 'Página de Canção Antiga', 'item_npc', 2),
    ('Bardo', 'Gota de Mel Dourado', 'fluido_biologico', 1),
    ('Bardo', 'Pedaço de Sino Quebrado', 'item_npc', 1),

    ('Suplicante da Luz', 'Corda de Violino Rompida', 'item_npc', 4),
    ('Suplicante da Luz', 'Página de Canção Antiga', 'item_npc', 3),
    ('Suplicante da Luz', 'Pedaço de Sino Quebrado', 'item_npc', 2),
    ('Suplicante da Luz', 'Vela de Cera Sagrada', 'item_npc', 1),

    ('Sumo Sacerdote do Sol', 'Página de Canção Antiga', 'item_npc', 4),
    ('Sumo Sacerdote do Sol', 'Vela de Cera Sagrada', 'item_npc', 3),
    ('Sumo Sacerdote do Sol', 'Fragmento de Vitral Iluminado', 'minerio', 2),
    ('Sumo Sacerdote do Sol', 'Núcleo de Luz Engarrafada', 'minerio', 1),

    ('Notário', 'Vela de Cera Sagrada', 'item_npc', 3),
    ('Notário', 'Núcleo de Luz Engarrafada', 'minerio', 2),
    ('Notário', 'Selo de Cera Mística', 'item_npc', 2),
    ('Notário', 'Contrato Assinado com Sangue', 'item_npc', 1),

    ('Sacerdote da Luz', 'Núcleo de Luz Engarrafada', 'minerio', 3),
    ('Sacerdote da Luz', 'Contrato Assinado com Sangue', 'item_npc', 2),
    ('Sacerdote da Luz', 'Coração de Santo Falso', 'parte_monstro', 1),
    ('Sacerdote da Luz', 'Fragmento da Primeira Chama Solar', 'minerio', 1)
) AS dados(nome_pocao, nome_ingrediente, tipo_ingrediente, quantidade)
ON dados.nome_pocao = fp.nome_pocao
ON CONFLICT (id_formula, nome_ingrediente) DO UPDATE SET
    tipo_ingrediente = EXCLUDED.tipo_ingrediente,
    quantidade = EXCLUDED.quantidade;


-- ============================================================
--  FIM DO SCRIPT
-- ============================================================


-- ============================================================
-- MIGRAÇÃO: CAMINHO ATUAL DO JOGADOR
-- Necessário para controlar a ordem de beber poções.
-- ============================================================

ALTER TABLE jogador
ADD COLUMN IF NOT EXISTS caminho_atual VARCHAR(50);
