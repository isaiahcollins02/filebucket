package com.isaiahvonrundstedt.bucket.objects.experience

import android.os.Bundle

data class SearchResult (var id: String? = null, var displayName: String? = null,
                         var args: Bundle? = null, var type: Int? = typeGeneric) {

    companion object {
        const val typeGeneric = 0
        const val typeFile = 1
        const val typeBox = 2

        const val argsFile = "file"
        const val argsAccount = "account"
    }

}