package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.adapter_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import com.github.rooneyandshadows.lightbulb.pickersdemo.models.DemoModel
import java.util.*

class VMAdapterPickerDemo : BaseObservableViewModel() {
    @get:Bindable
    var currentSelection: MutableList<UUID> = mutableListOf()
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentSelection)
        }
    val dataSet: MutableList<DemoModel> = mutableListOf()

    init {
        DemoModel.generateDemoCollection().apply {
            dataSet.addAll(this)
            val firstElementId = get(0).id
            currentSelection.add(firstElementId)
        }
    }
}