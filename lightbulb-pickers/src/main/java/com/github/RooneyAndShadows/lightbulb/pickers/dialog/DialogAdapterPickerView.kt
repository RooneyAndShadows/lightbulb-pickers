package com.github.rooneyandshadows.lightbulb.pickers.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView.*
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment.*
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogTypes.*
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.callbacks.DialogButtonClickListener
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.callbacks.DialogCancelListener
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialog
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import java.util.*

@Suppress("UNCHECKED_CAST", "unused", "MemberVisibilityCanBePrivate", "UnnecessaryVariable")
abstract class DialogAdapterPickerView<ItemType : EasyAdapterDataModel> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : BaseDialogPickerView<IntArray?>(context, attrs, defStyleAttr, defStyleRes) {
    private val validationCallbacks = ArrayList<ValidationCheck<ItemType>>()
    private val selectionChangedListeners = ArrayList<SelectionChangedListener>()
    private var itemDecoration: ItemDecoration? = null
    protected open val adapter: EasyRecyclerAdapter<ItemType>
        get() {
            val dialog = (pickerDialog as AdapterPickerDialog<ItemType>)
            return dialog.adapter
        }
    var data: List<ItemType>
        get() {
            return adapter.getItems()
        }
        set(data) {
            adapter.setCollection(data)
        }
    var selection: IntArray?
        set(value) {
            (pickerDialog as AdapterPickerDialog<ItemType>).setSelection(value)
        }
        get() = pickerDialog.getSelection()
    val selectedItems: List<ItemType>
        get() {
            return adapter.getItems(selection)
        }
    val hasSelection: Boolean
        get() = pickerDialog.hasSelection()
    override val viewText: String
        get() {
            return selection?.let {
                return@let adapter.getPositionStrings(it)
            } ?: ""
        }

    @Override
    abstract override fun initializeDialog(): AdapterPickerDialog<ItemType>

    @Suppress("UNCHECKED_CAST")
    @Override
    override fun onDialogInitialized(dialog: BasePickerDialogFragment<IntArray?>) {
        super.onDialogInitialized(dialog)
        val adapterDialog = dialog as AdapterPickerDialog<ItemType>
        adapterDialog.apply {
            addOnPositiveClickListener(object : DialogButtonClickListener {
                override fun doOnClick(buttonView: View?, dialogFragment: BaseDialogFragment) {
                    updateTextAndValidate()
                }
            })
            addOnNegativeClickListeners(object : DialogButtonClickListener {
                override fun doOnClick(buttonView: View?, dialogFragment: BaseDialogFragment) {
                    updateTextAndValidate()
                }
            })
            addOnCancelListener(object : DialogCancelListener {
                override fun doOnCancel(dialogFragment: BaseDialogFragment) {
                    updateTextAndValidate()
                }
            })
            addOnSelectionChangedListener(object : BasePickerDialogFragment.SelectionChangedListener<IntArray?> {
                override fun onSelectionChanged(
                    dialog: BasePickerDialogFragment<IntArray?>,
                    oldValue: IntArray?,
                    newValue: IntArray?,
                ) {
                    updateTextAndValidate()
                    dispatchSelectionChangedEvents(oldValue, newValue)
                }
            })
            setItemDecoration(itemDecoration)
        }
    }

    @Override
    override fun readAttributes(context: Context, attrs: AttributeSet?) {
        val a: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogAdapterPickerView, 0, 0)
        try {
        } finally {
            a.recycle()
        }
    }

    @Override
    override fun validate(): Boolean {
        var isValid = true
        if (isValidationEnabled) {
            if (required && !hasSelection) {
                errorEnabled = true
                errorText = pickerRequiredText
                return false
            }
            for (validationCallback in validationCallbacks) isValid = isValid and validationCallback.validate(
                selectedItems)
        }
        if (!isValid) errorEnabled = true
        else {
            errorEnabled = false
            errorText = null
        }
        return isValid
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
        super.onRestoreInstanceState(savedState.superState)
    }

    fun addSelectionChangedListener(listener: SelectionChangedListener) {
        selectionChangedListeners.add(listener)
    }

    fun removeSelectionChangedListener(listener: SelectionChangedListener) {
        selectionChangedListeners.add(listener)
    }

    fun removeAllSelectionChangedListeners() {
        selectionChangedListeners.clear()
    }

    fun addValidationCheck(validationCheck: ValidationCheck<ItemType>) {
        validationCallbacks.add(validationCheck)
    }

    fun removeValidationCheck(validationCheck: ValidationCheck<ItemType>) {
        validationCallbacks.remove(validationCheck)
    }

    fun removeAllValidationChecks() {
        validationCallbacks.clear()
    }

    fun setItemDecoration(itemDecoration: ItemDecoration?) {
        this.itemDecoration = itemDecoration
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        adapter.notifyDataSetChanged()
        updateTextAndValidate()
    }

    private fun compareValues(v1: IntArray?, v2: IntArray?): Boolean {
        return Arrays.equals(v1, v2)
    }

    private fun dispatchSelectionChangedEvents(oldValue: IntArray?, newValue: IntArray?) {
        if (compareValues(oldValue, newValue)) return
        for (selectionChangedListener in selectionChangedListeners) selectionChangedListener.execute(oldValue, newValue)
    }

    interface SelectionChangedListener {
        fun execute(newPositions: IntArray?, oldPositions: IntArray?)
    }

    interface ValidationCheck<ModelType : EasyAdapterDataModel?> {
        fun validate(selectedItems: List<ModelType>?): Boolean
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

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}