package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.time_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_time.TimePickerDialog.*
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR

class VMTimePickerDemo : BaseObservableViewModel() {
    @get:Bindable
    var currentSelection: Time? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentSelection)
        }

    init {
        val now = DateUtilsOffsetDate.nowLocal()
        val hour = now.hour
        val minutes = now.minute
        currentSelection = Time(hour, minutes)
    }
}