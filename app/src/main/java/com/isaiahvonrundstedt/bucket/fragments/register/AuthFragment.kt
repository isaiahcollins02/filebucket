package com.isaiahvonrundstedt.bucket.fragments.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.activities.MainActivity
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.objects.core.Account
import com.kaopiz.kprogresshud.KProgressHUD
import studio.carbonylgroup.textfieldboxes.ExtendedEditText

class AuthFragment: Fragment() {

    private var _firstName: String? = null
    private var _lastName: String? = null
    private var _email: String? = null

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private lateinit var rootView: View
    private lateinit var passwordField: ExtendedEditText
    private lateinit var confirmPasswordField: ExtendedEditText
    private lateinit var registerButton: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments.let {
            _firstName = it?.getString("_firstName")
            _lastName = it?.getString("_lastName")
            _email = it?.getString("_email")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_auth, container, false)

        passwordField = rootView.findViewById(R.id.passwordField)
        confirmPasswordField = rootView.findViewById(R.id.confirmPasswordField)
        registerButton = rootView.findViewById(R.id.registerButton)

        return rootView
    }

    private fun handleRegistration(_password: String, confirmPass: String){
        val userReference = firestore.collection(Firestore.users)

        if (_password == confirmPass){
            val dialog = KProgressHUD.create(rootView.context)
            dialog.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            dialog.setAnimationSpeed(2)
            dialog.setCancellable(false)
            dialog.setDimAmount(.05f)
            dialog.show()

            firebaseAuth.createUserWithEmailAndPassword(_email!!, _password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userID: String = it.result?.user!!.uid

                        val newAccount = Account().apply {
                            firstName = _firstName
                            lastName = _lastName
                            email = _email
                            password = _password
                            accountID = userID
                        }

                        userReference.document(userID).set(newAccount)
                            .addOnSuccessListener {
                                dialog.dismiss()

                                startActivity(Intent(rootView.context, MainActivity::class.java))
                                activity?.finish()
                            }
                            .addOnFailureListener {
                                dialog.dismiss()
                                Snackbar.make(rootView, R.string.status_unknown, Snackbar.LENGTH_SHORT).show()
                            }
                    } else
                        dialog.dismiss()
                        Snackbar.make(rootView, R.string.status_unknown, Snackbar.LENGTH_SHORT).show()
                }
        } else if (_password.isBlank() || confirmPass.isBlank())
            Snackbar.make(rootView, R.string.status_blank_fields, Snackbar.LENGTH_SHORT).show()
        else
            Snackbar.make(rootView, R.string.status_password_not_match, Snackbar.LENGTH_SHORT).show()

    }
    override fun onStart() {
        super.onStart()

        registerButton.setOnClickListener {
            handleRegistration(passwordField.text.toString(), confirmPasswordField.text.toString())
        }
    }

}