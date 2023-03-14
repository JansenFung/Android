package com.example.project_g08.ui.fragments.news

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_g08.listeners.OnUserNewsClickListener
import com.example.project_g08.models.UserArticle
import com.example.project_g08.db.UserArticleRepository
import com.example.project_g08.databinding.FragmentAllUsersNewsBinding
import com.example.project_g08.adapter.UserNewsAdapter
import com.example.project_g08.models.News
import com.example.project_g08.models.User

class AllUsersNewsFragment : Fragment(), OnUserNewsClickListener {
    private var _binding: FragmentAllUsersNewsBinding? = null
    private val binding get() = _binding!!
    lateinit var allUsersNewsList: ArrayList<UserArticle>
    lateinit var userArticleRepository : UserArticleRepository
    lateinit var userNewsAdapter: UserNewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userArticleRepository = UserArticleRepository(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAllUsersNewsBinding.inflate(inflater, container, false)

        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        allUsersNewsList =  ArrayList()
        userNewsAdapter = UserNewsAdapter(requireContext(), allUsersNewsList, this)
        binding.reUsersNews.layoutManager = LinearLayoutManager(requireContext())
        binding.reUsersNews.adapter = userNewsAdapter

        allUsersNewsList.clear()
        userArticleRepository.getAllUsersArticles()
        userNewsAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()

        userArticleRepository.getAllUsersArticles()

        //Get up-to-date user's post list from Firestore
        userArticleRepository.allUsersNewsLiveArticles.observe(this){
                list ->
            allUsersNewsList.clear()
            val unsortedList = ArrayList<UserArticle>()

            if(list != null){
                for(news in list){
                    unsortedList.add(news)
                    //allUsersNewsList.add(news)
                    //userNewsAdapter.notifyDataSetChanged()
                }
            }

            allUsersNewsList.addAll(unsortedList.sortedWith(compareByDescending { it.publishedDate }))
            userNewsAdapter.notifyDataSetChanged()
        }
    }

    override fun onPause() {
        super.onPause()

        allUsersNewsList.clear()
        userNewsAdapter.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onItemLongClickListener(newArticle: UserArticle) {
    }

    override fun onItemClickListener(newArticle: UserArticle) {
    }
}