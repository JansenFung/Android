package com.example.project_g08.ui.fragments.news

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.project_g08.models.UserArticle
import com.example.project_g08.db.UserArticleRepository
import com.example.project_g08.databinding.FragmentAddArticleBinding
import com.google.firebase.auth.FirebaseAuth

class AddArticleFragment : Fragment() {
    private var _binding: FragmentAddArticleBinding? = null
    private val binding get() = _binding!!
    private lateinit var userArticleRepository: UserArticleRepository
    lateinit var sharedPreferences : SharedPreferences
    lateinit var mAuth: FirebaseAuth
    var loggedInUserID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userArticleRepository = UserArticleRepository(requireContext())
        mAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext().getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE)

        if(sharedPreferences.contains("USER_DOC_ID"))
            loggedInUserID = sharedPreferences.getString("USER_DOC_ID", "").toString()
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddArticleBinding.inflate(inflater, container, false)

        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Add user's post
        binding.btnAddUserArticle.setOnClickListener{
            if(validateData()){
                val title = binding.etTitle.text.toString()
                val detail = binding.etBody.text.toString()
                var author = ""

                if(sharedPreferences.contains("USERNAME"))
                    author = sharedPreferences.getString("USERNAME","").toString()

                val userNews = UserArticle(title = title, detail =  detail, author =  author,)
                userArticleRepository.addUserArticle(userNews)

                binding.etTitle.setText("")
                binding.etBody.setText("")

                Toast.makeText(requireContext(), "Your story is added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun validateData(): Boolean{
        val inputTitle = binding.etTitle
        val inputBody = binding.etBody

        if(inputTitle.text.toString().isBlank()){
            inputTitle.error = "Please enter a Title"
            return false
        }

        if(inputBody.text.toString().isBlank()){
            inputBody.error = "Please enter something that your want to share"
            return false
        }

        return true
    }
}