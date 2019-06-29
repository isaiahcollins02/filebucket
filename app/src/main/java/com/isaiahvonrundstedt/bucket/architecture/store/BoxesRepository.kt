package com.isaiahvonrundstedt.bucket.architecture.store

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.isaiahvonrundstedt.bucket.constants.Firebase
import com.isaiahvonrundstedt.bucket.objects.User

class BoxesRepository {

    private var lastVisible: DocumentSnapshot? = null
    private var mainQuery: Query? = null

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    fun fetch( onFetch: (List<User>)-> Unit) {
        mainQuery = firestore.collection(Firebase.USERS.string)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limit(25)

        if (lastVisible != null)
            mainQuery = mainQuery?.startAfter(lastVisible!!)

        mainQuery?.get()?.addOnSuccessListener { snapshots ->
            //lastVisible = snapshots.documents[snapshots.size() - 1]

            onFetch(snapshots.map{
                val account = it.toObject(User::class.java).apply {
                    accountID = it.id
                }
                account
            })
        }
    }

    fun refresh(){
        lastVisible = null
    }

}