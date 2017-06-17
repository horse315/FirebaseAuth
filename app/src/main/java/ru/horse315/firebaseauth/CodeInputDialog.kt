package ru.horse315.firebaseauth

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.view.View

class CodeInputDialog: DialogFragment() {
    companion object{
        private val RESULT_CODE = "RESULT_CODE"
        private val RESEND_TIMEOUT = "RESEND_TIMEOUT"

        fun createInstance(resultCode: Int, resendTimeout: Long): CodeInputDialog{
            val args = Bundle()
            args.putInt(RESULT_CODE, resultCode)
            args.putLong(RESEND_TIMEOUT, resendTimeout)

            val dialog = CodeInputDialog()
            dialog.arguments = args
            return dialog
        }
    }

    val codeInput: AppCompatEditText by lazy(LazyThreadSafetyMode.NONE) { dialog.findViewById(R.id.codeInput) as AppCompatEditText }
    val btnResendCode: AppCompatButton by lazy(LazyThreadSafetyMode.NONE) { dialog.findViewById(R.id.btnResendCode) as AppCompatButton }

    var resultCode: Int = -1
    var resendTimeout: Long = -1
    var timer: CountDownTimer? = null

    init{
        retainInstance = true
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(activity)
                .setView(R.layout.code_imput_dialog)
                .setTitle("Enter SMS code")
                .create()

        return dialog
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        arguments ?: IllegalStateException("Dialog arguments are not set properly!")

        resultCode = arguments.getInt(RESULT_CODE)
        resendTimeout = arguments.getLong(RESEND_TIMEOUT)

        resetCountdownTimer()
    }

    fun resetCountdownTimer(){
        timer?.cancel()

        btnResendCode.isEnabled = false
        btnResendCode.text = "Can resend in $resendTimeout"

        timer = object: CountDownTimer(resendTimeout * 1000, 1000){
            override fun onFinish() {
                btnResendCode.isEnabled = true
                btnResendCode.text = "Resend code"
            }

            override fun onTick(millisUntilFinished: Long) {
                btnResendCode.text = "Can resend in ${millisUntilFinished / 1000}"
            }
        }

        timer?.start()
    }

    fun setCode(smsCode: String){
        codeInput.text.clear()
        codeInput.text.append(smsCode)
    }
}
