package com.github.rooneyandshadows.lightbulb.pickers.dialog

import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerDialogBuilder.withAnimations
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerDialogBuilder.withAnimations
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withPositiveButton
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withNegativeButton
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withOnCancelListener
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withOnDateSelectedEvent
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withAnimations
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialogBuilder.withAnimations
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import android.util.SparseArray
import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import android.content.Context
import android.util.AttributeSet
import com.github.rooneyandshadows.java.commons.string.StringUtils
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_time.TimePickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_time.TimePickerDialogBuilder
import java.time.OffsetDateTime
import java.util.*

class DialogTimePickerView(context: Context, attrs: AttributeSet?) : BaseDialogPickerView(context, attrs) {
    private var cachedDate: OffsetDateTime? = null
    var selectedTime: IntArray?
        private set
    private var datePickerFormat: String? = null
    private var dataBindingListener: SelectionChangedListener? = null
    private val validationCallbacks = ArrayList<ValidationCheck>()
    private val selectionChangedListeners = ArrayList<SelectionChangedListener>()

    constructor(context: Context) : this(context, null) {}

    protected override val viewText: String
        protected get() = if (selectedTime == null) "" else DateUtilsOffsetDate.getDateString(datePickerFormat,
            selectionAsDate)

    override fun readAttributes(context: Context, attrs: AttributeSet?) {
        if (StringUtils.isNullOrEmptyString(datePickerFormat)) datePickerFormat = "HH:mm"
        if (!hasSelection()) {
            val now = DateUtilsOffsetDate.nowLocal()
            selectedTime = intArrayOf(DateUtilsOffsetDate.getHourOfDay(now), DateUtilsOffsetDate.getMinuteOfHour(now))
        }
    }

    override fun validate(): Boolean {
        var isValid = true
        if (validationEnabled) {
            if (required && !hasSelection()) {
                isErrorEnabled = true
                setErrorText(pickerRequiredText)
                return false
            }
            for (validationCallback in validationCallbacks) isValid = isValid and validationCallback.execute(
                selectedTime)
        }
        if (!isValid) {
            isErrorEnabled = true
        } else {
            isErrorEnabled = false
            setErrorText(null)
        }
        return isValid
    }

    override fun initializeDialog(): TimePickerDialog? {
        val builder: TimePickerDialogBuilder = TimePickerDialogBuilder(manager, dialogTag)
            .withInitialTime(selectedTime)
            .withCancelOnClickOutsude(true)
            .withPositiveButton(DialogButtonConfiguration(pickerDialogPositiveButtonText)) { view, dialogFragment -> updateTextAndValidate() }
            .withNegativeButton(DialogButtonConfiguration(pickerDialogNegativeButtonText)) { view, dialogFragment -> updateTextAndValidate() }
            .withOnCancelListener { dialogFragment -> updateTextAndValidate() }
            .withOnDateSelectedEvent { previousSelection, newSelection -> selectInternally(newSelection) }
            .withAnimations(pickerDialogAnimationType)
        return builder.buildDialog()
    }

    protected override val dialog: BasePickerDialogFragment<*>?
        protected get() = pickerDialog as TimePickerDialog

    fun addSelectionChangedListener(listener: SelectionChangedListener) {
        selectionChangedListeners.add(listener)
    }

    fun addValidationCheck(validationCallback: ValidationCheck) {
        validationCallbacks.add(validationCallback)
    }

    fun hasSelection(): Boolean {
        return selectedTime != null && selectedTime!!.size == 2
    }

    // Used to keep day of previously set date
    val selectionAsDate: OffsetDateTime?
        get() {
            if (hasSelection()) {
                // Used to keep day of previously set date
                if (cachedDate != null) cachedDate = DateUtilsOffsetDate.setTimeToDate(cachedDate,
                    selectedTime!![0],
                    selectedTime!![1],
                    DateUtilsOffsetDate.getSecondOfMinute(cachedDate))
                val now = DateUtilsOffsetDate.nowLocal()
                return if (cachedDate == null) DateUtilsOffsetDate.setTimeToDate(now,
                    selectedTime!![0],
                    selectedTime!![1],
                    DateUtilsOffsetDate.getSecondOfMinute(now)) else cachedDate
            }
            return null
        }

