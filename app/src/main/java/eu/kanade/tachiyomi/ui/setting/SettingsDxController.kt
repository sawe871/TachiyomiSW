package eu.kanade.tachiyomi.ui.setting

import android.support.v7.preference.PreferenceScreen
import eu.kanade.tachiyomi.data.preference.PreferenceKeys
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceManager
import eu.kanade.tachiyomi.source.online.HttpSource
import eu.kanade.tachiyomi.source.online.LoginSource
import eu.kanade.tachiyomi.widget.preference.LoginCheckBoxPreferenceDex
import eu.kanade.tachiyomi.widget.preference.LoginPreference
import eu.kanade.tachiyomi.widget.preference.SourceLoginDialog
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * MangaDex Settings fragment
 */

class SettingsDxController : SettingsController(), SourceLoginDialog.Listener {
	private val source by lazy { Injekt.get<SourceManager>().get(2499283573021220255) as HttpSource }
	
	override fun setupPreferenceScreen(screen: PreferenceScreen) = with(screen) {

		title = source.name

		val sourcePreference = LoginCheckBoxPreferenceDex(context, source).apply {
			title = "MangaDex Login"
			key = getSourceKey(2499283573021220255)
			setOnLoginClickListener {
				val dialog = SourceLoginDialog(source)
				dialog.targetController = this@SettingsDxController
				dialog.showDialog(router)
			}
		}

		preferenceScreen.addPreference(sourcePreference)

	}

	override fun loginDialogClosed(source: LoginSource) {
		val pref = findPreference(getSourceKey(source.id)) as? LoginCheckBoxPreferenceDex
		pref?.notifyChanged()
	}

	private fun getSourceKey(sourceId: Long): String {
		return "source_$sourceId"
	}

	inline fun PreferenceScreen.mangaDexLogin(
			source: Source,
			block: (@DSL LoginPreference).() -> Unit
	): LoginPreference {
		return initThenAdd(LoginPreference(context).apply {
			key = "source_${source.id}"
			title = "Login"
		}, block)
	}

}
