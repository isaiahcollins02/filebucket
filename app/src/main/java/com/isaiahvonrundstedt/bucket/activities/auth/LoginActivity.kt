package com.isaiahvonrundstedt.bucket.activities.auth

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.wrapper.IntroActivity
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseActivity
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.objects.core.Account
import com.isaiahvonrundstedt.bucket.utils.Preferences
import com.isaiahvonrundstedt.bucket.utils.User
import com.kaopiz.kprogresshud.KProgressHUD
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
                            startActivity(Intent(this, IntroActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            dialog.dismiss()
                            Snackbar.make(window.decorView.rootView, R.string.status_error_unknown, Snackbar.LENGTH_SHORT).show()
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