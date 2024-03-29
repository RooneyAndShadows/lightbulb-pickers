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
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialog.*
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import java.time.OffsetDateTime
import java.util.*

@Suppress("RedundantOverride", "unused", "UnnecessaryVariable")
class DialogDateRangePickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BaseDialogPickerView<DateRange>(context, attrs, defStyleAttr) {
    override val dialog: DateRangePickerDialog
        get() = super.dialog as DateRangePickerDialog
    override val viewText: String
        get() {
            val default = ResourceUtils.getPhrase(context, R.string.dialog_date_picker_empty_text)
            if (!hasSelection) return default
            val format = getDatePickerFormat()
            val from = DateUtilsOffsetDate.getDateString(format, selection!!.from)
            val to = DateUtilsOffsetDate.getDateString(format, selection!!.to)
            val viewTextFormat = "{from} - {to}"
            return viewTextFormat.replace("{from}", from).replace("{to}", to)
        }

    init {
        readAttributes(context, attrs)
    }

    @Override
    override fun getDialogBuilder(
        fragmentManager: FragmentManager,
        dialogTag: String,
    ): BaseDialogBuilder<out BasePickerDialogFragment<DateRange>> {
        return DateRangePickerDialogBuilder(dialogTag, fragmentManager)
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

    fun setDatePickerFormat(format: String?) {
        dialog.setDialogDateFormat(format ?: DEFAULT_DATE_FORMAT)
        updateTextAndValidate()
    }

    fun setDatePickerFromText(text: String?) {
        dialog.setDialogTextFrom(text)
    }

    fun setDatePickerToText(text: String?) {
        dialog.setDialogTextTo(text)
    }

    fun getDatePickerFormat(): String {
        return dialog.dialogDateFormat
    }

    fun getDatePickerFromText(): String? {
        return dialog.dialogTextFrom
    }

    fun getDatePickerToText(): String? {
        return dialog.dialogTextTo
    }

    private fun compareValues(v1: DateRange?, v2: DateRange?): Boolean {
        if ((v1 == null) && (v2 == null)) return true
        if ((v1 == null) || (v2 == null)) return false
        return v1.compare(v2)
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogDateRangePickerView, 0, 0)
        try {
            attrTypedArray.apply {
                getString(R.styleable.DialogDateRangePickerView_drpv_date_format).apply {
                    val default = DEFAULT_DATE_FORMAT
                    whenDialogReady {
                        val dialog = it as DateRangePickerDialog
                        dialog.setDialogDateFormat(if (isNullOrBlank()) default else this)
                    }
                }
                getString(R.styleable.DialogDateRangePickerView_drpv_text_from).apply {
                    val default = ResourceUtils.getPhrase(context, R.string.picker_default_date_from_text)
                    whenDialogReady {
                        val dialog = it as DateRangePickerDialog
                        dialog.setDialogTextFrom(if (isNullOrBlank()) default else this)
                    }
                }
                getString(R.styleable.DialogDateRangePickerView_drpv_text_to).apply {
                    val default = ResourceUtils.getPhrase(context, R.string.picker_default_date_to_text)
                    whenDialogReady {
                        val dialog = it as DateRangePickerDialog
                        dialog.setDialogTextFrom(if (isNullOrBlank()) default else this)
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
        private const val DEFAULT_DATE_FORMAT = "yyyy/MM/dd"
    }

    object Databinding {
        @BindingAdapter("dateRangePickerSelection")
        @JvmStatic
        fun setDateRange(view: DialogDateRangePickerView, newDateRange: DateRange?) {
            view.selection = newDateRange
        }

        @InverseBindingAdapter(attribute = "dateRangePickerSelection", event = "dateRangeSelectionChanged")
        @JvmStatic
        fun getDateRange(view: DialogDateRangePickerView): DateRange? {
            return view.selection
        }


        @BindingAdapter("dateRangeSelectionChanged")
        @JvmStatic
        fun setListeners(view: DialogDateRangePickerView, attrChange: InverseBindingListener) {
            if (view.hasSelection) attrChange.onChange()
            view.dataBindingListener = SelectionChangedListener { newSelection, oldSelection ->
                if (!view.compareValues(newSelection, oldSelection)) {
                    attrChange.onChange()
                }
            }
        }
    }
}