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
			titleRes = R.string.label_login // TODO change this thing's name
			onClick {
				openLoginWebview()
			}
		}
	}
	private fun openLoginWebview() {
		// TODO - inject JS into login page webview
		val script="""var logout=function(){$.ajax({url:'https://mangadex.org/ajax/actions.ajax.php?function=logout',type:'post',success:function(h){location.reload(true)}})};var div=$("<div />",{html:"<style>body>:not(#content),#content>*:not(#login_container),#forgot_button,#signup_button,[id='2fa_field']+div{display:none}</style>"}).appendTo("body");if($("#login_container p").length){$("#login_container p")[0].innerHTML='<button onmousedown="logout()" tabindex="5" class="btn btn-lg btn-danger btn-block" type="submit" id="login_button"><span class="fas fa-sign-out-alt fa-fw " aria-hidden="true"></span>Log Out</button>'}else{$("#remember_me")[0].checked=1}"""
		router.pushController(MangaWebViewController(id, "https://mangadex.org/login", js=script).withFadeTransaction())
	}

}
