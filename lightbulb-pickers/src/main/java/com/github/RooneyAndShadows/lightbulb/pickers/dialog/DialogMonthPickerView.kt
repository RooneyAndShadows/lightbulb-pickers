package com.github.rooneyandshadows.lightbulb.pickers.dialog

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.fragment.app.FragmentManager
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.java.commons.string.StringUtils
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.callbacks.DialogButtonClickListener
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.callbacks.DialogCancelListener
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialog.*
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import java.time.OffsetDateTime
import java.util.*

class DialogMonthPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : BaseDialogPickerView<Month>(context, attrs, defStyleAttr, defStyleRess) {
    private val dialog: MonthPickerDialog
        get() {
            return pickerDialog as MonthPickerDialog
        }
    var monthPickerFormat: String = DEFAULT_DATE_FORMAT
        set(value) {
            field = value
            dialog.dialogDateFormat = field
            updateTextAndValidate()
        }
        get() = dialog.dialogDateFormat
    var minYear = DEFAULT_MIN_YEAR
        private set(value) {
            field = value
            //TODO SET MIN YEAR TO DIALOG
        }
    var maxYear = DEFAULT_MAX_YEAR
        private set(value) {
            field = value
            //TODO SET MIN YEAR TO DIALOG
        }
    var disabledMonths: List<Month> = listOf()
        set(value) {
            field = value
            dialog.setDisabledMonths(field)
        }

    //TODO get()
    var enabledMonths: List<Month> = listOf()
        set(value) {
            field = value
            dialog.setEnabledMonths(field)
        }

    //TODO get()
    override val viewText: String
        get() = if (!hasSelection()) "" else DateUtilsOffsetDate.getDateString(monthPickerFormat, selectionAsDate)

