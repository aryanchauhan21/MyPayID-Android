package com.leotarius.mypayid

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    var user: FirebaseUser? = null
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mainViewModel: MainActivityViewModel
    private lateinit var drawerLayout: DrawerLayout

    companion object{
        fun hideKeyboardFrom(context: Context, view: View) {
            val imm: InputMethodManager = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Instantiate current user
        val user = FirebaseAuth.getInstance().currentUser
        if(user == null){
            navigateToLoginScreen()
        }

        // UI stuff
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        drawerLayout = findViewById(R.id.drawer_layout)
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setupWithNavController(navController, appBarConfiguration)

        val navView: NavigationView = findViewById(R.id.nav_view)
        navView.setupWithNavController(navController)


        // NavigationUI handles all fragment transitions itself
        // To do some extra work instead for fragment transition
        // we can do it on setNavigationItemSelectedListener
//        navView.setNavigationItemSelectedListener {
//            when(it.itemId){
//                R.id.logout -> {
//                    logoutUser()
//                    drawerLayout.closeDrawer(GravityCompat.START)
//                    true
//                }
//                else -> false
//            }
//        }

        // View model declaration
        mainViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        // observing the dbUser live data from viewModel
        mainViewModel.getUser()?.observe(this, Observer {
            // setting values of nav header
            if (it != null) {
                full_name.text = (it.name)
                email.text = (it.email)
            }
        })

        // making function trigger call to get database object, this should trigger above observe function
        mainViewModel.getUserFromDatabase()

    }

    private fun navigateToLoginScreen() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        navigateToLoginScreen()
    }


    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

//    private fun replaceFragment(fragment: Fragment){
//        val fragmentManager = supportFragmentManager
//        val transaction = fragmentManager.beginTransaction()
//        transaction.replace(R.id.nav_host_fragment, fragment)
//        transaction.addToBackStack(null)
//        transaction.commit()
//        drawerLayout.closeDrawer(GravityCompat.START)
//    }

}
