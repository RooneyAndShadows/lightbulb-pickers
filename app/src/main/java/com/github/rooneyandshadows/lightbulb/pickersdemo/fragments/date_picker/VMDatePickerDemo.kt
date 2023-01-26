package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.date_picker

import androidx.databinding.Bindable
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.BR
import java.time.OffsetDateTime

class VMDatePickerDemo : BaseObservableViewModel() {
    @get:Bindable
    var boxedSelection: OffsetDateTime? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.boxedSelection)
        }

    @get:Bindable
    var outlinedSelection: OffsetDateTime? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.outlinedSelection)
        }

    @get:Bindable
    var buttonSelection: OffsetDateTime? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.buttonSelection)
        }

    @get:Bindable
    var imageButtonSelection: OffsetDateTime? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.imageButtonSelection)
        }

    init {
        boxedSelection = DateUtilsOffsetDate.nowLocal()
        outlinedSelection = DateUtilsOffsetDate.nowLocal()
        buttonSelection = DateUtilsOffsetDate.nowLocal()
        imageButtonSelection = DateUtilsOffsetDate.nowLocal()
    }
}