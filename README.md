# 🏦 BankCore - Sistema Bancário Orientado a Objetos

[![Java](https://img.shields.io/badge/Java-17+-blue.svg)](https://adoptium.net/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

BankCore é um sistema bancário educacional desenvolvido em Java, demonstrando conceitos avançados de programação orientada a objetos, estruturas de dados personalizadas, serialização, tratamento de exceções, logging e muito mais.

---

## ✨ Funcionalidades

- ✅ Cadastro e autenticação de clientes (CPF com validação de dígitos)
- ✅ Criação de contas **corrente** e **poupança**
- ✅ Depósitos, saques e transferências com rollback atômico
- ✅ Extrato detalhado com histórico de transações
- ✅ Projeção de juros para contas poupança
- ✅ Persistência completa via serialização (salva/restaura estado)
- ✅ Logs de auditoria em arquivo (`logs/audit.log`)
- ✅ Senhas armazenadas com hash SHA‑256 + salt
- ✅ Formatação monetária no padrão brasileiro (R\$ 1.500,50)

---

## 🧱 Arquitetura e Conceitos Abordados


| Camada          | Pacote                      | Conteúdo                                                                 |
|-----------------|-----------------------------|--------------------------------------------------------------------------|
| Apresentação    | `app`                       | `BankApplicationOO` (menu interativo) e `Main`                          |
| Serviços        | `service`                   | Regras de negócio (contas, transferência, juros, autenticação, extrato) |
| Domínio         | `domain.account`, `domain.customer`, `domain.transaction`, `domain.enums` | Entidades ricas com validações e herança |
| Repositórios    | `repository`                | `AccountRepository`, `CustomerRepository` (usam `BinarySearchTree`)      |
| Infraestrutura  | `infrastructure.logging`, `infrastructure.security`, `infrastructure.persistence` | Log, hash de senha, serialização |
| Coleções próprias | `collections`             | `BinarySearchTree`, `GenericLinkedList`, `GenericQueue`, `GenericStack` |
| Utilitários     | `util`                      | `CurrencyFormatter`, `GenericUtils`                                     |

> 🧠 **Diferencial didático**: o projeto **não** usa `HashMap` nos repositórios – implementa sua própria `BinarySearchTree` para busca O(log n) e utiliza `GenericUtils` para programação funcional.

---

## 🚀 Como executar

### Pré‑requisitos
- Java 17 ou superior
- Terminal (PowerShell, bash, cmd)

### Passos

1. **Clone o repositório**
   ```bash
   git clone https://github.com/ErickRocha7/bankcore.git
   cd bankcore
   ```

2. **Compile os fontes**
   ```bash
   javac -d out -cp lib/junit-platform-console-standalone-1.11.4.jar src/**/*.java
   ```
   *(O parâmetro `-cp` só é necessário se for executar os testes)*

3. **Execute a aplicação**
   ```bash
   java -cp out app.Main
   ```

Interaja pelo menu – as opções são autoexplicativas.

💾 Os dados são salvos automaticamente em `data/bankdata.ser` e recarregados na próxima execução.

---

## 🧪 Executando os testes unitários

Os testes utilizam JUnit 5 (arquivo JAR incluído em `lib/`). Para rodar todos os testes:

```bash
java -jar lib/junit-platform-console-standalone-1.11.4.jar --class-path out --scan-class-path
```

---

## 📁 Estrutura de diretórios

```text
bankcore/
├── data/                     # Arquivos serializados do banco
├── logs/                     # Arquivos de log (audit.log)
├── lib/                      # JARs de bibliotecas (JUnit)
├── out/                      # Bytecode compilado
├── src/                      # Código-fonte
│   ├── app/                  # Aplicação principal
│   ├── collections/          # Estruturas de dados personalizadas
│   ├── domain/               # Entidades de domínio
│   ├── exceptions/           # Exceções customizadas
│   ├── infrastructure/       # Log, segurança, persistência
│   ├── repository/           # Repositórios com BinarySearchTree
│   ├── service/              # Camada de serviços
│   └── util/                 # Classes utilitárias
├── test/                     # Testes unitários
├── .gitignore                # Arquivos ignorados pelo Git
└── README.md                 # Este arquivo
```

---

## 🧠 Conceitos técnicos destacados

- **Orientação a objetos** – herança, polimorfismo, encapsulamento, composição.
- **Generics** – nas coleções `GenericLinkedList`, `BinarySearchTree`, `GenericUtils`.
- **Recursão** – inserção, busca e travessia na `BinarySearchTree`.
- **Tratamento de exceções** – exceções verificadas e não verificadas, rollback em transferência.
- **Serialização** – persistência com `ObjectOutputStream` / `ObjectInputStream`.
- **Programação funcional** – `GenericUtils.filter`, `GenericUtils.Predicate`.
- **Boas práticas** – validação de entradas, clonagem defensiva, injeção de dependência manual.

---

## 🤝 Contribuição

Contribuições são bem‑vindas! Sinta‑se à vontade para abrir issues e pull requests.

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas alterações (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

---

## 📜 Licença

Este projeto está licenciado sob a licença MIT – veja o arquivo [LICENSE](LICENSE) para detalhes.

---

## 👨‍💻 Autor

**Erick Rocha** – Projeto desenvolvido para fins educacionais, demonstrando domínio de Java avançado.

⭐ Se este projeto te ajudou ou serviu de estudo, considere dar uma estrela no GitHub!
