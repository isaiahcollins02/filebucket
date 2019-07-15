package com.isaiahvonrundstedt.bucket.adapters.support

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.support.VaultActivity
import com.isaiahvonrundstedt.bucket.activities.wrapper.FrameActivity
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.objects.core.Account
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.objects.experience.SearchResult
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager
import java.text.DecimalFormat

class SearchAdapter(private var itemList: List<SearchResult>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val rowView: View
        return if (viewType == viewTypeFile){
            rowView = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_files, viewGroup, false)
            FileViewHolder(rowView)
        } else {
            rowView = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_users, viewGroup, false)
            BoxViewHolder(rowView)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is FileViewHolder)
            viewHolder.bind(itemList[position])
        else if (viewHolder is BoxViewHolder)
            viewHolder.bind(itemList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return when (itemList[position].type){
            SearchResult.typeFile -> viewTypeFile
            SearchResult.typeBox -> viewTypeBox
            else -> viewTypeGeneric
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    companion object {
        private const val viewTypeGeneric = 0
        private const val viewTypeFile = 1
        private const val viewTypeBox = 2
    }

    inner class BoxViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val rootView: View = itemView.findViewById(R.id.rootView)
        private val titleView: TextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitleView)

        fun bind(searchResult: SearchResult){
            val bundle: Bundle? = searchResult.args
            val account: Account? = bundle?.getParcelable(SearchResult.argsAccount)

            val fullName = StringBuilder(account?.firstName!!).append(" ").append(account.lastName).toString()
            titleView.text = fullName
            subtitleView.text = account.email

            val intent = Intent(rootView.context, VaultActivity::class.java)
            intent.putExtra(Params.author, fullName)
            rootView.setOnClickListener { it.context.startActivity(intent) }
        }
    }

    inner class FileViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val rootView: View = itemView.findViewById(R.id.rootView)
        private val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        private val titleView: TextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitleView)
        private val sizeView: TextView = itemView.findViewById(R.id.sizeView)

        fun bind(searchResult: SearchResult){
            val bundle: Bundle? = searchResult.args
            val file: File? = bundle?.getParcelable(SearchResult.argsFile)

            titleView.text = file?.name ?: searchResult.displayName
            subtitleView.text = file?.author
            sizeView.text = DataManager.formatSize(itemView.context, file?.fileSize)
            iconView.setImageDrawable(ItemManager.getFileIcon(rootView.context, file?.fileType))

            val intent = Intent(itemView.context, FrameActivity::class.java)
            intent.putExtra(Params.viewType, FrameActivity.viewTypeDetails)
            intent.putExtra(Params.viewArgs, bundle)
            rootView.setOnClickListener { it.context.startActivity(intent) }
        }
    }
}