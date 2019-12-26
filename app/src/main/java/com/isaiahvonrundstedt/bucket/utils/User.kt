package com.isaiahvonrundstedt.bucket.utils

import android.content.Context
import android.content.SharedPreferences
import com.isaiahvonrundstedt.bucket.features.auth.Account

class User(var context: Context) {
    
    companion object {
        private const val storageKey = "userPreference"
        private const val idKey = "accountID"
        private const val firstNameKey = "firstName"
        private const val lastNameKey = "lastName"
        private const val emailKey = "emailKey"
        private const val imageURLKey = "imageURLKey"
    }

    private var sharedPreferences: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    var id: String?
        set(value){
            editor = context.getSharedPreferences(storageKey, Context.MODE_PRIVATE)?.edit()
            editor?.putString(idKey, value)
            editor?.apply()
        }
        get(){
            sharedPreferences = context.getSharedPreferences(storageKey, Context.MODE_PRIVATE)
            return sharedPreferences?.getString(idKey, null)
        }

    var firstName: String?
        set(value) {
            editor = context.getSharedPreferences(storageKey, Context.MODE_PRIVATE)?.edit()
            editor?.putString(firstNameKey, value)
            editor?.apply()
        }
        get() {
            sharedPreferences = context.getSharedPreferences(storageKey, Context.MODE_PRIVATE)
            return sharedPreferences?.getString(firstNameKey, null)
        }

    var lastName: String?
        set(value) {
            editor = context.getSharedPreferences(storageKey, Context.MODE_PRIVATE)?.edit()
            editor?.putString(lastNameKey, value)
            editor?.apply()
        }
        get() {
            sharedPreferences = context.getSharedPreferences(storageKey, Context.MODE_PRIVATE)
            return sharedPreferences?.getString(lastNameKey, null)
        }

    val fullName: String?
        get() {
            sharedPreferences = context.getSharedPreferences(storageKey, Context.MODE_PRIVATE)
            val firstName: String? = sharedPreferences?.getString(firstNameKey, null)
            val lastName: String? = sharedPreferences?.getString(lastNameKey, null)
            return "$firstName $lastName"
        }

    var email: String?
        set(value) {
            editor = context.getSharedPreferences(storageKey, Context.MODE_PRIVATE)?.edit()
            editor?.putString(emailKey, value)
            editor?.apply()
        }
        get() {
            sharedPreferences = context.getSharedPreferences(storageKey, Context.MODE_PRIVATE)
            return sharedPreferences?.getString(emailKey, null)
        }

    var imageURL: String?
        set(value) {
            editor = context.getSharedPreferences(storageKey, Context.MODE_PRIVATE)?.edit()
            editor?.putString(imageURLKey, value)
            editor?.apply()
        }
        get() {
            sharedPreferences = context.getSharedPreferences(storageKey, Context.MODE_PRIVATE)
            return sharedPreferences?.getString(imageURLKey, null)
        }

    fun fetch(onFetch: (Account) -> Unit){
        val user = Account().apply {
            sharedPreferences = context.getSharedPreferences(storageKey, Context.MODE_PRIVATE)
            firstName = sharedPreferences?.getString("firstName", null)
            lastName = sharedPreferences?.getString("lastName", null)
            email = sharedPreferences?.getString("email", null)
            imageURL = sharedPreferences?.getString("imageURL", null)
        }
        onFetch(user)
    }

    fun save(account: Account){
        firstName = account.firstName
        lastName = account.lastName
        email = account.email
        imageURL = account.imageURL
    }
}