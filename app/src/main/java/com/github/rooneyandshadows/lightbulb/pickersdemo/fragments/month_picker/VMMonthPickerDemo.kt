package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.month_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialog.Month
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import java.time.OffsetDateTime

class VMMonthPickerDemo : BaseObservableViewModel() {
    @get:Bindable
    var boxedSelection: Month? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.boxedSelection)
        }

    @get:Bindable
    var outlinedSelection: Month? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.outlinedSelection)
        }

    @get:Bindable
    var buttonSelection: Month? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.buttonSelection)
        }

    @get:Bindable
    var imageButtonSelection: Month? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageButtonSelection)
        }

    init {
        val now = DateUtilsOffsetDate.nowLocal()
        val year = DateUtilsOffsetDate.extractYearFromDate(now)
        val month = DateUtilsOffsetDate.extractMonthOfYearFromDate(now)
        boxedSelection = Month(year,month)
        outlinedSelection = Month(year,month)
        buttonSelection = Month(year,month)
        imageButtonSelection = Month(year,month)
    }
}