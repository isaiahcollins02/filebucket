package com.isaiahvonrundstedt.bucket.fragments.registration

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.interfaces.FirebaseAuthVerifier
import kotlinx.android.synthetic.main.fragment_auth_email.*

class EmailFragment: BaseFragment(), FirebaseAuthVerifier {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        continueButton.setOnClickListener {
            val email: String = emailField.text.toString()
            if (email.isNotEmpty()){
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches())
                    onVerifyEmail(email, this)
                else Snackbar.make(view, R.string.status_invalid_email_address, Snackbar.LENGTH_SHORT).show()
            } else
                Snackbar.make(view, R.string.status_blank_fields, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onVerified(status: Boolean) {
        if (status){
            val action = EmailFragmentDirections.actionContinueEmail(emailField.text.toString())
            Navigation.findNavController(view!!).navigate(action)
        } else
            Snackbar.make(view!!, R.string.status_email_already_taken, Snackbar.LENGTH_SHORT).show()
    }

    private fun onVerifyEmail(email: String, listener: FirebaseAuthVerifier){
        val firebaseAuthCallback: FirebaseAuthVerifier? = listener
        if (email.isNotEmpty()){
            firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnSuccessListener {
                    firebaseAuthCallback?.onVerified(it?.signInMethods?.isEmpty() == true)
                }
                .addOnFailureListener {
                    Snackbar.make(view!!, R.string.status_error_occurred, Snackbar.LENGTH_SHORT).show()
                }
        } else
            Snackbar.make(view!!, R.string.status_invalid_email_address, Snackbar.LENGTH_SHORT).show()
    }

}