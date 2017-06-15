package ru.horse315.firebaseauth

import android.os.Bundle
import android.support.v4.widget.TextViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException



class MainActivity : AppCompatActivity() {

    lateinit var phone: AppCompatEditText
    lateinit var btn: AppCompatButton
    lateinit var logs: LinearLayout

    init{
        FirebaseApp.initializeApp(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        phone = findViewById(R.id.phone_number) as AppCompatEditText
        btn = findViewById(R.id.log_me_in) as AppCompatButton
        logs = findViewById(R.id.logs) as LinearLayout

        btn.setOnClickListener {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phone.text.toString(),
                    60,
                    TimeUnit.SECONDS,
                    this,
                    authCallback);
        }
    }

    val authCallback = object : OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verificaiton without
            //     user action.
            log("onVerificationCompleted", credential.toString())

        }

        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.

            if (e is FirebaseAuthInvalidCredentialsException) {
                // Invalid request
                // ...
            } else if (e is FirebaseTooManyRequestsException) {
                // The SMS quota for the project has been exceeded
                // ...
            }

            // Show a message and update the UI
            // ...

            log("onVerificationFailed", e.toString())
        }

        override fun onCodeSent(verificationId: String,
                       token: PhoneAuthProvider.ForceResendingToken) {
            // The SMS verification code has been sent to the provided phone number, we
            // now need to ask the user to enter the code and then construct a credential
            // by combining the code with a verification ID.

            // Save verification ID and resending token so we can use them later
            val mVerificationId = verificationId
            val mResendToken = token

            // ...
            log("onCodeSent", "verificationId: ${verificationId}\ntoken: ${token}")
        }

        override fun onCodeAutoRetrievalTimeOut(msg: String?) {
            log("onCodeAutoRetrievalTimeOut", msg ?: "NONE")
        }
    }

    fun log(caption: String, message: String){
        val v = TextView(this)
        v.text = "${caption}: $message"
        v.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        logs.addView(v)
    }
}
