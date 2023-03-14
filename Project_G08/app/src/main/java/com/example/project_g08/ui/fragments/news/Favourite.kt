package com.example.project_g08.ui.fragments.news

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.project_g08.adapter.FavouriteNewsAdapter
import com.example.project_g08.listeners.OnNewsClickListener
import com.example.project_g08.models.FavouriteNews
import com.example.project_g08.db.FavouriteNewsRepository
import com.example.project_g08.databinding.FragmentFavouriteBinding

class Favourite : Fragment(), OnNewsClickListener {
    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    lateinit var NewsList: ArrayList<FavouriteNews>
    lateinit var NewsAdapter: FavouriteNewsAdapter
    lateinit var favouriteNewsRepository : FavouriteNewsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        favouriteNewsRepository = FavouriteNewsRepository(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)

        NewsList = ArrayList()
        NewsAdapter = FavouriteNewsAdapter(requireContext(), NewsList, this)

        val view = binding.root

        return view
    }

    override fun onStart() {
        super.onStart()

        NewsList =  ArrayList()
        NewsAdapter = FavouriteNewsAdapter(requireContext(),NewsList, this)
        binding.rvView.layoutManager = LinearLayoutManager(requireContext())
        binding.rvView.adapter = NewsAdapter

        NewsList.clear()
        favouriteNewsRepository.getFavouriteNews()
        NewsAdapter.notifyDataSetChanged()

    }

    override fun onResume() {
        super.onResume()

        favouriteNewsRepository.getFavouriteNews()

        //Get up-to-date favorite list from Firestore
        favouriteNewsRepository.userFavouriteNewsLive.observe(this){
                list ->
            NewsList.clear()
            if(list != null){
                for(news in list){
                    NewsList.add(news)
                    NewsAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    //redirect the user to browser
    override fun onItemClickListener(link: String) {
        val urlToOpen = Uri.parse(link)

        val intent = Intent(Intent.ACTION_VIEW, urlToOpen)

        if(intent.resolveActivity(requireActivity().packageManager) != null)
            startActivity(intent)
        else{
            Log.e("ERROR", "Can't open the link")
        }
    }

    override fun onItemLongClickListener(news: FavouriteNews) {
        //Delete a post
        val alert = AlertDialog.Builder(requireContext())
            .setTitle("Delete")
            .setMessage("Do you want to remove it from your favorite list?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Confirm") { which, dialog ->
                favouriteNewsRepository.deleteFromFavorite(news.id)
                NewsList.clear()
                favouriteNewsRepository.getFavouriteNews()
                NewsAdapter.notifyDataSetChanged()
            }

        alert.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}