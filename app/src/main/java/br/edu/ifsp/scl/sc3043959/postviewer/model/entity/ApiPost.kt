package br.edu.ifsp.scl.sc3043959.postviewer.model.entity

data class ApiPost(
    val id: Int,
    val userId: Int,
    val title: String,
    val body: String
)
