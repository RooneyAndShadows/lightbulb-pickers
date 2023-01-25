package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.date_range_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialog.DateRange
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

class VMDateRangePickerDemo : BaseObservableViewModel() {
    private var boxedSelection: DateRange? = null
    private var outlinedSelection: DateRange? = null
    private var buttonSelection: DateRange? = null
    private var imageButtonSelection: DateRange? = null

    fun initialize() {
        val from = DateUtilsOffsetDate.nowLocal()
        val to = from.plus(5, ChronoUnit.DAYS)
        boxedSelection = DateRange(from, to)
        outlinedSelection = DateRange(from, to)
        buttonSelection = DateRange(from, to)
        imageButtonSelection = DateRange(from, to)
    }

    fun setBoxedSelection(boxedSelection: DateRange?) {
        this.boxedSelection = boxedSelection
        notifyPropertyChanged(BR.boxedSelection)
    }

    fun setOutlinedSelection(outlinedSelection: DateRange?) {
        this.outlinedSelection = outlinedSelection
        notifyPropertyChanged(BR.outlinedSelection)
    }

    fun setButtonSelection(buttonSelection: DateRange?) {
        this.buttonSelection = buttonSelection
        notifyPropertyChanged(BR.buttonSelection)
    }

    fun setImageButtonSelection(imageButtonSelection: DateRange?) {
        this.imageButtonSelection = imageButtonSelection
        notifyPropertyChanged(BR.imageButtonSelection)
    }

    @Bindable
    fun getBoxedSelection(): DateRange? {
        return boxedSelection
    }

    @Bindable
    fun getOutlinedSelection(): DateRange? {
        return outlinedSelection
    }

    @Bindable
    fun getButtonSelection(): DateRange? {
        return buttonSelection
    }

    @Bindable
    fun getImageButtonSelection(): DateRange? {
        return imageButtonSelection
    }
}