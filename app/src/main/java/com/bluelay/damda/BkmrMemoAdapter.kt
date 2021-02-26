package com.bluelay.damda

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_view_bkmr_memo.view.*
import kotlinx.android.synthetic.main.adapter_view_main_memo.view.*

class BkmrMemoAdapter(val context : Context, val bmList : ArrayList<BkmrMemo>) : RecyclerView.Adapter<BkmrMemoAdapter.ViewHolder>() {

    override fun getItemCount() = bmList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_view_bkmr_memo, parent, false)

        return ViewHolder(context, itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bm = bmList[position]
        holder.getBkmrMemo(bm)
    }


    class ViewHolder(val context : Context, itemView: View) : RecyclerView.ViewHolder(itemView), SetMemo{
        private var view : View = itemView
        fun getBkmrMemo(bm: BkmrMemo){
            view.tv_bkmrType.text = bm.type
            view.tv_bkmrDate.text = bm.wdate
            setColor(context, bm.color, view.adapterBkmrMemo)
        }
    }
}