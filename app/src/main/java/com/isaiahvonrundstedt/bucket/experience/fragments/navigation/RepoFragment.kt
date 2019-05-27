package com.isaiahvonrundstedt.bucket.experience.fragments.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.adapters.RepoAdapter
import com.isaiahvonrundstedt.bucket.core.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.core.components.experience.decorations.RepoItemDecoration
import com.isaiahvonrundstedt.bucket.core.constants.Firebase
import com.isaiahvonrundstedt.bucket.core.interfaces.ActionBarInvoker
import com.isaiahvonrundstedt.bucket.core.objects.Account
import com.isaiahvonrundstedt.bucket.experience.activities.MainActivity

class RepoFragment: BaseFragment(), ActionBarInvoker {

    private val itemList: ArrayList<Account> = ArrayList()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var rootView: View
    private lateinit var adapter: RepoAdapter
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_nav_repo, container, false)
        setRootBackground(rootView)

        adapter = RepoAdapter(itemList)
        swipeRefreshContainer = rootView.findViewById(R.id.swipeRefreshContainer)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(RepoItemDecoration(container!!.context))
        recyclerView.adapter = adapter

        swipeRefreshContainer.setColorSchemeResources(
            R.color.colorIndicatorBlue,
            R.color.colorIndicatorGreen,
            R.color.colorIndicatorRed,
            R.color.colorIndicatorYellow
        )
        swipeRefreshContainer.setOnRefreshListener {
            adapter.removeAllData()
            onPopulate()
        }

        onPopulate()

        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (activity is MainActivity)
            (activity as MainActivity).setSearchListener(this)

    }

    private fun onPopulate(){
        val userReference = firestore.collection(Firebase.USERS.string)
        userReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                for (documentSnapshot in task.result!!){
                    val account: Account = documentSnapshot.toObject(Account::class.java)
                    account.accountID = documentSnapshot.id
                    if (account.accountID != firebaseAuth.currentUser?.uid)
                        itemList.add(account)
                    itemList.sort()
                    adapter.notifyDataSetChanged()
                }
                if (swipeRefreshContainer.isRefreshing)
                    swipeRefreshContainer.isRefreshing = false
            } else
                Snackbar.make(rootView, R.string.status_error_occurred, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onSearch(query: String?) {
        adapter.filter.filter(query)
    }

}