# Sistema de inimigos e perseguição

Implementado nesta versão:

- Inimigos da rua agora guardam um model real (`Bandido` ou `Beyonder`) junto com o retângulo visual.
- Ruas iniciais geram mais bandidos.
- Quanto maior o número da rua, maior a chance de gerar Beyonder.
- A partir da rua 21, todos os inimigos gerados são Beyonders.
- Bandidos aparecem em vermelho.
- Beyonders aparecem em roxo.
- Inimigos ficam parados até o jogador entrar no raio de detecção.
- Ao detectar o jogador, o inimigo começa a perseguir.
- Quando chega perto o suficiente, aparece a mensagem de combate futuro.

Arquivos principais alterados:

- `src/main/java/com/cls/projetoluacarmesim/StreetsController.java`
- `src/main/java/com/cls/projetoluacarmesim/EstadoJogo.java`
- `src/main/java/com/cls/projetoluacarmesim/util/InimigoMapa.java`
