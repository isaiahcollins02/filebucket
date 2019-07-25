package com.isaiahvonrundstedt.bucket.architecture.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.isaiahvonrundstedt.bucket.architecture.viewmodel.network.FileViewModel

class FileFactory(private var authorParams: String?) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FileViewModel(authorParams) as T
    }

}