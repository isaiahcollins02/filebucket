package com.isaiahvonrundstedt.bucket.interfaces

interface TransferListener {

    fun onDownloadQueued(downloadID: Long)

}