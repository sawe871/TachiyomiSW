package eu.kanade.tachiyomi.ui.migration

import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.util.system.getResourceColor

/**
 * Adapter that holds the catalogue cards.
 *
 * @param controller instance of [MigrationController].
 */
class SourceAdapter(val controller: MigrationController) :
    FlexibleAdapter<IFlexible<*>>(null, controller, true) {

    val cardBackground = controller.activity!!.getResourceColor(R.attr.colorSurface)

    private var items: List<IFlexible<*>>? = null

    init {
        setDisplayHeadersAtStartUp(true)
    }

    /**
     * Listener for browse item clicks.
     */
    val selectClickListener: OnSelectClickListener? = controller

    /**
     * Listener which should be called when user clicks select.
     */
    interface OnSelectClickListener {
        fun onSelectClick(position: Int)
    }

    /**
     * Listener for auto item clicks.
     */
    val autoClickListener: OnAutoClickListener? = controller

    /**
     * Listener which should be called when user clicks select.
     */
    interface OnAutoClickListener {
        fun onAutoClick(position: Int)
    }

    override fun updateDataSet(items: MutableList<IFlexible<*>>?) {
        if (this.items !== items) {
            this.items = items
            super.updateDataSet(items)
        }
    }
}
