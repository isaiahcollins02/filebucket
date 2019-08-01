package com.isaiahvonrundstedt.bucket.activities.support

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.LoaderDialog
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.objects.diagnostics.Support
import com.isaiahvonrundstedt.bucket.service.SupportService
import kotlinx.android.synthetic.main.activity_feedback.*

class SupportActivity: BaseAppBarActivity() {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        setToolbarTitle(R.string.about_feedback)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_support, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId){
            android.R.id.home -> finish()
            R.id.action_send -> sendFeedback()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
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

        startService(Intent(this, SupportService::class.java)
                .putExtra(SupportService.extraSupportItem, support)
                .setAction(SupportService.actionSendFeedback))
    }

}