package com.isaiahvonrundstedt.bucket.architecture.store

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.isaiahvonrundstedt.bucket.constants.Firebase
import com.isaiahvonrundstedt.bucket.objects.File

class FileRepository {

    private var lastVisible: DocumentSnapshot? = null
    private var mainQuery: Query? = null

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    fun fetch( onFetch: (List<File>)-> Unit) {
        mainQuery = firestore.collection(Firebase.FILES.string)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limit(25)

        if (lastVisible != null)
            mainQuery = mainQuery?.startAfter(lastVisible!!)

        mainQuery?.get()?.addOnSuccessListener { snapshots ->
            lastVisible = snapshots.documents[snapshots.size() - 1]

            onFetch(snapshots.map{
                val file = it.toObject(File::class.java).apply {
                    fileID = it.id
                }
                file
            })
        }
    }

    fun refresh(){
        lastVisible = null
    }

}