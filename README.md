# PostViewer

Aplicativo Android desenvolvido para a disciplina de Programação para Dispositivos Moveis.

## Descrição

O PostViewer consome a API publica JSONPlaceholder para exibir posts e os seus comentários. O aplicativo também permite adicionar comentários locais em cada post, que são persistidos no dispositivo com Room e continuam disponíveis mesmo ao reabrir o app.

Funcionalidades implementadas:

- Listagem de posts vindos de `https://jsonplaceholder.typicode.com/posts`.
- Navegação da lista para a tela de detalhes do post.
- Listagem de comentários da API em `https://jsonplaceholder.typicode.com/posts/{id}/comments`.
- Campo para adicionar comentário local associado ao `id` do post.
- Persistência dos comentários locais com Room.
- Tratamento de carregamento e erro nas telas.

## Como Executar

Requisitos:

- Android Studio.
- JDK compatível com o projeto Android.
- conexao com a ‘internet’ para consumir a API JSONPlaceholder.

Passos:

1. Clone ou abra este repositorio no Android Studio.
2. Aguarde a sincronização do Gradle.
3. Execute o ‘app’ num emulador ou dispositivo Android.

Também e possível compilar pelo terminal:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Tecnologias Utilizadas

- Kotlin.
- Jetpack Compose.
- Navigation Compose.
- ViewModel.
- StateFlow.
- Retrofit.
- Gson Converter.
- Room.
- KSP.
- Material 3.

## Arquitetura

O projeto foi organizado separando responsabilidades:

- `model/entity`: modelos da API e entidade local do Room.
- `model/network`: configuração do Retrofit e endpoints da API.
- `model/dao`: operacoes SQL usadas pelo Room.
- `model/database`: configuração do banco Room.
- `model/repository`: ponto central de acesso à API e banco local.
- `ui/navigation`: rotas usadas pelo Navigation Compose.
- `ui/composable`: telas e componentes visuais em Compose.
- `PostViewModel`: gerencia mento de estado com StateFlow.

Fluxo principal:

```text
Tela Compose -> ViewModel -> Repository -> Retrofit/Room
```

As telas observam `StateFlow`. Quando o ViewModel atualiza o estado, o Compose redesenha automaticamente a interface.

## Decisões De ‘Design’

- `ApiPost` e `ApiComment` representam dados vindos da API.
- `LocalComment` representa comentários criados pelo usuário e salvos no Room.
- O Repository isola a origem dos dados, evitando que as telas acessem Retrofit ou Room diretamente.
- `StateFlow` foi usado para expor estados de loading, sucesso e erro.
- `Room` foi usado para persistir somente os comentários locais, pois os posts e comentários originais já são fornecidos pela API.
- `suspend fun` foi usada em Retrofit e Room para executar operacoes demoradas sem bloquear a ‘interface’.
- Comentários explicativos foram adicionados em trechos centrais do código para facilitar manutenção e a etapa presencial da avaliação.

## Prints

Prints do aplicativo em execucao essão armazenados na pasta `docs/`.

Links diretos:

- [Tela de lista de posts com dados carregados](docs/PostsList.png).
- [Tela de detalhes com comentarios da API e comentario local](docs/PostDetails.png).
