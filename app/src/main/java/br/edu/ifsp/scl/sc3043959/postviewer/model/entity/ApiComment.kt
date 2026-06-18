package br.edu.ifsp.scl.sc3043959.postviewer.model.entity

data class ApiComment(
    val id: Int,
    val postId: Int,
    val name: String,
    val email: String,
    val body: String
)
