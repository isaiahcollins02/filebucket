package com.isaiahvonrundstedt.bucket.interfaces

interface FirebaseAuthVerifier {
    fun onVerified(status: Boolean)
}