package com.isaiahvonrundstedt.bucket.utils.managers

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.res.ResourcesCompat
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.objects.core.Notification

object ItemManager {

    fun getFileIcon(context: Context?, type: Int?): Drawable? {
        val drawableID: Int = when (type) {
            File.TYPE_DOCUMENT -> R.drawable.ic_object_document
            File.TYPE_CODE -> R.drawable.ic_object_code
            File.TYPE_PACKAGE -> R.drawable.ic_object_update
            File.TYPE_IMAGE -> R.drawable.ic_object_photo
            File.TYPE_AUDIO -> R.drawable.ic_object_music
            File.TYPE_VIDEO -> R.drawable.ic_object_video
            else -> R.drawable.ic_object_generic
        }
        return ResourcesCompat.getDrawable(context?.resources!!, drawableID, null)
    }
    fun getFileType(context: Context?, type: Int?): String? {
        val resources = context?.resources!!
        return when (type){
            File.TYPE_DOCUMENT -> resources.getString(R.string.file_type_document)
            File.TYPE_CODE -> resources.getString(R.string.file_type_code)
            File.TYPE_IMAGE -> resources.getString(R.string.file_type_photo)
            File.TYPE_AUDIO -> resources.getString(R.string.file_type_music)
            File.TYPE_VIDEO -> resources.getString(R.string.file_type_video)
            else -> resources.getString(R.string.file_type_unknown)
        }
    }
    fun obtainFileExtension(uri: Uri): Int {
        // Get the extension of the file
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        when (fileExtension.toString().toLowerCase()){
            "apk" -> return File.TYPE_PACKAGE
            "png" -> return File.TYPE_IMAGE
            "jpg" -> return File.TYPE_IMAGE
            "bmp" -> return File.TYPE_IMAGE
            "pdf" -> return File.TYPE_DOCUMENT
            "xls" -> return File.TYPE_DOCUMENT
            "xlsx" -> return File.TYPE_DOCUMENT
            "doc" -> return File.TYPE_DOCUMENT
            "docx"  -> return File.TYPE_DOCUMENT
            "ppt" -> return File.TYPE_DOCUMENT
            "pptx" -> return File.TYPE_DOCUMENT
            "html" -> return File.TYPE_DOCUMENT
            "psd"  -> return File.TYPE_DOCUMENT
            "ai" -> return File.TYPE_DOCUMENT
            "c" -> return File.TYPE_CODE
            "cpp" -> return File.TYPE_CODE
            "kt" -> return File.TYPE_CODE
            "java" -> return File.TYPE_CODE
            "mp3" -> return File.TYPE_AUDIO
            "ogg" -> return File.TYPE_AUDIO
            "wav" -> return File.TYPE_AUDIO
            "mp4" -> return File.TYPE_VIDEO
            "mov" -> return File.TYPE_VIDEO
            "mkv" -> return File.TYPE_VIDEO
            "avi" -> return File.TYPE_VIDEO
            "wmv" -> return File.TYPE_VIDEO
            else -> return File.TYPE_GENERIC
        }
    }

    fun getNotificationIcon(context: Context?, type: Int?): Drawable? {
        val drawableID = when (type){
            Notification.typeGeneric -> R.drawable.ic_vector_notifications
            Notification.typeNewFile -> R.drawable.ic_vector_new
            Notification.typePackage -> R.drawable.ic_vector_update
            Notification.typeFetched -> R.drawable.ic_vector_download
            Notification.typeTransfered -> R.drawable.ic_vector_upload
            else -> null
        }
        return ResourcesCompat.getDrawable(context?.resources!!, drawableID!!, null)
    }

}