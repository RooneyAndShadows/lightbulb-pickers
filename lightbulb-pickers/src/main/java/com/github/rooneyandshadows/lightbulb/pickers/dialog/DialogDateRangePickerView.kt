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
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialog.*
import com.github.rooneyandshadows.lightbulb.pickers.R
import java.time.OffsetDateTime
import java.util.*

@Suppress("RedundantOverride", "unused", "UnnecessaryVariable")
class DialogDateRangePickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : BaseDialogPickerView<DateRange>(context, attrs, defStyleAttr, defStyleRes) {
    private val dialog: DateRangePickerDialog
        get() = pickerDialog as DateRangePickerDialog
    private var datePickerFormat: String = DEFAULT_DATE_FORMAT
        set(value) {
            field = value
            dialog.dialogDateFormat
            updateTextAndValidate()
        }
        get() = dialog.dialogDateFormat
    private var datePickerFromText: String? = null
        set(value) {
            field = value
            dialog.dialogTextFrom = field
        }
        get() = dialog.dialogTextFrom
    private var datePickerToText: String? = null
        set(value) {
            field = value
            dialog.dialogTextTo = field
        }
        get() = dialog.dialogTextTo
    override val viewText: String
        get() {
            val default = ResourceUtils.getPhrase(context, R.string.dialog_date_picker_empty_text)
            if (!hasSelection) return default
            val from = DateUtilsOffsetDate.getDateString(datePickerFormat, selection!!.from)
            val to = DateUtilsOffsetDate.getDateString(datePickerFormat, selection!!.to)
            val viewTextFormat = "{from} - {to}"
            return viewTextFormat.replace("{from}", from).replace("{to}", to)
        }

    @Override
    override fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogDateRangePickerView, 0, 0)
        try {
            attrTypedArray.apply {
                datePickerFormat = getString(R.styleable.DialogDateRangePickerView_drpv_date_format).let {
                    val default = DEFAULT_DATE_FORMAT
                    if (it.isNullOrBlank()) return@let default
                    else return@let it
                }
                datePickerFromText = getString(R.styleable.DialogDateRangePickerView_drpv_text_from).let {
                    val default = ResourceUtils.getPhrase(context, R.string.picker_default_date_from_text)
                    if (it.isNullOrBlank()) return@let default
                    else return@let it
                }
                datePickerToText = getString(R.styleable.DialogDateRangePickerView_drpv_text_to).let {
                    val default = ResourceUtils.getPhrase(context, R.string.picker_default_date_to_text)
                    if (it.isNullOrBlank()) return@let default
                    else return@let it
                }
            }
        } finally {
            attrTypedArray.recycle()
        }
    }

    override fun initializeDialog(fragmentManager: FragmentManager): BasePickerDialogFragment<DateRange> {
        return DateRangePickerDialogBuilder(null, fragmentManager, pickerDialogTag)
            .buildDialog()
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

    fun setRange(start: OffsetDateTime, end: OffsetDateTime) {
        selection = DateRange(start, end)
    }

    private fun compareValues(v1: DateRange?, v2: DateRange?): Boolean {
        if ((v1 == null) && (v2 == null)) return true
        if ((v1 == null) || (v2 == null)) return false
        return v1.compare(v2)
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
        private const val DEFAULT_DATE_FORMAT = "yyyy/MM/dd"

        @JvmStatic
        @BindingAdapter("dateRangePickerSelection")
        fun setDateRange(view: DialogDateRangePickerView, newDateRange: DateRange?) {
            view.selection = newDateRange
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "dateRangePickerSelection", event = "dateRangeSelectionChanged")
        fun getDateRange(view: DialogDateRangePickerView): DateRange? {
            return view.selection
        }

        @JvmStatic
        @BindingAdapter("dateRangeSelectionChanged")
        fun setListeners(view: DialogDateRangePickerView, attrChange: InverseBindingListener) {
            view.dataBindingListener = object : SelectionChangedListener<DateRange> {
                override fun execute(newSelection: DateRange?, oldSelection: DateRange?) {
                    if (!view.compareValues(newSelection, oldSelection)) attrChange.onChange()
                }
            }
        }
    }
}