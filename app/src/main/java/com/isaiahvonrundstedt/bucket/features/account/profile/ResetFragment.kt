package com.isaiahvonrundstedt.bucket.features.account.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.features.shared.custom.LoaderDialog
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.utils.User
import kotlinx.android.synthetic.main.fragment_user_reset.*

class ResetFragment: BaseFragment() {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_reset, container, false)
    }

    override fun onStart() {
        super.onStart()

        emailField.setText(User(context!!).email)

        continueButton.setOnClickListener {
            if (emailField.text.toString().isNotBlank()){

                val progress = LoaderDialog(getString(R.string.dialog_sending_reset_link))
                progress.invoke(childFragmentManager)

                firebaseAuth.sendPasswordResetEmail(emailField.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful)
                            Snackbar.make(view!!, R.string.status_email_reset_sent, Snackbar.LENGTH_SHORT)
                        else
                            Snackbar.make(view!!, R.string.status_error_unknown, Snackbar.LENGTH_SHORT)

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