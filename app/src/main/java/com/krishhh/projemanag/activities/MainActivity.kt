package com.krishhh.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.krishhh.projemanag.R
import com.krishhh.projemanag.adapters.BoardItemsAdapter
import com.krishhh.projemanag.databinding.ActivityMainBinding
import com.krishhh.projemanag.databinding.AppBarMainBinding
import com.krishhh.projemanag.firebase.FirestoreClass
import com.krishhh.projemanag.models.Board
import com.krishhh.projemanag.models.User
import com.krishhh.projemanag.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarBinding: AppBarMainBinding
    private lateinit var mUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appBarBinding = binding.appBarMain

        setupActionBar()

        // Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.
        binding.navView.setNavigationItemSelectedListener(this)

        // Get the current logged in user details.
        FirestoreClass().loadUserData(this@MainActivity, true)

        // Launch the Create Board screen on a fab button click.
        appBarBinding.fabCreateBoard.setOnClickListener {
            val intent = Intent(this@MainActivity, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }

    }

    // Add a onBackPressed function and check if the navigation drawer is open or closed.
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // A double back press function is added in Base Activity.
            doubleBackToExit()
        }
    }

    // Implement members of NavigationView.OnNavigationItemSelectedListener.
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        // Add the click events of navigation menu items.
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {
                // Launch the MyProfileActivity Screen.
                startActivityForResult(Intent(
                    this@MainActivity,
                    MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE
                )
            }

            R.id.nav_sign_out -> {
                // Here sign outs the user from firebase in this device.
                FirebaseAuth.getInstance().signOut()

                // Send the user to the intro screen of the application.
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Add the onActivityResult function and check the result of the activity for which we expect the result.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE) {
            // Get the user updated details.
            FirestoreClass().loadUserData(this@MainActivity)
        } else if (resultCode == Activity.RESULT_OK && requestCode == CREATE_BOARD_REQUEST_CODE) {
            // Get the latest boards list.
            FirestoreClass().getBoardsList(this@MainActivity)
        } else {
            Log.e("Cancelled", "Cancelled")
        }
    }


    // A function to setup action bar
    private fun setupActionBar() {

        setSupportActionBar(appBarBinding.toolbarMainActivity)
        appBarBinding.toolbarMainActivity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        // Add click event for navigation in the action bar and call the toggleDrawer function.
        appBarBinding.toolbarMainActivity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    //A function for opening and closing the Navigation Drawer.
    private fun toggleDrawer() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    // A function to get the current user details from firebase.
    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean) {

        // The instance of the header view of the navigation view.
        val headerView = binding.navView.getHeaderView(0)

        // The instance of the user image of the navigation view.
        val navUserImage = headerView.findViewById<ImageView>(R.id.nav_user_image)

        mUserName = user.name

        // Load the user image in the ImageView.
        Glide
            .with(this@MainActivity)
            .load(user.image) // URL of the image
            .centerCrop() // Scale type of the image.
            .placeholder(R.drawable.ic_user_place_holder) // A default place holder
            .into(navUserImage) // the view in which the image will be loaded.

        // The instance of the user name TextView of the navigation view.
        val navUsername = headerView.findViewById<TextView>(R.id.tv_username)
        // Set the user name
        navUsername.text = user.name

        // Here if the isToReadBoardList is TRUE then get the list of boards.
        if (readBoardsList) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this@MainActivity)
        }

    }

    // A function to populate the result of BOARDS list in the UI i.e in the recyclerView.
    fun populateBoardsListToUI(boardsList: ArrayList<Board>) {
        hideProgressDialog()

        val contentBinding = binding.appBarMain.contentMain

        if (boardsList.isNotEmpty()) {
            contentBinding.rvBoardsList.visibility = View.VISIBLE
            contentBinding.tvNoBoardsAvailable.visibility = View.GONE

            contentBinding.rvBoardsList.layoutManager = LinearLayoutManager(this@MainActivity)
            contentBinding.rvBoardsList.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this@MainActivity, boardsList)
            contentBinding.rvBoardsList.adapter = adapter

            adapter.setOnClickListener(object : BoardItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Board) {
                    startActivity(Intent(this@MainActivity, TaskListActivity::class.java))
                }
            })

        } else {
            contentBinding.rvBoardsList.visibility = View.GONE
            contentBinding.tvNoBoardsAvailable.visibility = View.VISIBLE
        }
    }


    // A companion object to declare the constants.
    companion object {
        // A unique code for starting the activity for result
        const val MY_PROFILE_REQUEST_CODE: Int = 11

        // Add a unique code for starting the create board activity for result
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

}
