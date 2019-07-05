package com.isaiahvonrundstedt.bucket.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.objects.File
import com.isaiahvonrundstedt.bucket.utils.Preferences
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager
import com.isaiahvonrundstedt.bucket.utils.managers.ItemManager
import java.text.DecimalFormat

class RelatedAdapter(options: FirestoreRecyclerOptions<File>)
    : FirestoreRecyclerAdapter<File, RelatedAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val rootView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_files, viewGroup, false)
        return ViewHolder(rootView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int, file: File) {
        viewHolder.bind(file)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val rootView: View = itemView.findViewById(R.id.rootView)
        private val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        private val titleView: TextView = itemView.findViewById(R.id.titleView)
        private val subtitleView: TextView = itemView.findViewById(R.id.subtitleView)
        private val sizeView: TextView = itemView.findViewById(R.id.sizeView)

        fun bind(file: File){
            titleView.text = file.name
            subtitleView.text =
                when (Preferences(itemView.context).metadata){
                    Preferences.metadataTimestamp -> DataManager.formatTimestamp(itemView.context, file.timestamp)
                    Preferences.metadataAuthor -> file.author
                    else -> null
                }
            val decimalFormat = DecimalFormat("#.##")
            sizeView.text = String.format(itemView.resources.getString(R.string.detail_file_size), decimalFormat.format(file.fileSize))
            iconView.setImageDrawable(ItemManager.getFileIcon(itemView.context, file.fileType))
        }
    }

}