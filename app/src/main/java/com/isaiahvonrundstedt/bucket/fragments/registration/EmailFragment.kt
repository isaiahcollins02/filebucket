package com.isaiahvonrundstedt.bucket.fragments.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.isaiahvonrundstedt.bucket.R
import com.isaiahvonrundstedt.bucket.components.abstracts.BaseFragment
import com.isaiahvonrundstedt.bucket.constants.Params
import kotlinx.android.synthetic.main.fragment_auth_email.*

class EmailFragment: BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_email, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        continueButton.setOnClickListener {
            if (emailField.text != null){
                val arguments = Bundle()
                arguments.putString(Params.email, emailField.text.toString())

                val authFragment = AuthFragment()
                authFragment.arguments = arguments

                activity?.supportFragmentManager?.beginTransaction()?.run {
                    setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_out_left, R.anim.slide_in_right)
                    replace(R.id.childLayout, authFragment)
                    addToBackStack("viewTag")
                    commit()
                }
            } else
                Snackbar.make(view, R.string.status_blank_fields, Snackbar.LENGTH_SHORT).show()
        }
    }
}