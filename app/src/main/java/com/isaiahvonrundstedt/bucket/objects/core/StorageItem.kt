package com.isaiahvonrundstedt.bucket.objects.core

import android.content.Context
import android.net.Uri
import android.os.Parcelable
import android.text.format.DateUtils
import android.webkit.MimeTypeMap
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.firebase.Timestamp
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.utils.converters.TimestampConverter
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "collections")
@Parcelize
data class StorageItem @JvmOverloads constructor(
        @PrimaryKey
        var id: String = "",
        var name: String? = null,
        var author: String? = null,
        var type: Int = typeGeneric,
        var size: Long = 0L,
        var args: String? = null,
        @TypeConverters(TimestampConverter::class)
        var timestamp: Timestamp? = null
    ): Comparable<StorageItem>, Parcelable {

    override fun compareTo(other: StorageItem): Int {
        return this.id.compareTo(other.id)
    }

    fun formatTimestamp(context: Context): String? {
        val date: Date? = timestamp?.toDate()
        return if (date != null) {
            val milliseconds: Long = date.time
            val isToday = DateUtils.isToday(milliseconds)

            return when {
                !isToday -> SimpleDateFormat("h:mm a, MMM d", Locale.getDefault()).format(date)
                else -> String.format(context.getString(R.string.file_timestamp_today),
                    SimpleDateFormat("h:mm a", Locale.getDefault()).format(date))
            }
        } else null
    }

    fun formatSize(context: Context): String {
        val formattedSize = (size / 1024) / 1024
        // Check if the size is below 1MB, then formatSize it
        // to kilobytes
        return if (formattedSize < 1)
            String.format(context.getString(R.string.file_size_kilobytes), size / 1024)
        else String.format(context.getString(R.string.file_size_megabytes), formattedSize)
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
        const val typeDatabase = 8

        fun obtainIconID(type: Int?): Int {
            return when (type){
                typeDirectory -> R.drawable.ic_folder
                typeDocument -> R.drawable.ic_file
                typeImage -> R.drawable.ic_image
                typeAudio -> R.drawable.ic_headset
                typeVideo -> R.drawable.ic_video_camera
                typeCode -> R.drawable.ic_coding
                typePackage -> R.drawable.ic_package
                typeDatabase -> R.drawable.ic_database
                else -> R.drawable.ic_file
            }
        }

        fun obtainColorID(type: Int?): Int {
            return when (type) {
                typeDirectory -> R.color.colorIconOrange
                typeImage -> R.color.colorIconBlue
                typeCode -> R.color.colorIconSea
                typeAudio -> R.color.colorIconPurple
                typeVideo -> R.color.colorIconRed
                typePackage -> R.color.colorIconTeal
                typeDocument -> R.color.colorIconMagenta
                typeDatabase -> R.color.colorIconOrange
                else -> R.color.colorIconYellow
            }
        }

        fun determineExtension(uri: Uri): Int {
            // Get the extension of the typeGeneric
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            when (fileExtension.toString().toLowerCase(Locale.getDefault())){
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
                "accdb" -> return typeDatabase
                "mdb" -> return typeDatabase
                "frm" -> return typeDatabase
                "sql" -> return typeDocument
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
                typeDatabase -> R.string.file_type_database
                else -> R.string.file_type_unknown
            }
        }

    }

}