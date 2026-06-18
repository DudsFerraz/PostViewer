package br.edu.ifsp.scl.sc3043959.postviewer.model.network

import br.edu.ifsp.scl.sc3043959.postviewer.model.entity.ApiComment
import br.edu.ifsp.scl.sc3043959.postviewer.model.entity.ApiPost
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// Service centraliza a configuracao do Retrofit e os endpoints da API.
// Assim o restante do app chama funções Kotlin em vez de montar URLs manualmente.
object JsonPlaceholderService {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    interface JsonPlaceholderApi {
        @GET("posts")
        suspend fun getPosts(): List<ApiPost>

        @GET("posts/{id}/comments")
        suspend fun getCommentsByPostId(@Path("id") postId: Int): List<ApiComment>
    }

    // GsonConverterFactory converte o JSON recebido em data classes Kotlin.
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Retrofit cria a implementação real da interface acima em tempo de execução.
    val jsonPlaceholderApi: JsonPlaceholderApi by lazy {
        retrofit.create(JsonPlaceholderApi::class.java)
    }
}
