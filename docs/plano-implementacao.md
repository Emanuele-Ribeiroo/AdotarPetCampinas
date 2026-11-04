# Plano de Implementação — Adotar Pet

> Base: *Projeto de Arquitetura - Adotar Pet* (v1.0) + *Documento de Requisitos* (Set/2026) + estado atual do código.
> Entrega prevista: **Novembro de 2026**. Equipe: Emanuele, Larissa, Maria Luisa, Samyra.
>
> ⚠️ Este plano será revisado quando o documento de requisitos atualizado chegar.

---

## 1. Visão do que muda

O app hoje é **100% local**: Room no aparelho, login **mockado** (contas fixas no código), sem internet. A arquitetura aprovada exige um sistema **cliente-servidor em 3 camadas**:

```
App (Kotlin/Compose, MVVM)  ──HTTPS/JSON──►  API REST (Node.js, MVC)  ──►  Google Sheets (dados)
         │                                            │
    Room = cache + fila offline                   Google Drive (imagens, salva só o link)
```

Ou seja, além de **completar funcionalidades que faltam**, há um **trabalho novo grande**: construir a API, migrar o armazenamento para Google Sheets/Drive, e trocar o acesso local do app por chamadas de rede com autenticação real.

### Legenda de status
- ✅ **Pronto** — já existe e atende
- 🟡 **Parcial** — existe, mas precisa de ajuste/complemento
- 🔴 **A fazer** — não existe

---

## 2. Rastreabilidade: requisito → estado atual → o que falta

| Req | Descrição | Hoje | O que falta |
|-----|-----------|------|-------------|
| **RF01** | Cadastro, login, logout, recuperação de senha | 🟡 login/logout mockados | Tela de **cadastro**, **recuperação de senha**, auth real via API (JWT) |
| **RF02** | Perfis de acesso (comum × admin) | 🟡 papel mockado no aparelho | Perfil validado pelo **token no servidor**; rotas de gestão só p/ admin |
| **RF03** | Listagem de animais (foto, nome, espécie, idade) | ✅ UI pronta | Trocar fonte local por **GET /animais?pagina=** |
| **RF04** | Cadastro de animais (admin) | ✅ UI pronta | Enviar via **POST /animais** + upload de imagens ao Drive |
| **RF05** | Edição e exclusão (admin) | ✅ UI pronta | **PUT/DELETE /animais/:id** com checagem de admin |
| **RF06** | Formulário de adoção completo | 🟡 faltam campos | Add: **tipo de moradia, outros animais, experiência, rotina, motivo**; enviar via API |
| **RF07** | Registro de resgate (descrição, fotos, GPS/manual, espécie, contato) | 🟡 só endereço manual | **GPS (geolocator)**, descrição em texto, envio via API |
| **RNF01** | Confiabilidade (confirmação do servidor + fila offline + retry) | 🔴 não há servidor | API + **fila offline (Room + WorkManager)** + estado "enviado" só após confirmação |
| **RNF02** | Eficiência (compressão/redimensionamento + paginação 10) | 🔴 | **Compressão/thumbnail** de imagens + **paginação** de 10 |
| **RNF03** | Portabilidade (Android 8.0+, telas 4,7"–6,9", tablet) | 🟡 `minSdk=24` | Ajustar `minSdk=26`; **testar tablets** e telas grandes |
| **RNF04** | Privacidade/LGPD (dados só p/ admin + aceite da política) | 🟡 dados locais, sem aceite | **Aceite da política no cadastro**; dados pessoais só visíveis a admin |
| **RNF05** | Imagens JPG/PNG, até 10 MB, máx. 10/animal e 5/resgate | 🔴 sem validação | Validar **tipo, tamanho e quantidade** (app + API) |
| **RNF06** | Localização sob demanda + fallback manual + nunca exibida a outros | 🟡 manual só | Permissão **GPS no momento do uso**; nunca expor a outros usuários |
| **Favoritos** | Marcar/consultar favoritos (usuário logado) | ✅ UI/local | Sincronizar com **aba Favoritos** via API |

---

## 3. Fases de implementação

As fases estão em ordem de dependência. Back-end e as lacunas de UI que **não** dependem de rede podem andar em paralelo.

