package com.bluelay.damda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_unlock_password.*

class UnlockPWActivity : AppCompatActivity() {
    private lateinit var nextIntent : Intent
    private lateinit var inputMethodManager : InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unlock_password)

        val sharedPref = this.getSharedPreferences("memoLock", Context.MODE_PRIVATE)
        inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        etUnlockPassword.setFocusAndShowKeyboard()
        etUnlockPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString().length == 4) {
                    if (sharedPref.getString("memoLock", "0").equals(s.toString())) {
                        if (intent.hasExtra("memo")) {
                            val memo = intent.getSerializableExtra("memo") as MemoInfo
                            when (memo.type) {
                                "Memo" -> {
                                    nextIntent = Intent(this@UnlockPWActivity, MemoActivity::class.java)
                                }
                                "TodoList" -> {
                                    nextIntent = Intent(this@UnlockPWActivity, ToDoActivity::class.java)
                                }
                                "WishList" -> {
                                    nextIntent = Intent(this@UnlockPWActivity, WishActivity::class.java)
                                }
                                "Weekly" -> {
                                    nextIntent = Intent(this@UnlockPWActivity, WeeklyActivity::class.java)
                                }
                                "Recipe" -> {
                                    nextIntent = Intent(this@UnlockPWActivity, RecipeActivity::class.java)
                                }
                                "Movie" -> {
                                    nextIntent = Intent(this@UnlockPWActivity, MovieActivity::class.java)
                                }
                            }
                            nextIntent.putExtra("memo", memo)
                            startActivity(nextIntent)
                            inputMethodManager.hideSoftInputFromWindow(etUnlockPassword.windowToken, 0)
                            finish()
                        }
                    }
                    else {
                        Toast.makeText(this@UnlockPWActivity, "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show()
                        etUnlockPassword.setFocusAndShowKeyboard()
                    }
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
}