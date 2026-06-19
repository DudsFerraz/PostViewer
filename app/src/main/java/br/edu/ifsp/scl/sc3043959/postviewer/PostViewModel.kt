package br.edu.ifsp.scl.sc3043959.postviewer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifsp.scl.sc3043959.postviewer.model.entity.ApiComment
import br.edu.ifsp.scl.sc3043959.postviewer.model.entity.ApiPost
import br.edu.ifsp.scl.sc3043959.postviewer.model.entity.LocalComment
import br.edu.ifsp.scl.sc3043959.postviewer.model.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado da tela de lista. Agrupa loading, dados e erro em uma unica classe, assim a tela observa apenas um objeto.
data class PostsUiState(
    val isLoading: Boolean = false,
    val posts: List<ApiPost> = emptyList(),
    val errorMessage: String? = null
)

// Estado da tela de detalhes. Mantém LocalComments e ApiComments separados antes de exibi-los juntos na tela.
data class PostDetailsUiState(
    val isLoading: Boolean = false,
    val postId: Int? = null,
    val apiComments: List<ApiComment> = emptyList(),
    val localComments: List<LocalComment> = emptyList(),
    val errorMessage: String? = null
)

class PostViewModel(application: Application) : AndroidViewModel(application) {

    // O ViewModel conversa com o Repository, deixando a UI independente da origem dos dados.
    private val postRepository = PostRepository

    // MutableStateFlow fica privado para evitar que a tela altere o estado diretamente.
    // A tela recebe apenas StateFlow (somente leitura).
    private val _postsUiState = MutableStateFlow(PostsUiState())
    val postsUiState: StateFlow<PostsUiState> = _postsUiState.asStateFlow()

    private val _postDetailsUiState = MutableStateFlow(PostDetailsUiState())
    val postDetailsUiState: StateFlow<PostDetailsUiState> = _postDetailsUiState.asStateFlow()

    init {
        // O Repository precisa do applicationContext para abrir o banco Room.
        // Usar applicationContext evita guardar referencia a uma Activity.
        postRepository.init(application.applicationContext)

        // posts carregados assim que o ViewModel for criado.
        loadPosts()
    }

    fun loadPosts() {

        // viewModelScope cria uma coroutine ligada ao ciclo de vida do ViewModel.
        viewModelScope.launch {
            _postsUiState.value = PostsUiState(isLoading = true)

            try {
                val posts = postRepository.getPosts()
                _postsUiState.value = PostsUiState(posts = posts)
            } catch (exception: Exception) {
                _postsUiState.value = PostsUiState(
                    errorMessage = "Não foi possível carregar os posts."
                )
            }
        }
    }

    fun loadPostDetails(postId: Int) {
        viewModelScope.launch {

            _postDetailsUiState.value = PostDetailsUiState(
                isLoading = true,
                postId = postId
            )

            try {
                // Na UI eles serão exibidos juntos, mas continuam separados no estado.
                val apiComments = postRepository.getApiCommentsByPostId(postId)
                val localComments = postRepository.getLocalCommentsByPostId(postId)

                _postDetailsUiState.value = PostDetailsUiState(
                    postId = postId,
                    apiComments = apiComments,
                    localComments = localComments
                )
            } catch (exception: Exception) {
                _postDetailsUiState.value = PostDetailsUiState(
                    postId = postId,
                    errorMessage = "Nao foi possível carregar os comentários."
                )
            }
        }
    }

    suspend fun loadPostCommentCount(postId: Int): Int {
        val localCommentsCount = postRepository.getLocalCommentsCountByPostId(postId)
        val apiCommentsCount = postRepository.getApiCommentsByPostId(postId).size
        return localCommentsCount + apiCommentsCount
    }

    fun addLocalComment(postId: Int, body: String) {

        val sanitizedBody = body.trim()
        if (sanitizedBody.isBlank()) {
            return
        }

        viewModelScope.launch {
            try {
                postRepository.addLocalComment(postId, sanitizedBody)

                // Recarrega somente os comentários locais. Sem necessidade de buscar novamente os comentários da API, eles não mudaram.
                val currentState = _postDetailsUiState.value
                _postDetailsUiState.value = currentState.copy(
                    localComments = postRepository.getLocalCommentsByPostId(postId),
                    errorMessage = null
                )
            } catch (exception: Exception) {
                _postDetailsUiState.value = _postDetailsUiState.value.copy(
                    errorMessage = "Nao foi possível salvar o comentário local."
                )
            }
        }
    }
}
