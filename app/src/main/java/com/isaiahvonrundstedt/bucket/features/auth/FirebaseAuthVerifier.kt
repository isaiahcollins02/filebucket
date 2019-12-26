package com.isaiahvonrundstedt.bucket.features.auth

interface FirebaseAuthVerifier {
    fun onVerified(status: Boolean)
}