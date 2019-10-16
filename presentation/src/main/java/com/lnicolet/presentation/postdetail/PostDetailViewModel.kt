package com.lnicolet.presentation.postdetail

import androidx.lifecycle.ViewModel
import com.lnicolet.domain.usecase.CommentsAndUserUseCase
import com.lnicolet.domain.usecase.PostsAndUsersUseCase
import com.lnicolet.presentation.base.BaseIntent
import com.lnicolet.presentation.base.BaseViewModel
import com.lnicolet.presentation.base.TaskStatus
import com.lnicolet.presentation.postdetail.model.PostDetail
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

open class PostDetailViewModel @Inject constructor(
    private val postDetailProcessor: PostDetailProcessor
) : ViewModel(), BaseViewModel<PostDetailIntent, PostDetailViewState> {

    private var intentsSubject: PublishSubject<PostDetailIntent> = PublishSubject.create()
    private val intentFilter: ObservableTransformer<PostDetailIntent, PostDetailIntent> =
        ObservableTransformer { it.publish { observable -> observable } }

    private val reducer: BiFunction<PostDetailViewState, PostDetailResult, PostDetailViewState> =
        BiFunction { _, result ->
            when (result) {
                is PostDetailResult.LoadPostDetailTask -> {
                    when (result.status) {
                        TaskStatus.LOADING -> {
                            result.user?.let { PostDetailViewState.InProgressComments }
                                ?: PostDetailViewState.InProgressBoth
                        }
                        TaskStatus.FAILURE -> {
                            result.user?.let { PostDetailViewState.FailedComments }
                                ?: PostDetailViewState.FailedBoth
                        }
                        TaskStatus.SUCCESS -> {
                            result.user?.let {
                                PostDetailViewState.SuccessBoth(
                                    PostDetail(
                                        result.user,
                                        result.commentList.orEmpty()
                                    )
                                )
                            } ?: PostDetailViewState.SuccessComments(
                                PostDetail(
                                    result.user!!,
                                    result.commentList.orEmpty()
                                )
                            )
                        }
                    }
                }
            }
        }

    override fun processIntents(intents: Observable<PostDetailIntent>) {
        intents.subscribe(intentsSubject)
    }

    private val statesSubject: Observable<PostDetailViewState> = compose()
    override fun states(): Observable<PostDetailViewState> = statesSubject

    private fun compose(): Observable<PostDetailViewState> =
        intentsSubject.compose(intentFilter)
            .map { this.actionFromIntent(it) }
            .compose(postDetailProcessor.actionProcessor)
            .scan(PostDetailViewState.Idle, reducer)
            .replay(1)
            .autoConnect(0)

    private fun actionFromIntent(intent: BaseIntent): PostDetailAction =
        when (intent) {
            is PostDetailIntent.LoadComments -> PostDetailAction.LoadCommentsOnly(intent.postId, intent.user)
            is PostDetailIntent.LoadEverything -> PostDetailAction.LoadCommentsAndUser(intent.userId, intent.postId)
            else -> throw UnsupportedOperationException(
                "Ooops, that looks like an unknown intent: $intent"
            )
        }
}