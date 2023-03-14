package com.example.project_g08.ui.fragments.news

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.project_g08.api.IAPIResponse
import com.example.project_g08.api.RetrofitInstance
import com.example.project_g08.listeners.OnNewsClickListener
import com.example.project_g08.models.Results
import com.example.project_g08.databinding.FragmentWorldBinding
import com.example.project_g08.adapter.NewsAdapter
import com.example.project_g08.models.FavouriteNews
import com.example.project_g08.db.FavouriteNewsRepository
import com.example.project_g08.models.News
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class WorldFragment : Fragment(), OnNewsClickListener {
    private var _binding: FragmentWorldBinding? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private val binding get() = _binding!!
    private lateinit var favouriteNewsRepository: FavouriteNewsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance()

        sharedPreferences = requireContext().getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE)
        favouriteNewsRepository = FavouriteNewsRepository(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentWorldBinding.inflate(inflater, container, false)

        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var NewsList: ArrayList<Results> = ArrayList()

        var adapter: NewsAdapter = NewsAdapter(requireContext(), NewsList, this)

        binding.rvView.layoutManager = LinearLayoutManager(requireContext())
        binding.rvView.adapter = adapter

        var api: IAPIResponse = RetrofitInstance.retrofitService

        //Retrieve world news from API
        viewLifecycleOwner.lifecycleScope.launch {
            val newsListFromAPI: News = api.getWorld()

            NewsList.clear()
            NewsList.addAll(newsListFromAPI.results)
            adapter.notifyDataSetChanged()
        }

        val simpleCallback: ItemTouchHelper.SimpleCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                //Save a selected news into user favorite list
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if (direction == ItemTouchHelper.LEFT) {
                        val addDialog = AlertDialog.Builder(requireContext())
                            .setTitle("Do you want to save this ?")
                            .setPositiveButton("Yes") { dialog, which ->
                                if (mAuth.currentUser == null) {
                                    val addDialog = AlertDialog.Builder(requireContext())
                                        .setTitle("You need to sign in first")
                                        .setNegativeButton("ok"){dialog,which->
                                            adapter.notifyDataSetChanged()
                                            dialog.dismiss()}
                                        .create()
                                    addDialog.show()
                                }else{
                                    var position = viewHolder.adapterPosition
                                    var image = ""
                                    if(NewsList[position].image_url!=null){
                                        image = NewsList[position].image_url.toString()
                                    }

                                    val favouriteNews = FavouriteNews(title=NewsList[position].title, pic =  image, link = NewsList[position].link)
                                    favouriteNewsRepository.addFavouriteNews(favouriteNews)
                                    Toast.makeText(requireContext(), "News is added", Toast.LENGTH_SHORT).show()
                                    adapter.notifyDataSetChanged()
                                }
                            }
                            .setNegativeButton("No"){dialog ,which->
                                adapter.notifyDataSetChanged()
                                dialog.dismiss()

                            }
                            .create()
                        addDialog.show()
                    }
                }
            }
        val helper = ItemTouchHelper(simpleCallback)
        helper.attachToRecyclerView(binding!!.rvView)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}