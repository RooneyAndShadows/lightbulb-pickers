package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.adapter_picker

import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import java.util.*

class VMAdapterPickerDemo : BaseObservableViewModel() {
    private val boxedSelection: MutableList<UUID> = ArrayList()
    private val outlinedSelection: MutableList<UUID> = ArrayList()
    private val buttonSelection: MutableList<UUID> = ArrayList()
    private val imageButtonSelection: MutableList<UUID> = ArrayList()
    private val dataSets: MutableMap<Int?, List<DemoModel>> = HashMap<Int?, List<DemoModel>>()
    fun initialize() {
        dataSets[0] = DemoModel.Companion.generateDemoCollection()
        dataSets[1] = DemoModel.Companion.generateDemoCollection()
        dataSets[2] = DemoModel.Companion.generateDemoCollection()
        dataSets[3] = DemoModel.Companion.generateDemoCollection()
        boxedSelection.add(dataSets[0]!![0].getId())
        outlinedSelection.add(dataSets[1]!![0].getId())
        buttonSelection.add(dataSets[2]!![0].getId())
        imageButtonSelection.add(dataSets[3]!![0].getId())
    }

    fun setBoxedSelection(boxedSelection: List<UUID>?) {
        this.boxedSelection.clear()
        this.boxedSelection.addAll(boxedSelection!!)
        notifyPropertyChanged(BR.boxedSelection)
    }

    fun setOutlinedSelection(outlinedSelection: List<UUID>?) {
        this.outlinedSelection.clear()
        this.outlinedSelection.addAll(outlinedSelection!!)
        notifyPropertyChanged(BR.outlinedSelection)
    }

    fun setButtonSelection(buttonSelection: List<UUID>?) {
        this.buttonSelection.clear()
        this.buttonSelection.addAll(buttonSelection!!)
        notifyPropertyChanged(BR.buttonSelection)
    }

    fun setImageButtonSelection(imageButtonSelection: List<UUID>?) {
        this.imageButtonSelection.clear()
        this.imageButtonSelection.addAll(imageButtonSelection!!)
        notifyPropertyChanged(BR.imageButtonSelection)
    }

    fun getDataSets(): Map<Int?, List<DemoModel>> {
        return dataSets
    }

    @Bindable
    fun getBoxedSelection(): List<UUID> {
        return boxedSelection
    }

    @Bindable
    fun getOutlinedSelection(): List<UUID> {
        return outlinedSelection
    }

    @Bindable
    fun getButtonSelection(): List<UUID> {
        return buttonSelection
    }

    @Bindable
    fun getImageButtonSelection(): List<UUID> {
        return imageButtonSelection
    }
}