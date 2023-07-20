package com.github.rooneyandshadows.lightbulb.pickersdemo.views.dialogs

import android.content.Context
import android.graphics.drawable.Drawable
import com.github.rooneyandshadows.lightbulb.commons.utils.DrawableUtils.Companion.getRoundedCornersDrawable
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.adapter.DialogPickerCheckBoxAdapter
import com.github.rooneyandshadows.lightbulb.pickersdemo.R.attr.colorOnSurface
import com.github.rooneyandshadows.lightbulb.pickersdemo.R.dimen.ICON_SIZE_RECYCLER_ITEM
import com.github.rooneyandshadows.lightbulb.pickersdemo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils.getIconWithAttributeColor

class DemoMultipleSelectionDialog : AdapterPickerDialog<DemoModel>() {
    override val adapter: DialogPickerCheckBoxAdapter<DemoModel>
        get() = super.adapter as DialogPickerCheckBoxAdapter

    companion object {
        fun newInstance(): DemoMultipleSelectionDialog {
            return DemoMultipleSelectionDialog()
        }
    }

    init {
        setAdapter(createAdapter())
    }

    private fun createAdapter(): DialogPickerCheckBoxAdapter<DemoModel> {
        return object : DialogPickerCheckBoxAdapter<DemoModel>() {
            @Override
            override fun getItemIcon(context: Context, item: DemoModel): Drawable {
                return getIconWithAttributeColor(context, item.icon, colorOnSurface, ICON_SIZE_RECYCLER_ITEM)
            }

            @Override
            override fun getItemIconBackground(context: Context, item: DemoModel): Drawable {
                return getRoundedCornersDrawable(item.iconBackgroundColor.color, ResourceUtils.dpToPx(100))
            }
        }
    }
}