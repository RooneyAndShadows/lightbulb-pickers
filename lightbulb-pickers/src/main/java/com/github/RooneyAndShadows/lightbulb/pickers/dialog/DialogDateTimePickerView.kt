package com.github.rooneyandshadows.lightbulb.pickers.dialog

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
import androidx.fragment.app.FragmentManager
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_datetime.DateTimePickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_datetime.DateTimePickerDialogBuilder
import java.time.OffsetDateTime

@Suppress("RedundantOverride", "UnnecessaryVariable", "unused")
class DialogDateTimePickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : BaseDialogPickerView<OffsetDateTime?>(context, attrs, defStyleAttr, defStyleRes) {
    private val dialog: DateTimePickerDialog
        get() = pickerDialog as DateTimePickerDialog
    private var datePickerFormat: String = DEFAULT_DATE_FORMAT
        set(value) {
            field = value
            dialog.dialogDateFormat = field
            updateTextAndValidate()
        }
        get() = dialog.dialogDateFormat
    override val viewText: String
        get() {
            val default = ResourceUtils.getPhrase(context, R.string.dialog_date_picker_empty_text)
            if (!hasSelection) return default
            return DateUtilsOffsetDate.getDateString(datePickerFormat, selection)
        }

    override fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogDatePickerView, 0, 0)
        try {
            attrTypedArray.apply {
                datePickerFormat = getString(R.styleable.DialogDatePickerView_DPV_DateFormat).let {
                    val default = DEFAULT_DATE_FORMAT
                    if (it.isNullOrBlank()) return@let default
                    else return@let it
                }
            }
        } finally {
            attrTypedArray.recycle()
        }
    }

    @Override
    override fun initializeDialog(fragmentManager: FragmentManager): BasePickerDialogFragment<OffsetDateTime?> {
        return DateTimePickerDialogBuilder(null, fragmentManager, pickerDialogTag)
            .buildDialog()
    }

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

    @Override
    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    @Override
    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    private fun compareValues(v1: OffsetDateTime?, v2: OffsetDateTime?): Boolean {
        return DateUtilsOffsetDate.isDateEqual(v1, v2, true)
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
            @Override
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            @Override
            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }

    companion object {
        private const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm"

        @JvmStatic
        @BindingAdapter("datePickerSelection")
        fun updatePickerSelectionBinding(view: DialogDateTimePickerView, selectedDate: OffsetDateTime?) {
            view.selection = selectedDate
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "datePickerSelection", event = "dateSelectionChanged")
        fun getText(view: DialogDateTimePickerView): OffsetDateTime? {
            return view.selection
        }

        @JvmStatic
        @BindingAdapter("dateSelectionChanged")
        fun setListeners(view: DialogDateTimePickerView, attrChange: InverseBindingListener) {
            view.dataBindingListener = object : SelectionChangedListener<OffsetDateTime?> {
                override fun execute(newSelection: OffsetDateTime?, oldSelection: OffsetDateTime?) {
                    if (!view.compareValues(newSelection, oldSelection))
                        attrChange.onChange()
                }
            }
        }
    }
}