package com.isaiahvonrundstedt.bucket.fragments.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
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
            if (emailField.text.isNotEmpty()){
                val action = EmailFragmentDirections.actionContinueEmail(emailField.text.toString())
                Navigation.findNavController(view).navigate(action)
            } else
                Snackbar.make(view, R.string.status_blank_fields, Snackbar.LENGTH_SHORT).show()
        }
    }
}