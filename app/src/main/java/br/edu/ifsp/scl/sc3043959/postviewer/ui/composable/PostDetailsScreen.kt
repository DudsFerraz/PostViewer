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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.ifsp.scl.sc3043959.postviewer.PostViewModel
import br.edu.ifsp.scl.sc3043959.postviewer.model.entity.ApiComment

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

    when {
        postDetailsUiState.isLoading -> {
            DetailsLoadingContent(modifier = modifier)
        }

        postDetailsUiState.errorMessage != null -> {
            DetailsErrorContent(
                errorMessage = postDetailsUiState.errorMessage,
                onRetryClick = { postViewModel.loadPostDetails(postId) },
                modifier = modifier
            )
        }

        else -> {
            ApiCommentsContent(
                postId = postId,
                apiComments = postDetailsUiState.apiComments,
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
private fun ApiCommentsContent(
    postId: Int,
    apiComments: List<ApiComment>,
    modifier: Modifier = Modifier
) {
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

        items(
            items = apiComments,
            key = { comment -> comment.id }
        ) { comment ->
            ApiCommentItem(apiComment = comment)
        }
    }
}

@Composable
private fun ApiCommentItem(apiComment: ApiComment) {
    // Card separa visualmente cada comentario.
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
