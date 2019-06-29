package com.isaiahvonrundstedt.bucket.utils

import android.content.Context
import android.content.SharedPreferences
import com.isaiahvonrundstedt.bucket.objects.User

class Account(var context: Context) {

    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    var firstName: String?
        set(value) {
            editor = context.getSharedPreferences("userPreference", Context.MODE_PRIVATE)?.edit()
            editor?.putString("firstName", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = context.getSharedPreferences("userPreference", Context.MODE_PRIVATE)
            return sharedPreferences?.getString("firstName", null)
        }

    var lastName: String?
        set(value) {
            editor = context.getSharedPreferences("userPreference", Context.MODE_PRIVATE)?.edit()
            editor?.putString("lastName", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = context.getSharedPreferences("userPreference", Context.MODE_PRIVATE)
            return sharedPreferences?.getString("lastName", null)
        }

    val fullName: String?
        get() {
            sharedPreferences = context.getSharedPreferences("userPreference", Context.MODE_PRIVATE)
            val firstName: String? = sharedPreferences?.getString("firstName", null)
            val lastName: String? = sharedPreferences?.getString("lastName", null)
            return "$firstName $lastName"
        }

    var email: String?
        set(value) {
            editor = context.getSharedPreferences("userPreference", Context.MODE_PRIVATE)?.edit()
            editor?.putString("email", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = context.getSharedPreferences("userPreference", Context.MODE_PRIVATE)
            return sharedPreferences?.getString("email", null)
        }

    var imageURL: String?
        set(value) {
            editor = context.getSharedPreferences("userPreference", Context.MODE_PRIVATE)?.edit()
            editor?.putString("imageURL", value)
            editor?.apply()
        }
        get() {
            sharedPreferences = context.getSharedPreferences("userPreference", Context.MODE_PRIVATE)
            return sharedPreferences?.getString("imageURL", null)
        }

    fun fetch(onFetch: (User)-> Unit){
        val user = User().apply {
            sharedPreferences = context.getSharedPreferences("userPreference", Context.MODE_PRIVATE)
            firstName = sharedPreferences?.getString("firstName", null)
            lastName = sharedPreferences?.getString("lastName", null)
            email = sharedPreferences?.getString("email", null)
            imageURL = sharedPreferences?.getString("imageURL", null)
        }
        onFetch(user)
    }
    fun save(user: User){
        firstName = user.firstName
        lastName = user.lastName
        email = user.email
        imageURL = user.imageURL
    }
}