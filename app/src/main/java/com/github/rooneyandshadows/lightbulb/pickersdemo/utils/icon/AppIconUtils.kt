package com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.DimenRes
import com.github.rooneyandshadows.lightbulb.commons.utils.IconUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerAdapter.*
import com.github.rooneyandshadows.lightbulb.pickersdemo.R
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIcons
import com.mikepenz.iconics.IconicsDrawable
import java.util.ArrayList

object AppIconUtils {
    fun getIconWithAttributeColor(
        context: Context,
        icon: IDemoIcon,
        @AttrRes colorRef: Int,
        @DimenRes dimenId: Int
    ): IconicsDrawable {
        val sizeInPx: Int = ResourceUtils.getDimenPxById(context, dimenId)
        return IconUtils.getIconWithAttributeColor(context, icon.icon, colorRef, sizeInPx)
    }

    fun getIconWithAttributeColor(context: Context, icon: IDemoIcon): IconicsDrawable {
        val sizeInPx: Int = ResourceUtils.getDimenPxById(context, R.dimen.ICON_SIZE_MEDIUM)
        return IconUtils.getIconWithAttributeColor(context, icon.icon, R.attr.colorPrimary, sizeInPx)
    }

    val allForPicker: ArrayList<IconModel>
        get() {
            val result: ArrayList<IconModel> = ArrayList<IconModel>()
            for (icon in DemoIcons.values()) result.add(
                IconModel(icon.iconName, IconSet.FONTAWESOME)
            )
            return result
        }
}