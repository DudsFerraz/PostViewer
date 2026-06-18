package br.edu.ifsp.scl.sc3043959.postviewer.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.edu.ifsp.scl.sc3043959.postviewer.model.entity.LocalComment

@Dao
interface LocalCommentDao {

    @Query("SELECT * FROM local_comment WHERE postId = :postId ORDER BY id")
    suspend fun getCommentsByPostId(postId: Int): List<LocalComment>

    @Insert
    suspend fun addComment(localComment: LocalComment)
}
