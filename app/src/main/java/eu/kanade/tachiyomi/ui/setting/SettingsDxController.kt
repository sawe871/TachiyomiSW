package eu.kanade.tachiyomi.ui.setting

import android.support.v7.preference.PreferenceScreen
import eu.kanade.tachiyomi.data.preference.PreferenceKeys

/**
 * MangaDex Settings fragment
 */

class SettingsDxController : SettingsController() {
	override fun setupPreferenceScreen(screen: PreferenceScreen) = with(screen) {
		title = "MangaDex"
// TODO - login
//		trackPreference(trackManager.myAnimeList) {
//			onClick {
//				val dialog = SourceLoginDialog(trackManager.myAnimeList)
//				dialog.targetController = this@SettingsTrackingController
//				dialog.showDialog(router)
//			}
//		}

	}
}
