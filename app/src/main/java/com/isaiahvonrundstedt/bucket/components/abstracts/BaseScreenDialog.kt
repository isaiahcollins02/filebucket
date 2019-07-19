package com.isaiahvonrundstedt.bucket.components.abstracts

import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.isaiahvonrundstedt.bucket.R

abstract class BaseScreenDialog: DialogFragment(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_ScreenDialog)
    }
    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setWindowAnimations(R.style.AppTheme_Slide)

    }

}