package com.github.rooneyandshadows.lightbulb.pickers.dialog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.SparseArray
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.github.rooneyandshadows.java.commons.string.StringUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter.ColorModel
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.buildDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withNegativeButton
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withOnCancelListener
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withPositiveButton
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_time.TimePickerDialogBuilder.buildDialog
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.DialogPickerTriggerLayout
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import java.util.*

@Suppress("RedundantOverride")
class DialogColorPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : DialogAdapterPickerView<ColorModel>(context, attrs, defStyleAttr, defStyleRes) {
    override val adapter: ColorPickerAdapter
        get() {
            val dialog = (pickerDialog as ColorPickerDialog)
            return dialog.adapter as ColorPickerAdapter
        }

    init {
        addSelectionChangedListener(object : SelectionChangedListener {
            override fun execute(newPositions: IntArray?, oldPositions: IntArray?) {
                updatePickerIcon(newPositions)
            }
        })
        addOnTriggerAttachedListener(object : TriggerAttachedCallback<IntArray?> {
            override fun onAttached(triggerView: DialogPickerTriggerLayout, pickerView: BaseDialogPickerView<IntArray?>) {
                updatePickerIcon(null)
            }
        })
    }
    @Override
    override fun readAttributes(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogColorPickerView, 0, 0)
        try {
            showSelectedTextValue = false
        } finally {
            a.recycle()
        }
    }

    @Override
    override fun initializeDialog(): ColorPickerDialog? {
        val dialogBuilder = ColorPickerDialogBuilder(manager, dialogTag, adapter)
        return dialogBuilder
            .withSelection(selection!!)
            .withDialogType(pickerDialogType)
            .withAnimations(pickerDialogAnimationType)
            .withCancelOnClickOutsude(pickerDialogCancelable)
            .withMessage(dialogMessage)
            .withTitle(dialogTitle)
            .withPositiveButton(DialogButtonConfiguration(pickerDialogPositiveButtonText)) { view, dialog -> updateTextAndValidate() }
            .withNegativeButton(DialogButtonConfiguration(pickerDialogNegativeButtonText)) { view, dialog -> updateTextAndValidate() }
            .withOnCancelListener { dialogFragment -> updateTextAndValidate() }
            .withSelectionCallback { oldValue, newValue -> selectInternally(newValue, false) }
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
    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        return myState
    }

    @Override
    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        updatePickerIcon(selection)
        super.onRestoreInstanceState(savedState.superState)
    }

    private fun updatePickerIcon(selection: IntArray?) {
        if (selection == null || selection.isEmpty()) {
            val defaultDrawable = ResourceUtils.getDrawable(context, R.drawable.color_picker_default_icon)
            pickerIcon = defaultDrawable
        } else {
            val firstSelectedPosition = selection[0]
            val selectedModel: ColorModel = adapter.getItem(firstSelectedPosition)!!
            val drawable = adapter.getColorDrawable(context, selectedModel)
            val color = Color.parseColor(selectedModel.colorHex)
            setPickerIcon(drawable, color)
        }
    }

    private class SavedState : BaseSavedState {

        constructor(superState: Parcelable?) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
        }

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
        @JvmStatic
        @InverseBindingAdapter(attribute = "colorPickerSelection", event = "colorPickerSelectionChanged")
        fun getSelectedValue(view: DialogColorPickerView): String? {
            return if (view.hasSelection()) {
                view.selectedItems[0].getColorExternalName()
            } else null
        }

        @JvmStatic
        @BindingAdapter(value = ["colorPickerSelection"])
        fun setPickerSelection(view: DialogColorPickerView, newExternalName: String) {
            if (StringUtils.isNullOrEmptyString(newExternalName)) return
            if (view.hasSelection()) {
                val currentSelection: ColorModel = view.selectedItems[0]
                if (currentSelection.getColorExternalName().equals(newExternalName)) return
            }
            for (selectableTransactionTypeModel in view.data!!) if (newExternalName == selectableTransactionTypeModel.getColorExternalName()) {
                view.selectItem(selectableTransactionTypeModel)
                break
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["colorPickerSelectionChanged"], requireAll = false)
        fun bindPickerEvent(view: DialogColorPickerView, bindingListener: InverseBindingListener) {
            if (view.hasSelection()) bindingListener.onChange()
            view.addSelectionChangedListener(SelectionChangedListener { newPositions: IntArray?, oldPositions: IntArray? -> bindingListener.onChange() })
        }
    }
}