package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.icon_picker

import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import java.util.HashMap

class VMIconPickerDemo : BaseObservableViewModel() {
    private var boxedSelection: String? = null
    private var outlinedSelection: String? = null
    private var buttonSelection: String? = null
    private var imageButtonSelection: String? = null
    private val dataSets: MutableMap<Int?, List<IconModel>> = HashMap<Int?, List<IconModel>>()
    fun initialize() {
        dataSets[0] = AppIconUtils.getAllForPicker()
        dataSets[1] = AppIconUtils.getAllForPicker()
        dataSets[2] = AppIconUtils.getAllForPicker()
        dataSets[3] = AppIconUtils.getAllForPicker()
        boxedSelection = dataSets[0]!![0].getIconExternalName()
        outlinedSelection = dataSets[1]!![0].getIconExternalName()
        buttonSelection = dataSets[2]!![0].getIconExternalName()
        imageButtonSelection = dataSets[3]!![0].getIconExternalName()
    }

    fun setBoxedSelection(boxedSelection: String?) {
        this.boxedSelection = boxedSelection
        notifyPropertyChanged(BR.boxedSelection)
    }

    fun setOutlinedSelection(outlinedSelection: String?) {
        this.outlinedSelection = outlinedSelection
        notifyPropertyChanged(BR.outlinedSelection)
    }

    fun setButtonSelection(buttonSelection: String?) {
        this.buttonSelection = buttonSelection
        notifyPropertyChanged(BR.buttonSelection)
    }

    fun setImageButtonSelection(imageButtonSelection: String?) {
        this.imageButtonSelection = imageButtonSelection
        notifyPropertyChanged(BR.imageButtonSelection)
    }

    fun getDataSets(): Map<Int?, List<IconModel>> {
        return dataSets
    }

    @Bindable
    fun getBoxedSelection(): String? {
        return boxedSelection
    }

    @Bindable
    fun getOutlinedSelection(): String? {
        return outlinedSelection
    }

    @Bindable
    fun getButtonSelection(): String? {
        return buttonSelection
    }

    @Bindable
    fun getImageButtonSelection(): String? {
        return imageButtonSelection
    }
}