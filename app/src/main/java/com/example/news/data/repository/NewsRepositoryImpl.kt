package com.example.news.data.repository

import com.example.news.data.datasource.NewsDataSource
import com.example.news.domain.model.NewsDomainModel
import com.example.news.domain.repository.NewsRepository

class NewsRepositoryImpl(
    private val newsRemoteDataSource: NewsDataSource.Remote,
    private val newsLocalDataSource: NewsDataSource.Local
) : NewsRepository {

    override suspend fun fetchNews(): List<NewsDomainModel> {
        return newsLocalDataSource.fetchNews()
    }

    override suspend fun loadNews() {
        newsRemoteDataSource.loadNews().let {
            if (it.isNotEmpty()) {
                newsLocalDataSource.deleteNews()
                newsLocalDataSource.addNews(it)
            }
        }
    }
}