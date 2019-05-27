package com.isaiahvonrundstedt.bucket.core.utils.managers

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.res.ResourcesCompat
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.objects.File

object ItemManager {

    const val CATEGORY_ALL = 0
    const val CATEGORY_DOCUMENTS = 1
    const val CATEGORY_CODES = 2
    const val CATEGORY_MEDIA = 3
    const val CATEGORY_REMOVE = 4

    fun getFileCategory(fileType: Int): Int{
        return when (fileType){
            File.TYPE_GENERIC -> CATEGORY_REMOVE
            File.TYPE_VIDEO -> CATEGORY_MEDIA
            File.TYPE_IMAGE -> CATEGORY_MEDIA
            File.TYPE_AUDIO -> CATEGORY_MEDIA
            File.TYPE_CODE -> CATEGORY_CODES
            File.TYPE_DOCUMENT -> CATEGORY_DOCUMENTS
            File.TYPE_PACKAGE -> CATEGORY_REMOVE
            else -> CATEGORY_DOCUMENTS
        }
    }
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
}