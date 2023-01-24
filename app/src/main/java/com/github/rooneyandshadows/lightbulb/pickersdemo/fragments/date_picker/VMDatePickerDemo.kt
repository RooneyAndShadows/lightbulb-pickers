package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.date_picker

import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import java.time.OffsetDateTime

class VMDatePickerDemo : BaseObservableViewModel() {
    private var boxedSelection: OffsetDateTime? = null
    private var outlinedSelection: OffsetDateTime? = null
    private var buttonSelection: OffsetDateTime? = null
    private var imageButtonSelection: OffsetDateTime? = null
    fun initialize() {
        boxedSelection = DateUtilsOffsetDate.nowLocal()
        outlinedSelection = DateUtilsOffsetDate.nowLocal()
        buttonSelection = DateUtilsOffsetDate.nowLocal()
        imageButtonSelection = DateUtilsOffsetDate.nowLocal()
    }

    fun setBoxedSelection(boxedSelection: OffsetDateTime?) {
        this.boxedSelection = boxedSelection
        notifyPropertyChanged(BR.boxedSelection)
    }

    fun setOutlinedSelection(outlinedSelection: OffsetDateTime?) {
        this.outlinedSelection = outlinedSelection
        notifyPropertyChanged(BR.outlinedSelection)
    }

    fun setButtonSelection(buttonSelection: OffsetDateTime?) {
        this.buttonSelection = buttonSelection
        notifyPropertyChanged(BR.buttonSelection)
    }

    fun setImageButtonSelection(imageButtonSelection: OffsetDateTime?) {
        this.imageButtonSelection = imageButtonSelection
        notifyPropertyChanged(BR.imageButtonSelection)
    }

    @Bindable
    fun getBoxedSelection(): OffsetDateTime? {
        return boxedSelection
    }

    @Bindable
    fun getOutlinedSelection(): OffsetDateTime? {
        return outlinedSelection
    }

    @Bindable
    fun getButtonSelection(): OffsetDateTime? {
        return buttonSelection
    }

    @Bindable
    fun getImageButtonSelection(): OffsetDateTime? {
        return imageButtonSelection
    }
}