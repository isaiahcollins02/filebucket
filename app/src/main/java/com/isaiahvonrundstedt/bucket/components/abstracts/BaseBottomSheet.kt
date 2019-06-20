package com.isaiahvonrundstedt.bucket.components.abstracts

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.utils.Preferences

open class BaseBottomSheet: BottomSheetDialogFragment(){

    override fun getTheme(): Int {
        return when (Preferences(requireContext()).theme) {
            Preferences.THEME_DARK -> R.style.AppTheme_BottomSheet_Dark
            Preferences.THEME_AMOLED -> R.style.AppTheme_BottomSheet_AMOLED
            else -> R.style.AppTheme_BottomSheet_Light
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

}