package com.github.rooneyandshadows.lightbulb.pickersdemo.utils.color

import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.adapter.ColorModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.color.colors.DemoColors
import java.util.ArrayList

object AppColorUtils {
    fun getColor(desiredColor: IDemoColor): Int {
        return desiredColor.color
    }

    val allForPicker: ArrayList<ColorModel>
        get() {
            val result = ArrayList<ColorModel>()
            for (color in DemoColors.values())
                result.add(ColorModel(color.hexCode, color.colorName))
            return result
        }
}