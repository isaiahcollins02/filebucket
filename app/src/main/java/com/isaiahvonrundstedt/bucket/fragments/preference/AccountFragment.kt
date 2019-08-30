package com.isaiahvonrundstedt.bucket.fragments.preference

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.preference.Preference
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.auth.FirstRunActivity
import com.isaiahvonrundstedt.bucket.activities.wrapper.FrameActivity
import com.isaiahvonrundstedt.bucket.architecture.database.AppDatabase
import com.isaiahvonrundstedt.bucket.components.abstracts.BasePreference
import com.isaiahvonrundstedt.bucket.components.modules.GlideApp
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.service.TransferService
import com.isaiahvonrundstedt.bucket.utils.Permissions
import com.isaiahvonrundstedt.bucket.utils.Preferences
import gun0912.tedbottompicker.TedBottomPicker

class AccountFragment: BasePreference() {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_account, rootKey)
    }

    override fun onStart() {
        super.onStart()

        val profileNav: Preference? = findPreference(getKey(R.string.profileKey))
        profileNav?.setOnPreferenceClickListener {
            if (Permissions(context!!).writeAccessGranted){
                TedBottomPicker.with(activity).setImageProvider { imageView, imageUri ->
                    val requestOptions = RequestOptions()
                    requestOptions.centerCrop()
                    requestOptions.priority(Priority.NORMAL)

                    GlideApp.with(this).load(imageUri).into(imageView)
                }.show { uri ->
                    context?.startService(Intent(context, TransferService::class.java)
                        .setAction(TransferService.actionProfile)
                        .putExtra(TransferService.extraFileURI, uri)
                        .putExtra(TransferService.extraAccountID, firebaseAuth.currentUser?.uid))
                }
            }

            return@setOnPreferenceClickListener true
        }

        val secureNav: Preference? = findPreference(getKey(R.string.secureNavKey))
        secureNav?.setOnPreferenceClickListener {
            startActivity(Intent(context, FrameActivity::class.java)
                    .putExtra(Params.viewType, FrameActivity.viewTypePassword))
            return@setOnPreferenceClickListener true
        }

        val resetNav: Preference? = findPreference(getKey(R.string.resetNavKey))
        resetNav?.setOnPreferenceClickListener {
            MaterialDialog(context!!).show {
                lifecycleOwner(this@AccountFragment)
                title(R.string.navigation_reset)
                message(R.string.instruction_reset)
                input(waitForPositiveButton = true)
                positiveButton(R.string.action_continue) {
                    val email: String? = getInputField().text.toString()
                    if (email?.isNotBlank() == true && Patterns.EMAIL_ADDRESS.matcher(email).matches())
                        firebaseAuth.sendPasswordResetEmail(email)
                    else Toast.makeText(it.context, R.string.status_invalid_email_address, Toast.LENGTH_SHORT).show()
                }
                negativeButton(R.string.action_cancel)
            }
            return@setOnPreferenceClickListener true
        }

        val signoutNav: Preference? = findPreference(getKey(R.string.signoutNavKey))
        signoutNav?.setOnPreferenceClickListener {
            MaterialDialog(context!!).show {
                lifecycleOwner(this@AccountFragment)
                title(R.string.dialog_sign_out_title)
                message(R.string.dialog_sign_out_summary)
                positiveButton(R.string.navigation_signout) {
                    firebaseAuth.signOut()

                    Preferences(context).clear()
                    AppDatabase.destroyDatabase()

                    if (firebaseAuth.currentUser == null){
                        startActivity(Intent(context, FirstRunActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                        activity?.finish()
                    }
                }
                negativeButton(R.string.action_cancel)
            }
            return@setOnPreferenceClickListener true
        }
    }

    private fun getKey(@StringRes id: Int) = getString(id)

    override fun onDetach() {
        super.onDetach()
        activity?.finish()
    }
}