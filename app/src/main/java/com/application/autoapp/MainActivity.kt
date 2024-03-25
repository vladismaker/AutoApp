package com.application.autoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.application.autoapp.frameworks.ListFragment
import com.application.autoapp.repositories.DataAutoRepository

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DataAutoRepository.REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            val fragment = supportFragmentManager.findFragmentById(R.id.container_main)
            if (fragment is ListFragment) {
                fragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }
}