package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.adapter_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import com.github.rooneyandshadows.lightbulb.pickersdemo.models.DemoModel
import java.util.*

class VMAdapterPickerDemo : BaseObservableViewModel() {
    private val boxedSelection: MutableList<UUID> = mutableListOf()
    private val outlinedSelection: MutableList<UUID> = mutableListOf()
    private val buttonSelection: MutableList<UUID> = mutableListOf()
    private val imageButtonSelection: MutableList<UUID> = mutableListOf()
    val dataSets: MutableMap<Int?, List<DemoModel>> = HashMap<Int?, List<DemoModel>>()

    fun initialize() {
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