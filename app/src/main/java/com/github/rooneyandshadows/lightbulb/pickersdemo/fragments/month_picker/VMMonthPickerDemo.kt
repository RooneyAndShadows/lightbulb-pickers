package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.month_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialog.Month
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR

class VMMonthPickerDemo : BaseObservableViewModel() {
    @get:Bindable
    var currentSelection: Month? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.currentSelection)
        }

    init {
        val now = DateUtilsOffsetDate.nowLocal()
        val year = DateUtilsOffsetDate.extractYearFromDate(now)
        val month = DateUtilsOffsetDate.extractMonthOfYearFromDate(now)
        currentSelection = Month(year,month)
    }
}