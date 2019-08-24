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
import com.isaiahvonrundstedt.bucket.activities.MainActivity
import com.isaiahvonrundstedt.bucket.activities.auth.FirstRunActivity
import com.isaiahvonrundstedt.bucket.components.LoaderDialog
import com.isaiahvonrundstedt.bucket.constants.Firestore
import com.isaiahvonrundstedt.bucket.objects.core.Account
import com.isaiahvonrundstedt.bucket.receivers.NetworkReceiver
import kotlinx.android.synthetic.main.fragment_auth_information.*
import timber.log.Timber

class InformationFragment: Fragment() {

    private var credentialEmail: String? = null
    private var credentialPassword: String? = null

    private val newAccount: Account = Account()

    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments.let {
            val args = InformationFragmentArgs.fromBundle(it!!)
            credentialEmail = args.email
            credentialPassword = args.password
        }

        registerButton.setOnClickListener {
            if (NetworkReceiver.getConnectivityStatus(context) != NetworkReceiver.typeNotConnected){
                val dialog = LoaderDialog(getString(R.string.dialog_creating_account))
                dialog.invoke(childFragmentManager)

                if (firstNameField.text != null && lastNameField.text != null){

                    newAccount.email = credentialEmail
                    newAccount.firstName = firstNameField.text.toString()
                    newAccount.lastName = lastNameField.text.toString()

                    if (credentialEmail != null && credentialPassword != null){
                        firebaseAuth.createUserWithEmailAndPassword(credentialEmail!!, credentialPassword!!)
                            .addOnCompleteListener {
                                dialog.dismiss()
                            }
                            .addOnSuccessListener { authResult ->
                                newAccount.accountID = authResult?.user?.uid

                                firestore.collection(Firestore.users).document(newAccount.accountID!!).set(newAccount)
                                    .addOnSuccessListener {
                                        startActivity(Intent(context, MainActivity::class.java)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                                        activity?.finish()
                                    }
                            }
                            .addOnFailureListener { exception ->
                                Timber.e(exception)
                                Snackbar.make(view, R.string.status_error_occurred, Snackbar.LENGTH_SHORT).show()
                            }
                    } else {
                        dialog.dismiss()

                        Snackbar.make(view, R.string.status_error_unknown, Snackbar.LENGTH_SHORT).show()
                        startActivity(Intent(context, FirstRunActivity::class.java))
                    }
                } else {
                    dialog.dismiss()

                    Snackbar.make(view, R.string.status_blank_fields, Snackbar.LENGTH_SHORT).show()
                }
            } else
                Snackbar.make(it, R.string.status_network_no_internet, Snackbar.LENGTH_SHORT).show()
        }
    }

}