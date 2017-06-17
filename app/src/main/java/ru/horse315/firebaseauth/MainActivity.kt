package ru.horse315.firebaseauth

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    lateinit var phone: AppCompatEditText
    lateinit var btn: AppCompatButton
    lateinit var logs: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        phone = findViewById(R.id.phone_number) as AppCompatEditText
        btn = findViewById(R.id.log_me_in) as AppCompatButton
        logs = findViewById(R.id.logs) as LinearLayout

        FirebaseApp.initializeApp(this)

        btn.setOnClickListener {
            btn.isEnabled = false
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
            btn.isEnabled = true

            // This callback will be invoked in two situations:
            // 1 - Instant verification. In some cases the phone number can be instantly
            //     verified without needing to send or enter a verification code.
            // 2 - Auto-retrieval. On some devices Google Play services can automatically
            //     detect the incoming verification SMS and perform verificaiton without
            //     user action.
            log("onVerificationCompleted", credential.toString())
        }

        override fun onVerificationFailed(e: FirebaseException) {
            btn.isEnabled = true

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
            log("onCodeSent", "verificationId: ${verificationId}\ntoken: ${token}")

            // open enter code dialog
        }

        override fun onCodeAutoRetrievalTimeOut(msg: String?) {
            log("onCodeAutoRetrievalTimeOut", msg ?: "NONE")
        }
    }

    fun log(caption: String, message: String){
        val v = TextView(this)

        val text = SpannableString("$caption: $message")

        text.setSpan(ForegroundColorSpan(Color.BLUE), 0, caption.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        v.text = text
        v.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        logs.addView(v)
    }
}
