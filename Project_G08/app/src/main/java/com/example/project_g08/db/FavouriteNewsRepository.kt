package com.example.project_g08.db

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import com.example.project_g08.models.FavouriteNews
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FavouriteNewsRepository(private val context: Context) {
    private val db = Firebase.firestore
    private val sharedPreferences = context.getSharedPreferences("USER_PREFERENCE", Context.MODE_PRIVATE)
    private var COLLECTION_USERS = "users"
    private var COLLECTION_FAVOURITE = "favourites"
    private var loggedInUserID = ""
    var userFavouriteNewsLive = MutableLiveData<List<FavouriteNews>>()

    init {
        if (sharedPreferences.contains("USER_DOC_ID")) {
            loggedInUserID = sharedPreferences.getString("USER_DOC_ID", "").toString()
            Log.d("Favorite", "init User: $loggedInUserID")
        }
    }

    //Add a news into "favorites" sub-collection in the current user document
    fun addFavouriteNews(newFavourite: FavouriteNews) {
        loggedInUserID = sharedPreferences.getString("USER_DOC_ID", "").toString()
        Log.d("Favorite", "addFavouriteNews(): $loggedInUserID")

        db.collection(COLLECTION_USERS).document(loggedInUserID)
                .collection(COLLECTION_FAVOURITE)
                .add(newFavourite)
    }

    //Get all user's favorite news from the "favorites" sub-collection
    fun getFavouriteNews() {
        try {
            if (loggedInUserID.isNotBlank()) {
                db.collection(COLLECTION_USERS)
                    .document(loggedInUserID)
                    .collection(COLLECTION_FAVOURITE)
                    .addSnapshotListener(EventListener { snapshot, error ->
                        if (error != null) {
                            return@EventListener
                        }

                        if (snapshot != null) {
                            val newsList = ArrayList<FavouriteNews>()

                            for (documentChange in snapshot.documentChanges) {
                                val news = FavouriteNews()

                                news.title = documentChange.document.get("title").toString()
                                news.id = documentChange.document.id
                                news.pic = documentChange.document.get("pic").toString()
                                news.link = documentChange.document.get("link").toString()

                                when (documentChange.type) {
                                    DocumentChange.Type.ADDED -> newsList.add(news)
                                    DocumentChange.Type.MODIFIED -> {}
                                    DocumentChange.Type.REMOVED -> newsList.remove(news)
                                }
                            }

                            userFavouriteNewsLive.postValue(newsList)
                        }
                    })
            }
        }
        catch (ex: Exception) {
            Log.e("ERROR", "getFavouriteNews(): Couldn't get news from favorite")
        }
    }

    //Delete a favorite news from Firestore with document id equals to the given docId
    fun deleteFromFavorite(docId: String) {
        try {
            db.collection(COLLECTION_USERS)
                .document(loggedInUserID)
                .collection(COLLECTION_FAVOURITE)
                .document(docId)
                .delete()
        }
        catch (ex: Exception) {
            Log.e("ERROR", "deleteFromFavorite(): Couldn't delete a given news from favorite")
        }
    }
}