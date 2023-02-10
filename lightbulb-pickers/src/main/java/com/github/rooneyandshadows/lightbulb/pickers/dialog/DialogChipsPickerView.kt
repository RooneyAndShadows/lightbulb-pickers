package com.github.rooneyandshadows.lightbulb.pickers.dialog

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.SparseArray
import androidx.fragment.app.FragmentManager
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogBuilder
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.ChipsPickerAdapter
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.ChipsPickerAdapter.ChipModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.ChipsPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.ChipsPickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerDialogBuilder
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
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogIconPickerView, 0, 0)
        try {
            attrTypedArray.apply {

            }
            showSelectedTextValue = true
        } finally {
            attrTypedArray.recycle()
        }
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
        /*@BindingAdapter(value = ["iconPickerSelection"])
        @JvmStatic
        fun setIcon(view: ChipsPickerView, newIconName: String?) {
            if (newIconName.isNullOrBlank()) {
                if (view.hasSelection) view.selection = null
                return
            }
            if (view.hasSelection) {
                val currentSelection: IconModel = view.selectedItems[0]
                if (currentSelection.iconName == newIconName) return
            }
            for (iconModel in view.data) if (newIconName == iconModel.iconName) {
                view.selectItem(iconModel)
                break
            }
        }

        @InverseBindingAdapter(attribute = "iconPickerSelection", event = "iconPickerSelectionChanged")
        @JvmStatic
        fun getIcon(view: ChipsPickerView): String? {
            return if (view.hasSelection) {
                view.selectedItems[0].iconName
            } else null
        }

        @BindingAdapter(value = ["iconPickerSelectionChanged"], requireAll = false)
        @JvmStatic
        fun bindPickerEvent(view: ChipsPickerView, bindingListener: InverseBindingListener) {
            if (view.hasSelection) bindingListener.onChange()
            view.dataBindingListener = object : SelectionChangedListener<IntArray> {
                override fun execute(newSelection: IntArray?, oldSelection: IntArray?) {
                    bindingListener.onChange()
                }
            }
        }*/
    }
}