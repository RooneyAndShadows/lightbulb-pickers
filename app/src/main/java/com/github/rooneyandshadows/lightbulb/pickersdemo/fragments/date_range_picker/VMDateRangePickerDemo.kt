package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.date_range_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.commons.databinding.ObservableProperty
import com.github.rooneyandshadows.lightbulb.commons.lifecycle.ObservableViewModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialog.DateRange
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import java.time.temporal.ChronoUnit

class VMDateRangePickerDemo : ObservableViewModel() {
    @get:Bindable
    var currentSelection: DateRange? by ObservableProperty(null, BR.currentSelection)
    init {
        val from = DateUtilsOffsetDate.nowLocal()
        val to = from.plus(6, ChronoUnit.DAYS)
        currentSelection = DateRange(from, to)
    }
}