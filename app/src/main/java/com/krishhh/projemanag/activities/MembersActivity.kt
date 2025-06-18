package com.krishhh.projemanag.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.krishhh.projemanag.R
import com.krishhh.projemanag.adapters.MemberListItemsAdapter
import com.krishhh.projemanag.models.Board
import com.krishhh.projemanag.utils.Constants
import com.krishhh.projemanag.databinding.ActivityMembersBinding
import com.krishhh.projemanag.firebase.FirestoreClass
import com.krishhh.projemanag.models.User

class MembersActivity : BaseActivity() {

    private lateinit var binding: ActivityMembersBinding
    private lateinit var mBoardDetails: Board

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

    // A function to setup assigned members list into recyclerview.
    fun setupMembersList(list: ArrayList<User>) {

        hideProgressDialog()

        binding.rvMembersList.layoutManager = LinearLayoutManager(this@MembersActivity)
        binding.rvMembersList.setHasFixedSize(true)

        val adapter = MemberListItemsAdapter(this@MembersActivity, list)
        binding.rvMembersList.adapter = adapter
    }


}