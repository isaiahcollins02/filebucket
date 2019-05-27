package com.isaiahvonrundstedt.bucket.experience.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.core.utils.Client
import com.kaopiz.kprogresshud.KProgressHUD
import studio.carbonylgroup.textfieldboxes.ExtendedEditText

class ResetFragment: BaseFragment() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    private lateinit var rootView: View
    private lateinit var emailField: ExtendedEditText
    private lateinit var continueButton: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_reset, container, false)

        emailField = rootView.findViewById(R.id.emailField)
        continueButton = rootView.findViewById(R.id.continueButton)

        return rootView
    }

    override fun onStart() {
        super.onStart()

        emailField.setText(Client(rootView.context).email)

        continueButton.setOnClickListener {
            if (emailField.text.toString().isNotBlank()){

                val progress: KProgressHUD = KProgressHUD(it.context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setAnimationSpeed(2)
                    .setCancellable(false)
                    .setDimAmount(.05f)
                    .show()

                firebaseAuth.sendPasswordResetEmail(emailField.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful)
                            Snackbar.make(rootView, R.string.status_email_reset_sent, Snackbar.LENGTH_SHORT)
                        else
                            Snackbar.make(rootView, R.string.status_unknown, Snackbar.LENGTH_SHORT)

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