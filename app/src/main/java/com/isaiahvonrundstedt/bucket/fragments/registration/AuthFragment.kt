package com.isaiahvonrundstedt.bucket.fragments.registration

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.SplashActivity
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.constants.Params
import com.isaiahvonrundstedt.bucket.objects.core.Account
import com.isaiahvonrundstedt.bucket.utils.User
import com.kaopiz.kprogresshud.KProgressHUD
import kotlinx.android.synthetic.main.fragment_auth.*

class AuthFragment: Fragment() {

    private var _firstName: String? = null
    private var _lastName: String? = null
    private var _email: String? = null

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments.let {
            _firstName = it?.getString(Params.firstName)
            _lastName = it?.getString(Params.lastName)
            _email = it?.getString(Params.email)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth, container, false)
    }

    private fun onRegister(password: String){
        val userReference = firestore.collection(Firestore.users)

        val kProgressHUD = KProgressHUD.create(context)
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setAnimationSpeed(2)
        .setCancellable(false)
        .setDimAmount(.05f)
        .show()

        firebaseAuth.createUserWithEmailAndPassword(_email!!, password)
            .addOnSuccessListener {
                val userID: String = it.user.uid

                val newAccount = Account(accountID = userID, firstName = _firstName, lastName = _lastName, email = _email)

                userReference.document(userID).set(newAccount)
                    .addOnCompleteListener { kProgressHUD.dismiss() }
                    .addOnSuccessListener {
                        User(context!!).save(newAccount)
                        startActivity(Intent(context, SplashActivity::class.java))
                        activity?.finish()
                    }
                    .addOnFailureListener {
                        Snackbar.make(view!!, R.string.status_error_unknown, Snackbar.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Snackbar.make(view!!, R.string.status_error_unknown, Snackbar.LENGTH_SHORT).show()
            }
    }
    override fun onStart() {
        super.onStart()

        registerButton.setOnClickListener {
            val firstPassword: String? = passwordField.text.toString()
            val secondPassword: String? = confirmPasswordField.text.toString()

            if (firstPassword == secondPassword)
                onRegister(firstPassword!!)
            else if (firstPassword.isNullOrBlank() || secondPassword.isNullOrBlank())
                Snackbar.make(view!!, R.string.status_blank_fields, Snackbar.LENGTH_SHORT).show()
            else
                Snackbar.make(view!!, R.string.status_password_not_match, Snackbar.LENGTH_SHORT).show()
        }
    }

}