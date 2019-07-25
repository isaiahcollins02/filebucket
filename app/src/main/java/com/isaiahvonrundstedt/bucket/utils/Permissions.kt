package com.isaiahvonrundstedt.bucket.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

class Permissions(var context: Context) {

    companion object {
        const val storageRequestCode = 2
        const val readRequestCode = 3
        const val writeRequestCode = 4
    }

    val readAccessGranted: Boolean
        get() {
            // For API 23 below, the permission is already granted upon installation
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            } else
                true
        }

    val writeAccessGranted: Boolean
        get() {
            // For API 23 below, the permission is already granted upon installation
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            } else
                true
        }

}