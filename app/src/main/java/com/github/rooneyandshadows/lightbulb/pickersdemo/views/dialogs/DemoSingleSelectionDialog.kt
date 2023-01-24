package com.github.rooneyandshadows.lightbulb.dialogsdemo.dialogs

import android.content.Context
import android.graphics.drawable.Drawable
import com.github.rooneyandshadows.lightbulb.pickersdemo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.DrawableUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialog
import com.github.rooneyandshadows.lightbulb.pickersdemo.R
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.RadioButtonSelectableAdapter

class DemoSingleSelectionDialog : AdapterPickerDialog<DemoModel>() {
    override val adapterCreator: AdapterCreator<DemoModel>
        get() = object : AdapterCreator<DemoModel> {
            @Override
            override fun createAdapter(): EasyRecyclerAdapter<DemoModel> {
                return object : RadioButtonSelectableAdapter<DemoModel>() {
                    @Override
                    override fun getItemIcon(context: Context, item: DemoModel): Drawable {
                        return AppIconUtils.getIconWithAttributeColor(
                            context,
                            item.icon,
                            R.attr.colorOnSurface,
                            R.dimen.ICON_SIZE_RECYCLER_ITEM
                        )
                    }

                    @Override
                    override fun getItemIconBackground(context: Context, item: DemoModel): Drawable {
                        return DrawableUtils.getRoundedCornersDrawable(
                            item.iconBackgroundColor.color,
                            ResourceUtils.dpToPx(100)
                        )
                    }
                }
            }
        }

    companion object {
        fun newInstance(): DemoSingleSelectionDialog {
            return DemoSingleSelectionDialog()
        }
    }
}