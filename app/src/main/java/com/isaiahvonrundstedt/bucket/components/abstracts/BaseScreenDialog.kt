package com.isaiahvonrundstedt.bucket.components.abstracts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.isaiahvonrundstedt.bucket.R

abstract class BaseScreenDialog: DialogFragment(){

    internal var toolbar: Toolbar? = null
    internal var toolbarTitle: AppCompatTextView? = null

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

    fun invoke(fragmentManager: FragmentManager){
        if (dialog?.isShowing != true)
            show(fragmentManager, "screenTag")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rootView: ViewGroup? = (view as ViewGroup)

        val appbarView: View = LayoutInflater.from(context!!).inflate(R.layout.layout_appbar_flat, rootView, false)
        rootView?.addView(appbarView)

        toolbar = appbarView.findViewById(R.id.toolbar)
        toolbarTitle = appbarView.findViewById(R.id.toolbarTitle)

        toolbar?.setNavigationOnClickListener { dismiss() }
        toolbar?.navigationIcon = ResourcesCompat.getDrawable(context?.resources!!, R.drawable.ic_error, null)
    }

}