package eu.kanade.tachiyomi.ui.source.browse

import android.util.TypedValue
import android.view.View
import com.bumptech.glide.load.engine.DiskCacheStrategy
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.kanade.tachiyomi.data.database.models.Manga
import eu.kanade.tachiyomi.data.glide.GlideApp
import eu.kanade.tachiyomi.data.glide.toMangaThumbnail
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.preference.getOrDefault
import eu.kanade.tachiyomi.widget.StateImageViewTarget
import kotlinx.android.synthetic.main.source_grid_item.card
import kotlinx.android.synthetic.main.source_grid_item.progress
import kotlinx.android.synthetic.main.source_grid_item.thumbnail
import kotlinx.android.synthetic.main.source_grid_item.title
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

/**
 * Class used to hold the displayed data of a manga in the catalogue, like the cover or the title.
 * All the elements from the layout file "item_source_grid" are available in this class.
 *
 * @param view the inflated view for this holder.
 * @param adapter the adapter handling this holder.
 * @constructor creates a new catalogue holder.
 */
class SourceGridHolder(private val view: View, private val adapter: FlexibleAdapter<*>) :
    SourceHolder(view, adapter) {

    private val preferences: PreferencesHelper = Injekt.get()

    /**
     * Method called from [CatalogueAdapter.onBindViewHolder]. It updates the data for this
     * holder with the given manga.
     *
     * @param manga the manga to bind.
     */
    override fun onSetValues(manga: Manga) {
        // Set alpha of thumbnail.
        thumbnail.alpha = if (manga.favorite) 0.3f else 1.0f

        setImage(manga)
    }

    override fun setImage(manga: Manga) {
        // Set manga title
        title.text = manga.title

        card.radius = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            preferences.eh_library_corner_radius().getOrDefault().toFloat(),
            view.context.resources.displayMetrics
        )

        GlideApp.with(view.context).clear(thumbnail)
        if (!manga.thumbnail_url.isNullOrEmpty()) {
            GlideApp.with(view.context)
                .load(manga.toMangaThumbnail())
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
                .placeholder(android.R.color.transparent)
                .into(StateImageViewTarget(thumbnail, progress))
        }
    }
}
