package com.isaiahvonrundstedt.bucket.adapters.filterable

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.support.VaultActivity
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.objects.core.Account
import com.isaiahvonrundstedt.bucket.service.UsageService
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager

class BoxesAdapter: RecyclerView.Adapter<BoxesAdapter.ViewHolder>() {

    private var itemList: ArrayList<Account> = ArrayList()

    fun setObservableItems(items: List<Account>){
        val callback = ItemCallback(itemList, items)
        val result = DiffUtil.calculateDiff(callback)

        itemList.clear()
        itemList.addAll(items)

        result.dispatchUpdatesTo(this)
    }

    inner class ItemCallback (private var oldItems: List<Account>, private var newItems: List<Account>): DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition].accountID == newItems[newItemPosition].accountID
        }

        override fun getOldListSize(): Int {
            return oldItems.size
        }

        override fun getNewListSize(): Int {
            return newItems.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val rowView: View = LayoutInflater.from(viewGroup.context).inflate(R.layout.layout_item_users, viewGroup, false)
        return ViewHolder(rowView)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(itemList[position])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val rootView: View = itemView.findViewById(R.id.rootView)
        val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        val titleView: TextView = itemView.findViewById(R.id.titleView)
        val subtitleView: TextView = itemView.findViewById(R.id.subtitleView)

        fun bind (currentUser: Account){
            val string: String = currentUser.firstName + " " + currentUser.lastName

            GlideApp.with(rootView)
                .load(currentUser.imageURL)
                .placeholder(R.drawable.ic_brand_user)
                .error(R.drawable.ic_brand_user)
                .centerCrop()
                .apply(RequestOptions().circleCrop())
                .into(iconView)

            rootView.setOnClickListener {
                it.context.startService(Intent(it.context, UsageService::class.java)
                    .setAction(UsageService.sendBoxUsage)
                    .putExtra(UsageService.extraObjectID, itemList[position].accountID))

                val intent = Intent(it.context, VaultActivity::class.java)
                intent.putExtra(Params.author, string)
                it.context.startActivity(intent)
            }
            titleView.text = DataManager.capitalizeEachWord(string)
            subtitleView.text = currentUser.email
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}