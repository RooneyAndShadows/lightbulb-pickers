package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.chips_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.ChipsPickerAdapter
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.ChipsPickerAdapter.ChipModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerAdapter.IconModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import com.github.rooneyandshadows.lightbulb.pickersdemo.generateChips
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils

class VMChipsPickerDemo : BaseObservableViewModel() {
    @get:Bindable
    var currentSelection: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentSelection)
        }
    val dataSet: MutableList<ChipModel> = mutableListOf()

    init {
        generateChips().apply {
            dataSet.addAll(this)
        }
    }
}