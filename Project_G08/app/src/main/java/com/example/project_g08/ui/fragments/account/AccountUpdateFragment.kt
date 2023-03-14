package com.example.project_g08.ui.fragments.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.project_g08.db.UserRepository
import com.example.project_g08.databinding.FragmentAccountUpdateBinding

class AccountUpdateFragment : Fragment() {
    private var _binding: FragmentAccountUpdateBinding? = null
    private val binding get() = _binding!!
    lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userRepository = UserRepository(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccountUpdateBinding.inflate(inflater, container, false)

        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener{
            val action = AccountUpdateFragmentDirections.actionAccountUpdateFragmentToAccountHomeFragment()
            findNavController().navigate(action)
        }

        //Save and update user's password
        binding.btnSave.setOnClickListener{
            if(validateData()){
                val currentPassword = binding.etCurrentPassword.text.toString()
                val newPassword = binding.etNewPassword.text.toString()

                userRepository.updateUserPassword(currentPassword, newPassword)

                val action = AccountUpdateFragmentDirections.actionAccountUpdateFragmentToAccountHomeFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun validateData(): Boolean{
        val inputCurrentPassword = binding.etCurrentPassword
        val inputPassword = binding.etNewPassword
        val inputConfirmPassword = binding.etConfirmPassword

        if(inputCurrentPassword.text.toString().isBlank()){
            inputPassword.error = "Please enter your current password"
            return false
        }

        if(inputPassword.text.toString().isBlank()){
            inputPassword.error = "Please enter a password"
            return false
        }

        if(inputConfirmPassword.text.toString().isBlank()){
            inputConfirmPassword.error = "Please enter a confirm password"
            return false
        }

        if(inputPassword.text.toString().length < 6){
            inputPassword.error = "Password should be at least 6 characters"
            return false
        }

        if(inputPassword.text.toString() != inputConfirmPassword.text.toString()){
            inputConfirmPassword.error = "Password do not match"
            return false
        }

        return true
    }
}