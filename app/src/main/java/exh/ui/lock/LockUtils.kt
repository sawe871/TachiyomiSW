package exh.ui.lock

import android.app.AppOpsManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import com.afollestad.materialdialogs.MaterialDialog
import com.elvishew.xlog.XLog
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.preference.getOrDefault
import java.security.MessageDigest
import kotlin.experimental.and
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * Password hashing utils
 */

/**
 * Yes, I know SHA512 is fast, but bcrypt on mobile devices is too slow apparently
 */
fun sha512(passwordToHash: String, salt: String): String {
    val md = MessageDigest.getInstance("SHA-512")
    md.update(salt.toByteArray(charset("UTF-8")))
    val bytes = md.digest(passwordToHash.toByteArray(charset("UTF-8")))
    val sb = StringBuilder()
    for (i in bytes.indices) {
        sb.append(Integer.toString((bytes[i] and 0xff.toByte()) + 0x100, 16).substring(1))
    }
    return sb.toString()
}

/**
 * Check if lock is enabled
 */
fun lockEnabled(prefs: PreferencesHelper = Injekt.get()) =
    prefs.eh_lockHash().get() != null &&
        prefs.eh_lockSalt().get() != null &&
        prefs.eh_lockLength().getOrDefault() != -1

/**
 * Check if the lock will function properly
 *
 * @return true if action is required, false if lock is working properly
 */
fun notifyLockSecurity(
    context: Context,
    prefs: PreferencesHelper = Injekt.get()
): Boolean {
    return false
    if (!prefs.eh_lockManually().getOrDefault() &&
        !hasAccessToUsageStats(context)
    ) {
        MaterialDialog(context)
            .title(text = "Permission required")
            .message(
                text = "${context.getString(R.string.app_name)} requires the usage stats permission to detect when you leave the app. " +
                    "This is required for the application lock to function properly. " +
                    "Press OK to grant this permission now."
            )
            .negativeButton(R.string.action_cancel)
            .positiveButton(android.R.string.ok) {
                try {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                } catch (e: ActivityNotFoundException) {
                    XLog.e("Device does not support USAGE_ACCESS_SETTINGS shortcut!")
                    MaterialDialog(context)
                        .title(text = "Grant permission manually")
                        .message(
                            text = "Failed to launch the window used to grant the usage stats permission. " +
                                "You can still grant this permission manually: go to your phone's settings and search for 'usage access'."
                        )
                        .positiveButton(android.R.string.ok) { it.dismiss() }
                        .cancelable(true)
                        .cancelOnTouchOutside(false)
                        .show()
                }
            }
            .cancelable(false)
            .show()
        return true
    } else {
        return false
    }
}

fun hasAccessToUsageStats(context: Context): Boolean {
    return try {
        val packageManager = context.packageManager
        val applicationInfo = packageManager.getApplicationInfo(context.packageName, 0)
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName)
        (mode == AppOpsManager.MODE_ALLOWED)
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}
