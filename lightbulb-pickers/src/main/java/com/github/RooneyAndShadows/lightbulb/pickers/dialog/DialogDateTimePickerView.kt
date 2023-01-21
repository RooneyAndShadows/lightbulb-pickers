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
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_datetime.DateTimePickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_datetime.DateTimePickerDialogBuilder
import java.time.OffsetDateTime
import java.util.ArrayList

class DialogDateTimePickerView(context: Context, attrs: AttributeSet?) : BaseDialogPickerView(context, attrs) {
    var selection: OffsetDateTime? = null
        private set
    private var datePickerFormat: String? = null
    private var dataBindingListener: SelectionChangedListener? = null
    protected var validationCallbacks = ArrayList<ValidationCheck>()
    private val selectionChangedListeners = ArrayList<SelectionChangedListener>()
    fun addValidationCheck(validationCallback: ValidationCheck) {
        validationCallbacks.add(validationCallback)
    }

    fun addSelectionChangedListener(listener: SelectionChangedListener) {
        selectionChangedListeners.add(listener)
    }

    constructor(context: Context) : this(context, null) {}

    override fun readAttributes(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogDatePickerView, 0, 0)
        try {
            datePickerFormat = a.getString(R.styleable.DialogDatePickerView_DPV_DateFormat)
            if (datePickerFormat == null || datePickerFormat == "") datePickerFormat = "yyyy-MM-dd HH:mm"
        } finally {
            a.recycle()
        }
    }

    override fun initializeDialog(): DateTimePickerDialog? {
        return DateTimePickerDialogBuilder(manager, dialogTag)
            .withSelection(selection)
            .withCancelOnClickOutsude(true)
            .withPositiveButton(DialogButtonConfiguration(pickerDialogPositiveButtonText)) { view, dialogFragment -> updateTextAndValidate() }
            .withNegativeButton(DialogButtonConfiguration(pickerDialogNegativeButtonText)) { view, dialogFragment -> updateTextAndValidate() }
            .withOnCancelListener { dialogFragment -> updateTextAndValidate() }
            .withOnDateSelectedEvent { oldValue, newValue -> selectInternally(newValue) }
            .withAnimations(pickerDialogAnimationType)
            .buildDialog()
    }

    protected override val dialog: BasePickerDialogFragment<*>?
        protected get() = pickerDialog as DateTimePickerDialog

    override fun validate(): Boolean {
        var isValid = true
        if (validationEnabled) {
            if (required && !hasSelection()) {
                isErrorEnabled = true
                setErrorText(pickerRequiredText)
                return false
            }
            for (validationCallback in validationCallbacks) isValid = isValid and validationCallback.validate(selection)
        }
        if (!isValid) {
            isErrorEnabled = true
        } else {
            isErrorEnabled = false
            setErrorText(null)
        }
        return isValid
    }

    protected override val viewText: String
        protected get() = if (selection == null) ResourceUtils.getPhrase(context,
            R.string.dialog_date_picker_empty_text) else DateUtilsOffsetDate.getDateString(
            datePickerFormat,
            selection)

    fun setDateFormat(datePickerFormat: String?) {
        this.datePickerFormat = datePickerFormat
        updateTextAndValidate()
    }

    fun setSelection(date: OffsetDateTime) {
        if (dialog == null) selectInternally(date) else dialog!!.setSelection(date)
    }

    fun hasSelection(): Boolean {
        return selection != null
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.selection = DateUtilsOffsetDate.getDateString(DateUtilsOffsetDate.defaultFormatWithTimeZone, selection)
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        selection =
            DateUtilsOffsetDate.getDateFromString(DateUtilsOffsetDate.defaultFormatWithTimeZone, savedState.selection)
        super.onRestoreInstanceState(savedState.superState)
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    private fun compareValues(v1: OffsetDateTime?, v2: OffsetDateTime): Boolean {
        return DateUtilsOffsetDate.isDateEqual(v1, v2, true)
    }

    private fun selectInternally(newSelection: OffsetDateTime) {
        val oldSelection = selection
        selection = newSelection
        updateTextAndValidate()
        dispatchSelectionChangedEvents(oldSelection, newSelection)
    }

    private fun dispatchSelectionChangedEvents(oldValue: OffsetDateTime?, newValue: OffsetDateTime) {
        if (compareValues(oldValue, newValue)) return
        for (listener in selectionChangedListeners) listener.onSelectionChanged(this@DialogDateTimePickerView,
            oldValue,
            newValue)
        if (dataBindingListener != null) dataBindingListener!!.onSelectionChanged(this@DialogDateTimePickerView,
            oldValue,
            newValue)
    }

    private class SavedState : BaseSavedState {
        var selection: String? = null

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            selection = `in`.readString()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeString(selection)
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

    interface ValidationCheck {
        fun validate(currentSelection: OffsetDateTime?): Boolean
    }

    interface SelectionChangedListener {
        fun onSelectionChanged(view: DialogDateTimePickerView?, oldValue: OffsetDateTime?, newValue: OffsetDateTime?)
    }

    companion object {
        @JvmStatic
        @BindingAdapter("datePickerSelection")
        fun updatePickerSelectionBinding(view: DialogDateTimePickerView, selectedDate: OffsetDateTime) {
            view.setSelection(selectedDate)
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "datePickerSelection", event = "dateSelectionChanged")
        fun getText(view: DialogDateTimePickerView): OffsetDateTime? {
            return view.selection
        }

        @JvmStatic
        @BindingAdapter("dateSelectionChanged")
        fun setListeners(view: DialogDateTimePickerView, attrChange: InverseBindingListener) {
            view.dataBindingListener =
                label@ SelectionChangedListener { view1: DialogDateTimePickerView, newValue: OffsetDateTime?, oldValue: OffsetDateTime ->
                    if (view1.compareValues(newValue, oldValue)) return@label
                    attrChange.onChange()
                }
        }
    }
}