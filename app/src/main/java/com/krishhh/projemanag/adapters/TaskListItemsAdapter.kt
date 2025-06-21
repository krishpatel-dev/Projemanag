package com.krishhh.projemanag.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.krishhh.projemanag.R
import com.krishhh.projemanag.activities.TaskListActivity
import com.krishhh.projemanag.models.Task
import com.krishhh.projemanag.databinding.ItemTaskBinding
import java.util.*

open class TaskListItemsAdapter(
    private val context: Context,
    private var list: ArrayList<Task>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // A global variable for position dragged FROM.
    private var mPositionDraggedFrom = -1
    // A global variable for position dragged TO.
    private var mPositionDraggedTo = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val binding = ItemTaskBinding.inflate(LayoutInflater.from(context), parent, false)

        // Here the layout params are converted dynamically according to the screen size as width is 70% and height is wrap_content.
        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        // Here the dynamic margins are applied to the view.
        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)
        binding.root.layoutParams = layoutParams

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            if (position == list.size - 1) {
                holder.binding.tvAddTaskList.visibility = View.VISIBLE
                holder.binding.llTaskItem.visibility = View.GONE
            } else {
                holder.binding.tvAddTaskList.visibility = View.GONE
                holder.binding.llTaskItem.visibility = View.VISIBLE
            }

            // Add a click event for showing the view for adding the task list name. And also set the task list title name.
            holder.binding.tvTaskListTitle.text = model.title

            holder.binding.tvAddTaskList.setOnClickListener {
                holder.binding.tvAddTaskList.visibility = View.GONE
                holder.binding.cvAddTaskListName.visibility = View.VISIBLE
            }

            // Add a click event for hiding the view for adding the task list name.
            holder.binding.ibCloseListName.setOnClickListener {
                holder.binding.tvAddTaskList.visibility = View.VISIBLE
                holder.binding.cvAddTaskListName.visibility = View.GONE
            }

            // Add a click event for passing the task list name to the base activity function. To create a task list.
            holder.binding.ibDoneListName.setOnClickListener {
                val listName = holder.binding.etTaskListName.text.toString()

                if (listName.isNotEmpty()) {
                    // Here we check the context is an instance of the TaskListActivity.
                    if (context is TaskListActivity) {
                        context.createTaskList(listName)
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }

            // Add a click event for iv_edit_list for showing the editable view.
            holder.binding.ibEditListName.setOnClickListener {
                holder.binding.etEditTaskListName.setText(model.title) // Set the existing title
                holder.binding.llTitleView.visibility = View.GONE
                holder.binding.cvEditTaskListName.visibility = View.VISIBLE
            }

            // Add a click event for iv_close_editable_view for hiding the editable view.
            holder.binding.ibCloseEditableView.setOnClickListener {
                holder.binding.llTitleView.visibility = View.VISIBLE
                holder.binding.cvEditTaskListName.visibility = View.GONE
            }

            // Add a click event for iv_edit_list for showing thr editable view.
            holder.binding.ibDoneEditListName.setOnClickListener {
                val listName = holder.binding.etEditTaskListName.text.toString()

                if (listName.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.updateTaskList(position, listName, model)
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name.", Toast.LENGTH_SHORT).show()
                }
            }

            // Add a click event for ib_delete_list for deleting the task list.
            holder.binding.ibDeleteList.setOnClickListener {
                alertDialogForDeleteList(position, model.title)
            }

            // Add a click event for adding a card in the task list.
            holder.binding.tvAddCard.setOnClickListener {

                holder.binding.tvAddCard.visibility = View.GONE
                holder.binding.cvAddCard.visibility = View.VISIBLE

                // Add a click event for closing the view for card add in the task list.
                holder.binding.ibCloseCardName.setOnClickListener {
                    holder.binding.tvAddCard.visibility = View.VISIBLE
                    holder.binding.cvAddCard.visibility = View.GONE
                }

                // Add a click event for adding a card in the task list.
                holder.binding.ibDoneCardName.setOnClickListener {

                    val cardName = holder.binding.etCardName.text.toString()

                    if (cardName.isNotEmpty()) {
                        if (context is TaskListActivity) {
                            context.addCardToTaskList(position, cardName)
                        }
                    }else{
                        Toast.makeText(context, "Please Enter Card Detail.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Load the cards list in the recyclerView.
            holder.binding.rvCardList.layoutManager = LinearLayoutManager(context)
            holder.binding.rvCardList.setHasFixedSize(true)

            val adapter = CardListItemsAdapter(context, model.cards)
            holder.binding.rvCardList.adapter = adapter

            // Add a click event on card items for card details.
            adapter.setOnClickListener(object :
                CardListItemsAdapter.OnClickListener {
                override fun onClick(cardPosition: Int) {

                    if (context is TaskListActivity) {
                        context.cardDetails(position, cardPosition)
                    }
                }
            })

            // Add a feature to drap and drop the card items.
            val dividerItemDecoration =
                DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            holder.binding.rvCardList.addItemDecoration(dividerItemDecoration)

            //  Creates an ItemTouchHelper that will work with the given Callback.
            val helper = ItemTouchHelper(object :
                ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

                /*Called when ItemTouchHelper wants to move the dragged item from its old position to
                 the new position.*/
                override fun onMove(
                    recyclerView: RecyclerView,
                    dragged: ViewHolder,
                    target: ViewHolder
                ): Boolean {
                    val draggedPosition = dragged.adapterPosition
                    val targetPosition = target.adapterPosition

                    // Assign the global variable with updated values.
                    if (mPositionDraggedFrom == -1) {
                        mPositionDraggedFrom = draggedPosition
                    }
                    mPositionDraggedTo = targetPosition

                    // Swaps the elements at the specified positions in the specified list.
                    Collections.swap(list[position].cards, draggedPosition, targetPosition)

                    // move item in `draggedPosition` to `targetPosition` in adapter.
                    adapter.notifyItemMoved(draggedPosition, targetPosition)

                    return false // true if moved, false otherwise
                }

                // Called when a ViewHolder is swiped by the user.
                override fun onSwiped(
                    viewHolder: ViewHolder,
                    direction: Int
                ) { // remove from adapter
                }

                // Finally when the dragging is completed than call the function to update the cards in the database and reset the global variables.
                /*Called by the ItemTouchHelper when the user interaction with an element is over and it
                 also completed its animation.*/
                override fun clearView(recyclerView: RecyclerView, viewHolder: ViewHolder) {
                    super.clearView(recyclerView, viewHolder)

                    if (mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 && mPositionDraggedFrom != mPositionDraggedTo) {

                        (context as TaskListActivity).updateCardsInTaskList(
                            position,
                            list[position].cards
                        )
                    }

                    // Reset the global variables
                    mPositionDraggedFrom = -1
                    mPositionDraggedTo = -1
                }
            })

            /*Attaches the ItemTouchHelper to the provided RecyclerView. If TouchHelper is already
            attached to a RecyclerView, it will first detach from the previous one.*/
            helper.attachToRecyclerView(holder.binding.rvCardList)
        }

    }

    // Gets the number of items in the list
    override fun getItemCount(): Int {
        return list.size
    }

    // A function to get density pixel from pixel
    private fun Int.toDp(): Int =
        (this / Resources.getSystem().displayMetrics.density).toInt()

    // A function to get pixel from density pixel
    private fun Int.toPx(): Int =
        (this * Resources.getSystem().displayMetrics.density).toInt()

    // Method is used to show the Alert Dialog for deleting the task list.
    private fun alertDialogForDeleteList(position: Int, title: String) {
        val builder = AlertDialog.Builder(context)
        //set title for alert dialog
        builder.setTitle("Alert")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete $title.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed

            if (context is TaskListActivity) {
                context.deleteTaskList(position)
            }
        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

    // A ViewHolder describes an item view and metadata about its place within the RecyclerView.
    class MyViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)
}
