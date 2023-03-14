package com.example.project_g08.ui.fragments.account

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.example.project_g08.R
import com.example.project_g08.databinding.FragmentAccountHomeBinding
import com.example.project_g08.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth

class AccountHomeFragment : Fragment() {
    private var _binding: FragmentAccountHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext().getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FragmentAccountHomeBinding.inflate(inflater, container, false)

        var view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //If there is no loggedIn user
        if (mAuth.currentUser == null) {
            binding.constraintSignIn.isVisible = true
            binding.constraintUpdatePassword.isVisible = false
            binding.constraintUpdateUsername.isVisible = false
            binding.constraintManageUserPosts.isVisible = false
            binding.btnSignOut.isVisible = false
            binding.tvAccount.text = "Account"

            //Sign In option
            binding.constraintSignIn.setOnClickListener {
                val action =
                    AccountHomeFragmentDirections.actionAccountHomeFragmentToSignInFragment()
                findNavController().navigate(action)
            }
        }
        else{
            val username =
                if(sharedPreferences.contains("USERNAME")) sharedPreferences.getString("USERNAME","")
                else ""

            binding.constraintSignIn.isVisible = false
            binding.constraintUpdatePassword.isVisible = true
            binding.constraintUpdateUsername.isVisible = true
            binding.constraintManageUserPosts.isVisible = true
            binding.btnSignOut.isVisible = true
            binding.tvAccount.text = "Welcome Back $username"

            //Sign out option
            binding.btnSignOut.setOnClickListener {
                FirebaseAuth.getInstance().signOut()

                Toast.makeText(requireContext(), "Sign Out Successfully", Toast.LENGTH_LONG).show()

                var intent = Intent(requireActivity(), MainActivity::class.java)
                startActivity(intent)
            }

            //Manage user posts option
            binding.constraintManageUserPosts.setOnClickListener{
                val action =
                    AccountHomeFragmentDirections.actionAccountHomeFragmentToUsersNewsFragment()
                findNavController().navigate(action)
            }

            //Update password option
            binding.constraintUpdatePassword.setOnClickListener{
                val action =
                    AccountHomeFragmentDirections.actionAccountHomeFragmentToAccountUpdateFragment()
                findNavController().navigate(action)
            }

            //Update username option
            binding.constraintUpdateUsername.setOnClickListener{
                val action =
                    AccountHomeFragmentDirections.actionAccountHomeFragmentToUpdateUsernameFragment()
                findNavController().navigate(action)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getUsernameFromSharedPreference()
    }

    //Get update-to-dates username from the sharedPreference
    private fun getUsernameFromSharedPreference(){
        requireContext().getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE)
            .registerOnSharedPreferenceChangeListener { sharedPreferences, s ->
                val username =
                    if (sharedPreferences.contains("USERNAME")) sharedPreferences.getString(
                        "USERNAME",
                        ""
                    )
                    else ""

                if (mAuth.currentUser != null)
                    binding.tvAccount.text = "Welcome Back $username"
            }
    }
}