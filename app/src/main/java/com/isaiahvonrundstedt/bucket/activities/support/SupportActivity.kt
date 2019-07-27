package com.isaiahvonrundstedt.bucket.activities.support

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.LoaderDialog
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.objects.diagnostics.Support
import kotlinx.android.synthetic.main.layout_dialog_support.*

class SupportActivity: BaseAppBarActivity() {

    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_dialog_support)
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
        val progress = LoaderDialog(getString(R.string.dialog_sending_reset_link))
        progress.invoke(supportFragmentManager)

        val support = Support(firebaseAuth.currentUser?.uid).apply {
            type = getCheckedChip(chipGroup)
            title = summaryField.text.toString()
            body = infoField.text.toString()
        }

        val reference = firestore.collection(Firestore.feedback)
        reference.add(support)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    Snackbar.make(window.decorView.rootView, R.string.status_sending_success, Snackbar.LENGTH_SHORT).show()
                    finish()
                } else
                    Snackbar.make(window.decorView.rootView, R.string.status_error_occurred, Snackbar.LENGTH_SHORT).show()
                progress.dismiss()
            }
    }

}