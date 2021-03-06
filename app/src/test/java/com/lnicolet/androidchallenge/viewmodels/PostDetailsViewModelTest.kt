package com.lnicolet.androidchallenge.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import com.lnicolet.androidchallenge.postdetails.mappers.CommentMapper
import com.lnicolet.androidchallenge.postdetails.mappers.CommentsMapper
import com.lnicolet.androidchallenge.postdetails.mappers.PostDetailMapper
import com.lnicolet.androidchallenge.postdetails.mappers.UserMapper
import com.lnicolet.androidchallenge.postdetails.models.PostDetail
import com.lnicolet.androidchallenge.postdetails.models.User
import com.lnicolet.androidchallenge.postdetails.viewmodels.PostDetailsViewModel
import com.lnicolet.androidchallenge.postdetails.viewmodels.PostDetailsViewState
import com.lnicolet.androidchallenge.utils.RxSchedulerRule
import com.lnicolet.domain.models.PostDetailDomainModel
import com.lnicolet.domain.models.UserDomainModel
import com.lnicolet.domain.repositories.CommentsRepository
import com.lnicolet.domain.repositories.UsersRepository
import com.lnicolet.domain.usecases.CommentsAndUserUseCase
import com.lnicolet.domain.usecases.CommentsUseCase
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.powermock.modules.junit4.PowerMockRunner

/**
 * Created by Luca Nicoletti
 * on 18/04/2019
 */

@RunWith(PowerMockRunner::class)
class PostDetailsViewModelTest {

    // Forces RxJava to execute on a specified thread for tests (Thanks to Tristan)
    @Rule
    val rxRule = RxSchedulerRule()
    @Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var lifecycleOwner: LifecycleOwner
    @Mock
    lateinit var commentsRepository: CommentsRepository
    @Mock
    lateinit var userRepository: UsersRepository
    @Mock
    lateinit var usersMapper: UserMapper
    @Mock
    lateinit var commentsMapper: CommentsMapper
    @Mock
    lateinit var commentMapper: CommentMapper
    @Mock
    lateinit var postDetailMapper: PostDetailMapper
    @Mock
    lateinit var postDetailsViewStateObserver: Observer<PostDetailsViewState>

    private lateinit var commentsAndUserUseCase: CommentsAndUserUseCase
    private lateinit var commentsUseCase: CommentsUseCase
    private lateinit var postDetailsViewModel: PostDetailsViewModel

    @Before
    fun `prepare for test`() {
        MockitoAnnotations.initMocks(this)
        setupViewModel()
    }


    @Test
    fun `verify that success response create correct view state when fetching user and comments`() {
        // Arrange
        val validDetailResponse = PostDetailDomainModel(
            UserDomainModel(1, "", "", "", mock(), "", "", mock(), ""),
            listOf()
        )
        Mockito.`when`(commentsRepository.getCommentsByPost(any()))
            .thenReturn(Single.just(listOf()))
        Mockito.`when`(userRepository.getUsersById(any()))
            .thenReturn(Single.just(validDetailResponse.user))
        Mockito.`when`(usersMapper.mapToPresentation(any()))
            .thenReturn(User(1, "", "", "", "", "", ""))
        Mockito.`when`(commentsMapper.mapToPresentation(any()))
            .thenReturn(listOf())
        Mockito.`when`(postDetailMapper.mapToPresentation(validDetailResponse))
            .thenReturn(
                PostDetail(
                    User(1, "", "", "", "", "", ""),
                    listOf()
                )
            )

        // Act
        postDetailsViewModel.loadCommentsAndUserData(1, 1)

        // Assert
        Mockito.verify(postDetailsViewStateObserver)
            .onChanged(
                PostDetailsViewState.SuccessBoth(
                    postDetailMapper.mapToPresentation(validDetailResponse).user,
                    postDetailMapper.mapToPresentation(validDetailResponse).commentList
                )
            )
    }


    @Test
    fun `verify that error response create correct view state when fetching user and comments`() {
        // Arrange
        Mockito.`when`(commentsRepository.getCommentsByPost(any()))
            .thenReturn(Single.error(Throwable("")))
        Mockito.`when`(userRepository.getUsersById(any()))
            .thenReturn(Single.just(UserDomainModel(1, "", "", "", mock(), "", "", mock(), "")))

        // Act
        postDetailsViewModel.loadCommentsAndUserData(1, 1)

        // Assert
        Mockito.verify(postDetailsViewStateObserver)
            .onChanged(
                PostDetailsViewState.ErrorBoth("")
            )
    }

    @Test
    fun `verify that success response create correct view state when fetching only comments`() {
        // Arrange
        Mockito.`when`(commentsRepository.getCommentsByPost(any()))
            .thenReturn(Single.just(listOf()))
        Mockito.`when`(commentsMapper.mapToPresentation(any()))
            .thenReturn(listOf())

        // Act
        postDetailsViewModel.loadComments(1)

        // Assert
        Mockito.verify(postDetailsViewStateObserver)
            .onChanged(
                PostDetailsViewState.SuccessComments(listOf())
            )
    }


    @Test
    fun `verify that error response create correct view state when fetching only comments`() {
        // Arrange
        Mockito.`when`(commentsRepository.getCommentsByPost(any()))
            .thenReturn(Single.error(Throwable("")))

        // Act
        postDetailsViewModel.loadComments(1)

        // Assert
        Mockito.verify(postDetailsViewStateObserver)
            .onChanged(
                PostDetailsViewState.ErrorComments("")
            )
    }

    private fun setupViewModel() {
        commentsAndUserUseCase = CommentsAndUserUseCase(commentsRepository, userRepository)
        commentsUseCase = CommentsUseCase(commentsRepository)
        postDetailsViewModel = PostDetailsViewModel(commentsAndUserUseCase, commentsUseCase, postDetailMapper, commentMapper)
        setupLifecycleOwner()
        setupObservers()
    }

    private fun setupObservers() {
        postDetailsViewModel.postDetailsViewState.observe(
            lifecycleOwner,
            postDetailsViewStateObserver
        )
    }

    private fun setupLifecycleOwner() {
        val lifecycle = LifecycleRegistry(lifecycleOwner)
        Mockito.`when`(lifecycleOwner.lifecycle).thenReturn(lifecycle)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }
}