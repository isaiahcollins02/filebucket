package com.isaiahvonrundstedt.bucket.activities.support

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RadioGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.constants.Firebase
import com.isaiahvonrundstedt.bucket.objects.Support
import com.kaopiz.kprogresshud.KProgressHUD
import studio.carbonylgroup.textfieldboxes.ExtendedEditText

class SupportActivity: BaseActivity() {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var summaryField: ExtendedEditText
    private lateinit var infoField: ExtendedEditText
    private lateinit var radioGroup: RadioGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setToolbarTitle(getString(R.string.settings_feedback))

        summaryField = findViewById(R.id.summaryField)
        infoField = findViewById(R.id.infoField)
        radioGroup = findViewById(R.id.radioGroup)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_support, menu)
        tintActionBarItem(menu, R.id.action_send, R.color.colorDefault)
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

    private fun sendFeedback(){

        val progress: KProgressHUD = KProgressHUD(this)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setAnimationSpeed(2)
            .setCancellable(false)
            .setDimAmount(.50f)
            .show()

        val support = Support(firebaseAuth.currentUser?.uid).apply {
            type = getCheckedID()
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

    private fun getCheckedID(): Int {
        return when (radioGroup.checkedRadioButtonId){
            R.id.radioSuggestion -> 1
            R.id.radioProblem -> 2
            else -> 0
        }
    }

}