package com.github.RooneyAndShadows.lightbulb.pickersdemo.utils.color

import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter
import com.github.RooneyAndShadows.lightbulb.pickersdemo.utils.color.colors.DemoColors
import java.util.ArrayList

object AppColorUtils {
    fun getColor(desiredColor: IDemoColor): Int {
        return desiredColor.color
    }

    val allForPicker: ArrayList<Any>
        get() {
            val result: ArrayList<ColorPickerAdapter.ColorModel> = ArrayList<ColorPickerAdapter.ColorModel>()
            for (color in DemoColors.values()) result.add(ColorModel(color.colorHex, color.getName()))
            return result
        }
}