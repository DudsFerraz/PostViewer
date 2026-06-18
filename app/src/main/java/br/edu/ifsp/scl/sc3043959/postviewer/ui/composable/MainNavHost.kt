package br.edu.ifsp.scl.sc3043959.postviewer.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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

            PostDetailsScreen(
                postId = postId,
                postViewModel = postViewModel
            )
        }
    }
}
