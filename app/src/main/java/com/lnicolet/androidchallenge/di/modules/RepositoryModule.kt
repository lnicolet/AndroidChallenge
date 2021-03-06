package com.lnicolet.androidchallenge.di.modules

import com.lnicolet.data.repositories.CommentsRepositoryImpl
import com.lnicolet.data.repositories.PostsRepositoryImpl
import com.lnicolet.data.repositories.UsersRepositoryImpl
import com.lnicolet.domain.repositories.CommentsRepository
import com.lnicolet.domain.repositories.PostsRepository
import com.lnicolet.domain.repositories.UsersRepository
import dagger.Binds
import dagger.Module

@Suppress("unused")
@Module(includes = [ApiModule::class])
abstract class RepositoryModule {

    @Binds
    abstract fun bindCommentsRepository(repository: CommentsRepositoryImpl): CommentsRepository

    @Binds
    abstract fun bindUsersRepository(repository: UsersRepositoryImpl): UsersRepository

    @Binds
    abstract fun bindPostsRepository(repository: PostsRepositoryImpl): PostsRepository
}