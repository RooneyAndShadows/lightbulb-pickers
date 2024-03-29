package com.github.rooneyandshadows.lightbulb.pickers.dialog

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.SparseArray
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.fragment.app.FragmentManager
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogBuilder
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialog.*
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import java.time.OffsetDateTime
import java.util.*

@Suppress("unused", "RedundantOverride", "MemberVisibilityCanBePrivate", "UnnecessaryVariable")
class DialogMonthPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BaseDialogPickerView<Month>(context, attrs, defStyleAttr) {
    override val dialog: MonthPickerDialog
        get() = super.dialog as MonthPickerDialog
    val selectionAsDate: OffsetDateTime?
        get() = selection?.toDate()
    override val viewText: String
        get() {
            val defaut = ResourceUtils.getPhrase(context, R.string.dialog_month_picker_empty_text)
            return if (!hasSelection) defaut
            else DateUtilsOffsetDate.getDateString(getMonthPickerFormat(), selectionAsDate)
        }

    init {
        readAttributes(context, attrs)
    }

    @Override
    override fun getDialogBuilder(
        fragmentManager: FragmentManager,
        dialogTag: String,
    ): BaseDialogBuilder<out BasePickerDialogFragment<Month>> {
        return MonthPickerDialogBuilder(dialogTag, fragmentManager)
    }
    @Override
    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    @Override
    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    @Override
    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        return myState
    }

    @Override
    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
    }

    fun setSelection(date: OffsetDateTime?) {
        selection = date?.let {
            return@let Month.fromDate(it)
        }
    }

    fun setMonthPickerFormat(format: String?) {
        dialog.setDialogDateFormat(format ?: DEFAULT_DATE_FORMAT)
        updateTextAndValidate()
    }

    fun setMinYear(year: Int) {
        dialog.setCalendarBounds(year, getMaxYear())
    }

    fun setMaxYear(year: Int) {
        dialog.setCalendarBounds(getMinYear(), year)
    }

    fun setDisabledMonths(disabledMonths: List<Month>) {
        dialog.setDisabledMonths(disabledMonths)
    }

    fun setEnabledMonths(enabledMonths: List<Month>) {
        dialog.setEnabledMonths(enabledMonths)
    }

    fun getMonthPickerFormat(): String {
        return dialog.dialogDateFormat
    }

    fun getMinYear(): Int {
        return dialog.minYear
    }

    fun getMaxYear(): Int {
        return dialog.maxYear
    }

    fun getDisabledMonths(): List<Month> {
        return dialog.disabledMonths
    }

    fun getEnabledMonths(): List<Month> {
        return dialog.enabledMonths
    }

    private fun compareValues(v1: Month?, v2: Month?): Boolean {
        if (v1 == null && v2 == null) return true
        if (v1 == null || v2 == null) return false
        return (v1.year == v2.year && v1.month == v2.month)
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogMonthPickerView, 0, 0)
        try {
            attrTypedArray.apply {
                attrTypedArray.getString(R.styleable.DialogMonthPickerView_mpv_date_format).apply {
                    val default = DEFAULT_DATE_FORMAT
                    whenDialogReady {
                        val dialog = it as MonthPickerDialog
                        dialog.setDialogDateFormat(if (isNullOrBlank()) default else this)
                    }
                }
                getInteger(R.styleable.DialogMonthPickerView_mpv_min_year, DEFAULT_MIN_YEAR).apply {
                    whenDialogReady {
                        val dialog = it as MonthPickerDialog
                        dialog.setMinYear(this)
                    }
                }
                getInteger(R.styleable.DialogMonthPickerView_mpv_max_year, DEFAULT_MAX_YEAR).apply {
                    whenDialogReady {
                        val dialog = it as MonthPickerDialog
                        dialog.setMaxYear(this)
                    }
                }
            }
        } finally {
            attrTypedArray.recycle()
        }
    }

    private class SavedState : BaseSavedState {

        constructor(superState: Parcelable?) : super(superState)
        private constructor(parcel: Parcel) : super(parcel)

        @Override
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
        }

        @Override
        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object {
        private const val DEFAULT_DATE_FORMAT = "YYYY, MMM"
        private const val DEFAULT_MAX_YEAR = 2100
        private const val DEFAULT_MIN_YEAR = 1970
    }

    object Databinding {
        @BindingAdapter("monthPickerSelection")
        @JvmStatic
        fun setMonth(view: DialogMonthPickerView, newSelection: Month?) {
            view.selection = newSelection
        }

        @InverseBindingAdapter(attribute = "monthPickerSelection", event = "monthPickerSelectionChanged")
        @JvmStatic
        fun getMonth(view: DialogMonthPickerView): Month? {
            return view.selection
        }

        @BindingAdapter("monthPickerSelectionChanged")
        @JvmStatic
        fun setListeners(view: DialogMonthPickerView, attrChange: InverseBindingListener) {
            if (view.hasSelection) attrChange.onChange()
            view.dataBindingListener = object : SelectionChangedListener<Month> {
                override fun execute(newSelection: Month?, oldSelection: Month?) {
                    if (view.compareValues(newSelection, oldSelection)) return
                    attrChange.onChange()
                }
            }
        }
    }
}