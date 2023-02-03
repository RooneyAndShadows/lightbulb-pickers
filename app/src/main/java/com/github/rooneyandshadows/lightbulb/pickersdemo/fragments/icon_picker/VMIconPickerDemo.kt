package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.icon_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerAdapter.IconModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils

class VMIconPickerDemo : BaseObservableViewModel() {
    @get:Bindable
    var currentSelection: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentSelection)
        }
    val dataSet: MutableList<IconModel> = mutableListOf()

    init {
        AppIconUtils.allForPicker.apply {
            dataSet.addAll(this)
            val firstColorName = get(0).iconName
            currentSelection = firstColorName
        }
    }
}