    override fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogMonthPickerView, 0, 0)
        try {
            attrTypedArray.apply {
                monthPickerFormat = attrTypedArray.getString(R.styleable.DialogMonthPickerView_MPV_DateFormat).let {
                    val default = DEFAULT_DATE_FORMAT
                    if (it.isNullOrBlank()) return@let default
                    else return@let it
                }
                minYear = getInteger(R.styleable.DialogMonthPickerView_MPV_MinYear, DEFAULT_MIN_YEAR)
                maxYear = getInteger(R.styleable.DialogMonthPickerView_MPV_MaxYear, DEFAULT_MAX_YEAR)
            }
        } finally {
            attrTypedArray.recycle()
        }
    }

    override fun initializeDialog(fragmentManager: FragmentManager): BasePickerDialogFragment<Month> {
        return MonthPickerDialogBuilder(null, fragmentManager, pickerDialogTag)
            .buildDialog()
    }

    fun setSelectionFromDate(newDate: OffsetDateTime?) {
        var newSelection: Month? = null
        if (newDate != null) {
            val year = DateUtilsOffsetDate.extractYearFromDate(newDate)
            val month = DateUtilsOffsetDate.extractMonthOfYearFromDate(newDate)
            newSelection = Month(year, month)
        }
        selection = newSelection
    }

    fun setCalendarBounds(min: Int, max: Int) {
        if (min > max) {
            maxYear = min
            minYear = max
        } else {
            minYear = min
            maxYear = max
        }
        if (dialog != null) dialog.setCalendarBounds(minYear, maxYear)
    }

    fun setDisabledMonths(disabled: ArrayList<IntArray>?) {
        disabledMonths = disabled
        if (disabledMonths != null) for (disabledMonth in disabledMonths!!) if (Arrays.equals(
                disabledMonth,
                selectionAsArray
            )
        ) setSelection(null)
        if (dialog != null) dialog.setDisabledMonths(disabledMonths)
    }

    fun setEnabledMonths(enabled: ArrayList<IntArray>) {
        enabledMonths = enabled
        if (enabledMonths != null) {
            minYear = DateUtilsOffsetDate.extractYearFromDate(DateUtilsOffsetDate.nowLocal())
            maxYear = minYear
            if (enabledMonths!!.size > 0) {
                minYear = enabled[0][0]
                maxYear = enabled[0][0]
            }
            var clearCurrentSelection = true
            for (month in enabledMonths!!) {
                val currentYear = month[0]
                if (Arrays.equals(month, selectionAsArray)) clearCurrentSelection = false
                if (currentYear < minYear) minYear = currentYear
                if (currentYear > maxYear) maxYear = currentYear
            }
            if (clearCurrentSelection) setSelection(null)
        }
        if (dialog != null) dialog.setEnabledMonths(enabledMonths)
    }

    // Used to keep day of previously set date
    val selectionAsDate: OffsetDateTime?
        get() {
            if (hasSelection()) {
                if (cachedDate != null) { // Used to keep day of previously set date
                    cachedDate = DateUtilsOffsetDate.setYearToDate(cachedDate, selectionAsArray!![0])
                    cachedDate = DateUtilsOffsetDate.setMonthToDate(cachedDate, selectionAsArray!![1])
                }
                return if (cachedDate == null) DateUtilsOffsetDate.date(
                    selectionAsArray!![0],
                    selectionAsArray!![1]
                ) else cachedDate
            }
            return null
        }

    fun hasSelection(): Boolean {
        return selectionAsArray != null && selectionAsArray!!.size == 2
    }

    private fun compareValues(v1: IntArray?, v2: IntArray?): Boolean {
        return Arrays.equals(v1, v2)
    }

    private fun selectInternally(newSelection: IntArray?) {
        var newSelection = newSelection
        val oldSelection = selectionAsArray
        newSelection = validateSelectionInput(newSelection)
        selectionAsArray = newSelection
        updateTextAndValidate()
        dispatchSelectionChangedEvents(oldSelection, newSelection)
    }

    private fun validateSelectionInput(selection: IntArray?): IntArray? {
        return if (selection == null) null else validateSelectionInput(selection[0], selection[1])
    }

    private fun validateSelectionInput(year: Int, month: Int): IntArray {
        var year = year
        var month = month
        if (year < minYear) year = minYear
        if (year > maxYear) year = maxYear
        if (month < 1) month = 1
        if (month > 12) month = 12
        return intArrayOf(year, month)
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
        myState.selection = selectionAsArray
        myState.cachedDate = DateUtilsOffsetDate.getDateString(DateUtilsOffsetDate.defaultFormatWithTimeZone, cachedDate)
        myState.minYear = minYear
        myState.maxYear = maxYear
        if (enabledMonths != null) {
            val enabledMonths = ArrayList<String>()
            for (enabledMonth in this.enabledMonths!!) enabledMonths.add(
                DateUtilsOffsetDate.getDateStringInDefaultFormat(
                    DateUtilsOffsetDate.date(
                        enabledMonth[0], enabledMonth[1]
                    )
                )
            )
            myState.enabledMonths = enabledMonths
        }
        if (disabledMonths != null) {
            val disabledMonths = ArrayList<String>()
            for (disabledMonth in this.disabledMonths!!) disabledMonths.add(
                DateUtilsOffsetDate.getDateStringInDefaultFormat(
                    DateUtilsOffsetDate.date(
                        disabledMonth[0], disabledMonth[1]
                    )
                )
            )
            myState.disabledMonths = disabledMonths
        }
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        selectionAsArray = savedState.selection
        cachedDate =
            DateUtilsOffsetDate.getDateFromString(DateUtilsOffsetDate.defaultFormatWithTimeZone, savedState.cachedDate)
        if (savedState.disabledMonths == null && savedState.enabledMonths == null) setCalendarBounds(
            savedState.minYear!!,
            savedState.maxYear!!
        )
        if (savedState.disabledMonths != null) {
            val previouslyDisabledMonths = ArrayList<IntArray>()
            for (disabledMonth in savedState.disabledMonths!!) {
                val monthAsDate = DateUtilsOffsetDate.getDateFromStringInDefaultFormat(disabledMonth)
                val year = DateUtilsOffsetDate.extractYearFromDate(monthAsDate)
                val month = DateUtilsOffsetDate.extractMonthOfYearFromDate(monthAsDate)
                previouslyDisabledMonths.add(intArrayOf(year, month))
            }
            setDisabledMonths(previouslyDisabledMonths)
        }
        if (savedState.enabledMonths != null) {
            val previouslyEnabledMonths = ArrayList<IntArray>()
            for (enabledMonth in savedState.enabledMonths!!) {
                val monthAsDate = DateUtilsOffsetDate.getDateFromStringInDefaultFormat(enabledMonth)
                val year = DateUtilsOffsetDate.extractYearFromDate(monthAsDate)
                val month = DateUtilsOffsetDate.extractMonthOfYearFromDate(monthAsDate)
                previouslyEnabledMonths.add(intArrayOf(year, month))
            }
            setEnabledMonths(previouslyEnabledMonths)
        }
        super.onRestoreInstanceState(savedState.superState)
    }

    private class SavedState : BaseSavedState {
        var enabledMonths: ArrayList<String>? = null
        var disabledMonths: ArrayList<String>? = null
        var selection: IntArray?
        var cachedDate: String? = null
        var minYear: Int? = null
        var maxYear: Int? = null

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            enabledMonths = `in`.createStringArrayList()
            disabledMonths = `in`.createStringArrayList()
            minYear = `in`.readInt()
            maxYear = `in`.readInt()
            selection = `in`.createIntArray()
            cachedDate = `in`.readString()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeIntArray(selection)
            out.writeInt(minYear!!)
            out.writeInt(maxYear!!)
            out.writeString(cachedDate)
            out.writeStringList(enabledMonths)
            out.writeStringList(disabledMonths)
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
        fun onSelectionChanged(view: DialogMonthPickerView?, oldValue: IntArray?, newValue: IntArray?)
    }

    interface ValidationCheck {
        fun validate(currentSelection: IntArray?): Boolean
    }

    companion object {
        private const val DEFAULT_DATE_FORMAT = "YYYY, MMM"
        private const val DEFAULT_MAX_YEAR = 2100
        private const val DEFAULT_MIN_YEAR = 1970

        @JvmStatic
        @BindingAdapter("monthPickerSelection")
        fun updatePickerSelectionBinding(view: DialogMonthPickerView, selectedDate: OffsetDateTime?) {
            view.setSelectionFromDate(selectedDate)
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "monthPickerSelection", event = "monthPickerSelectionChanged")
        fun getText(view: DialogMonthPickerView): OffsetDateTime? {
            return view.selectionAsDate
        }

        @JvmStatic
        @BindingAdapter("monthPickerSelectionChanged")
        fun setListeners(view: DialogMonthPickerView, attrChange: InverseBindingListener) {
            view.dataBindingListener =
                label@ SelectionChangedListener { view1: DialogMonthPickerView, newValue: IntArray?, oldValue: IntArray? ->
                    if (view1.compareValues(newValue, oldValue)) return@label
                    attrChange.onChange()
                }
        }
    }
}