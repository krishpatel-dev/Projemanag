package com.krishhh.projemanag.activities

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.krishhh.projemanag.R
import com.krishhh.projemanag.adapters.MemberListItemsAdapter
import com.krishhh.projemanag.models.Board
import com.krishhh.projemanag.utils.Constants
import com.krishhh.projemanag.databinding.ActivityMembersBinding
import com.krishhh.projemanag.databinding.DialogSearchMemberBinding
import com.krishhh.projemanag.firebase.FirestoreClass
import com.krishhh.projemanag.models.User

class MembersActivity : BaseActivity() {

    private lateinit var binding: ActivityMembersBinding
    private lateinit var mBoardDetails: Board
    private lateinit var mAssignedMembersList: ArrayList<User>
    private var anyChangesMade: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get the Board Details through intent and assign it to the global variable.
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra<Board>(Constants.BOARD_DETAIL)!!
        }

        // Call the setup action bar function.
        setupActionBar()

        // Get the members list details from the database.
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAssignedMembersListDetails(
            this@MembersActivity,
            mBoardDetails.assignedTo
        )

    }

    // A function to setup action bar
    private fun setupActionBar() {

        setSupportActionBar(binding.toolbarMembersActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.members)
        }

        binding.toolbarMembersActivity.setNavigationOnClickListener { onBackPressed() }
    }

    // Inflate the menu file for adding the member and also add the onOptionItemSelected function.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu to use in the action bar
        menuInflater.inflate(R.menu.menu_add_member, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_add_member -> {
                dialogSearchMember()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Send the result to the base activity onBackPressed.
    override fun onBackPressed() {
        if (anyChangesMade) {
            setResult(Activity.RESULT_OK)
        }
        super.onBackPressed()
    }


    // A function to setup assigned members list into recyclerview.
    fun setupMembersList(list: ArrayList<User>) {
        mAssignedMembersList = list

        hideProgressDialog()

        binding.rvMembersList.layoutManager = LinearLayoutManager(this@MembersActivity)
        binding.rvMembersList.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this@MembersActivity, list)
        binding.rvMembersList.adapter = adapter
    }


    // Method is used to show the Custom Dialog.
    private fun dialogSearchMember() {
        val dialog = Dialog(this)
        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        val dialogBinding = DialogSearchMemberBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.tvAdd.setOnClickListener {

            val email = dialogBinding.etEmailSearchMember.text.toString()

            if (email.isNotEmpty()) {
                dialog.dismiss()
                // Get the member details from the database.
                showProgressDialog(resources.getString(R.string.please_wait))
                FirestoreClass().getMemberDetails(this@MembersActivity, email)

            } else {
                Toast.makeText(
                    this@MembersActivity,
                    "Please enter member's email address.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        dialogBinding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        //Start the dialog and display it on screen.
        dialog.show()
    }

    // Here we will get the result of the member if it found in the database.
    fun memberDetails(user: User) {
        // Check if the user is already in the assigned members list
        val isAlreadyAssigned = mAssignedMembersList.any { it.id == user.id }

        if (isAlreadyAssigned) {
            hideProgressDialog()
            Toast.makeText(
                this@MembersActivity,
                "Member is already assigned to this board.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Here add the user id to the existing assigned members list of the board.
            mBoardDetails.assignedTo.add(user.id)
            // Finally assign the member to the board.
            FirestoreClass().assignMemberToBoard(this@MembersActivity, mBoardDetails, user)
        }
    }

    // A function to get the result of assigning the members.
    fun memberAssignSuccess(user: User) {
        hideProgressDialog()
        mAssignedMembersList.add(user)

        anyChangesMade = true

        setupMembersList(mAssignedMembersList)
    }


}