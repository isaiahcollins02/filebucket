package com.isaiahvonrundstedt.bucket.fragments.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.isaiahvonrundstedt.bucket.R
import kotlinx.android.synthetic.main.fragment_auth_main.*

class PasswordFragment: Fragment() {

    private var email: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments.let {
            val args = PasswordFragmentArgs.fromBundle(it!!)
            email = args.email
        }

        continueButton.setOnClickListener {
            if (passwordField.text != null && confirmPasswordField.text != null){

                val newPassword = passwordField.text.toString()
                val confirmedPassword = confirmPasswordField.text.toString()

                if (newPassword == confirmedPassword){
                    val action = PasswordFragmentDirections.actionContinueAuth(email!!, passwordField.text.toString())
                    action.email = email!!
                    action.password = newPassword
                    Navigation.findNavController(view).navigate(action)
                } else
                    Snackbar.make(view, R.string.status_password_not_match, Snackbar.LENGTH_SHORT).show()
            } else
                Snackbar.make(view, R.string.status_blank_field_password, Snackbar.LENGTH_SHORT).show()
        }
    }

}