package com.isaiahvonrundstedt.bucket.experience.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.components.abstracts.BaseFragment
import com.kaopiz.kprogresshud.KProgressHUD
import studio.carbonylgroup.textfieldboxes.ExtendedEditText

class SecureFragment: BaseFragment() {

    private var hasFieldChanges: Boolean = false

    private val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var rootView: View
    private lateinit var oldPasswordField: ExtendedEditText
    private lateinit var newPasswordField: ExtendedEditText
    private lateinit var confirmPasswordField: ExtendedEditText
    private lateinit var continueButton: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_security, container, false)

        oldPasswordField = rootView.findViewById(R.id.oldPasswordField)
        newPasswordField = rootView.findViewById(R.id.newPasswordField)
        confirmPasswordField = rootView.findViewById(R.id.confirmPasswordField)
        continueButton = rootView.findViewById(R.id.continueButton)

        return rootView
    }

    override fun onStart() {
        super.onStart()

        continueButton.setOnClickListener {
            if (hasFieldChanges){

                val progress: KProgressHUD = KProgressHUD(it.context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setAnimationSpeed(2)
                    .setCancellable(false)
                    .setDimAmount(.05f)
                    .show()

                val email: String = firebaseAuth.currentUser?.email!!
                val oldPassword: String = oldPasswordField.text.toString()
                val newPassword: String = newPasswordField.text.toString()
                val confirmPassword: String = confirmPasswordField.text.toString()

                val authCredential: AuthCredential = EmailAuthProvider.getCredential(email, oldPassword)

                firebaseAuth.currentUser?.reauthenticate(authCredential)
                    ?.addOnCompleteListener {  authTask ->
                        if (authTask.isSuccessful){
                            if (newPassword == confirmPassword){
                                firebaseAuth.currentUser!!.updatePassword(newPassword)
                                    .addOnCompleteListener { changeTask ->
                                        if (changeTask.isSuccessful)
                                            Snackbar.make(rootView, R.string.status_change_committed, Snackbar.LENGTH_SHORT)
                                        else
                                            Snackbar.make(rootView, R.string.status_unknown, Snackbar.LENGTH_SHORT)
                                    }
                            }
                        } else
                        Snackbar.make(rootView, R.string.dialog_token_error, Snackbar.LENGTH_SHORT)

                        progress.dismiss()

                    }
            }
        }

    }

    override fun onDetach() {
        super.onDetach()
        activity?.finish()
    }

}