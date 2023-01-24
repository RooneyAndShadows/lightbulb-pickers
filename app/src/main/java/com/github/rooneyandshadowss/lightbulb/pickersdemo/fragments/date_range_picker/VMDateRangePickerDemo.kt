package com.github.RooneyAndShadows.lightbulb.pickersdemo.fragments.date_range_picker

import com.github.RooneyAndShadows.java.commons.date.DateUtilsOffsetDate
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

class VMDateRangePickerDemo : BaseObservableViewModel() {
    private var boxedSelection: Array<OffsetDateTime?>
    private var outlinedSelection: Array<OffsetDateTime?>
    private var buttonSelection: Array<OffsetDateTime?>
    private var imageButtonSelection: Array<OffsetDateTime?>
    fun initialize() {
        val initialSelection = arrayOfNulls<OffsetDateTime>(2)
        initialSelection[0] = DateUtilsOffsetDate.nowLocal()
        initialSelection[1] = initialSelection[0]!!.plus(5, ChronoUnit.DAYS)
        boxedSelection = initialSelection
        outlinedSelection = initialSelection
        buttonSelection = initialSelection
        imageButtonSelection = initialSelection
    }

    fun setBoxedSelection(boxedSelection: Array<OffsetDateTime?>) {
        this.boxedSelection = boxedSelection
        notifyPropertyChanged(BR.boxedSelection)
    }

    fun setOutlinedSelection(outlinedSelection: Array<OffsetDateTime?>) {
        this.outlinedSelection = outlinedSelection
        notifyPropertyChanged(BR.outlinedSelection)
    }

    fun setButtonSelection(buttonSelection: Array<OffsetDateTime?>) {
        this.buttonSelection = buttonSelection
        notifyPropertyChanged(BR.buttonSelection)
    }

    fun setImageButtonSelection(imageButtonSelection: Array<OffsetDateTime?>) {
        this.imageButtonSelection = imageButtonSelection
        notifyPropertyChanged(BR.imageButtonSelection)
    }

    @Bindable
    fun getBoxedSelection(): Array<OffsetDateTime?> {
        return boxedSelection
    }

    @Bindable
    fun getOutlinedSelection(): Array<OffsetDateTime?> {
        return outlinedSelection
    }

    @Bindable
    fun getButtonSelection(): Array<OffsetDateTime?> {
        return buttonSelection
    }

    @Bindable
    fun getImageButtonSelection(): Array<OffsetDateTime?> {
        return imageButtonSelection
    }
}