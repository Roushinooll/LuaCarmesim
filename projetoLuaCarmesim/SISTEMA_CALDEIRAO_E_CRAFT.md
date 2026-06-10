# Sistema de Caldeirão, Nome do Jogador e Craft de Poções

## Fluxo atual do jogo

- Ao clicar em `Jogar`, o jogo pergunta o nome do jogador antes de carregar a Restroom.
- Esse nome é usado para buscar ou criar o jogador no PostgreSQL.
- Depois da sincronização, o jogo garante que exista ranking para esse jogador.
- A receita inicial `Vidente` é aprendida automaticamente para esse jogador.
- Só depois disso a Restroom é aberta.

## Como o caldeirão funciona

- Nas Restrooms existe um caldeirão interativo.
- Chegue perto dele e aperte `F`.
- O painel do caldeirão abre dentro do próprio jogo, sem `ChoiceDialog` externo.
- Use a lista para escolher a receita.
- `ENTER` ou o botão `Craftar` tentam criar a poção.
- `ESC` ou o botão `Fechar` fecham o caldeirão.
- Se faltar ingrediente, o painel mostra exatamente o que falta.
- Se tiver tudo, os ingredientes são consumidos e a poção entra no inventário como `TipoItem.POCAO`.

## Correções feitas nesta versão

- O pedido de nome saiu da porta da Restroom e foi para o botão `Jogar`.
- O caldeirão deixou de abrir popup externo e agora usa um painel interno mais estável.
- O hitbox do caldeirão foi separado do desenho visual para evitar falha de interação.
- A tecla `F` não fica presa/reabrindo o caldeirão depois de fechar.
- Corrigido erro de compilação no `StreetsController.java`: havia uma declaração duplicada de `Rectangle receita`.

## Arquivos principais alterados

- `MenuController.java`
  - Agora pergunta o nome antes de iniciar o jogo.
  - Busca/cria o jogador no banco.
  - Bloqueia o início do jogo se o banco falhar.

- `SessaoJogador.java`
  - Nova classe utilitária para sincronizar jogador, ranking e receita inicial.

- `RestroomController.java`
  - Caldeirão refeito com painel interno.
  - Craft por `ENTER`/botão.
  - Fechamento por `ESC`/botão.

- `restroom.fxml`
  - Caldeirão redesenhado.
  - Hitbox invisível maior.
  - Painel de craft adicionado.

- `StreetsController.java`
  - Corrigida linha duplicada que quebrava a compilação.

## Itens/ingredientes das poções

Os ingredientes completos estão no arquivo:

`src/main/resources/database/receitas_pocoes_completo.sql`

Cada poção possui ingredientes cadastrados na tabela `ingrediente_formula`.
O craft usa esses mesmos ingredientes, então não precisa duplicar receita no código Java.

## Observação importante sobre o banco

Se o banco já existe, rode esta migração no pgAdmin:

```sql
DO $$
BEGIN
    ALTER TYPE tipo_item_enum ADD VALUE IF NOT EXISTS 'ingrediente';
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;
```

Ou execute diretamente o arquivo:

`src/main/resources/database/migracao_caldeirao_pocoes.sql`

## Menu da rua por TAB

- Durante a rua, aperte `TAB` para abrir um menu por cima da tela.
- Enquanto o menu estiver aberto, a movimentação e colisões ficam pausadas.
- `TAB` ou `ESC` fecham o menu.
- O botão `Voltar para Restroom` leva o jogador de volta para a Restroom sem acionar morte.
- Ao voltar para a Restroom pelo menu, o estado da rua é limpo.
- Se o jogador sair pela porta da Restroom de novo, a rua começa novamente na sala 1.


## Fix de ruas e coleta

- Ao voltar da rua pelo menu do TAB, o estado da rua é limpo.
- Ao sair novamente pela porta da Restroom, a rua sempre começa na sala 1.
- A coleta de itens não consulta mais o banco a cada item coletado.
- Os ingredientes possíveis da rua são preparados uma vez quando a rua é criada/restaurada, reduzindo travadas na hora da coleta.


## Fix de desempenho ao entrar na rua

- A rua agora é montada primeiro com cenário, inimigos e itens básicos.
- Consultas pesadas ao banco saíram da thread principal do JavaFX.
- Geração de receita da rua roda em segundo plano.
- Preparação dos ingredientes coletáveis roda em segundo plano.
- Atualização do ranking da run roda em segundo plano.
- Coletar receita também sincroniza em segundo plano, evitando travada ao encostar nela.
- Foi adicionada uma versão interna da rua (`versaoRua`) para impedir que uma resposta atrasada do banco altere uma sala antiga depois que o jogador já saiu ou mudou de tela.
