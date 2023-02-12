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
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogBuilder
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.ChipsFilterView
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.ChipsFilterView.OnOptionCreatedListener
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.ChipsPickerAdapter
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.ChipsPickerAdapter.ChipModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.ChipsPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.ChipsPickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.DialogAdapterPickerView

@Suppress("unused")
class DialogChipsPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : DialogAdapterPickerView<ChipModel>(context, attrs, defStyleAttr) {
    override val adapter: ChipsPickerAdapter
        get() {
            val dialog = (pickerDialog as ChipsPickerDialog)
            return dialog.adapter as ChipsPickerAdapter
        }

    init {
        readAttributes(context, attrs)
    }

    @Override
    override fun onDialogInitialized(dialog: BasePickerDialogFragment<IntArray>) {
        super.onDialogInitialized(dialog)
        dialog.apply {
        }
    }

    @Override
    override fun initializeDialog(): AdapterPickerDialog<ChipModel> {
        return ChipsPickerDialog()
    }

    @Override
    override fun getDialogBuilder(
        fragmentManager: FragmentManager,
        fragmentTag: String,
    ): BaseDialogBuilder<out BasePickerDialogFragment<IntArray>> {
        return ChipsPickerDialogBuilder(null, fragmentManager, fragmentTag)
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

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogChipsPickerView, 0, 0)
        try {
            attrTypedArray.apply {
                getBoolean(R.styleable.DialogChipsPickerView_cpv_filterable, true).apply {
                    (pickerDialog as (ChipsPickerDialog)).setFilterable(this)
                }
                getBoolean(R.styleable.DialogChipsPickerView_cpv_allow_add_new_options, true).apply {
                    (pickerDialog as (ChipsPickerDialog)).setAllowAddNewOptions(this)
                }
            }
            showSelectedTextValue = true
        } finally {
            attrTypedArray.recycle()
        }
    }

    fun setOnChipCreatedListener(listener: OnOptionCreatedListener?) {
        (pickerDialog as (ChipsPickerDialog)).setOnNewOptionListener(listener)
    }

    fun setIsFilterable(isFilterable: Boolean) {
        (pickerDialog as (ChipsPickerDialog)).setFilterable(isFilterable)
    }

    fun setAllowAddNewOptions(allowNewOptions: Boolean) {
        (pickerDialog as (ChipsPickerDialog)).setAllowAddNewOptions(allowNewOptions)
    }

    private class SavedState : BaseSavedState {

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
        }

        @Override
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
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

    object Databinding {
        @BindingAdapter(value = ["chipsPickerSelection"])
        @JvmStatic
        fun setChip(view: DialogChipsPickerView, chipsToSelect: List<String>?) {
            if (chipsToSelect == null || chipsToSelect.isEmpty()) {
                if (view.hasSelection) view.selection = null
                return
            }
            println()
            val itemsToSelect = view.data.filter {
                return@filter chipListContainsChip(chipsToSelect, it)
            }.toList()
            view.setSelection(itemsToSelect)
        }

        @InverseBindingAdapter(attribute = "chipsPickerSelection", event = "chipsPickerSelectionChanged")
        @JvmStatic
        fun getChip(view: DialogChipsPickerView): List<String>? {
            return if (view.hasSelection) {
                val result = mutableListOf<String>().apply {
                    view.selectedItems.forEach {
                        add(it.chipTitle)
                    }
                }
                return result
            } else null
        }

        @BindingAdapter(value = ["chipsPickerSelectionChanged"], requireAll = false)
        @JvmStatic
        fun bindPickerEvent(view: DialogChipsPickerView, bindingListener: InverseBindingListener) {
            if (view.hasSelection) bindingListener.onChange()
            view.dataBindingListener = object : SelectionChangedListener<IntArray> {
                override fun execute(newSelection: IntArray?, oldSelection: IntArray?) {
                    bindingListener.onChange()
                }
            }
        }

        private fun chipListContainsChip(targetList: List<String>, chipModel: ChipModel): Boolean {
            targetList.forEach {
                if (it == chipModel.chipTitle) return true
            }
            return false
        }
    }
}