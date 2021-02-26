package com.bluelay.damda

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.adapter_view_main_memo.view.*

class MainMemoAdapter(val context : Context, val mmList : ArrayList<MainMemo>) : RecyclerView.Adapter<MainMemoAdapter.ViewHolder>() {
//    var mmList = ArrayList<MainMemo>()

    override fun getItemCount() = mmList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_view_main_memo, parent, false)

        return ViewHolder(context, itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mm = mmList[position]
        holder.getMainMemo(mm)
    }

    class ViewHolder(val context : Context, itemView: View) : RecyclerView.ViewHolder(itemView), SetMemo{
        private var view : View = itemView

        fun getMainMemo(mm: MainMemo){
            view.tvMemoType.text = mm.type
            view.tvMemoDate.text = mm.wdate
            setColor(context, mm.color, view.adapterMainMemo)
        }
    }
}