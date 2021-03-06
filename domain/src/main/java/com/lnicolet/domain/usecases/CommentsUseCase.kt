package com.lnicolet.domain.usecases

import com.lnicolet.domain.models.CommentDomainModel
import com.lnicolet.domain.repositories.CommentsRepository
import io.reactivex.Single
import javax.inject.Inject

class CommentsUseCase @Inject constructor(private val commentsRepository: CommentsRepository) {

    fun getComments(): Single<List<CommentDomainModel>> =
            commentsRepository.getComments()

    fun getCommentsByPostId(postId: Int): Single<List<CommentDomainModel>> =
            commentsRepository.getCommentsByPost(postId)
}