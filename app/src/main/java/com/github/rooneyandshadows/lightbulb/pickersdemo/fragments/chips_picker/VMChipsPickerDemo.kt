package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.chips_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.lightbulb.commons.databinding.ObservableProperty
import com.github.rooneyandshadows.lightbulb.commons.lifecycle.ObservableViewModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.adapter.ChipModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import com.github.rooneyandshadows.lightbulb.pickersdemo.generateChips

class VMChipsPickerDemo : ObservableViewModel() {
    @get:Bindable
    var currentSelection: MutableList<String> by ObservableProperty(mutableListOf(), BR.currentSelection)
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