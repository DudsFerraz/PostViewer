package br.edu.ifsp.scl.sc3043959.postviewer.ui.navigation

// Classe que centraliza as rotas usadas pelo Navigation Compose.
sealed class Screen(val route: String) {

    // Tela inicial do aplicativo. Exibe a lista de posts.
    object PostList : Screen(route = "postList")

    // Tela de detalhes recebe o id do post pela propria rota.
    object PostDetails : Screen(route = "postDetails/{postId}") {
        const val POST_ID_ARGUMENT = "postId"

        // Função auxiliar para montar a rota correta ao clicar num post.
        fun createRoute(postId: Int): String = "postDetails/$postId"
    }
}
