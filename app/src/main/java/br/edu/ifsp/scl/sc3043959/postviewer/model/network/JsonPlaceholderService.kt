package br.edu.ifsp.scl.sc3043959.postviewer.model.network

import br.edu.ifsp.scl.sc3043959.postviewer.model.entity.ApiComment
import br.edu.ifsp.scl.sc3043959.postviewer.model.entity.ApiPost
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object JsonPlaceholderService {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    interface JsonPlaceholderApi {
        @GET("posts")
        suspend fun getPosts(): List<ApiPost>

        @GET("posts/{id}/comments")
        suspend fun getCommentsByPostId(@Path("id") postId: Int): List<ApiComment>
    }

    // O Retrofit fica isolado para que ViewModel e telas nao conhecam detalhes da API.
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val jsonPlaceholderApi: JsonPlaceholderApi by lazy {
        retrofit.create(JsonPlaceholderApi::class.java)
    }
}
