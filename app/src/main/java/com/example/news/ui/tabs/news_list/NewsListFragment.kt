package com.example.news.ui.tabs.news_list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.navigation.fragment.NavHostFragment
import com.example.news.R
import com.example.news.data.objects.CategoriesData
import com.example.news.data.objects.RequestParam
import com.example.news.databinding.FragmentNewsListBinding
import com.example.news.domain.model.NewsDomainModel
import com.example.news.ui.news_detail.NewsDetailFragment
import com.example.news.ui.tabs.TabsFragmentDirections
import com.example.news.ui.tabs.profile.firebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewsListFragment : Fragment(),
    NewsAdapter.OnItemClickListener,
    NewsCategoriesAdapter.OnItemClickListener {

    private lateinit var binding: FragmentNewsListBinding
    private val adapterNews = NewsAdapter(this)
    private val adapterCategories = NewsCategoriesAdapter(this)
    private val newsViewModel by viewModel<NewsViewModel>()

    private val database = Firebase.database

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecycler()
        showNews()
    }

    private fun initRecycler() {
        binding.newsRecycler.adapter = adapterNews
        binding.newsCategoryRecycler.adapter = adapterCategories
    }

    private fun showNews() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            newsViewModel.loadNews()
        }

        newsViewModel.categoriesList.observe(viewLifecycleOwner) {
            adapterCategories.addData(it)
        }

        newsViewModel.newsList.observe(viewLifecycleOwner) {
            binding.swipeRefreshLayout.isRefreshing = false
            adapterNews.addData(it)
        }
    }

    override fun onItemNewsClick(url: String) {
        val topLevelHost = requireActivity().supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment

       topLevelHost.navController.navigate(TabsFragmentDirections.actionTabsFragmentToNewsDetailFragment(url))
    }

    override fun onImageShareClick(url: String) {
        ShareCompat.IntentBuilder(requireContext())
            .setType("text/plain")
            .setChooserTitle(R.string.share_url)
            .setText(url)
            .startChooser()
    }

    override fun onAddBookmarkClick(news: NewsDomainModel) {

        val myRef = firebaseUser?.uid?.let {
            database.reference.child("users").child(it).child("bookmarksNews")
        }

        if (firebaseUser == null)
            Toast.makeText(requireContext(), "Sign in account", Toast.LENGTH_SHORT).show()
        else {
            myRef?.push()?.setValue(news)?.addOnFailureListener {
                Toast.makeText(requireContext(), "Failure: ${it.message}", Toast.LENGTH_SHORT).show()
            }
                ?.addOnSuccessListener {
                    Toast.makeText(requireContext(), "News add to bookmark", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onItemCategoryClick(category: String) {
        RequestParam.CATEGORY = category
        binding.swipeRefreshLayout.isRefreshing = true
        newsViewModel.loadNews()
        binding.newsRecycler.layoutManager?.scrollToPosition(0)
    }
}