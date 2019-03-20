package eu.kanade.tachiyomi.source.online.all

import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.preference.getOrDefault
import exh.NHENTAI_SOURCE_ID
import exh.metadata.models.NHentaiMetadata
import exh.metadata.models.PageImageType
import exh.metadata.models.Tag
import exh.util.NHUtils.Companion.getArtists
import exh.util.NHUtils.Companion.getGroups
import exh.util.NHUtils.Companion.getTags
import exh.util.NHUtils.Companion.getTime
import rx.Observable
import com.google.gson.JsonObject
import exh.util.*
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.asObservableSuccess
import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import eu.kanade.tachiyomi.source.online.LewdSource
import eu.kanade.tachiyomi.util.asJsoup
import okhttp3.*
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.net.URLEncoder
import android.content.Context
import eu.kanade.tachiyomi.source.model.Filter

class SortFilter : Filter.Select<String>("Sort", arrayOf("Popular", "Date"))

class NHentai(context: Context) : ParsedHttpSource() {

    override val client: OkHttpClient = network.cloudflareClient.newBuilder().addInterceptor { chain ->
        val url = chain.request().url().toString()
         // ghetto throttlin
	Thread.sleep(250)
         chain.proceed(chain.request())
    }.build()

    final override val baseUrl = "https://nhentai.net"
    override val name = "nhentai"
    override val supportsLatest = true
    override val lang = "all"
    override val id = NHENTAI_SOURCE_ID
//    override fun queryAll() = NHentaiMetadata.EmptyQuery()
//    override fun queryFromUrl(url: String) = NHentaiMetadata.UrlQuery(url)

//compat with old EH extension
    override fun mangaDetailsRequest(manga: SManga): Request {
        return GET(baseUrl + "/g/" + manga.url.split("/").last { it.isNotBlank() }, headers)
    }
    override fun fetchMangaDetails(manga: SManga): Observable<SManga> {
        return client.newCall(mangaDetailsRequest(manga))
                .asObservableSuccess()
                .map { response ->
                    mangaDetailsParse(response).apply { initialized = true }
                }
    }

    override fun headersBuilder(): Headers.Builder {
	return Headers.Builder()
		.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36")
    }

    private val searchUrl = "$baseUrl/search"

    override fun chapterFromElement(element: Element) = throw UnsupportedOperationException("Not used")
//    override fun fetchSearchManga(page: Int, query: String, filters: FilterList) =
  //          urlImportFetchSearchManga(query, {
    //            super.searchMangaRequest(page, query, filters)
      //      })

    override fun chapterListParse(response: Response): List<SChapter> {
        val document = response.asJsoup()
        val chapterList = mutableListOf<SChapter>()
        val chapter = SChapter.create().apply {
            name = "Chapter"
            scanlator = getGroups(document)
            date_upload = getTime(document)
            setUrlWithoutDomain(response.request().url().encodedPath())
        }
	chapter.url = response.request().url().encodedPath()
        chapterList.add(chapter)

        return chapterList
    }

    override fun chapterListRequest(manga: SManga): Request {
        return GET(baseUrl + "/g/" + manga.url.split("/").last { it.isNotBlank() }, headers)
    }


    override fun chapterListSelector() = throw UnsupportedOperationException("Not used")

    override fun getFilterList(): FilterList = FilterList(SortFilter())

    override fun imageUrlParse(document: Document) = throw UnsupportedOperationException("Not used")

    override fun latestUpdatesFromElement(element: Element) = SManga.create().apply {
        setUrlWithoutDomain(element.select("a").attr("href"))
        title = element.select("a > div").text().replace("\"", "").trim()
    }

    override fun latestUpdatesNextPageSelector() = "#content > section.pagination > a.next"

    override fun latestUpdatesRequest(page: Int) = GET("$baseUrl/?page=$page", headers)

    override fun latestUpdatesSelector() = "#content > div > div"

    override fun mangaDetailsParse(document: Document) = SManga.create().apply {
        title = document.select("#info > h1").text().replace("\"", "").trim()
        thumbnail_url = document.select("#cover > a > img").attr("data-src")
        status = SManga.COMPLETED
        artist = getArtists(document)
        author = artist
        description = getTags(document)
    }

    override fun pageListParse(document: Document): List<Page> {
        val pageElements = document.select("#thumbnail-container > div")
        val pageList = mutableListOf<Page>()

        pageElements.forEach {
            Page(pageList.size).run {
//		if(Injekt.get<PreferencesHelper>().eh_nh_useHighQualityThumbs().getOrDefault()) {
	        this.imageUrl = it.select("a > img").attr("data-src").replace("t.nh", "i.nh").replace("t.", ".")
//		} else {
//			this.imageUrl = it.select("a > img").attr("data-src")
//		}
                pageList.add(pageList.size, this)
            }
        }

        return pageList
    }

    override fun pageListRequest(chapter: SChapter) = GET("$baseUrl${chapter.url}", headers)

    override fun popularMangaFromElement(element: Element) = SManga.create().apply {
        setUrlWithoutDomain(element.select("a").attr("href"))
        title = element.select("a > div").text().replace("\"", "").trim()
    }

    override fun popularMangaNextPageSelector() = "#content > section.pagination > a.next"

    override fun popularMangaRequest(page: Int) = GET("$baseUrl/language/translated/popular?page=$page", headers)
	
    //override fun popularMangaRequest(page: Int) = GET("$baseUrl/popular?page=$page", headers)//this is broken on purpose. causes 429s
	
    override fun popularMangaSelector() = "#content > div > div"

    override fun searchMangaFromElement(element: Element) = SManga.create().apply {
        setUrlWithoutDomain(element.select("a").attr("href"))
        title = element.select("a > div").text().replace("\"", "").trim()
    }

    override fun searchMangaNextPageSelector() = "#content > section.pagination > a.next"

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        val stringBuilder = StringBuilder()
        stringBuilder.append(searchUrl)
        stringBuilder.append("/?q=${URLEncoder.encode("$query", "UTF-8")}&")

        filters.forEach {
            when (it) {
                is SortFilter -> stringBuilder.append("sort=${it.values[it.state].toLowerCase()}&")
            }
        }

        stringBuilder.append("page=$page")

        return GET(stringBuilder.toString(), headers)
    }


    override fun searchMangaSelector() = "#content > div > div"


//    override val metaParser: NHentaiMetadata.(JsonObject) -> Unit = {}
}