### Fase 0 — Preparação e infraestrutura
Objetivo: destravar o resto do time.
- [ ] **Contrato da API**: documentar endpoints, formato JSON de entrada/saída, códigos de status e erros. (Fonte da verdade compartilhada entre app e back-end.)
- [ ] **Google Cloud / Workspace da ONG**:
  - [ ] Criar a planilha com as abas: `Usuarios`, `Animais`, `Adocoes`, `Resgates`, `Favoritos` (definir colunas de cada uma).
  - [ ] Criar a pasta no **Google Drive** para as imagens.
  - [ ] Criar **conta de serviço** + credenciais (JSON) e compartilhar planilha/pasta com ela.
- [ ] Criar o repositório/estrutura do back-end (pode ser pasta `/server` neste repo ou repo separado — **decidir**).
- [ ] Definir gestão de segredos: `.env` / variáveis de ambiente (chaves, credenciais, segredo do JWT) fora do código.

### Fase 1 — Back-end: API REST (Node.js, MVC)
- [ ] Estrutura MVC: `routes/`, `controllers/`, `services`/`models/`, `middlewares/`, `config/`.
- [ ] **Camada de dados Google Sheets**: funções de leitura/escrita por aba (CRUD), usando a API do Google Sheets.
- [ ] **Upload de imagens (Drive)**: `POST /imagens` → salva no Drive, devolve o link; valida **JPG/PNG, ≤10 MB** e limites de quantidade (10/animal, 5/resgate). (RNF05)
- [ ] **Autenticação** (RF01):
  - [ ] `POST /auth/registro` — cria usuário, senha com **bcrypt**.
  - [ ] `POST /auth/login` — devolve **JWT**.
  - [ ] Fluxo de **recuperação de senha**.
  - [ ] **Bloqueio por 5 tentativas erradas / 15 min**.
- [ ] **Middleware** de verificação de token + perfil (bloqueia não-admin nas rotas de gestão). (RF02)
- [ ] **Endpoints de negócio**:
  - [ ] `GET /animais?pagina=` — **10 por página**, com link da miniatura. (RF03, RNF02)
  - [ ] `GET /animais/:id`
  - [ ] `POST` / `PUT` / `DELETE /animais[/:id]` — **somente admin**. (RF04, RF05)
  - [ ] `POST /adocoes` (grava "em análise", devolve protocolo) e `GET /adocoes` (admin). (RF06)
  - [ ] `POST /resgates`. (RF07)
  - [ ] `GET` / `POST` / `DELETE /favoritos`.
- [ ] Boas práticas: validação server-side, HTTPS, consultas parametrizadas, dados pessoais só p/ admin. (Segurança / RNF04)
- [ ] **Testes** unitários e de integração da API (regras de permissão, adoção, resgate).

### Fase 2 — App: camada de rede e segurança
- [ ] Adicionar permissão **`INTERNET`** no `AndroidManifest.xml`.
- [ ] Adicionar **Retrofit + OkHttp** (ou Ktor) + serialização (kotlinx.serialization/Moshi).
- [ ] Cliente HTTP com **interceptor que injeta o token**; tratamento central de erros.
- [ ] **Armazenamento seguro do token** (EncryptedSharedPreferences / DataStore). (Segurança)
- [ ] DTOs de rede ↔ modelos de domínio (mapeadores).
- [ ] Estados de UI padronizados: **carregando / erro / vazio / sucesso**.

### Fase 3 — App: consolidar MVVM e migrar repositórios
Hoje só existe `AdotarViewModel`; as telas chamam os repositórios locais direto.
- [ ] Criar ViewModels: `AuthViewModel`, `PetDetailViewModel`, `AdocaoViewModel`, `ResgateViewModel`, `AdminViewModel`, `FavoritosViewModel` (e manter/evoluir `AnimaisViewModel`).
- [ ] Mover **validações** (CPF, telefone, CEP, e-mail) para os ViewModels.
- [ ] Repositórios passam a chamar a **API**; o Room vira **cache + fila offline** (não mais a fonte principal).
- [ ] `AuthRepository` mockado → autenticação real pela API; `SettingsRepository` guarda sessão/token.

### Fase 4 — Confiabilidade / offline (RNF01)
- [ ] Fila de envios pendentes em **Room** (adoções e resgates).
- [ ] **WorkManager** para reenvio automático quando a conexão voltar.
- [ ] Marcar como **"enviado" só após confirmação do servidor** (exibir protocolo).
- [ ] Detecção de conectividade e feedback ao usuário.

