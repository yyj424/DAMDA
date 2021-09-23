package com.bluelay.damda

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.sulek.ssml.OnSwipeListener
import kotlinx.android.synthetic.main.adapter_view_main_memo.view.*
import java.text.SimpleDateFormat

class MainMemoAdapter(val context : Context, private val mmList : ArrayList<MemoInfo>, private val edit : Boolean) : RecyclerView.Adapter<MainMemoAdapter.ViewHolder>() {
    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: ItemClickListener
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    fun deleteItem(i : Int){
        mmList.removeAt(i)
        notifyDataSetChanged()
    }

    fun makeBKMRItem(i : Int){
        mmList.removeAt(i)
        notifyDataSetChanged()
    }

    override fun getItemCount() = mmList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.adapter_view_main_memo, parent, false)
        return ViewHolder(context, itemView, edit)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mm = mmList[position]
        holder.getMainMemo(mm)
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
        holder.setSampleData(mm)

    }

    class ViewHolder(val context : Context, itemView: View, private val edit : Boolean) : RecyclerView.ViewHolder(itemView), SetMemo{
        private var view : View = itemView
        private val formatWDate = SimpleDateFormat("yy.MM.dd")

        fun setSampleData(memoInfo: MemoInfo) {
            itemView.swipeContainer.setOnSwipeListener(object : OnSwipeListener {
                override fun onSwipe(isExpanded: Boolean) {
                    memoInfo.isExpanded = isExpanded
                }
            })
            itemView.swipeContainer.apply(memoInfo.isExpanded)
        }

        fun getMainMemo(mm: MemoInfo){
            if (edit) {
                view.ck_mainMemo.visibility = View.VISIBLE
            }
            else {
                view.ck_mainMemo.visibility = View.GONE
            }

            view.ck_mainMemo.setOnCheckedChangeListener(null)
            view.ck_mainMemo.isChecked = mm.check
            view.tvMemoTitle.text = mm.title
            view.tvMemoTypeNDate.text = mm.type +  "\n" + formatWDate.format(mm.wdate)
            setColor(context, mm.color, view.adapterMainMemo)
            view.ck_mainMemo.setOnCheckedChangeListener { _, isChecked ->
                mm.check = isChecked
            }
        }
    }
}