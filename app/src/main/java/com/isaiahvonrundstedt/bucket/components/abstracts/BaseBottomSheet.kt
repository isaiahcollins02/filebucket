package com.isaiahvonrundstedt.bucket.components.abstracts

import android.app.Dialog
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.isaiahvonrundstedt.bucket.R

abstract class BaseBottomSheet: BottomSheetDialogFragment(){

    override fun getTheme(): Int = R.style.AppTheme_BottomSheet

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)

}