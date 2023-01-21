package com.github.rooneyandshadows.lightbulb.pickers.dialog

import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_time.TimePickerDialogBuilder.buildDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withPositiveButton
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withNegativeButton
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withOnCancelListener
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.buildDialog
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.pickers.R
import android.util.SparseArray
import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import android.os.Bundle
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import android.content.Context
import android.util.AttributeSet
import com.github.rooneyandshadows.java.commons.string.StringUtils
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerAdapter
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterSelectableModes
import java.util.*

class DialogIconPickerView(context: Context, attrs: AttributeSet?) : BaseDialogPickerView(context, attrs) {
    private val validationCallbacks: ArrayList<ValidationCheck<IconModel>> = ArrayList<ValidationCheck<IconModel>>()
    private val selectionChangedListeners = ArrayList<SelectionChangedListener>()
    val adapter: IconPickerAdapter?
    private var dialogTitle: String? = null
    private var dialogMessage: String? = null
    private val pickerDialogType: DialogTypes? = null
    private var selectedIconSize = 0
    private var selection: IntArray?

    constructor(context: Context) : this(context, null) {}

    init {
        adapter = IconPickerAdapter(getContext(), EasyAdapterSelectableModes.SELECT_SINGLE)
        addSelectionChangedListener(SelectionChangedListener { oldPositions: IntArray?, newPositions: IntArray? ->
            if (newPositions != null && newPositions.size > 0) setPickerIcon(adapter.getDrawable(
                adapter.selectedItems[0], selectedIconSize)) else setPickerIcon(null)
        })
    }

