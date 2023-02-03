package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.date_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import java.time.OffsetDateTime

class VMDatePickerDemo : BaseObservableViewModel() {
    @get:Bindable
    var currentSelection: OffsetDateTime? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentSelection)
        }

    init {
        currentSelection = DateUtilsOffsetDate.nowLocal()
    }
}