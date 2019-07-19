package com.isaiahvonrundstedt.bucket.components.abstracts

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.isaiahvonrundstedt.bucket.objects.core.File

abstract class BaseAdapter(context: Context, manager: FragmentManager, request: RequestManager): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var itemList: ArrayList<File> = ArrayList()

    init {

    }

    internal fun showFileDetails(file: File?){

    }
}