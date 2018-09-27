package com.wzx.appshortcuts

import android.content.pm.ShortcutInfo
import android.support.annotation.Nullable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * 描述：
 *
 * 创建人： Administrator
 * 创建时间： 2018/9/27
 * 更新时间：
 * 更新内容：
 */
class ShortcutAdapter(@Nullable var data: ArrayList<ShortcutInfo>?,
                      @Nullable var listener: OnShortcutCliclListener?) : RecyclerView.Adapter<ShortcutAdapter.ShortcutViewHolder>() {


    interface OnShortcutCliclListener {
        fun onItemClick(position: Int, shortcutInfo: ShortcutInfo)
        fun onItemLongClick(position: Int, shortcutInfo: ShortcutInfo)
        fun onDeleteClick(position: Int, shortcutInfo: ShortcutInfo)
    }

    fun setOnShortcutCliclListener(listener: OnShortcutCliclListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int) =
            ShortcutViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_shortcut, parent, false))

    override fun getItemCount() = data?.size ?: 0

    override fun onBindViewHolder(holder: ShortcutViewHolder?, position: Int) {
        val shortcutInfo = data!![position]
        holder?.textView?.text = StringBuilder("isDynamic：").append(shortcutInfo.isDynamic)
                .append("\nshortLabel：").append(shortcutInfo.shortLabel)
                .append("\nlongLabel：").append(shortcutInfo.longLabel)
                .append("\ndisabledMessage：").append(shortcutInfo.disabledMessage)
                .toString()

        holder?.deleteIV?.setOnClickListener {
            listener?.onDeleteClick(position, data!![position])
        }

        holder?.itemView?.setOnClickListener {
            listener?.onItemClick(position, data!![position])
        }

        holder?.itemView?.setOnLongClickListener {
            listener?.onItemLongClick(position, data!![position])
            true
        }
    }

    fun newData(list: ArrayList<ShortcutInfo>) {
        data?.clear()
        data?.addAll(list)
        notifyDataSetChanged()
    }

    class ShortcutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var textView: TextView = itemView.findViewById(R.id.textView)

        var deleteIV: ImageView = itemView.findViewById(R.id.iv_delete)

    }
}