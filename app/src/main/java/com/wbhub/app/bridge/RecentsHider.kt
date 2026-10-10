package com.wbhub.app.bridge

import android.app.Activity
import android.app.ActivityManager

/**
 * Keeps the app's card out of the recents screen.
 *
 * The manifest attribute would do this too, but it is permanent: turning it on
 * there would give up the card the user closes the app with, with no way back
 * short of a rebuild. The platform exposes the same setting on a live task
 * through [ActivityManager.AppTask.setExcludeFromRecents], which is what lets
 * this stay a switch in the settings screen.
 *
 * The flag belongs to the task rather than the activity, so it survives the
 * activity being recreated but not the task being cleared. Reapplying it on
 * every activity start is what keeps it in force.
 */
object RecentsHider {

    /**
     * Applies [hide] to the task that owns [activity].
     *
     * Silently does nothing when the task cannot be found. That happens on a
     * device that manages recents itself and does not report tasks through this
     * API; failing the whole settings change over it would be worse than
     * leaving the card visible.
     */
    fun apply(activity: Activity, hide: Boolean) {
        val manager = activity.getSystemService(ActivityManager::class.java) ?: return
        val task = manager.appTasks.firstOrNull { it.taskInfo.taskId == activity.taskId } ?: return
        runCatching { task.setExcludeFromRecents(hide) }
    }
}
