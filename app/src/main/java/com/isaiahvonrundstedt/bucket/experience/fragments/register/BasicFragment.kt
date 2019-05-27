package com.isaiahvonrundstedt.bucket.experience.fragments.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.isaiahvonrundstedt.bucket.R
import studio.carbonylgroup.textfieldboxes.ExtendedEditText

class BasicFragment: Fragment() {

    private lateinit var rootView: View
    private lateinit var firstNameField: ExtendedEditText
    private lateinit var lastNameField: ExtendedEditText
    private lateinit var emailField: ExtendedEditText
    private lateinit var continueButton: MaterialButton

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_basic, container, false)

        firstNameField = rootView.findViewById(R.id.firstNameField)
        lastNameField = rootView.findViewById(R.id.lastNameField)
        emailField = rootView.findViewById(R.id.emailField)
        continueButton = rootView.findViewById(R.id.continueButton)

        return rootView
    }

    override fun onStart() {
        super.onStart()

        continueButton.setOnClickListener {

            if (firstNameField.text.toString().isNotBlank() || lastNameField.text.toString().isNotBlank() ||
                    emailField.text.toString().isNotBlank()) {

                val fragment = AuthFragment()
                fragment.arguments = Bundle().apply {
                    putString("firstName", firstNameField.text.toString())
                    putString("lastName", lastNameField.text.toString())
                    putString("email", emailField.text.toString())
                }

                activity!!.supportFragmentManager.beginTransaction().run {
                    setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_out_left, R.anim.slide_in_right)
                    replace(R.id.childLayout, fragment)
                    addToBackStack("basicFragment")
                    commit()
                }

            } else
                Snackbar.make(rootView, R.string.status_blank_fields, Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDetach() {
        super.onDetach()
        activity?.finish()
    }

}