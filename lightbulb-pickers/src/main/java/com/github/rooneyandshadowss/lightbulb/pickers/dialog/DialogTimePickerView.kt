package com.github.RooneyAndShadows.lightbulb.pickers.dialog

import com.github.RooneyAndShadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import android.util.SparseArray
import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import android.content.Context
import android.util.AttributeSet
import androidx.fragment.app.FragmentManager
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_time.TimePickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_time.TimePickerDialog.*
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_time.TimePickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.pickers.R
import java.time.OffsetDateTime
import java.util.*

@Suppress("RedundantOverride", "unused")
class DialogTimePickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : BaseDialogPickerView<Time>(context, attrs, defStyleAttr, defStyleRes) {
    private val dialog: TimePickerDialog
        get() = pickerDialog as TimePickerDialog
    var timeFormat = DEFAULT_TIME_FORMAT
        set(value) {
            field = value
            updateTextAndValidate()
        }
    private val selectionAsDate: OffsetDateTime?
        get() {
            return if (!hasSelection) null
            else DateUtilsOffsetDate.setTimeToDate(DateUtilsOffsetDate.nowLocal(), selection!!.hour, selection!!.minute, 0)
        }
    override val viewText: String
        get() {
            return if (!hasSelection) ""
            else DateUtilsOffsetDate.getDateString(timeFormat, selectionAsDate)
        }

    @Override
    override fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogTimePickerView, 0, 0)
        try {
            attrTypedArray.apply {
                timeFormat = attrTypedArray.getString(R.styleable.DialogTimePickerView_tpv_time_format).let {
                    val default = DEFAULT_TIME_FORMAT
                    if (it.isNullOrBlank()) return@let default
                    else return@let it
                }
            }
        } finally {
            attrTypedArray.recycle()
        }
    }

    @Override
    override fun initializeDialog(fragmentManager: FragmentManager): BasePickerDialogFragment<Time> {
        return TimePickerDialogBuilder(null, fragmentManager, pickerDialogTag)
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

    fun setSelection(hour: Int, minutes: Int) {
        selection = Time(hour, minutes)
    }

    private fun compareValues(v1: Time?, v2: Time?): Boolean {
        if (v1 == null && v2 == null) return true
        if (v1 == null || v2 == null) return false
        return (v1.hour == v2.hour && v1.minute == v2.minute)
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
        private const val DEFAULT_TIME_FORMAT = "HH:mm"

        @BindingAdapter("timePickerSelection")
        fun setTime(view: DialogTimePickerView, newSelection: Time?) {
            view.selection = newSelection
        }

        @InverseBindingAdapter(attribute = "timePickerSelection", event = "timePickerSelectionChanged")
        fun getTime(view: DialogTimePickerView): Time? {
            return view.selection
        }

        @BindingAdapter("timePickerSelectionChanged")
        fun setListeners(view: DialogTimePickerView, attrChange: InverseBindingListener) {
            view.dataBindingListener = object : SelectionChangedListener<Time> {
                override fun execute(newSelection: Time?, oldSelection: Time?) {
                    if (view.compareValues(newSelection, newSelection)) return
                    attrChange.onChange()
                }
            }
        }
    }
}