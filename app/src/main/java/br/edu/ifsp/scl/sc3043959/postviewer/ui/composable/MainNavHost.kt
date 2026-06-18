package br.edu.ifsp.scl.sc3043959.postviewer.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import br.edu.ifsp.scl.sc3043959.postviewer.PostViewModel
import br.edu.ifsp.scl.sc3043959.postviewer.ui.navigation.Screen

@Composable
fun MainNavHost(
    mainNavHostController: NavHostController,
    postViewModel: PostViewModel,
    modifier: Modifier = Modifier
) {
    // NavHost: o ponto central da navegação: ele associa cada rota a uma tela.
    // A primeira tela aberta pelo app é definida em startDestination.
    NavHost(
        navController = mainNavHostController,
        startDestination = Screen.PostList.route,
        modifier = modifier
    ) {
        // Rota da lista. A tela recebe uma função de click e não conhece os detalhes de navegação.
        composable(route = Screen.PostList.route) {
            PostListScreen(
                postViewModel = postViewModel,
                onPostClick = { postId ->
                    // createRoute monta a string com o id real do post.
                    mainNavHostController.navigate(Screen.PostDetails.createRoute(postId))
                }
            )
        }

        // Rota de detalhes.
        composable(
            route = Screen.PostDetails.route,
            arguments = listOf(
                navArgument(Screen.PostDetails.POST_ID_ARGUMENT) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            // O Navigation guarda os argumentos da rota no backStackEntry. Se algo inesperado acontecer, usa 0 como fallback para evitar crash.
            val postId = backStackEntry.arguments?.getInt(Screen.PostDetails.POST_ID_ARGUMENT) ?: 0

            PostDetailsNavigationPlaceholder(
                postId = postId,
                postViewModel = postViewModel
            )
        }
    }
}

@Composable
private fun PostDetailsNavigationPlaceholder(
    postId: Int,
    postViewModel: PostViewModel
) {
    // LaunchedEffect executa a carga de detalhes quando a tela entra em composicao
    // ou quando o postId muda. Isso evita chamar loadPostDetails a cada recomposicao.
    LaunchedEffect(postId) {
        postViewModel.loadPostDetails(postId)
    }

    // A tela observa o estado de detalhes para refletir carregamento, comentarios e erro.
    val postDetailsUiState by postViewModel.postDetailsUiState.collectAsState()

    // Tela provisoria: garante que o argumento postId chega corretamente na tela de detalhes.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Detalhes do post $postId",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(text = "Comentarios da API: ${postDetailsUiState.apiComments.size}")
        Text(text = "Comentarios locais: ${postDetailsUiState.localComments.size}")

        postDetailsUiState.errorMessage?.let { errorMessage ->
            // Mensagem amigavel para falhas de rede ou banco durante a carga dos detalhes.
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}
