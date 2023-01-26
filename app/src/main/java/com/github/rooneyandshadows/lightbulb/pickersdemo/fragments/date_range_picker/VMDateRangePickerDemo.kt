package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.date_range_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialog.DateRange
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.*

class VMDateRangePickerDemo : BaseObservableViewModel() {
    @get:Bindable
    var boxedSelection: DateRange? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.boxedSelection)
        }

    @get:Bindable
    var outlinedSelection: DateRange? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.outlinedSelection)
        }

    @get:Bindable
    var buttonSelection: DateRange? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.buttonSelection)
        }

    @get:Bindable
    var imageButtonSelection: DateRange? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageButtonSelection)
        }

    init {
        val from = DateUtilsOffsetDate.nowLocal()
        val to = from.plus(5, ChronoUnit.DAYS)
        boxedSelection = DateRange(from, to)
        outlinedSelection = DateRange(from, to)
        buttonSelection = DateRange(from, to)
        imageButtonSelection = DateRange(from, to)
    }
}