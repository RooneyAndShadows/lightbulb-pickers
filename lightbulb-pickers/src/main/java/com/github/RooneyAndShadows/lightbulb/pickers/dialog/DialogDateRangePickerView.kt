package com.github.rooneyandshadows.lightbulb.pickers.dialog

import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerDialogBuilder.withAnimations
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_time.TimePickerDialogBuilder.buildDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerDialogBuilder.withAnimations
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withPositiveButton
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withNegativeButton
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withOnCancelListener
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withOnDateSelectedEvent
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withAnimations
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.buildDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialogBuilder.withAnimations
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.pickers.R
import android.util.SparseArray
import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import android.content.Context
import android.util.AttributeSet
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialogBuilder
import java.time.OffsetDateTime
import java.util.*

class DialogDateRangePickerView(context: Context, attrs: AttributeSet?) : BaseDialogPickerView(context, attrs) {
    private var datePickerFormat: String? = null
    private var datePickerFromText: String? = null
    private var datePickerToText: String? = null
    var pickerRange: Array<OffsetDateTime?>?
        private set
    private var dataBindingListener: SelectionChangedListener? = null
    protected val validationCallbacks = ArrayList<ValidationCheck>()
    private val selectionChangedListeners = ArrayList<SelectionChangedListener>()
    fun addValidationCheck(validationCallback: ValidationCheck) {
        validationCallbacks.add(validationCallback)
    }

    fun addSelectionChangedListener(listener: SelectionChangedListener) {
        selectionChangedListeners.add(listener)
    }

    fun setDatePickerFormat(datePickerFormat: String?) {
        this.datePickerFormat = datePickerFormat
    }

    fun setDatePickerFromText(datePickerFromText: String?) {
        this.datePickerFromText = datePickerFromText
    }

    fun setDatePickerToText(datePickerToText: String?) {
        this.datePickerToText = datePickerToText
    }

    fun setPickerRange(dateFrom: OffsetDateTime?, dateTo: OffsetDateTime?) {
        var dateFrom = dateFrom
        var dateTo = dateTo
        if (dateFrom == null || dateTo == null) {
            dateFrom = null
            dateTo = null
        } else {
            if (DateUtilsOffsetDate.isDateBefore(dateTo, dateFrom)) {
                val temp: OffsetDateTime = dateFrom
                dateFrom = dateTo
                dateTo = temp
            }
        }
        if (dialog == null) selectInternally(dateFrom, dateTo) else dialog!!.setSelection(dateFrom, dateTo)
    }

    fun hasSelection(): Boolean {
        return pickerRange != null && pickerRange!!.size == 2 && pickerRange!![0] != null && pickerRange!![1] != null
    }

    protected override val viewText: String
        protected get() {
            if (pickerRange == null || !hasSelection()) return ResourceUtils.getPhrase(context,
                R.string.dialog_date_picker_empty_text)
            val from = DateUtilsOffsetDate.getDateString(datePickerFormat, pickerRange!![0])
            val to = DateUtilsOffsetDate.getDateString(datePickerFormat, pickerRange!![1])
            val viewTextFormat = "{from} - {to}"
            return viewTextFormat.replace("{from}", from).replace("{to}", to)
        }

    override fun validate(): Boolean {
        var isValid = true
        if (validationEnabled) {
            if (required && !hasSelection()) {
                isErrorEnabled = true
                setErrorText(pickerRequiredText)
                return false
            }
            for (validationCallback in validationCallbacks) isValid = isValid and validationCallback.validate(
                pickerRange)
        }
        if (!isValid) isErrorEnabled = true else {
            isErrorEnabled = false
            setErrorText(null)
        }
        return isValid
    }

