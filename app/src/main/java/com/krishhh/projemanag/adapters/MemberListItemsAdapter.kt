package com.krishhh.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krishhh.projemanag.R
import com.krishhh.projemanag.models.User
import com.krishhh.projemanag.databinding.ItemMemberBinding

open class MemberListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<User>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
        }
    }

    // Gets the number of items in the list
    override fun getItemCount(): Int {
        return list.size
    }

    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    private class MyViewHolder(val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root)
}