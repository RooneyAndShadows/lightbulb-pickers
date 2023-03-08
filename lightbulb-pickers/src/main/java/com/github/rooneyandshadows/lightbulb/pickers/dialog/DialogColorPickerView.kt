package com.github.rooneyandshadows.lightbulb.pickers.dialog

import android.content.Context
import android.graphics.Color
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
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter.ColorModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.DialogAdapterPickerView
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogBuilder
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_chips.ChipsPickerDialog
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.base.DialogTriggerView

@Suppress("RedundantOverride", "UnnecessaryVariable", "unused")
class DialogColorPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : DialogAdapterPickerView<ColorModel>(context, attrs, defStyleAttr) {
    private val dialog: ColorPickerDialog
        get() = pickerDialog as ColorPickerDialog
    override val adapter: ColorPickerAdapter
        get() = dialog.adapter as ColorPickerAdapter

    init {
        readAttributes(context, attrs)
        addOnTriggerAttachedListener(object : TriggerAttachedCallback<IntArray> {
            override fun onAttached(triggerView: DialogTriggerView, pickerView: BaseDialogPickerView<IntArray>) {
                updatePickerIcon(selection)
            }
        })
        addSelectionChangedListener(object : SelectionChangedListener<IntArray> {
            override fun execute(newSelection: IntArray?, oldSelection: IntArray?) {
                updatePickerIcon(newSelection)
            }
        })
    }

    @Override
    override fun getDialogBuilder(
        fragmentManager: FragmentManager,
        fragmentTag: String,
    ): BaseDialogBuilder<out BasePickerDialogFragment<IntArray>> {
        return ColorPickerDialogBuilder(null, fragmentManager, fragmentTag)
    }

    @Override
    override fun initializeDialog(): AdapterPickerDialog<ColorModel> {
        return ColorPickerDialog()
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
        updatePickerIcon(selection)
    }

    private fun updatePickerIcon(selection: IntArray?) {
        if (selection == null || selection.isEmpty()) {
            val defaultDrawable = ResourceUtils.getDrawable(context, R.drawable.color_picker_default_icon)
            setPickerIcon(defaultDrawable)
        } else {
            val firstSelectedPosition = selection[0]
            val selectedModel: ColorModel = adapter.getItem(firstSelectedPosition)!!
            val drawable = adapter.getColorDrawable(context, selectedModel)
            val color = Color.parseColor(selectedModel.colorHex)
            setPickerIcon(drawable, color)
        }
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogColorPickerView, 0, 0)
        try {
            showSelectedTextValue = false
        } finally {
            a.recycle()
        }
    }

    private class SavedState : BaseSavedState {
        constructor(superState: Parcelable?) : super(superState)
        constructor(parcel: Parcel) : super(parcel)

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

    object Databinding {
        @BindingAdapter(value = ["colorPickerSelection"])
        @JvmStatic
        fun setColor(view: DialogColorPickerView, newColorName: String?) {
            if (newColorName.isNullOrBlank()) {
                if (view.hasSelection) view.selection = null
                return
            }
            if (view.hasSelection) {
                val currentSelection: ColorModel = view.selectedItems[0]
                if (currentSelection.externalName == newColorName) return
            }
            for (colorModel in view.data)
                if (newColorName == colorModel.externalName) {
                    view.setSelection(colorModel)
                    break
                }
        }

        @InverseBindingAdapter(attribute = "colorPickerSelection", event = "colorPickerSelectionChanged")
        @JvmStatic
        fun getColor(view: DialogColorPickerView): String? {
            return if (view.hasSelection) {
                view.selectedItems[0].externalName
            } else null
        }

        @BindingAdapter(value = ["colorPickerSelectionChanged"], requireAll = false)
        @JvmStatic
        fun bindPickerEvent(view: DialogColorPickerView, bindingListener: InverseBindingListener) {
            if (view.hasSelection) bindingListener.onChange()
            view.dataBindingListener = object : SelectionChangedListener<IntArray> {
                override fun execute(newSelection: IntArray?, oldSelection: IntArray?) {
                    bindingListener.onChange()
                }
            }
        }
    }
}