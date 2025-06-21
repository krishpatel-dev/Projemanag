package com.krishhh.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krishhh.projemanag.R
import com.krishhh.projemanag.models.User
import com.krishhh.projemanag.databinding.ItemMemberBinding
import com.krishhh.projemanag.utils.Constants

open class MemberListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            ItemMemberBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.binding.ivMemberImage)

            holder.binding.tvMemberName.text = model.name
            holder.binding.tvMemberEmail.text = model.email

            if (model.selected) {
                holder.binding.ivSelectedMember.visibility = View.VISIBLE
            } else {
                holder.binding.ivSelectedMember.visibility = View.GONE
            }

            holder.itemView.setOnClickListener {

                if (onClickListener != null) {
                    if (model.selected) {
                        onClickListener!!.onClick(position, model, Constants.UN_SELECT)
                    } else {
                        onClickListener!!.onClick(position, model, Constants.SELECT)
                    }
                }
            }
        }
    }

    // Gets the number of items in the list
    override fun getItemCount(): Int {
        return list.size
    }

    // A function for OnClickListener where the Interface is the expected parameter..
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // An interface for onclick items.
    interface OnClickListener {
        fun onClick(position: Int, user: User, action: String)
    }

    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    private class MyViewHolder(val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root)
}