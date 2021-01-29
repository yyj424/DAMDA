package com.bluelay.damda

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.EditText

class BucketAdapter (val context : Context, val bucketList : ArrayList<Bucket>) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = LayoutInflater.from(context).inflate(R.layout.adapter_view_bucket, null)

        val cbBucket = view.findViewById<CheckBox>(R.id.cbBucket)
        val etBucket = view.findViewById<EditText>(R.id.etBucket)

        val bucket = bucketList[position]
        cbBucket.isChecked = bucket.checked == 1
        etBucket.setText(bucket.content)

        etBucket.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                bucketList[position].content = s.toString()
            }
        })

        cbBucket.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) bucket.checked = 1
            else bucket.checked = 0
        }

        return view
    }
    override fun getCount(): Int {
        return bucketList.size
    }

    override fun getItem(position: Int): Any {
        return bucketList[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }
}