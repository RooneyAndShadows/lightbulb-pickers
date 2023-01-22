package com.github.rooneyandshadows.lightbulb.pickers.dialog

import android.content.Context
import android.content.res.TypedArray
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment.*
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogButtonConfiguration
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogTypes
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogTypes.*
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.callbacks.DialogButtonClickListener
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.callbacks.DialogCancelListener
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import java.util.*

@Suppress("UNCHECKED_CAST")
abstract class DialogAdapterPickerView<ItemType : EasyAdapterDataModel> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : BaseDialogPickerView<IntArray?>(context, attrs, defStyleAttr, defStyleRes) {
    private val validationCallbacks = ArrayList<ValidationCheck<ItemType>>()
    private val selectionChangedListeners = ArrayList<SelectionChangedListener>()
    private var itemDecoration: RecyclerView.ItemDecoration? = null
    private val adapter: EasyRecyclerAdapter<ItemType>
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
    override val viewText: String
        get() {
            return selection?.let {
                return@let adapter.getPositionStrings(it)
            } ?: ""
        }

    override abstract fun initializeDialog(): AdapterPickerDialog<ItemType>

    @Suppress("UNCHECKED_CAST")
    override fun onDialogInitialized(dialog: BasePickerDialogFragment<IntArray?>) {
        super.onDialogInitialized(dialog)
        val adapterDialog = dialog as AdapterPickerDialog<ItemType>
        adapterDialog.apply {
            dialogTitle = this@DialogAdapterPickerView.dialogTitle
            dialogMessage = this@DialogAdapterPickerView.dialogMessage
            dialogType = pickerDialogType
            dialogAnimationType = pickerDialogAnimationType
            isCancelable = pickerDialogCancelable
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

    protected override fun readAttributes(context: Context, attrs: AttributeSet?) {
        val a: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogAdapterPickerView, 0, 0)
        try {
            dialogTitle = a.getString(R.styleable.DialogAdapterPickerView_APV_DialogTitle)
            dialogMessage = a.getString(R.styleable.DialogAdapterPickerView_APV_DialogMessage)
            pickerDialogType = valueOf(a.getInt(R.styleable.DialogAdapterPickerView_APV_DialogMode, 1))
            if (dialogTitle == null || dialogTitle == "") dialogTitle = ""
            if (dialogMessage == null || dialogMessage == "") dialogMessage = ""
        } finally {
            a.recycle()
        }
    }


    fun hasSelection(): Boolean {
        return selection != null && selection!!.size > 0
    }

    override fun validate(): Boolean {
        var isValid = true
        if (isValidationEnabled) {
            if (required && !hasSelection()) {
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

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState: Parcelable = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.selection = selection
        myState.adapterState = getAdapter().saveAdapterState()
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        selection = savedState.selection
        getAdapter().restoreAdapterState(savedState.adapterState)
        super.onRestoreInstanceState(savedState.superState)
    }

    fun addSelectionChangedListener(changedCallback: SelectionChangedListener) {
        selectionChangedListeners.add(changedCallback)
    }

    fun addValidationCheck(validationCallback: ValidationCheck<ItemType>) {
        validationCallbacks.add(validationCallback)
    }

    fun setItemDecoration(itemDecoration: RecyclerView.ItemDecoration?) {
        this.itemDecoration = itemDecoration
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        getAdapter().notifyDataSetChanged()
        updateTextAndValidate()
    }

    private fun compareValues(v1: IntArray?, v2: IntArray?): Boolean {
        return Arrays.equals(v1, v2)
    }

    private fun dispatchSelectionChangedEvents(oldValue: IntArray?, newValue: IntArray?) {
        if (compareValues(oldValue, newValue)) return
        for (selectionChangedListener in selectionChangedListeners) selectionChangedListener.execute(oldValue, newValue)
    }

    private fun selectInternally(newSelection: IntArray, selectInAdapter: Boolean) {
        val oldSelection = selection
        ensureAndApplySelection(newSelection)
        if (selectInAdapter) getAdapter().selectPositions(newSelection, true, false)
        updateTextAndValidate()
        dispatchSelectionChangedEvents(oldSelection, selection)
    }

    private fun ensureAndApplySelection(newSelection: IntArray) {
        val positionsToSelect: MutableList<Int> = ArrayList()
        for (positionToSelect in newSelection) {
            if (!getAdapter().positionExists(positionToSelect)) continue
            positionsToSelect.add(positionToSelect)
        }
        selection = IntArray(positionsToSelect.size)
        if (positionsToSelect.size <= 0) return
        for (i in positionsToSelect.indices) {
            selection!![i] = positionsToSelect[i]
        }
    }

    interface SelectionChangedListener {
        fun execute(newPositions: IntArray?, oldPositions: IntArray?)
    }

    interface ValidationCheck<ModelType : EasyAdapterDataModel?> {
        fun validate(selectedItems: List<ModelType>?): Boolean
    }

    private class SavedState : View.BaseSavedState {
        var selection: IntArray?
        var adapterState: Bundle? = null

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            selection = `in`.createIntArray()
            adapterState = `in`.readBundle(DialogAdapterPickerView::class.java.classLoader)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeIntArray(selection)
            out.writeBundle(adapterState)
        }

        companion object {
            val CREATOR: Creator<SavedState> = object : Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}