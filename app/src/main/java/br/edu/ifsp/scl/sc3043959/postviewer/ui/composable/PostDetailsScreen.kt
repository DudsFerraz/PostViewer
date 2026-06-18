package br.edu.ifsp.scl.sc3043959.postviewer.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.ifsp.scl.sc3043959.postviewer.PostViewModel
import br.edu.ifsp.scl.sc3043959.postviewer.model.entity.ApiComment
import br.edu.ifsp.scl.sc3043959.postviewer.model.entity.LocalComment

@Composable
fun PostDetailsScreen(
    postId: Int,
    postViewModel: PostViewModel,
    modifier: Modifier = Modifier
) {
    // Carrega os comentários quando a tela abre ou quando outro postId for recebido.
    // LaunchedEffect evita disparar uma nova chamada a cada recomposição.
    LaunchedEffect(postId) {
        postViewModel.loadPostDetails(postId)
    }

    // Observa o StateFlow de detalhes. Mudancas em loading, erro ou comentarios
    // atualiza automaticamente o conteudo exibido pelo Compose.
    val postDetailsUiState by postViewModel.postDetailsUiState.collectAsState()

    val hasComments =
        postDetailsUiState.apiComments.isNotEmpty() || postDetailsUiState.localComments.isNotEmpty()

    when {
        postDetailsUiState.isLoading -> {
            DetailsLoadingContent(modifier = modifier)
        }

        postDetailsUiState.errorMessage != null && !hasComments -> {
            DetailsErrorContent(
                errorMessage = postDetailsUiState.errorMessage,
                onRetryClick = { postViewModel.loadPostDetails(postId) },
                modifier = modifier
            )
        }

        else -> {
            CommentsContent(
                postId = postId,
                apiComments = postDetailsUiState.apiComments,
                localComments = postDetailsUiState.localComments,
                errorMessage = postDetailsUiState.errorMessage,
                onAddLocalComment = { body ->
                    postViewModel.addLocalComment(postId, body)
                },
                modifier = modifier
            )
        }
    }
}

@Composable
private fun DetailsLoadingContent(modifier: Modifier = Modifier) {
    // Mantem o feedback visual enquanto a chamada de rede esta em andamento.
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DetailsErrorContent(
    errorMessage: String?,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado de erro isolado permite tentar carregar novamente sem sair da tela.
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = errorMessage ?: "Erro inesperado.",
            color = MaterialTheme.colorScheme.error
        )

        Button(
            onClick = onRetryClick,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Tentar novamente")
        }
    }
}

@Composable
private fun CommentsContent(
    postId: Int,
    apiComments: List<ApiComment>,
    localComments: List<LocalComment>,
    errorMessage: String?,
    onAddLocalComment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // rememberSaveable preserva o texto em recriações simples da tela (ex: rotação). A chave postId reinicia o campo ao trocar de post.
    var newCommentBody by rememberSaveable(postId) { mutableStateOf("") }

    // LazyColumn permite misturar um cabecalho com a lista de comentarios.
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Comentarios do post #$postId",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            LocalCommentForm(
                newCommentBody = newCommentBody,
                onNewCommentBodyChange = { newCommentBody = it },
                onAddClick = {
                    if (newCommentBody.isNotBlank()) {
                        onAddLocalComment(newCommentBody)
                        newCommentBody = ""
                    }
                }
            )
        }

        errorMessage?.let { message ->
            item {
                // Se o erro aconteceu depois dos comentarios carregarem, exibe um aviso.
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        if (localComments.isNotEmpty()) {
            item {
                Text(
                    text = "Comentários locais",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(
                items = localComments,
                key = { comment -> "local-${comment.id}" }
            ) { comment ->
                LocalCommentItem(localComment = comment)
            }
        }

        item {
            Text(
                text = "Comentários da API",
                style = MaterialTheme.typography.titleMedium
            )
        }

        items(
            items = apiComments,
            key = { comment -> "api-${comment.id}" }
        ) { comment ->
            ApiCommentItem(apiComment = comment)
        }
    }
}

@Composable
private fun LocalCommentForm(
    newCommentBody: String,
    onNewCommentBodyChange: (String) -> Unit,
    onAddClick: () -> Unit
) {
    // O formulario fica junto da lista para o comentario local aparecer no mesmo contexto.
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Adicionar comentario local",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = newCommentBody,
                onValueChange = onNewCommentBodyChange,
                label = { Text(text = "Comentario") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onAddClick,
                enabled = newCommentBody.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Adicionar")
            }
        }
    }
}

@Composable
private fun LocalCommentItem(localComment: LocalComment) {
    // O comentario local tem layout proprio.
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Comentario local",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = localComment.body,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun ApiCommentItem(apiComment: ApiComment) {
    // Card separa visualmente cada comentario retornado pela API.
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = apiComment.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = apiComment.email,
                modifier = Modifier.padding(top = 4.dp),
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = apiComment.body,
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
