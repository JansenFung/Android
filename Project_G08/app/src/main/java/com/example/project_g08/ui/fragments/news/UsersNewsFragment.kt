package com.example.project_g08.ui.fragments.news

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_g08.listeners.OnUserNewsClickListener
import com.example.project_g08.models.UserArticle
import com.example.project_g08.db.UserArticleRepository
import com.example.project_g08.databinding.DialogEditNewsBinding
import com.example.project_g08.databinding.FragmentUsersNewsBinding
import com.example.project_g08.adapter.UserNewsAdapter

class UsersNewsFragment : Fragment(), OnUserNewsClickListener {
    private var _binding: FragmentUsersNewsBinding? = null
    private val binding get() = _binding!!
    lateinit var usersNewsList: ArrayList<UserArticle>
    lateinit var userArticleRepository : UserArticleRepository
    lateinit var userNewsAdapter: UserNewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userArticleRepository = UserArticleRepository(requireContext())
        usersNewsList =  ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUsersNewsBinding.inflate(inflater, container, false)

        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        usersNewsList =  ArrayList()
        userNewsAdapter = UserNewsAdapter(requireContext(), usersNewsList, this)
        binding.reUsersNews.layoutManager = LinearLayoutManager(requireContext())
        binding.reUsersNews.adapter = userNewsAdapter

        usersNewsList.clear()
        userArticleRepository.getUserArticles()
        userNewsAdapter.notifyDataSetChanged()
    }

    override fun onStart() {
        super.onStart()

        usersNewsList =  ArrayList()
        userNewsAdapter = UserNewsAdapter(requireContext(), usersNewsList, this)
        binding.reUsersNews.layoutManager = LinearLayoutManager(requireContext())
        binding.reUsersNews.adapter = userNewsAdapter

        usersNewsList.clear()
        userArticleRepository.getUserArticles()
        userNewsAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()

        userArticleRepository.getUserArticles()

        //Get up-to-date user's new list from Firestore
        userArticleRepository.userNewsLiveArticles.observe(this){
                list ->
            usersNewsList.clear()
            if(list != null){
                for(news in list){
                    usersNewsList.add(news)
                    userNewsAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("User post", "OnPause")
        usersNewsList.clear()
        userNewsAdapter.notifyDataSetChanged()
    }

    //Delete selected user's news from Firestore
    override fun onItemLongClickListener(userArticle: UserArticle) {
        val alert = AlertDialog.Builder(requireContext())
            .setTitle("Delete")
            .setMessage("Are you sure want to delete this post")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Confirm") { which, dialog ->
                userArticleRepository.deleteUserPost(userArticle.id)
                usersNewsList.clear()
                userArticleRepository.getUserArticles()
                userNewsAdapter.notifyDataSetChanged()
            }

        alert.show()
    }

    //Edit user news and update in Firestore
    override fun onItemClickListener(userArticle: UserArticle) {
        val binding = DialogEditNewsBinding.inflate(layoutInflater)

        val alert = AlertDialog.Builder(requireContext())
            .setTitle("Edit Post")
            .setView(binding.root)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Edit") { which, dialog ->

                if(binding.etTitle.text.isNotBlank() && binding.etBody.text.isNotBlank()){
                    val title = binding.etTitle.text.toString()
                    val body = binding.etBody.text.toString()

                    userArticleRepository.editUserPost(userArticle.id, title, body)
                    usersNewsList.clear()
                    userArticleRepository.getUserArticles()
                    userNewsAdapter.notifyDataSetChanged()

                    Toast.makeText(requireContext(), "Update Successfully", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(requireContext(), "Failed: Please enter the Title and Body", Toast.LENGTH_LONG).show()
                }
            }

        alert.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}