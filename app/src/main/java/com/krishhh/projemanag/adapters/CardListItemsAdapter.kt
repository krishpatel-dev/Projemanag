package com.krishhh.projemanag.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.krishhh.projemanag.models.Card
import com.krishhh.projemanag.databinding.ItemCardBinding

// Create an adapter class for cards list.
open class CardListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Card>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding = ItemCardBinding.inflate(LayoutInflater.from(context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.binding.tvCardName.text = model.name

            holder.binding.root.setOnClickListener {
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

    // A function for OnClickListener where the Interface is the expected parameter..
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // An interface for onclick items.
    interface OnClickListener {
        fun onClick(position: Int, card: Card)
    }

    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    class MyViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root)
}
