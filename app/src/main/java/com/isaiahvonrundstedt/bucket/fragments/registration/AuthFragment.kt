package com.isaiahvonrundstedt.bucket.fragments.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.constants.Params
import kotlinx.android.synthetic.main.fragment_auth_main.*

class AuthFragment: Fragment() {

    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        email = arguments?.getString(Params.email)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        continueButton.setOnClickListener {
            if (passwordField.text != null && confirmPasswordField.text != null){
                if (passwordField.text == confirmPasswordField.text){
                    val arguments = Bundle()
                    arguments.putString(Params.email, email)
                    arguments.putString(Params.password, passwordField.text.toString())

                    val basicFragment = BasicFragment()
                    basicFragment.arguments = arguments

                    activity?.supportFragmentManager?.beginTransaction()?.run {
                        setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_out_left, R.anim.slide_in_right)
                        replace(R.id.childLayout, basicFragment)
                        addToBackStack("viewTag")
                        commit()
                    }
                } else
                    Snackbar.make(view, R.string.status_password_not_match, Snackbar.LENGTH_SHORT).show()
            } else
                Snackbar.make(view, R.string.status_blank_fields, Snackbar.LENGTH_SHORT).show()
        }
    }

}