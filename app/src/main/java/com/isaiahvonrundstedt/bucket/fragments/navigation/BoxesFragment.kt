package com.isaiahvonrundstedt.bucket.fragments.navigation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.MainActivity
import com.isaiahvonrundstedt.bucket.adapters.BoxesAdapter
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.constants.Firebase
import com.isaiahvonrundstedt.bucket.interfaces.ScreenAction
import com.isaiahvonrundstedt.bucket.objects.User

class BoxesFragment: BaseFragment(), ScreenAction.Search {

    private val itemList: ArrayList<User> = ArrayList()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var rootView: View
    private lateinit var adapter: BoxesAdapter
    private lateinit var swipeRefreshContainer: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_boxes, container, false)

        adapter = BoxesAdapter(itemList)
        swipeRefreshContainer = rootView.findViewById(R.id.swipeRefreshContainer)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(context!!, DividerItemDecoration.VERTICAL))
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

    private fun onPopulate(){
        val userReference = firestore.collection(Firebase.USERS.string)
        userReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                for (documentSnapshot in task.result!!){
                    val user: User = documentSnapshot.toObject(User::class.java)
                    user.accountID = documentSnapshot.id
                    if (user.accountID != firebaseAuth.currentUser?.uid)
                        itemList.add(user)
                    itemList.sort()
                    adapter.notifyDataSetChanged()
                }
                if (swipeRefreshContainer.isRefreshing)
                    swipeRefreshContainer.isRefreshing = false
            } else
                Snackbar.make(rootView, R.string.status_error_occurred, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (activity is MainActivity)
            (activity as MainActivity).initializeSearch(this)
    }

    override fun onSearch(searchQuery: String?) {
        adapter.filter.filter(searchQuery)
    }

}