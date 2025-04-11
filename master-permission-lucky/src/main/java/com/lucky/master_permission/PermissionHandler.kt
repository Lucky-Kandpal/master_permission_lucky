package com.lucky.master_permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

/**
 * A reusable class for handling runtime permissions sequentially.
 *
 * Index Mapping:
 * 0: CALL_PHONE
 * 1: READ_CALL_LOG
 * 2: READ_SMS
 * 3: SEND_SMS
 * 4: RECEIVE_SMS
 * 5: READ_CONTACTS
 * 6: CAMERA
 * 7: ACCESS_FINE_LOCATION
 * 8: ACCESS_COARSE_LOCATION
 * 9: REQUEST_IGNORE_BATTERY_OPTIMIZATIONS (handled separately)
 * 10: Read storage (uses new media permissions on API 33+)
 * 11: Write storage (legacy permission on API < 33; use MediaStore for API 33+)
 * 12: POST_NOTIFICATIONS (only for API 33+)
 * 13: ACCESS_BACKGROUND_LOCATION (only for API 29+)
 */
class PermissionHandler(
    private val activity: AppCompatActivity,
    private val listener: PermissionResultListener
) {

    // Mapping indexes to corresponding permission strings.
    private val permissionMapping: Map<Int, List<String>> = mapOf(
        0 to listOf(Manifest.permission.CALL_PHONE),
        1 to listOf(Manifest.permission.READ_CALL_LOG),
        2 to listOf(Manifest.permission.READ_SMS),
        3 to listOf(Manifest.permission.SEND_SMS),
        4 to listOf(Manifest.permission.RECEIVE_SMS),
        5 to listOf(Manifest.permission.READ_CONTACTS),
        6 to listOf(Manifest.permission.CAMERA),
        7 to listOf(Manifest.permission.ACCESS_FINE_LOCATION),
        8 to listOf(Manifest.permission.ACCESS_COARSE_LOCATION),
        9 to listOf(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS),
        10 to if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        },
        11 to if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For API 33+, writing to shared storage should be handled via MediaStore.
            emptyList()
        } else {
            listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        },
        12 to if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyList()
        },
        13 to if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            listOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            emptyList()
        }
    )

    // Queue to hold permission indexes to process sequentially.
    private val pendingIndexes = mutableListOf<Int>()

    // Launcher to request runtime permissions.
    private val permissionLauncher: ActivityResultLauncher<Array<String>> =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            // Notify the listener for each permission result.
            listener.onPermissionResult(results)
            // Process the next permission in the queue.
            processNextPermission()
        }

    /**
     * Request one or more permissions sequentially by specifying their indexes.
     *
     * @param indexes Integer indexes from the permissionMapping.
     */
    fun requestPermissions(vararg indexes: Int) {
        pendingIndexes.clear()
        pendingIndexes.addAll(indexes.toList())
        processNextPermission()
    }

    /**
     * Processes the next permission request in the queue.
     * If the mapped list is empty, it will be skipped.
     */
    private fun processNextPermission() {
        if (pendingIndexes.isEmpty()) {
            listener.onAllPermissionsProcessed()
            return
        }

        val nextIndex = pendingIndexes.removeAt(0)

        // Handle battery optimization separately.
        if (nextIndex == 9) {
            requestBatteryOptimizationPermission()
            processNextPermission()
            return
        }

        val perms = permissionMapping[nextIndex]
        if (perms.isNullOrEmpty()) {
            Log.d("PermissionHandler", "Index $nextIndex has an empty permission list; skipping.")
            processNextPermission()
        } else {
            permissionLauncher.launch(perms.toTypedArray())
            // Once the launcher callback returns, processNextPermission() is called.
        }
    }

    /**
     * Requests battery optimization exemption by launching the system settings intent.
     */
    private fun requestBatteryOptimizationPermission() {
        val pm = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
        val packageName = activity.packageName
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                }
                activity.startActivity(intent)
                Log.d("PermissionHandler", "Battery optimization exemption requested.")
            } catch (e: Exception) {
                Log.e("PermissionHandler", "Battery optimization request failed: ${e.message}")
            }
        } else {
            Log.d("PermissionHandler", "Battery optimization already ignored.")
        }
    }
}