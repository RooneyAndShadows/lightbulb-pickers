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
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogBuilder
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerAdapter
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerAdapter.IconModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.DialogAdapterPickerView

@Suppress("unused")
class DialogIconPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : DialogAdapterPickerView<IconModel>(context, attrs, defStyleAttr) {
    var selectedIconSize = 0
        private set
    override val adapter: IconPickerAdapter
        get() {
            val dialog = (pickerDialog as IconPickerDialog)
            return dialog.adapter as IconPickerAdapter
        }

    init {
        readAttributes(context, attrs)
    }

    @Override
    override fun onDialogInitialized(dialog: BasePickerDialogFragment<IntArray>) {
        super.onDialogInitialized(dialog)
        dialog.apply {
            addSelectionChangedListener(object : SelectionChangedListener<IntArray> {
                override fun execute(newSelection: IntArray?, oldSelection: IntArray?) {
                    updatePickerIcon(newSelection)
                }
            })
        }
    }

    @Override
    override fun initializeDialog(): AdapterPickerDialog<IconModel> {
        return IconPickerDialog()
    }

    @Override
    override fun getDialogBuilder(
        fragmentManager: FragmentManager,
        fragmentTag: String,
    ): BaseDialogBuilder<out BasePickerDialogFragment<IntArray>> {
        return IconPickerDialogBuilder(null, fragmentManager, fragmentTag)
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
        myState.selectedIconSize = selectedIconSize
        return myState
    }

    @Override
    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        selectedIconSize = savedState.selectedIconSize
        updatePickerIcon(selection)
    }

    fun setSelectedIconSize(size: Int) {
        this.selectedIconSize = size
        updatePickerIcon(selection)
    }

    private fun updatePickerIcon(selection: IntArray?) {
        val icon = if (selection != null && selection.isNotEmpty()) {
            val firstSelectedPosition = selection[0]
            val selectedModel: IconModel = adapter.getItem(firstSelectedPosition)!!
            val drawable = adapter.getDrawable(context, selectedModel, selectedIconSize)
            drawable
        } else null
        setPickerIcon(icon)
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogIconPickerView, 0, 0)
        try {
            attrTypedArray.apply {
                val default = ResourceUtils.getDimenPxById(context, R.dimen.icon_picker_selected_size)
                selectedIconSize = getDimensionPixelSize(R.styleable.DialogIconPickerView_ipv_selected_icon_size, default)
            }
            showSelectedTextValue = false
        } finally {
            attrTypedArray.recycle()
        }
    }

    private class SavedState : BaseSavedState {
        var selectedIconSize = 0

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            selectedIconSize = parcel.readInt()
        }

        @Override
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(selectedIconSize)
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
        @BindingAdapter(value = ["iconPickerSelection"])
        @JvmStatic
        fun setIcon(view: DialogIconPickerView, newIconName: String?) {
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
        fun getIcon(view: DialogIconPickerView): String? {
            return if (view.hasSelection) {
                view.selectedItems[0].iconName
            } else null
        }

        @BindingAdapter(value = ["iconPickerSelectionChanged"], requireAll = false)
        @JvmStatic
        fun bindPickerEvent(view: DialogIconPickerView, bindingListener: InverseBindingListener) {
            if (view.hasSelection) bindingListener.onChange()
            view.dataBindingListener = object : SelectionChangedListener<IntArray> {
                override fun execute(newSelection: IntArray?, oldSelection: IntArray?) {
                    bindingListener.onChange()
                }
            }
        }
    }
}