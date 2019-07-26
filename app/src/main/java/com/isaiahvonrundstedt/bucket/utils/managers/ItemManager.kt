package com.isaiahvonrundstedt.bucket.utils.managers

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.objects.core.Notification
import com.isaiahvonrundstedt.bucket.objects.core.StorageItem

object ItemManager {

    fun getNotificationIcon(context: Context?, type: Int?): Drawable? {
        val drawableID = when (type){
            Notification.typeGeneric -> R.drawable.ic_bell
            Notification.typeNewFile -> R.drawable.ic_balloons
            Notification.typePackage -> R.drawable.ic_download
            Notification.typeFetched -> R.drawable.ic_download
            Notification.typeTransfered -> R.drawable.ic_upload
            else -> null
        }
        return ResourcesCompat.getDrawable(context?.resources!!, drawableID!!, null)
    }

}