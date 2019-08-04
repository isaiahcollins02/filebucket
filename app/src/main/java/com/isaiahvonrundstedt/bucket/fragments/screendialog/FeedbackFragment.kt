package com.isaiahvonrundstedt.bucket.fragments.screendialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseScreenDialog
import com.isaiahvonrundstedt.bucket.objects.diagnostics.Support
import com.isaiahvonrundstedt.bucket.service.SupportService
import kotlinx.android.synthetic.main.activity_support.*

class FeedbackFragment: BaseScreenDialog(), Toolbar.OnMenuItemClickListener {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_support, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbarTitle?.text = getString(R.string.about_feedback)
        toolbar?.inflateMenu(R.menu.menu_support)
        toolbar?.setOnMenuItemClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId){
            R.id.action_send -> {
                sendFeedback()
                true
            } else -> false
        }
    }

    private fun getCheckedChip(chipGroup: ChipGroup): Int {
        return when (chipGroup.checkedChipId){
            R.id.privacyChip -> Support.supportTypePrivacy
            R.id.interfaceChip -> Support.supportTypeInterface
            R.id.usageChip -> Support.supportTypeUsage
            R.id.accessibilityChip -> Support.supportTypeAccessibility
            else -> Support.supportTypeGeneric
        }
    }

    private fun sendFeedback(){
        val support = Support(firebaseAuth.currentUser?.uid).apply {
            type = getCheckedChip(chipGroup)
            title = summaryField.text.toString()
            body = infoField.text.toString()
        }

        context?.startService(Intent(context, SupportService::class.java)
                .putExtra(SupportService.extraSupportItem, support)
                .setAction(SupportService.actionSendFeedback))
    }


}