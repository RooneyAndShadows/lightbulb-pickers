package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.time_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_time.TimePickerDialog.*
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR

class VMTimePickerDemo : BaseObservableViewModel() {
    @get:Bindable
    var boxedSelection: Time? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.boxedSelection)
        }

    @get:Bindable
    var outlinedSelection: Time? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.outlinedSelection)
        }

    @get:Bindable
    var buttonSelection: Time? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.buttonSelection)
        }

    @get:Bindable
    var imageButtonSelection: Time? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageButtonSelection)
        }

    init {
        val now = DateUtilsOffsetDate.nowLocal()
        val hour = now.hour
        val minutes = now.minute
        boxedSelection = Time(hour, minutes)
        outlinedSelection = Time(hour, minutes)
        buttonSelection = Time(hour, minutes)
        imageButtonSelection = Time(hour, minutes)
    }
}