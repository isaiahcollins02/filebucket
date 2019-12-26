package com.isaiahvonrundstedt.bucket.features.core.file

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FileFactory(private var authorParams: String?) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FileViewModel(authorParams) as T
    }

}