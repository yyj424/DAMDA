package com.bluelay.damda

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_view_widget_memo.view.*
import java.text.SimpleDateFormat

class WidgetSelectAdapter(val context : Context, private val mmList : ArrayList<MemoInfo>) : RecyclerView.Adapter<WidgetSelectAdapter.ViewHolder>() {
    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun getItemCount() = mmList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_view_widget_memo, parent, false)
        return ViewHolder(context, itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mm = mmList[position]
        holder.getMainMemo(mm)
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    class ViewHolder(val context : Context, itemView: View) : RecyclerView.ViewHolder(itemView), SetMemo{
        private var view : View = itemView
        private val formatWDate = SimpleDateFormat("yy.MM.dd")

        fun getMainMemo(mm: MemoInfo){
            view.tvMemoTitle.text = mm.title
            view.tvMemoTypeNDate.text = mm.type +  "\n" + formatWDate.format(mm.wdate)
            setColor(context, mm.color, view.adapterWidgetMemo)
        }
    }
}