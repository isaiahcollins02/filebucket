package com.isaiahvonrundstedt.bucket.components.abstracts

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.isaiahvonrundstedt.bucket.R
import kotlinx.android.synthetic.main.layout_appbar_flat.*

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { dismiss() }
        toolbar.navigationIcon = ResourcesCompat.getDrawable(context?.resources!!, R.drawable.ic_vector_error, null)
    }

    internal fun setToolbarTitle(@StringRes stringRes: Int){
        toolbarTitle.text = context?.getString(stringRes)
    }

    internal fun setToolbarTitle(string: String?){
        toolbarTitle.text = string
    }

}