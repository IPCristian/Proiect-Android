package com.example.androidproject

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.androidproject.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private var CURRENT_FRAGMENT = "Home"

class HomeActivity : AppCompatActivity() {

    private lateinit var homeBinding: ActivityHomeBinding
    private lateinit var auth: FirebaseAuth
    private val userDatabase by lazy { UserDatabase.getDatabase(this).userDao() }
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(layoutInflater)
        auth = Firebase.auth
        setContentView(homeBinding.root)
        preferences = getSharedPreferences("CurrentUser", MODE_PRIVATE)

        when(CURRENT_FRAGMENT){
            "Home" -> fragmentSwitch(HomeFragment())
            "Maps" -> fragmentSwitch(MapsFragment())
            "Share" -> fragmentSwitch(SettingsFragment())
        }

        homeBinding.bottomNavigationView?.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> { CURRENT_FRAGMENT = "Home"
                                fragmentSwitch(HomeFragment()) }
                R.id.map -> { CURRENT_FRAGMENT = "Maps"
                            fragmentSwitch(MapsFragment()) }
                R.id.settings -> { CURRENT_FRAGMENT = "Share"
                            fragmentSwitch(SettingsFragment()) }

                // This shouldn't ever happen
                else -> {
                    Log.e("Error","Invalid Item ID")
                }
            }
            true
        }
    }

    // The next functions are for the fragments' use
    fun fragmentSwitch(fragment: Fragment)
    {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(R.anim.fade_in,R.anim.fade_out)
        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }

    fun logOut()
    {
        // Toast.makeText(applicationContext,"Logged out successfully", Toast.LENGTH_SHORT).show()
        Firebase.auth.signOut()
        preferences.edit().remove("name").apply()
        CURRENT_FRAGMENT = "Home"
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return false
    }

    override fun onBackPressed() {
        this.finishAffinity()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
//        Toast.makeText(applicationContext,"onNewIntent() in Home",Toast.LENGTH_SHORT).show()
        setIntent(intent)
        val bundle: Bundle? = intent!!.extras

        if (bundle != null)
        {
            val fragment = bundle.getString("FragmentName")

            if (fragment.equals("Maps"))
            {
                val longitude = bundle.getDouble("Longitude")
                val latitude = bundle.getDouble("Latitude")
                fragmentSwitch(MapsFragment())
            }
            else
            {
                Toast.makeText(applicationContext,"Aici",Toast.LENGTH_SHORT).show()
            }
            Toast.makeText(applicationContext,"Done",Toast.LENGTH_SHORT).show()
        }
    }
}