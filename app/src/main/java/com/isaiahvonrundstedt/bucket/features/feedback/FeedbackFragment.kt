package com.isaiahvonrundstedt.bucket.features.feedback

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
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseScreenDialog
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
            R.id.privacyChip -> Feedback.supportTypePrivacy
            R.id.interfaceChip -> Feedback.supportTypeInterface
            R.id.usageChip -> Feedback.supportTypeUsage
            R.id.accessibilityChip -> Feedback.supportTypeAccessibility
            else -> Feedback.supportTypeGeneric
        }
    }

    private fun sendFeedback(){
        val support = Feedback(firebaseAuth.currentUser?.uid).apply {
            type = getCheckedChip(chipGroup)
            title = summaryField.text.toString()
            body = infoField.text.toString()
        }

        context?.startService(Intent(context, SupportService::class.java)
                .putExtra(SupportService.extraSupportItem, support)
                .setAction(SupportService.actionSendFeedback))
    }


}