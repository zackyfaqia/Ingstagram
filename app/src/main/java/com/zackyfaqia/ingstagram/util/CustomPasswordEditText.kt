package com.zackyfaqia.ingstagram.util

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.zackyfaqia.ingstagram.R
import com.zackyfaqia.ingstagram.data.Constants.MIN_PASSWORD_LENGTH
import com.zackyfaqia.ingstagram.ui.auth.login.LoginActivity
import com.zackyfaqia.ingstagram.ui.auth.register.RegisterActivity

class CustomPasswordEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                LoginActivity.isErrorPassword(false)
                RegisterActivity.isErrorPassword(false)

                if (!p0.toString().isEmpty() && p0.toString().length < MIN_PASSWORD_LENGTH) {
                    error = context.getString(R.string.password_error)
                    LoginActivity.isErrorPassword(true)
                    RegisterActivity.isErrorPassword(true)
                }
            }
        })
    }
}