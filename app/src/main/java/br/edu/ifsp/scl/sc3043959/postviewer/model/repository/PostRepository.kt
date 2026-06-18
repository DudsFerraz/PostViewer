package br.edu.ifsp.scl.sc3043959.postviewer.model.repository

import android.content.Context
import androidx.room.Room
import br.edu.ifsp.scl.sc3043959.postviewer.model.dao.LocalCommentDao
import br.edu.ifsp.scl.sc3043959.postviewer.model.database.PostViewerDatabase
import br.edu.ifsp.scl.sc3043959.postviewer.model.entity.LocalComment
import br.edu.ifsp.scl.sc3043959.postviewer.model.network.JsonPlaceholderService

// Repository centraliza as fontes de dados do app.
// A UI e o ViewModel não precisam saber se o dado vem da API ou do banco local.
object PostRepository {
    private lateinit var applicationContext: Context

    fun init(applicationContext: Context) {
        // applicationContext vive enquanto o app estiver aberto. Isso evita manter referência a uma Activity.
        this.applicationContext = applicationContext
    }

    // Banco criado sob demanda para evitar custo de inicialização antes do primeiro uso.
    // by lazy também garante que a mesma instancia do DAO seja reaproveitada.
    private val localCommentDao: LocalCommentDao by lazy {
        Room.databaseBuilder(
            applicationContext,
            PostViewerDatabase::class.java,
            "postviewer_database"
        ).build().getLocalCommentDao()
    }

    suspend fun getPosts() = JsonPlaceholderService.jsonPlaceholderApi.getPosts()

    suspend fun getApiCommentsByPostId(postId: Int) =
        JsonPlaceholderService.jsonPlaceholderApi.getCommentsByPostId(postId)

    suspend fun getLocalCommentsByPostId(postId: Int) =
        localCommentDao.getCommentsByPostId(postId)

    suspend fun addLocalComment(postId: Int, body: String) {
        // id não é informado porque o Room gera automaticamente pela chave primaria.
        localCommentDao.addComment(
            LocalComment(
                postId = postId,
                body = body
            )
        )
    }
}
