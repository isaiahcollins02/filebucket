package com.isaiahvonrundstedt.bucket.features.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.features.support.SplashActivity
import com.isaiahvonrundstedt.bucket.features.shared.custom.LoaderDialog
import com.isaiahvonrundstedt.bucket.features.shared.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.features.auth.Account
import com.isaiahvonrundstedt.bucket.features.shared.receivers.NetworkReceiver
import com.isaiahvonrundstedt.bucket.utils.User
import kotlinx.android.synthetic.main.fragment_auth_login.*

class LoginFragment: BaseFragment() {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_login, container, false)
    }

    override fun onStart() {
        super.onStart()

        forgotButton.setOnClickListener {
            MaterialDialog(context!!).show {
                title(R.string.navigation_reset)
                message(R.string.instruction_reset)
                input(waitForPositiveButton = true, hintRes = R.string.field_hint_email) { _, inputText ->
                    firebaseAuth.sendPasswordResetEmail(inputText.toString())
                }
                positiveButton(R.string.action_continue)
            }
        }

        loginButton.setOnClickListener {
            if (NetworkReceiver.getConnectivityStatus(context) != NetworkReceiver.typeNotConnected){
                if (Patterns.EMAIL_ADDRESS.matcher(emailField.text.toString()).matches())
                    handleAuthentication()
                else
                    Snackbar.make(it, R.string.status_invalid_email_address, Snackbar.LENGTH_SHORT).show()
            } else
                Snackbar.make(it, R.string.status_network_no_internet, Snackbar.LENGTH_SHORT).show()
        }

        passwordField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                if (Patterns.EMAIL_ADDRESS.matcher(emailField.text.toString()).matches()){
                    handleAuthentication()
                } else Snackbar.make(view!!, R.string.status_invalid_email_address, Snackbar.LENGTH_SHORT).show()
                return@setOnEditorActionListener true
            } else
                return@setOnEditorActionListener false
        }
    }

    private fun handleAuthentication() {
        val dialog = LoaderDialog(getString(R.string.status_verifying))
        dialog.invoke(childFragmentManager)

        val authEmail = emailField.text
        val authPassword = passwordField.text

        if (!authEmail.isNullOrBlank() && !authPassword.isNullOrBlank()) {
            firebaseAuth.signInWithEmailAndPassword(authEmail.toString(), authPassword.toString())
                .addOnCompleteListener {
                    dialog.dismiss()
                }
                .addOnSuccessListener { authResult ->
                    val userID: String? = authResult.user?.uid

                    firestore.collection(Firestore.users).document(userID!!).get()
                        .addOnCompleteListener {
                            dialog.dismiss()
                        }
                        .addOnSuccessListener {
                            val account: Account? = it.toObject(Account::class.java)
                            User(context!!).save(account!!)

                            startActivity(Intent(context!!, SplashActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                            activity?.finish()
                        }
                        .addOnFailureListener {
                            Snackbar.make(view!!, R.string.status_error_unknown, Snackbar.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Snackbar.make(view!!, R.string.status_invalid_password, Snackbar.LENGTH_SHORT).show()
                }
        } else {
            dialog.dismiss()
            Snackbar.make(view!!, R.string.status_blank_field_email, Snackbar.LENGTH_SHORT).show()
        }
    }
}