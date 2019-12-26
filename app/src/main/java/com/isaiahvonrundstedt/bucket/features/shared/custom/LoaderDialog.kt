package com.isaiahvonrundstedt.bucket.features.shared.custom

import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.isaiahvonrundstedt.bucket.R
import kotlinx.android.synthetic.main.layout_dialog_progress.*

class LoaderDialog(var title: String? = null): DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        return inflater.inflate(R.layout.layout_dialog_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleView.text = title
    }

    override fun onStart() {
        super.onStart()

        val cardBackground = ResourcesCompat.getDrawable(context?.resources!!, R.drawable.shape_dialog_background, null)

        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawable(InsetDrawable(cardBackground, 48))
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun invoke(fragmentManager: FragmentManager){
        if (dialog?.isShowing != true)
            show(fragmentManager, "tag")
    }

}