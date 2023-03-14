package com.example.project_g08.ui.fragments.news

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.navigation.fragment.findNavController
import com.example.project_g08.R
import com.example.project_g08.databinding.FragmentNewsHomeBinding
import com.google.android.material.tabs.TabLayout

class NewsHomeFragment : Fragment() {

    private var _binding: FragmentNewsHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNewsHomeBinding.inflate(inflater, container, false)

        val view = binding.root
        val pagerAdapter = SectionsPagerAdapter(childFragmentManager)
        val pager = binding.pager

        pager.adapter = pagerAdapter

        val tabLayout = binding.tabs as TabLayout
        tabLayout.setupWithViewPager(pager)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.options_menu,menu)

        val searchViewItem: MenuItem = menu!!.findItem(R.id.search)
        val searchView : SearchView = MenuItemCompat.getActionView(searchViewItem) as SearchView

        searchView.setOnQueryTextListener(object  : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                val category = query?.lowercase()

                //Check if the category is one of the following categories
                if (category != "business" && category != "entertainment" && category != "food" &&
                    category != "environment" && category != "health" && category != "politics" &&
                    category != "science" && category != "sports" && category != "technology" &&
                    category != "top" && category != "world"
                ) {
                    AlertDialog.Builder(requireContext())
                        .setTitle("Invalid Category")
                        .setMessage(
                            "Category should be either of the following:\n" +
                                    "business\nentertainment\nfood\nenvironment\nhealth\npolitics\n" +
                                    "science\nsports\ntechnology\ntop\nworld"
                        )
                        .setNegativeButton("Ok", null).show()
                }
            else{
                    val action = NewsHomeFragmentDirections.actionNewsHomeFragmentToSearchBar(query!!)
                    findNavController().navigate(action)
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        return super.onCreateOptionsMenu(menu,inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val pagerAdapter = SectionsPagerAdapter(childFragmentManager)
//        val pager = binding.pager
//        pager.adapter = pagerAdapter
//
//        val tabLayout = binding.tabs as TabLayout
//        tabLayout.setupWithViewPager(pager)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}

class SectionsPagerAdapter: FragmentPagerAdapter {

    public constructor(fm : FragmentManager) : super(fm) {

    }
    override fun getCount(): Int {
        return 3
    }
    override fun getItem(position: Int): Fragment {
        when(position){
            0 ->{
                return NewsFragment()
            }
            1->{
                return WorldFragment()
            }
            2->{
                return SportsFragment()
            }
        }

        throw java.lang.IllegalStateException("Postion is invalid")

    }

    override fun getPageTitle(position: Int): String? {
        when(position){
            0 ->{
                return "Local"
            }
            1->{
                return "International"
            }
            2->{
                return "Sports"
            }
        }

        throw java.lang.IllegalStateException("Postion is invalid")
    }
}