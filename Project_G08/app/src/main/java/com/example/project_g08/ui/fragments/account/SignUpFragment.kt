package com.example.project_g08.ui.fragments.account

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.project_g08.models.User
import com.example.project_g08.db.UserRepository
import com.example.project_g08.databinding.FragmentSignUpBinding
import com.example.project_g08.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userRepository: UserRepository
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()

        userRepository = UserRepository(requireContext())
        sharedPreferences = requireContext().getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE)

    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignUpBinding.inflate(inflater, container, false )

        var view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //Underline the text
        val underlineText = SpannableString(binding.tvLogin.text.toString())

        underlineText.setSpan(UnderlineSpan(), 0, underlineText.length, 0)

        binding.tvLogin.text = underlineText

        //Create an new account
        binding.btnCreate.setOnClickListener{
            if(validateAccount()){
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()
                val username = binding.etUsername.text.toString()

                createAccount(email.uppercase(), password, username)
            }
        }

        binding.tvLogin.setOnClickListener{
            var action = SignUpFragmentDirections.actionSignUpFragmentToSignInFragment()
            findNavController().navigate(action)
        }
    }

    private fun validateAccount(): Boolean{
        val inputEmail = binding.etEmail
        val inputUsername = binding.etUsername
        val inputPassword = binding.etPassword
        val inputConfirmPassword = binding.etConfirmPassword

        if(inputEmail.text.toString().isBlank()){
            inputEmail.error = "Please enter a username"
            return false
        }

        if(inputUsername.text.toString().isBlank()){
            inputUsername.error = "Please enter a username"
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

        if(inputPassword.text.toString() != inputConfirmPassword.text.toString()){
            inputConfirmPassword.error = "Password do not match"
            return false
        }

        return true
    }

    //Create a new account in Firestore
    private fun createAccount(email: String, password: String, username: String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                    task ->
                if(task.isSuccessful){
                    rememberAccount(email)
                    safeCurrentUserNameToSharedPref(username)
                    userRepository.addNewUserInDB(User(email = email, password=password, username = username))

                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                }
                else{
                    val alertDialog = AlertDialog.Builder(requireContext())
                        .setTitle("ERROR")
                        .setMessage("${task.exception?.localizedMessage}")
                        .setNegativeButton("OK", null)

                    alertDialog.show()
                }
            }
    }

    //If user choose to "Remember me", save the email in the SharedPreference
    private fun rememberAccount(email: String){
        if(binding.swRemember.isChecked){
            with(sharedPreferences.edit()) {
                putString("USER_EMAIL", email.uppercase())
                apply()
            }
        }
        else{
            if(sharedPreferences.contains("USER_EMAIL")){
                with(sharedPreferences.edit()){
                    remove("USER_EMAIL")
                    apply()
                }
            }
        }
    }

    private fun safeCurrentUserNameToSharedPref(username: String){
        with(sharedPreferences.edit()) {
            putString("USERNAME", username)
            apply()
        }
    }
}