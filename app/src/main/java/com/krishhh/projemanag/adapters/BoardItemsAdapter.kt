package com.krishhh.projemanag.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krishhh.projemanag.databinding.ItemBoardBinding
import com.krishhh.projemanag.models.Board

open class BoardItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Board>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    // Inflates the item layout and returns the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemBoardBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    // Binds data to each item in the list
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(com.krishhh.projemanag.R.drawable.ic_board_place_holder)
                .into(holder.binding.ivBoardImage)

            holder.binding.tvName.text = model.name
            holder.binding.tvCreatedBy.text = "Created By: ${model.createdBy}"

            holder.itemView.setOnClickListener {
                // Calls the click listener when an item is clicked
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }


    // Gets the number of items in the list
    override fun getItemCount(): Int {
        return list.size
    }

    // A function for OnClickListener where the Interface is the expected parameter.
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // An interface for onclick items.
    interface OnClickListener {
        fun onClick(position: Int, model: Board)
    }



    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    class MyViewHolder(val binding: ItemBoardBinding) : RecyclerView.ViewHolder(binding.root)
}