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
import com.isaiahvonrundstedt.bucket.constants.Firebase
import com.isaiahvonrundstedt.bucket.objects.User
import com.kaopiz.kprogresshud.KProgressHUD
import studio.carbonylgroup.textfieldboxes.ExtendedEditText

class AuthFragment: Fragment() {

    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var rootView: View
    private lateinit var passwordField: ExtendedEditText
    private lateinit var confirmPasswordField: ExtendedEditText
    private lateinit var registerButton: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments.let {
            firstName = it?.getString("firstName")
            lastName = it?.getString("lastName")
            email = it?.getString("email")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_auth, container, false)

        passwordField = rootView.findViewById(R.id.passwordField)
        confirmPasswordField = rootView.findViewById(R.id.confirmPasswordField)
        registerButton = rootView.findViewById(R.id.registerButton)

        return rootView
    }

    private fun handleRegistration(password: String, confirmationPassword: String){
        val progress = KProgressHUD.create(rootView.context)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setAnimationSpeed(2)
            .setCancellable(false)
            .setDimAmount(.50f)
            .show()

        val userReference = firestore.collection(Firebase.USERS.string)

        if (password == confirmationPassword){
            firebaseAuth.createUserWithEmailAndPassword(email!!, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userID: String = it.result?.user!!.uid

                        val newAccount = User().also { thisObject ->
                            thisObject.firstName = firstName
                            thisObject.lastName = lastName
                            thisObject.email = email
                            thisObject.password = password
                        }

                        userReference.document(userID).set(newAccount)
                            .addOnSuccessListener {
                                progress.dismiss()

                                startActivity(Intent(rootView.context, MainActivity::class.java))
                                activity?.finish()
                            }
                            .addOnFailureListener {
                                Snackbar.make(rootView, R.string.status_unknown, Snackbar.LENGTH_SHORT).show()
                            }
                    } else
                        Snackbar.make(rootView, R.string.status_unknown, Snackbar.LENGTH_SHORT).show()
                }
        } else if (password.isBlank() || confirmationPassword.isBlank())
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