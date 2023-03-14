package com.example.project_g08.ui.fragments.account

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.project_g08.db.UserRepository
import com.example.project_g08.databinding.FragmentSignInBinding
import com.example.project_g08.ui.MainActivity
import com.google.firebase.auth.FirebaseAuth

class SignInFragment : Fragment() {
    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!
    lateinit var mAuth: FirebaseAuth
    lateinit var userRepository: UserRepository
    lateinit var sharedPreferences: SharedPreferences
    private var rememberEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        sharedPreferences = requireContext().getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE)

        if(sharedPreferences.contains("USER_EMAIL"))
            rememberEmail = sharedPreferences.getString("USER_EMAIL", "").toString()

        userRepository = UserRepository(requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSignInBinding.inflate(inflater, container, false)

        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(rememberEmail.isNotEmpty())
            binding.etEmail.setText(rememberEmail)

        //Sign In
        binding.btnSignIn.setOnClickListener {

            if(validateAccount()){
                val email = binding.etEmail.text.toString()
                val password = binding.etPassword.text.toString()

                loggIn(email, password)
            }
        }

        //Underline the "Create account" text
        val underlineText = SpannableString(binding.tvCreate.text.toString())

        underlineText.setSpan(UnderlineSpan(), 0, underlineText.length, 0)

        binding.tvCreate.text = underlineText

        //Redirect user to sign up fragment
        binding.tvCreate.setOnClickListener{
            var action =  SignInFragmentDirections.actionSignInFragmentToSignUpFragment()
            findNavController().navigate(action)
        }
    }

    private fun validateAccount(): Boolean{
        val inputEmail = binding.etEmail
        val inputPassword = binding.etPassword

        if(inputEmail.text.toString().isBlank()){
            inputEmail.error = "Please enter a username"
            return false
        }

        if(inputPassword.text.toString().isBlank()){
            inputPassword.error = "Please enter a password"
            return false
        }

        return true
    }

    private fun loggIn(email: String, password: String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    rememberAccount(email)
                    userRepository.searchUserWithEmail(email.uppercase())

                    Toast.makeText(requireContext(), "Welcome Back", Toast.LENGTH_LONG).show()

                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    startActivity(intent)
                }
                else {
                    val alert = AlertDialog.Builder(requireContext())
                        .setTitle("ERROR")
                        .setMessage("${task.exception?.localizedMessage}")
                        .setNegativeButton("OK", null)

                    alert.show()
                }
            }
    }

    //If user choose to "Remember me", save the email in the SharedPreference
    private fun rememberAccount(username: String){
        if(binding.swRemember.isChecked){
            with(sharedPreferences.edit()) {
                putString("USER_EMAIL", username.uppercase())
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
}