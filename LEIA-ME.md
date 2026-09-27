# Adotar Pet Campinas — base Android (Kotlin + Jetpack Compose)

Projeto base gerado a partir do protótipo Figma, pronto para abrir no Android Studio.

## Como abrir
1. Android Studio → **Open** → selecione a pasta `AdotarPet` (a que contém `settings.gradle.kts`).
2. Aguarde o **Gradle Sync** (primeira vez baixa o Gradle 8.7 e as dependências — precisa de internet).
3. Rode no emulador ou celular físico (botão ▶️).

## O que já está implementado
- Navegação completa com `navigation-compose`: Início → Adotar → Perfil do animal → Formulário → Confirmação, mais Perfil do usuário e Resgate.
- Bottom bar fixa (Início / Adotar / Perfil / Resgate), igual ao protótipo.
- Tema com a paleta de cores do Figma (bege, terracota, verde dos badges) em `ui/theme/`.
- Dados **mockados** em memória (`data/PetRepository.kt`) com 4 pets de exemplo e favoritos reativos.
- Todas as 7 telas do fluxo, com componentes reutilizáveis (`PetCard`, `FilterChip`, `InfoTag`, `BottomNavBar`).

## O que é só esqueleto / você vai querer trocar
- **Fotos dos animais**: hoje é um placeholder (ícone de pata sobre fundo colorido). Para usar fotos reais, adicione a dependência já incluída **Coil** (`io.coil-kt:coil-compose`) e troque `PetPhotoPlaceholder` em `PetCard.kt` / `PetDetailScreen.kt` por `AsyncImage(model = url, ...)`, ou coloque as imagens em `res/drawable` e use `Image(painterResource(...))`.
- **Backend**: tudo é mock local. Quando tiver uma API, dá pra manter a mesma interface do `PetRepository` (ele já expõe os dados como `StateFlow`) e só trocar a implementação interna por chamadas HTTP (ex.: Retrofit).
- **Login/Perfil real**: a tela de Perfil usa um usuário fixo ("Camila Ribeiro"). Falta autenticação.
- **Tela "Conhecer"**: veio em branco no export do Figma, então não foi implementada — avise se quiser que eu monte um layout pra ela.
- **Envio do formulário/resgate**: os botões "Enviar solicitação" e "Enviar resgate" hoje só navegam/ficam disponíveis; não persistem nada ainda.
- **Fonte serifada**: usei `FontFamily.Serif` como aproximação da fonte do Figma nos títulos. Se quiser bater 100%, baixe a fonte (ex. no Google Fonts) e coloque em `res/font/`.

## Estrutura
```
app/src/main/java/com/adotarpet/campinas/
├── MainActivity.kt        # NavHost + Scaffold com bottom bar
├── model/Pet.kt            # modelo de dados
├── data/PetRepository.kt   # dados mock (StateFlow)
├── navigation/Screen.kt    # rotas e itens da bottom bar
├── ui/theme/                # cores, tipografia, Material3 theme
├── ui/components/          # PetCard, FilterChip, InfoTag, BottomNavBar
└── ui/screens/              # as 7 telas do fluxo
```