### Fase 5 — Funcionalidades novas / lacunas de produto
Boa parte **independe da API** e pode começar já (mock temporário até a API existir).
- [ ] **RF01** — Tela de **cadastro** (nome, e-mail, telefone, senha) com validação.
- [ ] **RNF04** — **Aceite da política de privacidade** no cadastro (checkbox obrigatório + texto).
- [ ] **RF01** — Fluxo de **recuperação de senha** (tela + chamada à API).
- [ ] **RF06** — Campos extras no formulário de adoção: **tipo de moradia, presença de outros animais, experiência com pets, rotina, motivo da adoção**.
- [ ] **RF07 + RNF06** — **GPS** no resgate (permissão sob demanda), **fallback manual**, integração **ViaCEP** para endereço.
- [ ] **RNF02 + RNF05** — **Compressão/redimensionamento** de imagens (miniatura p/ lista, ampliada p/ perfil) e **validação** de tipo/tamanho/quantidade no app.
- [ ] **RNF02** — **Paginação** de 10 na lista (carregar mais ao rolar).
- [ ] **RNF03** — `minSdk = 26` (Android 8.0) e revisão de layout em **tablets** e telas 4,7"–6,9".

### Fase 6 — CI/CD e implantação
- [ ] **GitHub Actions**: rodar testes em cada PR; gerar APK/AAB ao juntar na branch principal.
- [ ] Hospedar a **API Node** em serviço gratuito (ex.: Render/Railway/Fly) + configurar variáveis de ambiente.
- [ ] Config de **release/assinatura** do app (keystore já previsto em `build.gradle.kts`) e preparo para **Play Store**.

### Fase 7 — Testes e validação final
- [ ] **Unitários**: ViewModels, validadores, regras da API.
- [ ] **Integração**: API ↔ Google Sheets, rotas de adoção e resgate.
- [ ] **Ponta a ponta**: fluxos completos de adoção, resgate e cadastro de animal.
- [ ] **Usabilidade**: teste com voluntários da ONG.

---

## 4. Ordem recomendada e dependências

```
Fase 0 (todos) ─► Fase 1 (back-end) ─► Fase 2 ─► Fase 3 ─► Fase 4
                                                     │
Fase 5 (UI/lacunas, em paralelo desde já) ──────────┘
Fase 6 e 7 ao final de cada entrega funcional.
```

- **Caminho crítico**: Fase 0 → 1 → 2 → 3. Sem a API, nada de rede funciona.
- **Pode começar hoje, sem bloquear**: itens da **Fase 5** que são só de UI (cadastro, campos do formulário, GPS, compressão, paginação) — usando dados mock até a API ficar pronta.

---

## 5. Sugestão de divisão (4 pessoas)

| Frente | Responsável sugerido | Entregas principais |
|--------|---------------------|---------------------|
| Back-end / API | 1 pessoa | Fases 0–1 (API, Sheets, Drive, auth) |
| Rede + MVVM no app | 1 pessoa | Fases 2–3 (Retrofit, ViewModels, repositórios) |
| Funcionalidades de produto | 1 pessoa | Fase 5 (cadastro, formulário, GPS, imagens, paginação) |
| Confiabilidade + CI/CD + testes | 1 pessoa | Fases 4, 6, 7 |

*(Ajustar conforme preferência da equipe; pares em partes críticas ajudam.)*

---

## 6. Riscos e pontos de atenção
- **Google Sheets como banco** tem limites de escrita/leitura e concorrência — definir quais colunas e evitar gravações pesadas por chamada.
- **Imagens no Drive**: garantir que só o link é salvo na planilha e que as permissões de compartilhamento estão corretas (sem expor dados).
- **Envio offline instável**: é o desafio nº 1 citado no próprio documento — tratar bem fila + retry + idempotência (evitar pedido duplicado).
- **LGPD**: localização do informante e dados pessoais (CPF/endereço/telefone) nunca podem vazar para usuários comuns.
- **Segredos**: credenciais do Google e segredo do JWT fora do código (variáveis de ambiente).

---

## 7. Decisões em aberto (precisam de definição da equipe)
1. Back-end no **mesmo repositório** (`/server`) ou repositório separado?
2. Biblioteca de rede: **Retrofit** (padrão Android) vs. Ktor Client.
3. Serviço de **hospedagem gratuita** da API.
4. Estrutura exata das **colunas de cada aba** da planilha.
5. Confirmar `minSdk` 26 (spec) — hoje está 24 (cobre mais aparelhos, mas foge da especificação).
