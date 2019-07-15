package com.isaiahvonrundstedt.bucket.fragments.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseBottomSheet
import com.isaiahvonrundstedt.bucket.objects.experience.SearchResult

class SearchBottomSheet: BaseBottomSheet() {

    private val itemList: List<SearchResult> = ArrayList()
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_sheet_search, container, false)
    }

    override fun onStart() {
        super.onStart()
    }

}