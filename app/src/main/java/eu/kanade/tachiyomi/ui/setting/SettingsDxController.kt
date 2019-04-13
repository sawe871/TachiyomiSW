package eu.kanade.tachiyomi.ui.setting

import android.os.Bundle
import android.support.v7.preference.PreferenceScreen
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import eu.kanade.tachiyomi.data.preference.PreferenceKeys
import eu.kanade.tachiyomi.ui.manga.info.MangaWebViewController
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceManager
import eu.kanade.tachiyomi.source.online.HttpSource
import eu.kanade.tachiyomi.ui.base.controller.BaseController
import eu.kanade.tachiyomi.ui.base.controller.withFadeTransaction
import eu.kanade.tachiyomi.util.WebViewClientCompat
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import kotlinx.android.synthetic.main.pref_library_columns.view.*


/**
 * MangaDex Settings fragment
 */

class SettingsDxController : SettingsController() {
	private val id = 2499283573021220255
	private val source by lazy { Injekt.get<SourceManager>().get(id) as HttpSource }

	override fun setupPreferenceScreen(screen: PreferenceScreen) = with(screen) {
		title = source.name
		preference {
			titleRes = R.string.pref_theme // TODO change this thing's name
			onClick {
				openLoginWebview()
			}
		}
	}
	private fun openLoginWebview() {
		// TODO - inject JS into login page webview
		router.pushController(MangaWebViewController(id, "https://mangadex.org/login").withFadeTransaction())
	}

}
