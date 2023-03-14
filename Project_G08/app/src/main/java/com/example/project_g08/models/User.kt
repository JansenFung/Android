package com.example.project_g08.models

import java.util.*

class User(var id: String = UUID.randomUUID().toString(),
           var email:String="",
           var password: String="",
           var username:String="") {

    override fun toString(): String {
        return "User(email='$email', password='$password', username='$username')"
    }
}