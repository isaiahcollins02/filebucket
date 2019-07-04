package com.isaiahvonrundstedt.bucket.activities.support

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioGroup
import android.widget.Toast
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseAppBarActivity
import com.isaiahvonrundstedt.bucket.constants.Firebase
import com.isaiahvonrundstedt.bucket.objects.Support
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.activity_support.*
import studio.carbonylgroup.textfieldboxes.ExtendedEditText

class SupportActivity: BaseAppBarActivity() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var summaryField: ExtendedEditText
    private lateinit var infoField: ExtendedEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)
        setToolbarTitle(R.string.settings_feedback)

        summaryField = findViewById(R.id.summaryField)
        infoField = findViewById(R.id.infoField)
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
        val progress: KProgressHUD = KProgressHUD(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setAnimationSpeed(2)
            .setCancellable(false)
            .setDimAmount(.50f)
            .show()

        val support = Support(firebaseAuth.currentUser?.uid).apply {
            type = getCheckedChip(chipGroup)
            title = summaryField.text.toString()
            body = infoField.text.toString()
        }

        val reference = firestore.collection(Firebase.FEEDBACK.string)
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