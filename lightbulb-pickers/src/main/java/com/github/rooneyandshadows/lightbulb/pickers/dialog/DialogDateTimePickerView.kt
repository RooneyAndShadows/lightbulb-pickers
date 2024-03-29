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
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_datetime.DateTimePickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_datetime.DateTimePickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import java.time.OffsetDateTime

@Suppress("RedundantOverride", "UnnecessaryVariable", "unused", "MemberVisibilityCanBePrivate")
class DialogDateTimePickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BaseDialogPickerView<OffsetDateTime>(context, attrs, defStyleAttr) {
    override val dialog: DateTimePickerDialog
        get() = super.dialog as DateTimePickerDialog
    var datePickerFormat: String = DEFAULT_DATE_FORMAT
        private set
        get() = dialog.dialogDateFormat
    override val viewText: String
        get() {
            val default = ResourceUtils.getPhrase(context, R.string.dialog_date_picker_empty_text)
            if (!hasSelection) return default
            return DateUtilsOffsetDate.getDateString(datePickerFormat, selection)
        }

    init {
        readAttributes(context, attrs)
    }

    @Override
    override fun getDialogBuilder(
        fragmentManager: FragmentManager,
        dialogTag: String,
    ): BaseDialogBuilder<out BasePickerDialogFragment<OffsetDateTime>> {
        return DateTimePickerDialogBuilder(dialogTag, fragmentManager)
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

    @Override
    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    @Override
    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    fun setDatePickerFormat(format: String?) {
        this.datePickerFormat = format ?: DEFAULT_DATE_FORMAT
        dialog.setDialogDateFormat(datePickerFormat)
        updateTextAndValidate()
    }

    private fun compareValues(v1: OffsetDateTime?, v2: OffsetDateTime?): Boolean {
        return DateUtilsOffsetDate.isDateEqual(v1, v2, true)
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogDateTimePickerView, 0, 0)
        try {
            attrTypedArray.apply {
                getString(R.styleable.DialogDateTimePickerView_dpv_date_format).apply {
                    val default = DEFAULT_DATE_FORMAT
                    whenDialogReady {
                        val dialog = it as DateTimePickerDialog
                        dialog.setDialogDateFormat(if (isNullOrBlank()) default else this)
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
    }

    object Databinding {
        @BindingAdapter("datePickerSelection")
        @JvmStatic
        fun setDateTime(view: DialogDateTimePickerView, newDateTime: OffsetDateTime?) {
            view.selection = newDateTime
        }


        @InverseBindingAdapter(attribute = "datePickerSelection", event = "dateSelectionChanged")
        @JvmStatic
        fun getDateTime(view: DialogDateTimePickerView): OffsetDateTime? {
            return view.selection
        }

        @BindingAdapter("dateSelectionChanged")
        @JvmStatic
        fun setListeners(view: DialogDateTimePickerView, attrChange: InverseBindingListener) {
            if (view.hasSelection) attrChange.onChange()
            view.dataBindingListener = SelectionChangedListener { newSelection, oldSelection ->
                if (!view.compareValues(newSelection, oldSelection)) {
                    attrChange.onChange()
                }
            }
        }
    }
}