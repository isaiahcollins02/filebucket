package com.isaiahvonrundstedt.bucket.utils.managers

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.res.ResourcesCompat
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.objects.core.LocalFile
import com.isaiahvonrundstedt.bucket.objects.core.Notification

object ItemManager {

    fun getLocalIcon(context: Context?, type: Int?): Drawable? {
        val drawableID: Int = when (type){
            LocalFile.file -> R.drawable.ic_vector_files
            LocalFile.directory -> R.drawable.ic_vector_folder
            else -> R.drawable.ic_vector_help
        }
        return ResourcesCompat.getDrawable(context?.resources!!, drawableID, null)
    }

    fun getFileIcon(context: Context?, type: Int?): Drawable? {
        val drawableID: Int = when (type) {
            File.typeDocument -> R.drawable.ic_object_document
            File.typeCode -> R.drawable.ic_object_code
            File.typePackage -> R.drawable.ic_object_update
            File.typeImage -> R.drawable.ic_object_photo
            File.typeAudio -> R.drawable.ic_object_music
            File.typeVideo -> R.drawable.ic_object_video
            else -> R.drawable.ic_object_generic
        }
        return ResourcesCompat.getDrawable(context?.resources!!, drawableID, null)
    }
    fun getFileType(context: Context?, type: Int?): String? {
        val resources = context?.resources!!
        return when (type){
            File.typeDocument -> resources.getString(R.string.file_type_document)
            File.typeCode -> resources.getString(R.string.file_type_code)
            File.typeImage -> resources.getString(R.string.file_type_photo)
            File.typeAudio -> resources.getString(R.string.file_type_music)
            File.typeVideo -> resources.getString(R.string.file_type_video)
            else -> resources.getString(R.string.file_type_unknown)
        }
    }
    fun obtainFileExtension(uri: Uri): Int {
        // Get the extension of the file
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        when (fileExtension.toString().toLowerCase()){
            "apk" -> return File.typePackage
            "png" -> return File.typeImage
            "jpg" -> return File.typeImage
            "bmp" -> return File.typeImage
            "pdf" -> return File.typeDocument
            "xls" -> return File.typeDocument
            "xlsx" -> return File.typeDocument
            "doc" -> return File.typeDocument
            "docx"  -> return File.typeDocument
            "ppt" -> return File.typeDocument
            "pptx" -> return File.typeDocument
            "html" -> return File.typeDocument
            "psd"  -> return File.typeDocument
            "ai" -> return File.typeDocument
            "c" -> return File.typeCode
            "cpp" -> return File.typeCode
            "kt" -> return File.typeCode
            "java" -> return File.typeCode
            "mp3" -> return File.typeAudio
            "ogg" -> return File.typeAudio
            "wav" -> return File.typeAudio
            "mp4" -> return File.typeVideo
            "mov" -> return File.typeVideo
            "mkv" -> return File.typeVideo
            "avi" -> return File.typeVideo
            "wmv" -> return File.typeVideo
            else -> return File.typeGeneric
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