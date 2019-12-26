package com.isaiahvonrundstedt.bucket.features.usage

import com.google.firebase.Timestamp

data class Usage (var timestamp: Timestamp? = null, var objectID: String? = null, var type: Int = 0){
    companion object {
        const val fileType = 1
        const val boxType = 2
    }
}