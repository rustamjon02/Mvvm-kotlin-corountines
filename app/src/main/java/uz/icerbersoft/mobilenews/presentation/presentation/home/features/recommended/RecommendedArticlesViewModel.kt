package uz.icerbersoft.mobilenews.presentation.presentation.home.features.recommended

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import uz.icerbersoft.mobilenews.domain.data.entity.article.Article
import uz.icerbersoft.mobilenews.domain.usecase.article.recommended.RecommendedArticlesUseCase
import uz.icerbersoft.mobilenews.presentation.global.router.GlobalRouter
import uz.icerbersoft.mobilenews.presentation.presentation.home.router.HomeRouter
import uz.icerbersoft.mobilenews.presentation.utils.LoadingState
import uz.icerbersoft.mobilenews.presentation.utils.LoadingState.*
import javax.inject.Inject

internal class RecommendedArticlesViewModel @Inject constructor(
    private val useCase: RecommendedArticlesUseCase,
    private val globalRouter: GlobalRouter,
    private val homeRouter: HomeRouter,
) : ViewModel() {

    private val _articlesLiveData = MutableLiveData<LoadingState<List<Article>>>()

    val articlesLiveData: LiveData<LoadingState<List<Article>>>
        get() = _articlesLiveData

    fun getRecommendedArticles() {
        useCase
            .getRecommendedArticles()
            .onStart { _articlesLiveData.value = LoadingItem }
            .catch { _articlesLiveData.value = LoadingItem }
            .onEach {
                _articlesLiveData.value =
                    if (it.articles.isNotEmpty()) SuccessItem(it.articles)
                    else EmptyItem
            }
            .launchIn(viewModelScope)

    }

    fun updateBookmark(article: Article) {
        useCase
            .updateBookmark(article)
            .launchIn(viewModelScope)
    }

    fun openArticleDetailScreen(articleId: String) =
        globalRouter.openArticleDetailScreen(articleId)

    fun back() =
        homeRouter.openDashboardTab(true)
}