    override fun readAttributes(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogIconPickerView, 0, 0)
        try {
            dialogTitle = a.getString(R.styleable.DialogIconPickerView_IPV_DialogTitle)
            dialogMessage = a.getString(R.styleable.DialogIconPickerView_IPV_DialogMessage)
            selectedIconSize = a.getDimensionPixelSize(R.styleable.DialogIconPickerView_IPV_SelectedIconSize,
                ResourceUtils.getDimenPxById(context, R.dimen.icon_picker_selected_size))
            if (dialogTitle == null || dialogTitle == "") dialogTitle = ""
            if (dialogMessage == null || dialogMessage == "") dialogMessage = ""
            showSelectedTextValue = false
        } finally {
            a.recycle()
        }
    }

    protected override val viewText: String
        protected get() {
            var text = ""
            if (adapter != null && selection != null) text = adapter.getPositionStrings(selection)
            return text
        }

    fun hasSelection(): Boolean {
        return selection != null && selection!!.size > 0
    }

    override fun validate(): Boolean {
        var isValid = true
        if (validationEnabled) {
            if (required && !hasSelection()) {
                isErrorEnabled = true
                setErrorText(pickerRequiredText)
                return false
            }
            for (validationCallback in validationCallbacks) isValid = isValid and validationCallback.validate(
                selectedItems)
        }
        if (!isValid) {
            isErrorEnabled = true
        } else {
            isErrorEnabled = false
            setErrorText(null)
        }
        return isValid
    }

    override fun initializeDialog(): IconPickerDialog? {
        val dialogBuilder = IconPickerDialogBuilder(manager, dialogTag, adapter)
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

    protected override val dialog: BasePickerDialogFragment<*>?
        protected get() = pickerDialog as IconPickerDialog

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.selection = selection
        myState.selectedIconSize = selectedIconSize
        myState.adapterState = adapter!!.saveAdapterState()
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        selection = savedState.selection
        selectedIconSize = savedState.selectedIconSize
        adapter!!.restoreAdapterState(savedState.adapterState!!)
        if (hasSelection()) setPickerIcon(adapter.getDrawable(selectedItems[0], selectedIconSize))
        super.onRestoreInstanceState(savedState.superState)
    }

    fun addSelectionChangedListener(changedCallback: SelectionChangedListener) {
        selectionChangedListeners.add(changedCallback)
    }

    fun addValidationCheck(validationCallback: ValidationCheck<IconModel>) {
        validationCallbacks.add(validationCallback)
    }

    private fun compareValues(v1: IntArray?, v2: IntArray?): Boolean {
        return Arrays.equals(v1, v2)
    }

    private fun dispatchSelectionChangedEvents(oldValue: IntArray?, newValue: IntArray?) {
        if (compareValues(oldValue, newValue)) return
        for (selectionChangedListener in selectionChangedListeners) {
            selectionChangedListener.execute(oldValue, newValue)
        }
    }

    fun refresh() {
        adapter?.notifyDataSetChanged()
        updateTextAndValidate()
    }

    private fun selectInternally(newSelection: IntArray, selectInAdapter: Boolean) {
        val oldSelection = selection
        ensureAndApplySelection(newSelection)
        if (selectInAdapter) adapter!!.selectPositions(selection, true, false)
        updateTextAndValidate()
        dispatchSelectionChangedEvents(oldSelection, selection)
    }

    private fun ensureAndApplySelection(newSelection: IntArray) {
        val positionsToSelect: MutableList<Int> = ArrayList()
        for (positionToSelect in newSelection) {
            if (!adapter!!.positionExists(positionToSelect)) continue
            positionsToSelect.add(positionToSelect)
        }
        selection = IntArray(positionsToSelect.size)
        if (positionsToSelect.size <= 0) return
        for (i in positionsToSelect.indices) {
            selection!![i] = positionsToSelect[i]
        }
    }

    fun selectItemAt(selection: Int) {
        val newSelection = intArrayOf(selection)
        if (dialog == null) selectInternally(newSelection, true) else dialog!!.setSelection(newSelection)
    }

    fun selectItem(item: IconModel?) {
        if (item == null) return
        if (adapter != null) {
            val position = adapter.getPosition(item)
            if (position != -1) {
                selectItemAt(position)
            }
        }
    }

    fun setSelection(selection: IntArray) {
        if (dialog == null) selectInternally(selection, true) else dialog!!.setSelection(selection)
    }

    fun setSelectedIconSize(selectedIconSize: Int) {
        this.selectedIconSize = selectedIconSize
        if (adapter!!.hasSelection()) setPickerIcon(adapter.getDrawable(adapter.selectedItems[0], selectedIconSize))
    }

    val selectedItems: List<Any>
        get() = adapter?.getItems(selection) ?: ArrayList()
    var data: List<Any>?
        get() = if (adapter == null) null else ArrayList<IconModel>(adapter.getItems())
        set(data) {
            if (adapter == null) return
            adapter.setCollection(data)
        }

    fun getSelectedIconSize(): Int {
        return selectedIconSize
    }

    interface SelectionChangedListener {
        fun execute(oldPositions: IntArray?, newPositions: IntArray?)
    }

    interface ValidationCheck<ModelType : EasyAdapterDataModel?> {
        fun validate(selectedItems: List<ModelType>?): Boolean
    }

    private class SavedState : BaseSavedState {
        var adapterState: Bundle? = null
        var selectedIconSize = 0
        var selection: IntArray?

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            selection = `in`.createIntArray()
            selectedIconSize = `in`.readInt()
            adapterState = `in`.readBundle(DialogIconPickerView::class.java.classLoader)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            if (selection != null) out.writeIntArray(selection)
            out.writeInt(selectedIconSize)
            out.writeBundle(adapterState)
        }

        companion object {
            val CREATOR: Creator<SavedState> = object : Creator<SavedState?> {
                override fun createFromParcel(`in`: Parcel): SavedState? {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    companion object {
        @JvmStatic
        @InverseBindingAdapter(attribute = "iconPickerSelection", event = "iconPickerSelectionChanged")
        fun getSelectedValue(view: DialogIconPickerView): String? {
            return if (view.hasSelection()) {
                view.selectedItems[0].getIconExternalName()
            } else null
        }

        @JvmStatic
        @BindingAdapter(value = ["iconPickerSelection"])
        fun setPickerSelection(view: DialogIconPickerView, newExternalName: String) {
            if (StringUtils.isNullOrEmptyString(newExternalName)) return
            if (view.hasSelection()) {
                val currentSelection: IconModel = view.selectedItems[0]
                if (currentSelection.getIconExternalName().equals(newExternalName)) return
            }
            for (selectableTransactionTypeModel in view.data!!) if (newExternalName == selectableTransactionTypeModel.getIconExternalName()) {
                view.selectItem(selectableTransactionTypeModel)
                break
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["iconPickerSelectionChanged"], requireAll = false)
        fun bindPickerEvent(view: DialogIconPickerView, bindingListener: InverseBindingListener) {
            if (view.hasSelection()) bindingListener.onChange()
            view.addSelectionChangedListener(SelectionChangedListener { oldPositions: IntArray?, newPositions: IntArray? -> bindingListener.onChange() })
        }
    }
}