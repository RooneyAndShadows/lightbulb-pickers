package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.icon_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerAdapter.IconModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils

class VMIconPickerDemo : BaseObservableViewModel() {
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
    val dataSets: MutableMap<Int?, List<IconModel>> = mutableMapOf()

    init {
        AppIconUtils.allForPicker.apply {
            dataSets[0] = this
            val firstColorName = get(0).iconName
            boxedSelection = firstColorName
        }
        AppIconUtils.allForPicker.apply {
            dataSets[1] = this
            val firstColorName = get(0).iconName
            outlinedSelection = firstColorName
        }
        AppIconUtils.allForPicker.apply {
            dataSets[2] = this
            val firstColorName = get(0).iconName
            buttonSelection = firstColorName
        }
        AppIconUtils.allForPicker.apply {
            dataSets[3] = this
            val firstColorName = get(0).iconName
            imageButtonSelection = firstColorName
        }
    }
}