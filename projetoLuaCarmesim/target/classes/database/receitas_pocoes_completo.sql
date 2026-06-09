
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

