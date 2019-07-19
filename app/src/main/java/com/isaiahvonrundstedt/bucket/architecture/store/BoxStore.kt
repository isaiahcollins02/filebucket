package com.isaiahvonrundstedt.bucket.architecture.store

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.objects.core.Account

class BoxStore {

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    private var resultSize: Int = 0
    private var currentUserID: String? = null
    private var initialQueryBatch: Query? = firestore.collection(Firestore.files).limit(15)
    private var nextQueryBatch: Query? = null

    init {
        currentUserID = firebaseAuth.currentUser?.uid
    }

    fun fetch( onFetch: (List<Account>)-> Unit) {
        val mainQuery = if (nextQueryBatch == null) initialQueryBatch else nextQueryBatch

        mainQuery?.get()
            ?.addOnSuccessListener { snapshots ->
                if (snapshots.size() > 0){
                    val lastVisible = snapshots.documents[snapshots.size() -1 ]
                    resultSize = snapshots.size()

                    nextQueryBatch = firestore.collection(Firestore.users)
                        .startAfter(lastVisible)
                        .limit(15)

                    val accountItems: ArrayList<Account> = ArrayList()
                    for (document: DocumentSnapshot in snapshots){
                        val account: Account = document.toObject(Account::class.java) as Account
                        if (account.accountID == currentUserID)
                            accountItems.add(account)
                    }
                    onFetch(accountItems)
                }
            }
    }

    fun size(): Int = resultSize

    fun refresh(){
        nextQueryBatch = null
    }

}