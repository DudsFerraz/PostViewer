package br.edu.ifsp.scl.sc3043959.postviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.edu.ifsp.scl.sc3043959.postviewer.ui.composable.MainNavHost
import br.edu.ifsp.scl.sc3043959.postviewer.ui.composable.MainTopAppBar
import br.edu.ifsp.scl.sc3043959.postviewer.ui.navigation.Screen
import br.edu.ifsp.scl.sc3043959.postviewer.ui.theme.PostViewerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // NavController controla a pilha de telas do Navigation Compose.
            // Lembrado pelo Compose para sobreviver as recomposicoes da tela.
            val mainNavHostController: NavHostController = rememberNavController()

            // O mesmo ViewModel compartilhado entre lista e detalhes.
            // Navegação troca a tela, mas o estado principal continua centralizado.
            val postViewModel: PostViewModel = viewModel()

            // Observa a rota atual para exibir o botão de voltar apenas fora da tela inicial.
            val navBackStackEntry by mainNavHostController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val canNavigateBack = currentRoute != Screen.PostList.route

            PostViewerTheme {
                // Scaffold cria a estrutura comum da tela: barra superior e conteúdo.
                // Conteúdo recebe innerPadding para não ficar escondido atrás da TopAppBar.
                Scaffold(
                    topBar = {
                        MainTopAppBar(
                            canNavigateBack = canNavigateBack,
                            onNavigateBack = { mainNavHostController.popBackStack() }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    // MainNavHost decide qual tela deve aparecer conforme a rota atual.
                    MainNavHost(
                        mainNavHostController = mainNavHostController,
                        postViewModel = postViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
