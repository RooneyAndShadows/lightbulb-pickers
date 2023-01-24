package com.github.RooneyAndShadows.lightbulb.pickersdemo.utils.icon

import android.content.Context
import androidx.annotation.AttrRes
import androidx.annotation.DimenRes
import com.github.RooneyAndShadows.lightbulb.commons.utils.IconUtils
import com.github.RooneyAndShadows.lightbulb.pickersdemo.utils.icon.icons.DemoIcons
import java.util.ArrayList

object AppIconUtils {
    fun getIconWithResolvedColor(
        context: Context?,
        icon: IDemoIcon,
        colorRef: Int,
        @DimenRes dimenId: Int
    ): IconicsDrawable {
        val sizeInPx: Int = ResourceUtils.getDimenPxById(context, dimenId)
        return IconUtils.getIconWithResolvedColor(context, icon.icon, colorRef, sizeInPx)
    }

    fun getIconWithResolvedColor(context: Context?, icon: IDemoIcon, colorRef: Int): IconicsDrawable {
        val sizeInPx: Int = ResourceUtils.getDimenPxById(context, R.dimen.ICON_SIZE_MEDIUM)
        return IconUtils.getIconWithResolvedColor(context, icon.icon, colorRef, sizeInPx)
    }

    fun getIconWithAttributeColor(
        context: Context?,
        icon: IDemoIcon?,
        @AttrRes colorRef: Int,
        @DimenRes dimenId: Int
    ): IconicsDrawable {
        val sizeInPx: Int = ResourceUtils.getDimenPxById(context, dimenId)
        return IconUtils.getIconWithAttributeColor(context, icon.getIcon(), colorRef, sizeInPx)
    }

    fun getIconWithAttributeColor(context: Context?, icon: IDemoIcon, @AttrRes colorRef: Int): IconicsDrawable {
        val sizeInPx: Int = ResourceUtils.getDimenPxById(context, R.dimen.ICON_SIZE_MEDIUM)
        return IconUtils.getIconWithAttributeColor(context, icon.icon, colorRef, sizeInPx)
    }

    fun getIconWithAttributeColor(context: Context?, icon: IDemoIcon): IconicsDrawable {
        val sizeInPx: Int = ResourceUtils.getDimenPxById(context, R.dimen.ICON_SIZE_MEDIUM)
        return IconUtils.getIconWithAttributeColor(context, icon.icon, R.attr.colorPrimary, sizeInPx)
    }

    val allForPicker: ArrayList<Any>
        get() {
            val result: ArrayList<IconPickerAdapter.IconModel> = ArrayList<IconPickerAdapter.IconModel>()
            for (icon in DemoIcons.values()) result.add(
                IconModel(
                    icon.icon.name,
                    icon.getName(),
                    IconPickerAdapter.IconSet.FONTAWESOME
                )
            )
            return result
        }
}