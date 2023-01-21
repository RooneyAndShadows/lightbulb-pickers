package com.github.rooneyandshadows.lightbulb.pickers.dialog

import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_time.TimePickerDialogBuilder.buildDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter.ColorModel.colorHex
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withPositiveButton
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withNegativeButton
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withOnCancelListener
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.buildDialog
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.DialogPickerTriggerLayout
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
import android.graphics.Color
import android.util.AttributeSet
import com.github.rooneyandshadows.java.commons.string.StringUtils
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterSelectableModes
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import java.util.*

class DialogColorPickerView(context: Context, attrs: AttributeSet?) : BaseDialogPickerView(context, attrs) {
    private val validationCallbacks: ArrayList<ValidationCheck<ColorModel>> = ArrayList<ValidationCheck<ColorModel>>()
    private val selectionChangedListeners = ArrayList<SelectionChangedListener>()
    private val adapter: ColorPickerAdapter?
    private var dialogTitle: String? = null
    private var dialogMessage: String? = null
    private val pickerDialogType: BaseDialogFragment.DialogTypes? = null
    private var selection: IntArray?

    constructor(context: Context) : this(context, null) {}

    init {
        adapter = ColorPickerAdapter(getContext(), EasyAdapterSelectableModes.SELECT_SINGLE)
        addSelectionChangedListener(SelectionChangedListener { oldPositions: IntArray?, newPositions: IntArray? ->
            updatePickerIcon(newPositions)
        })
        addOnTriggerAttachedCallback { triggerView1: DialogPickerTriggerLayout?, pickerView: BaseDialogPickerView? ->
            updatePickerIcon(null)
        }
    }

    override fun readAttributes(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogColorPickerView, 0, 0)
        try {
            dialogTitle = a.getString(R.styleable.DialogColorPickerView_CPV_DialogTitle)
            dialogMessage = a.getString(R.styleable.DialogColorPickerView_CPV_DialogMessage)
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

    protected override val dialog: BasePickerDialogFragment<*>?
        protected get() = pickerDialog as ColorPickerDialog

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
        myState.adapterState = getAdapter()!!.saveAdapterState()
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        selection = savedState.selection
        getAdapter()!!.restoreAdapterState(savedState.adapterState!!)
        updatePickerIcon(selection)
        super.onRestoreInstanceState(savedState.superState)
    }

    fun addSelectionChangedListener(changedCallback: SelectionChangedListener) {
        selectionChangedListeners.add(changedCallback)
    }

    fun addValidationCheck(validationCallback: ValidationCheck<ColorModel>) {
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

    fun getAdapter(): EasyRecyclerAdapter<ColorModel>? {
        return adapter
    }

    val selectedItems: List<Any>
        get() = adapter?.getItems(selection) ?: ArrayList()

    fun selectItemAt(selection: Int) {
        val newSelection = intArrayOf(selection)
        if (dialog == null) selectInternally(newSelection, true) else dialog!!.setSelection(newSelection)
    }

    fun setSelection(selection: IntArray) {
        if (dialog == null) selectInternally(selection, true) else dialog!!.setSelection(selection)
    }

    fun selectItem(item: ColorModel?) {
        if (item == null) return
        if (adapter != null) {
            val position = adapter.getPosition(item)
            if (position != -1) {
                selectItemAt(position)
            }
        }
    }

    var data: List<Any>?
        get() = if (adapter == null) null else ArrayList<ColorModel>(adapter.getItems())
        set(data) {
            if (adapter == null) return
            adapter.setCollection(data)
        }

    fun refresh() {
        adapter?.notifyDataSetChanged()
        updateTextAndValidate()
    }

    private fun selectInternally(newSelection: IntArray, selectInAdapter: Boolean) {
        val oldSelection = selection
        ensureAndApplySelection(newSelection)
        if (selectInAdapter) adapter!!.selectPositions(newSelection, true, false)
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

    private fun updatePickerIcon(selection: IntArray?) {
        if (!hasSelection()) {
            val defaultDrawable = ResourceUtils.getDrawable(context, R.drawable.color_picker_default_icon)
            setPickerIcon(defaultDrawable)
        } else {
            val selectedModel: ColorModel? = adapter!!.getItem(selection!![0])
            val drawable = adapter.getColorDrawable(selectedItems[0])
            val color = Color.parseColor(selectedModel.colorHex)
            setPickerIcon(drawable, color)
        }
    }

    interface SelectionChangedListener {
        fun execute(oldPositions: IntArray?, newPositions: IntArray?)
    }

    interface ValidationCheck<ModelType : EasyAdapterDataModel?> {
        fun validate(selectedItems: List<ModelType>?): Boolean
    }

    private class SavedState : BaseSavedState {
        var adapterState: Bundle? = null
        var selection: IntArray?

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            selection = `in`.createIntArray()
            adapterState = `in`.readBundle(DialogColorPickerView::class.java.classLoader)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            if (selection != null) out.writeIntArray(selection)
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