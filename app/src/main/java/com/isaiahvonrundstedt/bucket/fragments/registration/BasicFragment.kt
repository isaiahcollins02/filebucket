package com.isaiahvonrundstedt.bucket.fragments.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.constants.Params
import kotlinx.android.synthetic.main.fragment_basic.*

class BasicFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_basic, container, false)
    }

    override fun onStart() {
        super.onStart()

        continueButton.setOnClickListener {

            if (firstNameField.text.toString().isNotBlank() || lastNameField.text.toString().isNotBlank() ||
                    emailField.text.toString().isNotBlank()) {

                val fragment = AuthFragment()
                fragment.arguments = Bundle().apply {
                    putString(Params.firstName, firstNameField.text.toString())
                    putString(Params.lastName, lastNameField.text.toString())
                    putString(Params.email, emailField.text.toString())
                }

                activity!!.supportFragmentManager.beginTransaction().run {
                    setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_out_left, R.anim.slide_in_right)
                    replace(R.id.childLayout, fragment)
                    addToBackStack("basicFragment")
                    commit()
                }

            } else
                Snackbar.make(view!!, R.string.status_blank_fields, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDetach() {
        super.onDetach()
        activity?.finish()
    }

}