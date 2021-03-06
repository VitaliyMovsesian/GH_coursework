package com.example.news.ui.tabs.news_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.data.objects.CategoriesData
import com.example.news.data.objects.model.NewsCategory
import com.example.news.domain.model.NewsDomainModel
import com.example.news.domain.usecase.GetNewsListUseCase
import kotlinx.coroutines.launch

class NewsViewModel(
    private val getNewsListUseCase: GetNewsListUseCase
) : ViewModel() {

    private val _newsList = MutableLiveData<List<NewsDomainModel>>()
    private val _categoriesList = MutableLiveData<List<NewsCategory>>()

    val newsList: LiveData<List<NewsDomainModel>> = _newsList
    val categoriesList: LiveData<List<NewsCategory>> = _categoriesList



    init {
        loadNews()
        _categoriesList.value = CategoriesData.categoriesList
    }

    fun loadNews() {
        viewModelScope.launch {
            _newsList.value = getNewsListUseCase.execute()
        }
    }
}