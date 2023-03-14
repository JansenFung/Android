package com.example.project_g08.ui.fragments.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.project_g08.db.UserArticleRepository
import com.example.project_g08.db.UserRepository
import com.example.project_g08.databinding.FragmentUpdateUsernameBinding

class UpdateUsernameFragment : Fragment() {
    private var _binding: FragmentUpdateUsernameBinding? = null
    private val binding get() = _binding!!
    lateinit var userRepository: UserRepository
    lateinit var userArticleRepository: UserArticleRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userRepository = UserRepository(requireContext())
        userArticleRepository = UserArticleRepository(requireContext())

    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateUsernameBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener{
            val action = UpdateUsernameFragmentDirections.actionUpdateUsernameFragmentToAccountHomeFragment()
            findNavController().navigate(action)
        }

        //Update username
        binding.btnSave.setOnClickListener{
            if(validateData()){
                val inputNewUsername = binding.etNewUsername.text.toString()

                userRepository.updateUsername(inputNewUsername)

                val action = UpdateUsernameFragmentDirections.actionUpdateUsernameFragmentToAccountHomeFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun validateData(): Boolean{
        val inputNewUsername = binding.etNewUsername

        if(inputNewUsername.text.toString().isBlank()){
            inputNewUsername.error = "Please enter your new username"
            return false
        }

        return true
    }
}