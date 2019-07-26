package com.isaiahvonrundstedt.bucket.utils.managers

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.objects.core.File
import com.isaiahvonrundstedt.bucket.objects.core.LocalFile
import com.isaiahvonrundstedt.bucket.objects.core.Notification

object ItemManager {

    fun obtainFileIconRes(type: Int?): Int {
        return when (type){
            File.typeCode -> R.drawable.ic_coding
            File.typeAudio -> R.drawable.ic_headset
            File.typeVideo -> R.drawable.ic_video_camera
            File.typeImage -> R.drawable.ic_gallery
            File.typeDocument -> R.drawable.ic_file
            else -> R.drawable.ic_file
        }
    }
    fun obtainLocalIconRes(type: Int?): Int {
        return if (type == LocalFile.file) R.drawable.ic_file else R.drawable.ic_folder
    }
    fun obtainFileType(type: Int?): Int {
        return when (type) {
            File.typeCode -> R.string.file_type_code
            File.typeVideo -> R.string.file_type_video
            File.typeImage -> R.string.file_type_photo
            File.typeAudio -> R.string.file_type_music
            File.typeDocument -> R.string.file_type_document
            else -> R.string.file_type_unknown
        }
    }

    fun getLocalIcon(context: Context?, type: Int?): Drawable? {
        val drawableID: Int = if (type == LocalFile.file) R.drawable.ic_file else R.drawable.ic_folder
        val colorID: Int = if (type == LocalFile.file) R.color.colorIconBlue else R.color.colorIconOrange
        val drawable: Drawable? = ResourcesCompat.getDrawable(context?.resources!!, drawableID, null)
        drawable?.setColorFilter(ContextCompat.getColor(context, colorID), PorterDuff.Mode.SRC_ATOP)
        return drawable
    }

    fun getFileColor(type: Int?): Int {
        return when (type){
            File.typeDocument -> R.color.colorIconBlue
            File.typeAudio -> R.color.colorIconRed
            File.typeImage -> R.color.colorIconMagenta
            File.typeVideo -> R.color.colorIconYellow
            File.typeCode -> R.color.colorIconTeal
            File.typePackage -> R.color.colorIconPurple
            else -> R.color.colorIconYellow
        }
    }
    fun getLocalColor(type: Int?): Int {
        return if (type == LocalFile.file) R.color.colorIconSeaBlue else R.color.colorIconOrange
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