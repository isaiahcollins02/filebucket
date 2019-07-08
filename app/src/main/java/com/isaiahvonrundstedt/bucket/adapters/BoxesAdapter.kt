package com.isaiahvonrundstedt.bucket.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.support.VaultActivity
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.constants.Parameters
import com.isaiahvonrundstedt.bucket.objects.Account
import com.isaiahvonrundstedt.bucket.service.UsageService
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager

class BoxesAdapter(private var itemList: ArrayList<Account>): RecyclerView.Adapter<BoxesAdapter.ViewHolder>(),
    Filterable {

    private var filterList: ArrayList<Account> = itemList
    private var filter: AccountFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rootView: View = LayoutInflater.from(parent.context).inflate(R.layout.layout_item_users, parent, false)
        return ViewHolder(rootView)
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentUser: Account = filterList[position]
        val string: String = currentUser.firstName + " " + currentUser.lastName

        GlideApp.with(holder.rootView)
            .load(currentUser.imageURL)
            .placeholder(R.drawable.ic_brand_user)
            .error(R.drawable.ic_brand_user)
            .centerCrop()
            .apply(RequestOptions().circleCrop())
            .into(holder.iconView)

        holder.rootView.setOnClickListener {
            it.context.startService(Intent(it.context, UsageService::class.java)
                .setAction(UsageService.sendBoxUsage)
                .putExtra(UsageService.extraObjectID, filterList[position].accountID))

            val intent = Intent(it.context, VaultActivity::class.java)
            intent.putExtra(Parameters.AUTHOR.string, string)
            it.context.startActivity(intent)
        }
        holder.titleView.text = DataManager.capitalizeEachWord(string)
        holder.subtitleView.text = currentUser.email
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val rootView: View = itemView.findViewById(R.id.rootView)
        val iconView: AppCompatImageView = itemView.findViewById(R.id.iconView)
        val titleView: TextView = itemView.findViewById(R.id.titleView)
        val subtitleView: TextView = itemView.findViewById(R.id.subtitleView)
    }

    override fun getFilter(): Filter {
        if (filter == null)
            filter = AccountFilter()
        return filter as AccountFilter
    }

    inner class AccountFilter: Filter(){

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val searchTerm: String = constraint.toString().toLowerCase()
            val filterResults = FilterResults()
            val originalList = filterList
            val listCount = filterList.size

            var resultList: ArrayList<Account> = ArrayList(listCount)
            var filterableString: String

            if (searchTerm.isNotBlank() && searchTerm.isNotEmpty()) {
                for (i in 0 until listCount){
                    val string = originalList[i].firstName + " " + originalList[i].lastName
                    filterableString = string
                    if (filterableString.toLowerCase().contains(searchTerm))
                        resultList.add(originalList[i])
                }
            } else
                resultList = itemList

            filterResults.values = resultList
            filterResults.count = listCount

            return filterResults
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            @Suppress("UNCHECKED_CAST")
            filterList = results?.values as ArrayList<Account>
            notifyDataSetChanged()
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun removeAllData(){
        if (itemList.size > 0 && filterList.size > 0) {
            itemList.clear()
            filterList.clear()
            notifyDataSetChanged()
        }
    }

}