package com.example.project_g08.db

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.project_g08.models.UserArticle
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList

class UserArticleRepository(private val context:Context) {
    private val db = Firebase.firestore
    private val sharedPreferences = context.getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE)
    private var COLLECTION_USERS = "users"
    private var COLLECTION_ARTICLE = "articles"
    private var loggedInUserID = ""
    var userNewsLiveArticles = MutableLiveData<List<UserArticle>>()
    var allUsersNewsLiveArticles = MutableLiveData<List<UserArticle>>()

    init {
        if (sharedPreferences.contains("USER_DOC_ID")) {
            loggedInUserID = sharedPreferences.getString("USER_DOC_ID", "").toString()
        }
    }

    //Add a user's post into the "articles" sub-collection
    fun addUserArticle(newArticle: UserArticle) {

        if (loggedInUserID.isNotBlank()) {
            db.collection(COLLECTION_USERS).document(loggedInUserID)
                .collection(COLLECTION_ARTICLE)
                .add(newArticle)
        } else {
            Log.e("ERROR", "addUserArticle(): Couldn't add your story")
        }
    }

    //Get all users posts
    fun getAllUsersArticles(){
        try{
            //Search from "users" collection
            db.collection(COLLECTION_USERS).addSnapshotListener(EventListener{
                    snapshot, error ->
                if(error != null){
                    return@EventListener
                }

                if(snapshot != null) {
                    val array = ArrayList<UserArticle>()

                    //For each user in "user collection
                    for (documentChange in snapshot.documentChanges) {

                        //Search the user's post from "articles" sub-collection
                        db.collection(COLLECTION_USERS).document(documentChange.document.id)
                            .collection(COLLECTION_ARTICLE)
                            .addSnapshotListener(EventListener { snapshot, error ->
                                if(error != null){
                                    return@EventListener
                                }

                                if (snapshot != null){
                                    for(documentChange in snapshot.documentChanges){

                                        val news = UserArticle()
                                        news.id = documentChange.document.id
                                        news.title = documentChange.document.get("title").toString()
                                        news.detail = documentChange.document.get("detail").toString()
                                        news.author = documentChange.document.get("author").toString()
                                        news.publishedDate =
                                            documentChange.document.get("publishedDate").toString()

                                        //array.add(news)
                                        when (documentChange.type) {
                                            DocumentChange.Type.ADDED -> array.add(news)
                                            DocumentChange.Type.MODIFIED -> {}
                                            DocumentChange.Type.REMOVED -> array.remove(news)
                                        }
                                    }
                                    allUsersNewsLiveArticles.postValue(array)
                                }
                            })
                    }
                }
            })
        }
        catch(ex: Exception){
            Log.e("ERROR", "getAllUsersArticles(): Couldn't retrieve all user's posts")
        }
    }

    //Get all current user's post from Firestore
    fun getUserArticles() {
        if (loggedInUserID.isNotBlank()) {
            db.collection(COLLECTION_USERS)
                .document(loggedInUserID)
                .collection(COLLECTION_ARTICLE)
                .orderBy("publishedDate", Query.Direction.DESCENDING)
                .addSnapshotListener(EventListener { snapshot, error ->
                    if (error != null) {
                        return@EventListener
                    }

                    if (snapshot != null) {
                        val newsList = ArrayList<UserArticle>()

                        for (documentChange in snapshot.documentChanges) {
                            val news = UserArticle()
                            news.id = documentChange.document.id
                            news.title = documentChange.document.get("title").toString()
                            news.detail = documentChange.document.get("detail").toString()
                            news.author = documentChange.document.get("author").toString()
                            news.publishedDate =
                                documentChange.document.get("publishedDate").toString()

                            when (documentChange.type) {
                                DocumentChange.Type.ADDED -> newsList.add(news)
                                DocumentChange.Type.MODIFIED -> {}
                                DocumentChange.Type.REMOVED -> newsList.remove(news)
                            }
                        }

                        userNewsLiveArticles.postValue(newsList)
                    }
                })
        }
    }

    //Update user's post from Firestore
    fun editUserPost(docId: String, title: String, detail: String){
        try{
            editPostTitle(docId, title)
            editPostDetail(docId, detail)
            editPostPublishedDate(docId)
        }
        catch(ex: Exception){
            Log.e("ERROR", "editUserPost(): Couldn't update user's posts")
        }
    }

    //Update user's post title
    private fun editPostTitle(docId: String, title: String){
        try{
            db.collection(COLLECTION_USERS)
                .document(loggedInUserID)
                .collection(COLLECTION_ARTICLE)
                .document(docId)
                .update("title", title)
        }
        catch (ex: Exception){
            Log.e("ERROR", "editPostTitle(): Couldn't update title")
        }
    }

//Update user's post detail
    private fun editPostDetail(docId: String, detail: String){
        try{
            db.collection(COLLECTION_USERS)
                .document(loggedInUserID)
                .collection(COLLECTION_ARTICLE)
                .document(docId)
                .update("detail", detail)
        }
        catch (ex: Exception){
            Log.e("ERROR", "editPostDetail(): Couldn't update detail")
        }
    }

//Update user's post published date
    private fun editPostPublishedDate(docId: String){
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        cal.set(year, month, day)

        try{
            db.collection(COLLECTION_USERS)
                .document(loggedInUserID)
                .collection(COLLECTION_ARTICLE)
                .document(docId)
                .update("publishedDate", cal.time.toString())
        }
        catch (ex: Exception){
            Log.e("ERROR", "editPostDetail(): Couldn't update published date")
        }
    }

    //Delete a post from Firestore with document id equals to the given docId
    fun deleteUserPost(docId: String) {
        try {
            db.collection(COLLECTION_USERS)
                .document(loggedInUserID)
                .collection(COLLECTION_ARTICLE)
                .document(docId)
                .delete()
        }
        catch (ex: Exception) {
            Log.e("ERROR", "deleteUserPost(): Couldn't delete a given post")
        }
    }
}


