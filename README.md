master_permission_lucky


https://github.com/user-attachments/assets/804d37be-1a04-4eb0-85b0-3efb8d253cee

A lightweight, reusable Android library that simplifies runtime permission management across API levels 26–35. This library processes permission requests sequentially and delivers results via a callback interface, making it easy to integrate permission handling in any Android project.

Key Features:

Sequential Permission Requests:
Processes permissions one-by-one to avoid overlapping dialogs.

API-Level Handling:
Supports deprecated permissions (e.g. legacy storage) and conditional requests (e.g. POST_NOTIFICATIONS on Android 13+).

Battery Optimization Handling:
Special handling for permissions like REQUEST_IGNORE_BATTERY_OPTIMIZATIONS.

Callback Interface:
Uses a simple interface to decouple permission logic from your UI, allowing customized handling of each permission result.

Easy Integration:
Designed as a library module (or AAR) that can be added to any Android project.

Get started by integrating the library in your project and implementing the provided PermissionResultListener in your host Activity to handle permission results effortlessly.


Here’s a concise and well-structured `README.md` content for your GitHub repository:



## 🔧 Features

- ✅ Sequential permission requests
- 📱 API-specific permission handling (Android 13+)
- 🔋 Handles special permissions (e.g. battery optimization)
- 🔁 Reusable and easy to integrate
- 🎯 Simple callback interface for permission results

---

## 🛠️ Installation

### Step 1: Add JitPack to your root `build.gradle` (or `settings.gradle.kts`)

```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2: Add the dependency

```groovy
dependencies {
    implementation 'com.github.Lucky-Kandpal:master_permission_lucky:1.0.1'
}
```

---

## 🚀 Usage

### Implement the `PermissionResultListener` in your Activity:

```kotlin
class MainActivity : AppCompatActivity(), PermissionResultListener {

    private lateinit var permissionHandler: PermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionHandler = PermissionHandler(this, this)

        // Request multiple permissions by index
        permissionHandler.requestPermissions(
            6,  // CAMERA
            7,  // ACCESS_FINE_LOCATION
            10, // REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            12, // POST_NOTIFICATIONS
            13  // ACCESS_BACKGROUND_LOCATION
        )
    }

    override fun onPermissionResult(results: Map<String, Boolean>) {
        results.forEach { (permission, isGranted) ->
            Log.d("PermissionResult", "$permission: $isGranted")
        }
    }

    override fun onAllPermissionsProcessed() {
        Log.d("PermissionFlow", "All permissions have been handled.")
    }
}
```

---

## 📋 Supported Permissions (Index Map)

| Index | Permission                              |
|-------|------------------------------------------|
| 0     | CALL_PHONE                               |
| 1     | READ_CALL_LOG                            |
| 2     | READ_SMS                                 |
| 3     | SEND_SMS                                 |
| 4     | RECEIVE_SMS                              |
| 5     | READ_CONTACTS                            |
| 6     | CAMERA                                   |
| 7     | ACCESS_FINE_LOCATION                     |
| 8     | ACCESS_COARSE_LOCATION                   |
| 9     | READ/WRITE_EXTERNAL_STORAGE (legacy)     |
| 10    | REQUEST_IGNORE_BATTERY_OPTIMIZATIONS     |
| 11    | READ_MEDIA_IMAGES/VIDEO/AUDIO (API 33+)  |
| 12    | POST_NOTIFICATIONS (API 33+)             |
| 13    | ACCESS_BACKGROUND_LOCATION (API 29+)     |

---




[![](https://jitpack.io/v/Lucky-Kandpal/master_permission_lucky.svg)](https://jitpack.io/#Lucky-Kandpal/master_permission_lucky)
