package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.chips_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.adapter.ChipModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import com.github.rooneyandshadows.lightbulb.pickersdemo.generateChips

class VMChipsPickerDemo : BaseObservableViewModel() {
    @get:Bindable
    var currentSelection: MutableList<String>? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentSelection)
        }
    val dataSet: MutableList<ChipModel> = mutableListOf()

    init {
        generateChips().apply {
            dataSet.addAll(this)
            currentSelection = dataSet.subList(0, 5).map {
                return@map it.chipTitle
            }.toMutableList()
        }
    }
}