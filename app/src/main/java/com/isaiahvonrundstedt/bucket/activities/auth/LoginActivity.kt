package com.isaiahvonrundstedt.bucket.activities.auth

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.MainActivity
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.objects.core.Account
import com.isaiahvonrundstedt.bucket.utils.User
import com.kaopiz.kprogresshud.KProgressHUD
import studio.carbonylgroup.textfieldboxes.ExtendedEditText

class LoginActivity: AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var emailField: ExtendedEditText
    private lateinit var passwordField: ExtendedEditText
    private lateinit var loginButton: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailField = findViewById(R.id.emailField)
        passwordField = findViewById(R.id.passwordField)
        loginButton = findViewById(R.id.loginButton)
    }

    override fun onStart() {
        super.onStart()

        loginButton.setOnClickListener {
            handleAuthentication()
        }

        passwordField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                handleAuthentication()
                return@setOnEditorActionListener true
            } else
                return@setOnEditorActionListener false
        }

    }

    private fun handleAuthentication() {
        val dialog = KProgressHUD.create(this)
        dialog.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        dialog.setAnimationSpeed(2)
        dialog.setCancellable(false)
        dialog.setDimAmount(.05f)
        dialog.show()

        val authEmail = emailField.text
        val authPassword = passwordField.text

        if (!authEmail.isNullOrBlank() && !authPassword.isNullOrBlank()) {
            firebaseAuth.signInWithEmailAndPassword(authEmail.toString(), authPassword.toString())
                .addOnSuccessListener { authResult ->
                    val userID: String? = authResult.user.uid

                    firestore.collection(Firestore.users).document(userID!!)
                        .get().addOnSuccessListener {
                            val account: Account? = it.toObject(Account::class.java)
                            User(this).save(account!!)

                            dialog.dismiss()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            dialog.dismiss()
                            Snackbar.make(window.decorView.rootView, R.string.status_unknown, Snackbar.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    dialog.dismiss()
                    Snackbar.make(window.decorView.rootView, R.string.status_invalid_password, Snackbar.LENGTH_SHORT).show()
                }
        } else
            Snackbar.make(window.decorView.rootView, R.string.status_blank_fields, Snackbar.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return false
    }
}