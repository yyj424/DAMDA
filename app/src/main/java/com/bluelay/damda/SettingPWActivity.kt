package com.bluelay.damda

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_setting_pw.*

class SettingPWActivity : AppCompatActivity() {
    private lateinit var nPassword : String
    private lateinit var cPassword : String
    private lateinit var sharedPref : SharedPreferences
    private lateinit var inputMethodManager : InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_pw)

        sharedPref = getSharedPreferences("memoLock", Context.MODE_PRIVATE)
        cPassword = sharedPref.getString("memoLock", "0").toString()
        inputMethodManager =
            this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(cPassword != "0") {
            tvSetPWState.text = getString(R.string.input_cur_pw)
        }
        else {
            tvSetPWState.text = getString(R.string.input_new_pw)
        }
        etSetPassword.setFocusAndShowKeyboard()

        etSetPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 4) {
                    if (cPassword != "0") {
                        if (checkPassword(cPassword, s.toString())) {
                            tvSetPWState.text = getString(R.string.input_new_pw)
                            cPassword = "0"
                        } else {
                            tvSetPWState.text = getString(R.string.diff_pw)
                        }
                    } else {
                        if (::nPassword.isInitialized) { // 두번째 입력
                            if (checkPassword(nPassword, s.toString())) {
                                sharedPref.edit().apply {
                                    putString("memoLock", nPassword)
                                    apply()
                                }
                                inputMethodManager.hideSoftInputFromWindow(etSetPassword.windowToken, 0)
                                finish()
                            } else {
                                tvSetPWState.text = getString(R.string.diff_pw)
                            }
                        } else {
                            nPassword = s.toString()
                            tvSetPWState.text = getString(R.string.re_input_new_pw)
                        }
                    }
                    etSetPassword.setFocusAndShowKeyboard()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun EditText.setFocusAndShowKeyboard() {
        this.requestFocus()
        setSelection(this.text.length)
        this.postDelayed({
            inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_FORCED)
        }, 100)
        this.setText("")
    }

    private fun checkPassword(p1 : String, p2 : String) : Boolean {
        return p1 == p2
    }
}