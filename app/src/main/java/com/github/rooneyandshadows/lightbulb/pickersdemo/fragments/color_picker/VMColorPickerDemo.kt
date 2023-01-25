package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.color_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter.ColorModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import com.github.rooneyandshadows.lightbulb.pickersdemo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.color.AppColorUtils
import java.util.HashMap

class VMColorPickerDemo : BaseObservableViewModel() {
    private var boxedSelection: String? = null
    private var outlinedSelection: String? = null
    private var buttonSelection: String? = null
    private var imageButtonSelection: String? = null
    val dataSets: MutableMap<Int?, List<ColorModel>> = HashMap<Int?, List<ColorModel>>()
    fun initialize() {
        AppColorUtils.allForPicker.apply {
            dataSets[0] = this
            val firstColorName = get(0).externalName
            boxedSelection = firstColorName
        }
        AppColorUtils.allForPicker.apply {
            dataSets[1] = this
            val firstColorName = get(0).externalName
            outlinedSelection = firstColorName
        }
        AppColorUtils.allForPicker.apply {
            dataSets[2] = this
            val firstColorName = get(0).externalName
            buttonSelection = firstColorName
        }
        AppColorUtils.allForPicker.apply {
            dataSets[3] = this
            val firstColorName = get(0).externalName
            imageButtonSelection = firstColorName
        }
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