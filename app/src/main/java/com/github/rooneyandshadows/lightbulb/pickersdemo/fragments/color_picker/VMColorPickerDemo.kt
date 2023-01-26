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
    @get:Bindable
    var boxedSelection: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.boxedSelection)
        }

    @get:Bindable
    var outlinedSelection: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.outlinedSelection)
        }

    @get:Bindable
    var buttonSelection: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.buttonSelection)
        }

    @get:Bindable
    var imageButtonSelection: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageButtonSelection)
        }
    val dataSets: MutableMap<Int?, List<ColorModel>> = HashMap<Int?, List<ColorModel>>()

    init {
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
}