    private fun compareValues(v1: IntArray?, v2: IntArray?): Boolean {
        return Arrays.equals(v1, v2)
    }

    private fun selectInternally(newSelection: IntArray?) {
        var newSelection = newSelection
        val oldSelection = selectedTime
        newSelection = validateTimeInput(newSelection)
        selectedTime = newSelection
        updateTextAndValidate()
        dispatchSelectionChangedEvents(oldSelection, newSelection)
    }

    private fun selectInternally(hour: Int, minutes: Int) {
        selectInternally(intArrayOf(hour, minutes))
    }

    fun setSelection(hour: Int, minutes: Int) {
        setSelection(intArrayOf(hour, minutes))
    }

    fun setSelection(time: IntArray?) {
        var time = time
        time = validateTimeInput(time)
        if (dialog == null) selectInternally(time) else dialog!!.setSelection(time)
    }

    fun setSelectionFromDate(newDate: OffsetDateTime?) {
        var newSelection: IntArray? = null
        if (newDate != null) newSelection =
            intArrayOf(DateUtilsOffsetDate.getHourOfDay(newDate), DateUtilsOffsetDate.getMinuteOfHour(newDate))
        cachedDate = newDate
        setSelection(newSelection)
    }

    private fun validateTimeInput(time: IntArray?): IntArray? {
        if (time == null) return null
        var hour = time[0]
        var minutes = time[1]
        if (minutes >= 60) {
            hour++
            minutes = 0
        }
        if (minutes < 0) minutes = 0
        if (hour >= 24) {
            hour = 23
            minutes = 59
        }
        if (hour < 0) {
            hour = 0
        }
        return intArrayOf(hour, minutes)
    }

    private fun updateUI() {
        updateTextAndValidate()
    }

    private fun dispatchSelectionChangedEvents(oldValue: IntArray?, newValue: IntArray?) {
        if (compareValues(oldValue, newValue)) return
        for (listener in selectionChangedListeners) listener.onSelectionChanged(this, oldValue, newValue)
        if (dataBindingListener != null) dataBindingListener!!.onSelectionChanged(this, oldValue, newValue)
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.selection = selectedTime
        myState.cachedDate = DateUtilsOffsetDate.getDateString(DateUtilsOffsetDate.defaultFormatWithTimeZone, cachedDate)
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        selectedTime = savedState.selection
        cachedDate =
            DateUtilsOffsetDate.getDateFromString(DateUtilsOffsetDate.defaultFormatWithTimeZone, savedState.cachedDate)
        super.onRestoreInstanceState(savedState.superState)
    }

    private class SavedState : BaseSavedState {
        var cachedDate: String? = null
        var selection: IntArray?

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            selection = `in`.createIntArray()
            cachedDate = `in`.readString()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeIntArray(selection)
            out.writeString(cachedDate)
        }

        companion object {
            val CREATOR: Creator<SavedState> = object : Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    interface SelectionChangedListener {
        fun onSelectionChanged(view: DialogTimePickerView?, oldValue: IntArray?, newValue: IntArray?)
    }

    interface ValidationCheck {
        fun execute(currentSelection: IntArray?): Boolean
    }

    companion object {
        @BindingAdapter("timePickerSelection")
        fun updatePickerSelectionBinding(view: DialogTimePickerView, selectedDate: OffsetDateTime?) {
            view.setSelectionFromDate(selectedDate)
        }

        @InverseBindingAdapter(attribute = "timePickerSelection", event = "timePickerSelectionChanged")
        fun getText(view: DialogTimePickerView): OffsetDateTime? {
            return view.selectionAsDate
        }

        @BindingAdapter("timePickerSelectionChanged")
        fun setListeners(view: DialogTimePickerView, attrChange: InverseBindingListener) {
            view.dataBindingListener =
                label@ SelectionChangedListener { view1: DialogTimePickerView, newValue: IntArray?, oldValue: IntArray? ->
                    if (view1.compareValues(newValue, oldValue)) return@label
                    attrChange.onChange()
                }
        }
    }
}