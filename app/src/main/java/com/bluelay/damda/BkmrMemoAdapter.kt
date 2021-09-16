package com.bluelay.damda

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.adapter_view_bkmr_memo.view.*
import java.text.SimpleDateFormat

class BkmrMemoAdapter(val context : Context, private val bmList : ArrayList<MemoInfo>, private val edit : Boolean) : RecyclerView.Adapter<BkmrMemoAdapter.ViewHolder>() {
    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: ItemClickListener

    fun makeBKMRItem(){
        notifyDataSetChanged()
    }

    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun getItemCount() = bmList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_view_bkmr_memo, parent, false)

        return ViewHolder(context, itemView, edit)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bm = bmList[position]
        holder.getBkmrMemo(bm)
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    class ViewHolder(val context : Context, itemView: View, private val edit : Boolean) : RecyclerView.ViewHolder(itemView), SetMemo{
        private var view : View = itemView
        private val formatWDate = SimpleDateFormat("yy.MM.dd")
        fun getBkmrMemo(bm: MemoInfo){
            if (edit) {
                view.ck_bkmrMemo.visibility = View.VISIBLE
            }
            else {
                view.ck_bkmrMemo.visibility = View.GONE
            }
            view.ck_bkmrMemo.setOnCheckedChangeListener(null)
            view.ck_bkmrMemo.isChecked = bm.check
            view.tv_bkmrTitle.text = bm.title
            view.tv_bkmrTypeNDate.text = bm.type + "\n" + formatWDate.format(bm.wdate)
            setColor(context, bm.color, view.adapterBkmrMemo)
            view.ck_bkmrMemo.setOnCheckedChangeListener { _, isChecked ->
                bm.check = isChecked
            }
        }
    }
}