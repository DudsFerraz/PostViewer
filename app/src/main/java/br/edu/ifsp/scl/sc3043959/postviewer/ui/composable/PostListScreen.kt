package br.edu.ifsp.scl.sc3043959.postviewer.ui.composable

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.ifsp.scl.sc3043959.postviewer.PostViewModel
import br.edu.ifsp.scl.sc3043959.postviewer.model.entity.ApiPost

@Composable
fun PostListScreen(
    postViewModel: PostViewModel,
    modifier: Modifier = Modifier,
    onPostClick: (Int) -> Unit
) {
    // collectAsState observa o StateFlow do ViewModel. Sempre que postsUiState muda, o Compose redesenha esta tela.
    val postsUiState by postViewModel.postsUiState.collectAsState()

    when {
        postsUiState.isLoading -> {
            LoadingContent(modifier = modifier)
        }

        postsUiState.errorMessage != null -> {
            ErrorContent(
                errorMessage = postsUiState.errorMessage,
                onRetryClick = { postViewModel.loadPosts() },
                modifier = modifier
            )
        }

        else -> {
            PostListContent(
                posts = postsUiState.posts,
                onPostClick = onPostClick,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    // Box permite centralizar o indicador de carregamento na tela inteira.
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(
    errorMessage: String?,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado de erro separado facilita mostrar uma mensagem amigavel e permitir nova tentativa.
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
private fun PostListContent(
    posts: List<ApiPost>,
    onPostClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // LazyColumn renderiza apenas os itens visíveis, evita problemas com listas grandes.
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = posts,
            key = { post -> post.id }
        ) { post ->
            PostListItem(
                post = post,
                onPostClick = onPostClick
            )
        }
    }
}

@Composable
private fun PostListItem(
    post: ApiPost,
    onPostClick: (Int) -> Unit
) {
    // Card deixa cada post visualmente separado e a area inteira clicavel.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPostClick(post.id) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Post #${post.id}",
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
