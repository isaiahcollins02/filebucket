package com.isaiahvonrundstedt.bucket.architecture.store

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.objects.core.File

class CoreStore {

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    private var resultSize: Int = 0
    private var initialQueryBatch: Query? = firestore.collection(Firestore.files).limit(15)
    private var nextQueryBatch: Query? = null

    fun fetch( onFetch: (List<File>) -> Unit ){
        val mainQuery = if (nextQueryBatch == null) initialQueryBatch else nextQueryBatch

        mainQuery?.get()
            ?.addOnSuccessListener { snapshots ->
                if (snapshots.size() > 0){
                    val lastVisible = snapshots.documents[snapshots.size() - 1]
                    resultSize = snapshots.size()

                    nextQueryBatch = firestore.collection(Firestore.files)
                        .startAfter(lastVisible)
                        .limit(15)

                    onFetch (snapshots.map { val file: File = it.toObject(File::class.java).apply { fileID = it.id }; file })
                }
            }
    }

    fun size(): Int = resultSize

    fun refresh(){
        nextQueryBatch = null
    }

}