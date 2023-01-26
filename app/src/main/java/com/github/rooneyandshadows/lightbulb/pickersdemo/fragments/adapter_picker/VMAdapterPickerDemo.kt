package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.adapter_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import com.github.rooneyandshadows.lightbulb.pickersdemo.models.DemoModel
import java.util.*

class VMAdapterPickerDemo : BaseObservableViewModel() {
    @get:Bindable
    var boxedSelection: MutableList<UUID> = mutableListOf()
        set(value) {
            field = value
            notifyPropertyChanged(BR.boxedSelection)
        }

    @get:Bindable
    var outlinedSelection: MutableList<UUID> = mutableListOf()
        set(value) {
            field = value
            notifyPropertyChanged(BR.outlinedSelection)
        }

    @get:Bindable
    var buttonSelection: MutableList<UUID> = mutableListOf()
        set(value) {
            field = value
            notifyPropertyChanged(BR.buttonSelection)
        }

    @get:Bindable
    var imageButtonSelection: MutableList<UUID> = mutableListOf()
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageButtonSelection)
        }
    val dataSets: MutableMap<Int?, List<DemoModel>> = HashMap<Int?, List<DemoModel>>()

    init {
        DemoModel.generateDemoCollection().apply {
            dataSets[0] = this
            val firstElementId = get(0).id
            boxedSelection.add(firstElementId)
        }
        DemoModel.generateDemoCollection().apply {
            dataSets[1] = this
            val firstElementId = get(0).id
            outlinedSelection.add(firstElementId)
        }
        DemoModel.generateDemoCollection().apply {
            dataSets[2] = this
            val firstElementId = get(0).id
            buttonSelection.add(firstElementId)
        }
        DemoModel.generateDemoCollection().apply {
            dataSets[3] = this
            val firstElementId = get(0).id
            imageButtonSelection.add(firstElementId)
        }
    }
}