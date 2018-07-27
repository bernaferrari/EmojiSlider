package com.bernaferrari.emojislidersample.groupie

import android.view.ViewGroup.MarginLayoutParams
import com.bernaferrari.emojislidersample.R
import com.bernaferrari.emojislidersample.extensions.pxToDp
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_emojipicker.*

/**
 * Creates a ColorPicker RecyclerView. This will be used on create/edit dialogs.
 *
 * @param isFirstIndex if true, adds extra padding to the first item
 * @param isSwitchOn if true, sets the item as selected
 * @param gradientColor the color pair which will be set for this item
 */
class EmojiPickerItem(
    val emoji: String,
    private val isFirstIndex: Boolean,
    var isSwitchOn: Boolean,
    private val listener: (EmojiPickerItem) -> (Unit)
) : Item() {

    override fun getLayout(): Int = R.layout.item_emojipicker

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.containerView.setOnClickListener {
            // We don't want to allow deselection
            if (!isSwitchOn) {
                isSwitchOn = true
                listener.invoke(this)
                viewHolder.paintItem.reverseSelection()
            }
        }

        updateContainerMargins(viewHolder)

        // first time it loads, or if recycles, the gradientColor need to be set correctly.
        if (viewHolder.paintItem.emoji != emoji) {
            viewHolder.paintItem.emoji = emoji
            viewHolder.paintItem.updateColor()

            // select/deselect without animation. Sometimes this will be called while scrolling,
            // so we want it to behave invisibly.
            when (isSwitchOn) {
                true -> viewHolder.paintItem.selectIfDeselected(false)
                false -> viewHolder.paintItem.deselectIfSelected(false)
            }
        }

        // when deselectAndNotify is called, this is used to deselect with animation.
        when (isSwitchOn) {
            true -> viewHolder.paintItem.selectIfDeselected(true)
            false -> viewHolder.paintItem.deselectIfSelected(true)
        }
    }

    private fun updateContainerMargins(viewHolder: ViewHolder) {
        // This is necessary for extra left padding on the first item, so it looks
        // visually identical to other items.
        // Since it is inside a recyclerView, the margin needs to be set at all the items.
        viewHolder.containerView.run {
            val dp8 = pxToDp(SMALL, resources)
            val dp16 = pxToDp(MEDIUM, resources)
            val dp32 = pxToDp(BIG, resources)

            val layoutParams = layoutParams as? MarginLayoutParams ?: return
            layoutParams.width = dp32
            layoutParams.height = dp32

            if (isFirstIndex) {
                layoutParams.setMargins(dp16, dp8, dp8, dp8)
            } else {
                layoutParams.setMargins(dp8, dp8, dp8, dp8)
            }

            viewHolder.containerView.layoutParams = layoutParams
        }
    }

    fun deselectAndNotify() {
        isSwitchOn = false
        notifyChanged()
    }

    private companion object {
        private const val SMALL = 8
        private const val MEDIUM = 16
        private const val BIG = 32
    }
}
