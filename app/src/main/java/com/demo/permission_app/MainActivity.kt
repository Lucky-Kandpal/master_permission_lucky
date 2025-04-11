package com.demo.permission_app

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lucky.master_permission.PermissionHandler
import com.lucky.master_permission.PermissionResultListener

class MainActivity : AppCompatActivity(), PermissionResultListener {
    private lateinit var permissionHandler: PermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        permissionHandler = PermissionHandler(this, this)

        // For example, to request camera (index 2) and location (index 3) permissions:
        permissionHandler.requestPermissions(2,9, 7,1,4,6)
    }
    /**
     * Receives permission results for each group requested.
     */
    override fun onPermissionResult(results: Map<String, Boolean>) {
        results.forEach { (permission, isGranted) ->
            Log.d("MainActivity", "$permission is ${if (isGranted) "granted" else "denied"}")
            // Here you can handle the result for each permission as needed.
        }
    }

    /**
     * Called when all queued permission requests have been processed.
     */
    override fun onAllPermissionsProcessed() {
        Log.d("MainActivity", "All permission requests have been processed.")
        // Perform any final actions after all permissions have been handled.
    }
}