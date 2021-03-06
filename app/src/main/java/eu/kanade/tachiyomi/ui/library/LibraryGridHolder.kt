package eu.kanade.tachiyomi.ui.library

import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.kanade.tachiyomi.data.glide.GlideApp
import eu.kanade.tachiyomi.data.glide.toMangaThumbnail
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.preference.getOrDefault
import eu.kanade.tachiyomi.source.LocalSource
import eu.kanade.tachiyomi.util.view.visibleIf
import kotlinx.android.synthetic.main.source_grid_item.card
import kotlinx.android.synthetic.main.source_grid_item.download_text
import kotlinx.android.synthetic.main.source_grid_item.local_text
import kotlinx.android.synthetic.main.source_grid_item.thumbnail
import kotlinx.android.synthetic.main.source_grid_item.title
import kotlinx.android.synthetic.main.source_grid_item.unread_text
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * Class used to hold the displayed data of a manga in the library, like the cover or the title.
 * All the elements from the layout file "item_source_grid" are available in this class.
 *
 * @param view the inflated view for this holder.
 * @param adapter the adapter handling this holder.
 * @param listener a listener to react to single tap and long tap events.
 * @constructor creates a new library holder.
 */
class LibraryGridHolder(
    private val view: View,
    adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>

) : LibraryHolder(view, adapter) {
    private val preferences: PreferencesHelper = Injekt.get()
    /**
     * Method called from [LibraryCategoryAdapter.onBindViewHolder]. It updates the data for this
     * holder with the given manga.
     *
     * @param item the manga item to bind.
     */
    override fun onSetValues(item: LibraryItem) {
        // Update the title of the manga.
        title.text = item.manga.title

        // Update the unread count and its visibility.
        with(unread_text) {
            visibleIf { item.manga.unread > 0 }
            text = item.manga.unread.toString()
        }
        // Update the download count and its visibility.
        with(download_text) {
            visibleIf { item.downloadCount > 0 }
            text = item.downloadCount.toString()
        }
        // set local visibility if its local manga
        local_text.visibleIf { item.manga.source == LocalSource.ID }

        card.radius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            preferences.eh_library_corner_radius().getOrDefault().toFloat(),
            view.context.resources.displayMetrics
        )

        // Update the cover.
        GlideApp.with(view.context).clear(thumbnail)
        GlideApp.with(view.context)
            .load(item.manga.toMangaThumbnail())
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .centerCrop()
            .into(thumbnail)
    }
}
