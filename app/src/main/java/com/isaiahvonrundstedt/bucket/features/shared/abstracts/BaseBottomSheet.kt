package com.isaiahvonrundstedt.bucket.features.shared.abstracts

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.isaiahvonrundstedt.bucket.R
import timber.log.Timber

abstract class BaseBottomSheet: BottomSheetDialogFragment(){

    override fun getTheme(): Int = R.style.AppTheme_BottomSheet

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

    fun invoke(fragmentManager: FragmentManager){
        if (!this.isAdded)
            show(fragmentManager, "bottomSheetTag")
        else Timber.e("bottomSheetTag is already shown")
    }

}