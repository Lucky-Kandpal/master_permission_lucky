package com.lucky.master_permission


/**
 * Callback interface for permission results.
 */
interface PermissionResultListener {
    /**
     * Called when a group of permissions have been processed.
     *
     * @param results Map where key is the permission and value indicates whether it is granted.
     */
    fun onPermissionResult(results: Map<String, Boolean>)

    /**
     * Called when all queued permission requests have been processed.
     */
    fun onAllPermissionsProcessed()
}
