package com.lnicolet.domain.model

data class CommentDomainModel(
    val postId: Int,
    val id: Int,
    val name: String,
    val email: String,
    val body: String
)