package com.bluelay.damda

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_unlock_password.*

class UnlockPWActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlock_password)

        var sharedPref = this.getSharedPreferences("memoLock", Context.MODE_PRIVATE)

        etUnlockPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 4) {
                    if (sharedPref.getString("memoLock", "0").equals(s.toString())) {
                        finish()
                    }
                    else {
                        Toast.makeText(this@UnlockPWActivity, "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }
}