package com.bluelay.damda

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import org.w3c.dom.Text

class WishAdapter (val context : Context, val wishList : ArrayList<Wish>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = LayoutInflater.from(context).inflate(R.layout.adapter_view_wish, null)

        val cbWish = view.findViewById<CheckBox>(R.id.cbWish)
        val etWishItem = view.findViewById<EditText>(R.id.etWishItem)
        val etWishPrice = view.findViewById<EditText>(R.id.etWishPrice)
        val cbWishLink = view.findViewById<CheckBox>(R.id.cbWishLink)

        val wish = wishList[position]
        cbWish.isChecked = wish.checked == 1
        etWishItem.setText(wish.item)
        etWishPrice.setText(wish.price)
        cbWishLink.isChecked = wish.link == 1

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                wishList[position].item = s.toString()
                wishList[position].price = s.toString()
            }
        }

        cbWish.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) wish.checked = 1
            else wish.checked = 0
        }

        val checkedListner = object : CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if(isChecked) wish.checked = 1
            else wish.checked = 0
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