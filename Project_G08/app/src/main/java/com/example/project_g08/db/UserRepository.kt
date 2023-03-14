package com.example.project_g08.db

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.project_g08.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserRepository(private var context: Context) {
    private var db = Firebase.firestore
    private var COLLECTION_USERS = "users"
    private var sharedPreferences = context.getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE)
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUserDocId = ""

    init{
        if(sharedPreferences.contains("USER_DOC_ID")){
            currentUserDocId = sharedPreferences.getString("USER_DOC_ID", "").toString()
        }
    }

    //Add a new user in to the Firestore database
    fun addNewUserInDB(newUser: User){
        try {
            db.collection(COLLECTION_USERS).add(newUser)
                .addOnSuccessListener { document ->
                    with(sharedPreferences.edit()) {
                        putString("USER_DOC_ID", document.id)
                        apply()
                    }

                    //Set the user ID equals to the corresponding document ID
                    db.collection(COLLECTION_USERS).document(document.id).update("id", document.id)
                }
        }
        catch(ex: Exception){
            Log.e("ERROR", "addNewUserInDB(): Fail to add a new user into Firestore")
        }
    }

    //Search the user in the "users" collection and save the user_document_id and username in the sharedPreference
    fun searchUserWithEmail(email: String){
        try {
            db.collection(COLLECTION_USERS).whereEqualTo("email", email)
                .addSnapshotListener(EventListener { snapshot, error ->
                    if (error != null) {
                        return@EventListener
                    }

                    if (snapshot != null) {
                        for (documentChange in snapshot.documentChanges) {
                            with(sharedPreferences.edit()) {
                                putString("USER_DOC_ID", documentChange.document.id)
                                putString("USERNAME", documentChange.document.get("username").toString())
                                apply()
                            }
                        }
                    }
                })
        }
        catch(ex: Exception){
            Log.e("ERROR", "searchUserWithEmail(): Couldn't find a user")
        }
    }

    //Update user password
    fun updateUserPassword(password: String, newPassword:String){
        //Search for current user in "users" collection
        db.collection(COLLECTION_USERS).document(currentUserDocId).get().addOnCompleteListener {
            task ->
            if (task.isSuccessful){
                //Check if the current password is valid
                if(task.result.data?.get("password").toString() == password){
                    //update the password in the Authentication of Firebase
                    mAuth.currentUser?.updatePassword(newPassword)

                    //update the "password" field of corresponding user document in "users" collection in Firebase
                    db.collection(COLLECTION_USERS).document(currentUserDocId).update("password", newPassword)

                    Toast.makeText(context, "Update password successfully", Toast.LENGTH_SHORT).show()
                }
                //if the current password isn't matched
                else{
                    Log.d("PASSWORD", "unMatch")
                    AlertDialog.Builder(context).setTitle("ERROR")
                        .setMessage("The current password is invalid")
                        .setNegativeButton("OK", null).show()
                }
            }
            else
                Log.d("ERROR", "updateUserPassword(): Couldn't Retrieve the document")
        }
    }

    //Update current user's username
    fun updateUsername(newUsername:String){
        db.collection(COLLECTION_USERS).document(currentUserDocId)
            .update("username", newUsername)
            .addOnSuccessListener {
                with(sharedPreferences.edit()) {
                    putString("USERNAME", newUsername)
                    apply()
                }
                Toast.makeText(context, "Update username successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                AlertDialog.Builder(context).setTitle("ERROR")
                    .setMessage("Unable to update username. Please try again")
                    .setNegativeButton("OK", null)
                    .show()
            }
    }
}