package com.shubham.inapp.update

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import java.lang.ref.WeakReference

/**
 * InAppUpdateManager is a singleton utility class for handling Android In-App Updates
 * using Google's Play Core library. It supports both IMMEDIATE and FLEXIBLE update flows.
 *
 * This class simplifies the integration of in-app update checks, flow triggering,
 * resume logic, and lifecycle-safe memory management using WeakReferences.
 *
 * @author
 * Created by Shubham Kansagra on 27-05-2025.
 */
object InAppUpdateManager {

    private const val TAG = "InAppUpdateManager"
    private const val UPDATE_REQUEST_CODE = 123

    private var activityRef: WeakReference<Activity>? = null
    private var updateManager: AppUpdateManager? = null
    private var callback: InAppUpdateCallback? = null
    private var updateType: Int = AppUpdateType.IMMEDIATE

    /**
     * Initializes the update manager and begins checking for updates.
     *
     * @param activity The host activity used to show update UI.
     * @param isForceUpdate If true, uses IMMEDIATE update; otherwise, uses FLEXIBLE update.
     * @param callback The callback interface to receive update events.
     */
    fun init(
        activity: Activity,
        isForceUpdate: Boolean = false,
        callback: InAppUpdateCallback
    ) {
        this.activityRef = WeakReference(activity)
        this.callback = callback
        this.updateType = if (isForceUpdate) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE
        this.updateManager = AppUpdateManagerFactory.create(activity)

        if (updateType == AppUpdateType.FLEXIBLE) {
            updateManager?.registerListener(installStateUpdatedListener)
        }

        checkForUpdates()
        resumeUpdate() // Needed in case update was interrupted previously
    }

    /**
     * Listener used only in FLEXIBLE update flow to complete updates after download.
     */
    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADED -> {
                Log.d(TAG, "Update downloaded, completing update.")
                updateManager?.completeUpdate()
            }

            InstallStatus.INSTALLED -> {
                Log.d(TAG, "Update installed.")
                callback?.onUpdateSuccess()
            }

            else -> {
                Log.d(TAG, "Update status: ${state.installStatus()}")
            }
        }
    }

    /**
     * Checks for available app updates and starts the update flow if eligible.
     */
    private fun checkForUpdates() {
        updateManager?.appUpdateInfo
            ?.addOnSuccessListener { info ->
                val isUpdateAvailable =
                    info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                val isAllowed = when (updateType) {
                    AppUpdateType.FLEXIBLE -> info.isFlexibleUpdateAllowed
                    AppUpdateType.IMMEDIATE -> info.isImmediateUpdateAllowed
                    else -> false
                }

                if (isUpdateAvailable && isAllowed) {
                    activityRef?.get()?.let { activity ->
                        // Note: This method is deprecated. For future-proofing, migrate to ActivityResultLauncher.
                        updateManager?.startUpdateFlowForResult(
                            info,
                            updateType,
                            activity,
                            UPDATE_REQUEST_CODE
                        )
                    }
                } else {
                    callback?.onUpdateSuccess()
                }
            }
            ?.addOnFailureListener {
                Log.e(TAG, "Update check failed: ${it.message}")
                callback?.onUpdateFailed()
            }
    }

    /**
     * Resumes update flow for IMMEDIATE updates that were previously interrupted
     * (e.g., if the app was killed or paused mid-update).
     *
     * This should be called during the host Activity's onResume() lifecycle method.
     */
    fun resumeUpdate() {
        if (updateType == AppUpdateType.IMMEDIATE) {
            updateManager?.appUpdateInfo?.addOnSuccessListener { info ->
                if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    activityRef?.get()?.let { activity ->
                        updateManager?.startUpdateFlowForResult(
                            info,
                            updateType,
                            activity,
                            UPDATE_REQUEST_CODE
                        )
                    }
                }
            }
        }
    }

    /**
     * Handles the result from the update flow. Must be called from your Activity's `onActivityResult()`.
     *
     * @param requestCode Request code received in onActivityResult().
     * @param resultCode Result code received in onActivityResult().
     */
    fun handleResult(requestCode: Int, resultCode: Int) {
        if (requestCode == UPDATE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Log.d(TAG, "User accepted the update.")
                    callback?.onUpdateSuccess()
                }

                Activity.RESULT_CANCELED -> {
                    Log.d(TAG, "User canceled the update.")
                    callback?.onUpdateCanceled()
                    activityRef?.get()
                        ?.finish() // Optional: close app if mandatory update was skipped
                }

                else -> {
                    activityRef?.get()?.let {
                        Toast.makeText(it, "Something went wrong during update", Toast.LENGTH_SHORT)
                            .show()
                    }
                    callback?.onUpdateFailed()
                }
            }
        }
    }

    /**
     * Cleans up internal references and unregisters any listeners.
     * Should be called in the Activity's onDestroy() to avoid memory leaks.
     */
    fun destroy() {
        updateManager?.unregisterListener(installStateUpdatedListener)
        updateManager = null
        activityRef = null
        callback = null
    }
}

/**
 * Interface to receive callbacks from InAppUpdateManager.
 */
interface InAppUpdateCallback {
    /**
     * Called when the update was completed successfully or no update was required.
     */
    fun onUpdateSuccess()

    /**
     * Called when the user canceled the update.
     */
    fun onUpdateCanceled()

    /**
     * Called when the update process failed or encountered an error.
     */
    fun onUpdateFailed()
}