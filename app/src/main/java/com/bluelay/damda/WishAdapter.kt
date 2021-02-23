package com.bluelay.damda

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat.startActivity

class WishAdapter(val context: Context, val wishList: ArrayList<Wish>) : BaseAdapter() {
    //var v : View = LayoutInflater.from(context).inflate(R.layout.activity_wish, null)
    //var activityTotal = v.findViewById<TextView>(R.id.tvWishTotal)
    interface TotalListner {
        fun passTotal(data: String)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = LayoutInflater.from(context).inflate(R.layout.adapter_view_wish, null)
        val cbWish = view.findViewById<CheckBox>(R.id.cbWish)
        val etWishItem = view.findViewById<EditText>(R.id.etWishItem)
        val etWishPrice = view.findViewById<EditText>(R.id.etWishPrice)
        val btnWishLink = view.findViewById<Button>(R.id.btnWishLink)

        val wish = wishList[position]
        cbWish.isChecked = wish.checked == 1
        if(cbWish.isChecked == true) {
            etWishItem.setTextColor(Color.parseColor("#969191"))
            etWishPrice.setTextColor(Color.parseColor("#969191"))
        }
        etWishItem.setText(wish.item)
        var total = 0

        if (wish.price == null) {
            etWishPrice.setText("")
        }
        else {
            etWishPrice.setText(wish.price.toString())
        }
        if (wish.link != "") {
            btnWishLink.setBackgroundResource(R.drawable.link_checked)
        }
        else {
            btnWishLink.setBackgroundResource(R.drawable.link_default)
        }

        etWishItem.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                wish.item = s.toString()
            }
        })

        etWishPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != "") {
                    wish.price = Integer.parseInt(s.toString())
                }
                total = 0
                for (w in wishList) {
                    if (w.price != null) {
                        total += w.price!!
                    }
                }
                Log.d("yyj", "total : " + total.toString())
                //activityTotal.setText(total.toString())
                //passTotal(total.toString())
            }
        })

        cbWish.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                wish.checked = 1
                etWishItem.setTextColor(Color.parseColor("#969191"))
                etWishPrice.setTextColor(Color.parseColor("#969191"))
            }
            else {
                wish.checked = 0
                etWishItem.setTextColor(Color.parseColor("#000000"))
                etWishPrice.setTextColor(Color.parseColor("#000000"))
            }
        }

        btnWishLink.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_wish_link, null)
            val etWishLink = view.findViewById<EditText>(R.id.etWishLink)
            val btnWishLinkOpen = view.findViewById<Button>(R.id.btnWishLInkOpen)

            etWishLink.setText(wish.link)

            btnWishLinkOpen.setOnClickListener {
                var open = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(etWishLink.text.toString())
                )

                fun String.isValidUrl(): Boolean = Patterns.WEB_URL.matcher(this).matches()

                if (!etWishLink.text.toString().isValidUrl()) {
                    Toast.makeText(context, "올바른 형식이 아닙니다.", Toast.LENGTH_SHORT).show()
                } else{
                    startActivity(context, open, null)
                }
            }

            builder.setView(view)
                    .setPositiveButton("OK") { dialogInterface, i ->
                        if (!etWishLink.text.toString().replace(" ", "").equals("")) {
                            btnWishLink.setBackgroundResource(R.drawable.link_checked)
                            wish.link = etWishLink.text.toString()
                        }
                        else {
                            btnWishLink.setBackgroundResource(R.drawable.link_default)
                            wish.link = ""
                        }
                    }
                    .show()
        }
        return view
    }
    override fun getCount(): Int {
        return wishList.size
    }

    override fun getItem(position: Int): Any {
        return wishList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}