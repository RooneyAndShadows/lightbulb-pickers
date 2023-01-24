package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.color_picker

import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import java.util.HashMap

class VMColorPickerDemo : BaseObservableViewModel() {
    private var boxedSelection: String? = null
    private var outlinedSelection: String? = null
    private var buttonSelection: String? = null
    private var imageButtonSelection: String? = null
    private val dataSets: MutableMap<Int?, List<ColorModel>> = HashMap<Int?, List<ColorModel>>()
    fun initialize() {
        dataSets[0] = AppColorUtils.getAllForPicker()
        dataSets[1] = AppColorUtils.getAllForPicker()
        dataSets[2] = AppColorUtils.getAllForPicker()
        dataSets[3] = AppColorUtils.getAllForPicker()
        boxedSelection = dataSets[0]!![0].getColorExternalName()
        outlinedSelection = dataSets[1]!![0].getColorExternalName()
        buttonSelection = dataSets[2]!![0].getColorExternalName()
        imageButtonSelection = dataSets[3]!![0].getColorExternalName()
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

    fun getDataSets(): Map<Int?, List<ColorModel>> {
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