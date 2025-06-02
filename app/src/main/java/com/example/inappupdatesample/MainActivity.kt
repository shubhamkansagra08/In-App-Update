package com.example.inappupdatesample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shubham.inapp.update.InAppUpdateCallback
import com.shubham.inapp.update.InAppUpdateManager

class MainActivity : AppCompatActivity(), InAppUpdateCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        InAppUpdateManager.init(this, true, this)
    }

    override fun onUpdateSuccess() {
        startNextActivity()
    }

    override fun onUpdateCanceled() {
        Toast.makeText(this, "Update canceled", Toast.LENGTH_SHORT).show()
    }

    override fun onUpdateFailed() {
        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        InAppUpdateManager.destroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        InAppUpdateManager.handleResult(requestCode, resultCode)
    }

    private fun startNextActivity() {
        // Start your next activity here
    }
}