    override fun readAttributes(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogDateRangePickerView, 0, 0)
        try {
            if (pickerRange == null) pickerRange = arrayOfNulls(2)
            datePickerFormat = a.getString(R.styleable.DialogDateRangePickerView_DRPV_DateFormat)
            datePickerFromText = a.getString(R.styleable.DialogDateRangePickerView_DRPV_TextFrom)
            datePickerToText = a.getString(R.styleable.DialogDateRangePickerView_DRPV_TextTo)
            if (datePickerFormat == null || datePickerFormat == "") datePickerFormat = "yyyy/MM/dd"
            if (datePickerFromText == null || datePickerFromText == "") datePickerFromText = "FROM"
            if (datePickerToText == null || datePickerToText == "") datePickerToText = "TO"
        } finally {
            a.recycle()
        }
    }

    override fun initializeDialog(): DateRangePickerDialog? {
        return DateRangePickerDialogBuilder(manager, dialogTag)
            .withSelection(pickerRange!!)
            .withCancelOnClickOutsude(true)
            .withTextFrom(datePickerFromText)
            .withTextTo(datePickerToText)
            .withPositiveButton(DialogButtonConfiguration(pickerDialogPositiveButtonText)) { view, dialogFragment -> updateTextAndValidate() }
            .withNegativeButton(DialogButtonConfiguration(pickerDialogNegativeButtonText)) { view, dialogFragment -> updateTextAndValidate() }
            .withOnCancelListener { dialogFragment -> updateTextAndValidate() }
            .withOnDateSelectedEvent { oldValue, newValue -> selectInternally(newValue.get(0), newValue.get(1)) }
            .withAnimations(pickerDialogAnimationType)
            .buildDialog()
    }

    protected override val dialog: BasePickerDialogFragment<*>?
        protected get() = pickerDialog as DateRangePickerDialog

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.selectionFrom =
            DateUtilsOffsetDate.getDateString(DateUtilsOffsetDate.defaultFormatWithTimeZone, pickerRange!![0])
        myState.selectionTo =
            DateUtilsOffsetDate.getDateString(DateUtilsOffsetDate.defaultFormatWithTimeZone, pickerRange!![1])
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        val currentFrom =
            DateUtilsOffsetDate.getDateFromString(DateUtilsOffsetDate.defaultFormatWithTimeZone, savedState.selectionFrom)
        val currentTo =
            DateUtilsOffsetDate.getDateFromString(DateUtilsOffsetDate.defaultFormatWithTimeZone, savedState.selectionTo)
        pickerRange = arrayOf(currentFrom, currentTo)
        super.onRestoreInstanceState(savedState.superState)
    }

    private fun compareValues(v1: Array<OffsetDateTime>, v2: Array<OffsetDateTime>): Boolean {
        return Arrays.equals(v1, v2)
    }

    private fun selectInternally(dateFrom: OffsetDateTime?, dateTo: OffsetDateTime?) {
        val oldSelection = arrayOf(pickerRange!![0], pickerRange!![1])
        val newSelection = arrayOf(dateFrom, dateTo)
        pickerRange = newSelection
        updateTextAndValidate()
        dispatchRangeSelectionChangedEvent(oldSelection, newSelection)
    }

    private fun dispatchRangeSelectionChangedEvent(oldValue: Array<OffsetDateTime?>, newValue: Array<OffsetDateTime?>) {
        for (publicChangedListener in selectionChangedListeners) publicChangedListener.onSelectionChanged(this,
            oldValue,
            newValue)
        if (dataBindingListener != null) dataBindingListener!!.onSelectionChanged(this@DialogDateRangePickerView,
            oldValue,
            newValue)
    }

    private class SavedState : BaseSavedState {
        var selectionFrom: String? = null
        var selectionTo: String? = null

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            selectionFrom = `in`.readString()
            selectionTo = `in`.readString()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(selectionFrom)
            out.writeString(selectionTo)
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
        fun onSelectionChanged(
            view: DialogDateRangePickerView?,
            oldRange: Array<OffsetDateTime?>?,
            newRange: Array<OffsetDateTime?>?
        )
    }

    interface ValidationCheck {
        fun validate(currentSelection: Array<OffsetDateTime?>?): Boolean
    }

    companion object {
        @JvmStatic
        @BindingAdapter("dateRangePickerSelection")
        fun updatePickerSelectionBinding(view: DialogDateRangePickerView, selectedRange: Array<OffsetDateTime?>) {
            view.setPickerRange(selectedRange[0], selectedRange[1])
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "dateRangePickerSelection", event = "dateRangeSelectionChanged")
        fun getText(view: DialogDateRangePickerView): Array<OffsetDateTime?>? {
            return view.pickerRange
        }

        @JvmStatic
        @BindingAdapter("dateRangeSelectionChanged")
        fun setListeners(view: DialogDateRangePickerView, attrChange: InverseBindingListener) {
            view.dataBindingListener =
                label@ SelectionChangedListener { view1: DialogDateRangePickerView, newValue: Array<OffsetDateTime>, oldValue: Array<OffsetDateTime> ->
                    if (view1.compareValues(newValue, oldValue)) return@label
                    attrChange.onChange()
                }
        }
    }
}