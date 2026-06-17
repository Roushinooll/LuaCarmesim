# Sistema de tomar poções

Alterações incluídas:

- O jogador pode beber poções pelo inventário.
- No inventário: selecione uma poção na lista de itens e pressione ENTER.
- A primeira poção precisa ser de Sequência 9.
- A primeira poção define o caminho do jogador.
- Depois disso, o jogador só pode beber poções do mesmo caminho.
- A ordem é obrigatória: 9 -> 8 -> 7 -> 6 -> 5.
- Ao beber, uma unidade da poção é removida do inventário.
- `sequencia_atual` e `caminho_atual` são salvos no banco.

## Migração obrigatória

Rode uma vez no banco existente:

```sql
ALTER TABLE jogador
ADD COLUMN IF NOT EXISTS caminho_atual VARCHAR(50);
```

O mesmo comando está no arquivo:

`src/main/resources/database/migracao_progressao_pocoes.sql`

