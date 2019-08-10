package com.isaiahvonrundstedt.bucket.fragments.auth

import android.content.Intent
import android.os.Bundle
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
import com.isaiahvonrundstedt.bucket.activities.SplashActivity
import com.isaiahvonrundstedt.bucket.components.LoaderDialog
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.objects.core.Account
import com.isaiahvonrundstedt.bucket.utils.Data
import com.isaiahvonrundstedt.bucket.utils.User
import kotlinx.android.synthetic.main.fragment_auth_login.*

class AuthFragment: BaseFragment() {

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
                    firebaseAuth?.sendPasswordResetEmail(inputText.toString())
                }
                positiveButton(R.string.button_continue)
            }
        }

        loginButton.setOnClickListener {
            if (Data.isValidEmailAddress(emailField.text.toString()))
                handleAuthentication()
            else
                Snackbar.make(view!!, R.string.fui_invalid_email_address, Snackbar.LENGTH_SHORT).show()
        }

        passwordField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                if (Data.isValidEmailAddress(emailField.text.toString())){
                    handleAuthentication()
                } else Snackbar.make(view!!, R.string.fui_invalid_email_address, Snackbar.LENGTH_SHORT).show()
                return@setOnEditorActionListener true
            } else
                return@setOnEditorActionListener false
        }
    }

    private fun handleAuthentication() {
        val dialog = LoaderDialog(getString(R.string.fui_verifying))
        dialog.invoke(childFragmentManager)

        val authEmail = emailField.text
        val authPassword = passwordField.text

        if (!authEmail.isNullOrBlank() && !authPassword.isNullOrBlank()) {
            firebaseAuth.signInWithEmailAndPassword(authEmail.toString(), authPassword.toString())
                .addOnCompleteListener {
                    dialog.dismiss()
                }
                .addOnSuccessListener { authResult ->
                    val userID: String? = authResult.user.uid

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
            Snackbar.make(view!!, R.string.status_blank_fields, Snackbar.LENGTH_SHORT).show()
        }
    }
}