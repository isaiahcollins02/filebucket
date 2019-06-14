package com.isaiahvonrundstedt.bucket.experience.activities.auth

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.core.constants.Firebase
import com.isaiahvonrundstedt.bucket.core.objects.Account
import com.isaiahvonrundstedt.bucket.core.utils.Client
import com.isaiahvonrundstedt.bucket.experience.activities.MainActivity
import com.isaiahvonrundstedt.bucket.experience.activities.wrapper.FrameActivity
import com.kaopiz.kprogresshud.KProgressHUD
import studio.carbonylgroup.textfieldboxes.ExtendedEditText

class LoginActivity: AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var emailField: ExtendedEditText
    private lateinit var passwordField: ExtendedEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var forgotButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailField = findViewById(R.id.emailField)
        passwordField = findViewById(R.id.passwordField)
        loginButton = findViewById(R.id.loginButton)
        forgotButton = findViewById(R.id.forgotButton)

    }

    override fun onStart() {
        super.onStart()

        loginButton.setOnClickListener {
            handleAuthentication()
        }

        forgotButton.setOnClickListener {
            startActivity(Intent(this, FrameActivity::class.java)
                .putExtra("VIEW_TYPE", FrameActivity.VIEW_TYPE_RESET))
        }

        passwordField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                handleAuthentication()
                return@setOnEditorActionListener true
            } else
                return@setOnEditorActionListener false
        }

    }

    private fun handleAuthentication(){
        if (emailField.text.toString().isNotBlank() && passwordField.text.toString().isNotBlank()){

            val progressHUD = KProgressHUD(this).apply {
                setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                setAnimationSpeed(2)
                setCancellable(false)
                setDimAmount(.50f)
            }.show()

            firebaseAuth.signInWithEmailAndPassword(emailField.text.toString(), passwordField.text.toString())
                .addOnCompleteListener { authTask ->

                    if (authTask.isSuccessful){
                        val userID: String = authTask.result?.user?.uid!!

                        val userReference: DocumentReference = firestore.collection(Firebase.USERS.string).document(userID)
                        userReference.get().addOnCompleteListener { firestoreTask ->
                            val account: Account = firestoreTask.result?.toObject(Account::class.java) as Account

                            Client(this).run {
                                firstName = account.firstName
                                lastName = account.lastName
                                email = account.email
                                imageURL = account.imageURL
                            }

                            progressHUD.dismiss()

                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        Snackbar.make(window.decorView.rootView, R.string.status_invalid_password, Snackbar.LENGTH_SHORT).show()
                        progressHUD.dismiss()
                    }
                }
        } else
            MaterialDialog(this).show {
                title(R.string.dialog_email_or_password_cannot_be_blank)
                positiveButton(R.string.button_continue)
            }
    }
}