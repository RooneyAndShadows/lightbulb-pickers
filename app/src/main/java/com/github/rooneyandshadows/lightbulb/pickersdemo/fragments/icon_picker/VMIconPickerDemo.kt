package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.icon_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.lightbulb.commons.databinding.ObservableProperty
import com.github.rooneyandshadows.lightbulb.commons.lifecycle.ObservableViewModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.adapter.IconModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils

class VMIconPickerDemo : ObservableViewModel() {
    @get:Bindable
    var currentSelection: String? by ObservableProperty(null, BR.currentSelection)
    val dataSet: MutableList<IconModel> = mutableListOf()

    init {
        AppIconUtils.allForPicker.apply {
            dataSet.addAll(this)
            val firstColorName = get(0).iconName
            currentSelection = firstColorName
        }
    }
}