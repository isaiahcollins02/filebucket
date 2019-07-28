package com.isaiahvonrundstedt.bucket.activities.auth

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatDelegate
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.SplashActivity
import com.isaiahvonrundstedt.bucket.components.LoaderDialog
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.objects.core.Account
import com.isaiahvonrundstedt.bucket.utils.Preferences
import com.isaiahvonrundstedt.bucket.utils.User
import com.isaiahvonrundstedt.bucket.utils.managers.DataManager
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: BaseActivity() {

    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setSupportActionBar(toolbar)
        supportActionBar?.title = null
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()

        forgotButton.setOnClickListener {
            MaterialDialog(this).show {
                title(R.string.profile_reset_password)
                message(R.string.instruction_reset)
                input(waitForPositiveButton = true, hintRes = R.string.field_hint_email) { _, inputText ->
                    firebaseAuth?.sendPasswordResetEmail(inputText.toString())
                }
                positiveButton(R.string.button_continue)
            }
        }

        loginButton.setOnClickListener {
            if (DataManager.isValidEmailAddress(emailField.text.toString()))
                handleAuthentication()
            else
                Snackbar.make(window.decorView.rootView, R.string.fui_invalid_email_address, Snackbar.LENGTH_SHORT).show()
        }

        passwordField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE){
                if (DataManager.isValidEmailAddress(emailField.text.toString())){
                    handleAuthentication()
                } else Snackbar.make(window.decorView.rootView, R.string.fui_invalid_email_address, Snackbar.LENGTH_SHORT).show()
                return@setOnEditorActionListener true
            } else
                return@setOnEditorActionListener false
        }

    }

    private fun handleAuthentication() {
        val dialog = LoaderDialog(getString(R.string.fui_verifying))
        dialog.invoke(supportFragmentManager)

        val authEmail = emailField.text
        val authPassword = passwordField.text

        if (!authEmail.isNullOrBlank() && !authPassword.isNullOrBlank()) {
            firebaseAuth.signInWithEmailAndPassword(authEmail.toString(), authPassword.toString())
                .addOnSuccessListener { authResult ->
                    val userID: String? = authResult.user.uid

                    firestore.collection(Firestore.users).document(userID!!).get()
                        .addOnCompleteListener {
                            dialog.dismiss()
                        }
                        .addOnSuccessListener {
                            val account: Account? = it.toObject(Account::class.java)
                            User(this).save(account!!)

                            startActivity(Intent(this, SplashActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Snackbar.make(window.decorView.rootView, R.string.status_error_unknown, Snackbar.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Snackbar.make(window.decorView.rootView, R.string.status_invalid_password, Snackbar.LENGTH_SHORT).show()
                }
        } else {
            dialog.dismiss()
            Snackbar.make(window.decorView.rootView, R.string.status_blank_fields, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_auth, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.action_theme)?.isChecked = Preferences(this).theme == Preferences.themeDark
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId){
            R.id.action_theme -> {
                if (!item.isChecked){
                    item.isChecked = true
                    Preferences(this).theme = Preferences.themeDark
                    delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    item.isChecked = false
                    Preferences(this).theme = Preferences.themeLight
                    delegate.localNightMode = AppCompatDelegate.MODE_NIGHT_NO
                }
                true
            } android.R.id.home -> {
                onBackPressed()
                true
            } else -> super.onOptionsItemSelected(item)
        }
    }
}