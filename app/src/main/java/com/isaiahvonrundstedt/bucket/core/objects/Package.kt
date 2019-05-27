package com.isaiahvonrundstedt.bucket.core.objects

data class Package (var exists: Boolean = false){
    var version: Float = 0.0F
    var downloadURL: String? = null
}