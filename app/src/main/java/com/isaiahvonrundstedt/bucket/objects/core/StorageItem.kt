package com.isaiahvonrundstedt.bucket.objects.core

import android.net.Uri
import android.os.Parcelable
import android.webkit.MimeTypeMap
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.firebase.Timestamp
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.utils.converters.TimestampConverter
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "collections")
@Parcelize
data class StorageItem @JvmOverloads constructor(
        @PrimaryKey
        var id: String = "",
        var name: String? = null,
        var author: String? = null,
        var type: Int = typeGeneric,
        var size: Long? = null,
        var args: String? = null,
    @TypeConverters(TimestampConverter::class)
        var timestamp: Timestamp? = null
    ): Comparable<StorageItem>, Parcelable {

    override fun compareTo(other: StorageItem): Int {
        return this.id.compareTo(other.id)
    }

    companion object {
        const val typeGeneric = 0
        const val typeDirectory = 1
        const val typeDocument = 2
        const val typeImage = 3
        const val typeAudio = 4
        const val typeVideo = 5
        const val typeCode = 6
        const val typePackage = 7

        fun obtainIconID(type: Int?): Int {
            return when (type){
                typeDirectory -> R.drawable.ic_folder
                typeDocument -> R.drawable.ic_file
                typeImage -> R.drawable.ic_image
                typeAudio -> R.drawable.ic_headset
                typeVideo -> R.drawable.ic_video_camera
                typeCode -> R.drawable.ic_coding
                typePackage -> R.drawable.ic_package
                else -> R.drawable.ic_file
            }
        }

        fun obtainColorID(type: Int?): Int {
            return when (type){
                typeDirectory -> R.color.colorIconOrange
                typeImage -> R.color.colorIconBlue
                typeCode -> R.color.colorIconSea
                typeAudio -> R.color.colorIconPurple
                typeVideo -> R.color.colorIconRed
                typePackage -> R.color.colorIconTeal
                typeDocument-> R.color.colorIconMagenta
                else -> R.color.colorIconYellow
            }
        }

        fun determineExtension(uri: Uri): Int {
            // Get the extension of the typeGeneric
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            when (fileExtension.toString().toLowerCase()){
                "apk" -> return typePackage
                "png" -> return typeImage
                "jpg" -> return typeImage
                "jpeg" -> return typeImage
                "bmp" -> return typeImage
                "pdf" -> return typeDocument
                "xls" -> return typeDocument
                "xlsx" -> return typeDocument
                "doc" -> return typeDocument
                "docx"  -> return typeDocument
                "ppt" -> return typeDocument
                "pptx" -> return typeDocument
                "html" -> return typeDocument
                "psd"  -> return typeDocument
                "ai" -> return typeDocument
                "c" -> return typeCode
                "cpp" -> return typeCode
                "kt" -> return typeCode
                "java" -> return typeCode
                "mp3" -> return typeAudio
                "ogg" -> return typeAudio
                "wav" -> return typeAudio
                "mp4" -> return typeVideo
                "mov" -> return typeVideo
                "mkv" -> return typeVideo
                "avi" -> return typeVideo
                "wmv" -> return typeVideo
                else -> return typeGeneric
            }
        }
        fun obtainItemTypeID(type: Int?): Int {
            return when (type) {
                typeDirectory -> R.string.file_type_directory
                typeCode -> R.string.file_type_code
                typeVideo -> R.string.file_type_video
                typeImage -> R.string.file_type_photo
                typeAudio -> R.string.file_type_music
                typeDocument -> R.string.file_type_document
                typePackage -> R.string.file_type_package
                else -> R.string.file_type_unknown
            }
        }
    }

}