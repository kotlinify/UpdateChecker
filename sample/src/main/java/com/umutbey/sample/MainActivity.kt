package com.umutbey.sample

import android.os.Bundle

import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.kotlinify.updatechecker.GoogleChecker

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        GoogleChecker(activity = this@MainActivity,packageName = "com.teknasyon.photofont", lang = "en", showPopup = false){
            Toast.makeText(this@MainActivity, "Is There a New Version: $it", Toast.LENGTH_SHORT).show()

        }


        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            GoogleChecker( activity = this@MainActivity,packageName = "com.teknasyon.photofont", haveNoButton = true, lang = "en")
//            GoogleChecker( activity = this@MainActivity, haveNoButton = true, lang = "en")
//            GoogleChecker( activity = this@MainActivity,packageName = "com.teknasyon.photofont", haveNoButton = true, lang = "en")
//            GoogleChecker( activity = this@MainActivity)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }
}
