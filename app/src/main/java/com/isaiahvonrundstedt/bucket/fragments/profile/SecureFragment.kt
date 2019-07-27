package com.isaiahvonrundstedt.bucket.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.LoaderDialog
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import kotlinx.android.synthetic.main.fragment_security.*

class SecureFragment: BaseFragment() {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_security, container, false)
    }

    override fun onStart() {
        super.onStart()

        continueButton.setOnClickListener {

            val progress = LoaderDialog(getString(R.string.dialog_working_on_it))
            progress.invoke(childFragmentManager)

            val email: String = firebaseAuth.currentUser?.email!!
            val oldPassword: String = oldPasswordField.text.toString()
            val newPassword: String = newPasswordField.text.toString()
            val confirmPassword: String = confirmPasswordField.text.toString()

            val authCredential: AuthCredential = EmailAuthProvider.getCredential(email, oldPassword)

            firebaseAuth.currentUser?.reauthenticate(authCredential)
                ?.addOnSuccessListener { _ ->
                    progress.dismiss()
                    if (newPassword == confirmPassword){
                        firebaseAuth.currentUser!!.updatePassword(newPassword)
                            .addOnSuccessListener {
                                Snackbar.make(view!!, R.string.status_change_committed, Snackbar.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Snackbar.make(view!!, R.string.status_error_unknown, Snackbar.LENGTH_SHORT).show()
                            }
                    } else
                        Snackbar.make(view!!, R.string.status_password_not_match, Snackbar.LENGTH_SHORT).show()
                }
                ?.addOnFailureListener {
                    progress.dismiss()
                    Snackbar.make(view!!, R.string.dialog_token_error, Snackbar.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDetach() {
        super.onDetach()
        activity?.finish()
    }

}