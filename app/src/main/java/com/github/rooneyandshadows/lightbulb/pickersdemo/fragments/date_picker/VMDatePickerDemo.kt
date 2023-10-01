package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.date_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.commons.databinding.ObservableProperty
import com.github.rooneyandshadows.lightbulb.commons.lifecycle.ObservableViewModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import java.time.OffsetDateTime

class VMDatePickerDemo : ObservableViewModel() {
    @get:Bindable
    var currentSelection: OffsetDateTime? by ObservableProperty(null, BR.currentSelection)

    init {
        currentSelection = DateUtilsOffsetDate.nowLocal()
    }
}