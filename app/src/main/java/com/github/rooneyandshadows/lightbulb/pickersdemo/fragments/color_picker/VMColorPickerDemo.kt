package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.color_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.adapter.ColorModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.color.AppColorUtils

class VMColorPickerDemo : BaseObservableViewModel() {
    @get:Bindable
    var currentSelection: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentSelection)
        }
    val dataSet: MutableList<ColorModel> = mutableListOf()

    init {
        AppColorUtils.allForPicker.apply {
            dataSet.addAll(this)
            val firstColorName = get(0).externalName
            currentSelection = firstColorName
        }
    }
}