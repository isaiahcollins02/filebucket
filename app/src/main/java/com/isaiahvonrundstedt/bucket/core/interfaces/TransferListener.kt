package com.isaiahvonrundstedt.bucket.core.interfaces

interface TransferListener {

    fun onDownloadQueued(downloadID: Long)

}