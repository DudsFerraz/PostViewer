package br.edu.ifsp.scl.sc3043959.postviewer.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_comment")
data class LocalComment(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val postId: Int,
    val body: String
)
