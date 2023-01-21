package com.github.rooneyandshadows.lightbulb.pickers.dialog

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogFragment.DialogButtonConfiguration
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import java.util.*

abstract class DialogAdapterPickerView<ModelType : EasyAdapterDataModel?>(
    context: Context?,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : BaseDialogPickerView(context, attrs, defStyleAttr, defStyleRes) {
    private val validationCallbacks = ArrayList<ValidationCheck<ModelType>>()
    private val selectionChangedListeners = ArrayList<SelectionChangedListener>()
    private var adapter: EasyRecyclerAdapter<ModelType>? = null
    private var itemDecoration: RecyclerView.ItemDecoration? = null
    private var dialogTitle: String? = null
    private var dialogMessage: String? = null
    private var pickerDialogType: BaseDialogFragment.DialogTypes? = null
    private var selection: IntArray?

    constructor(context: Context) : this(context, null) {}
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0) {}

    protected abstract fun initializeAdapter(): EasyRecyclerAdapter<ModelType>
    protected override fun readAttributes(context: Context, attrs: AttributeSet?) {
        val a: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogAdapterPickerView, 0, 0)
        try {
            dialogTitle = a.getString(R.styleable.DialogAdapterPickerView_APV_DialogTitle)
            dialogMessage = a.getString(R.styleable.DialogAdapterPickerView_APV_DialogMessage)
            pickerDialogType = DialogTypes.valueOf(a.getInt(R.styleable.DialogAdapterPickerView_APV_DialogMode, 1))
            if (dialogTitle == null || dialogTitle == "") dialogTitle = ""
            if (dialogMessage == null || dialogMessage == "") dialogMessage = ""
        } finally {
            a.recycle()
        }
    }

    protected val viewText: String
        protected get() {
            var text = ""
            if (selection != null) text = getAdapter().getPositionStrings(selection)
            return text
        }

    fun hasSelection(): Boolean {
        return selection != null && selection!!.size > 0
    }

    override fun validate(): Boolean {
        var isValid = true
        if (validationEnabled) {
            if (required && !hasSelection()) {
                setErrorEnabled(true)
                setErrorText(pickerRequiredText)
                return false
            }
            for (validationCallback in validationCallbacks) isValid = isValid and validationCallback.validate(
                selectedItems)
        }
        if (!isValid) {
            setErrorEnabled(true)
        } else {
            setErrorEnabled(false)
            setErrorText(null)
        }
        return isValid
    }

    protected override fun initializeDialog(): AdapterPickerDialog<ModelType>? {
        val dialogBuilder: AdapterPickerDialogBuilder<ModelType> = AdapterPickerDialogBuilder(manager, DIALOG_TAG, adapter)
        return dialogBuilder
            .withSelection(selection!!)
            .withDialogType(pickerDialogType)
            .withAnimations(pickerDialogAnimationType)
            .withCancelOnClickOutsude(pickerDialogCancelable)
            .withMessage(dialogMessage)
            .withTitle(dialogTitle)
            .withItemDecoration(itemDecoration)
            .withPositiveButton(DialogButtonConfiguration(pickerDialogPositiveButtonText)) { view, dialog -> updateTextAndValidate() }
            .withNegativeButton(DialogButtonConfiguration(pickerDialogNegativeButtonText)) { view, dialog -> updateTextAndValidate() }
            .withOnCancelListener { dialogFragment -> updateTextAndValidate() }
            .withSelectionCallback { oldValue, newValue -> selectInternally(newValue, false) }
            .buildDialog()
    }

    protected val dialog: AdapterPickerDialog<ModelType?>?
        protected get() = pickerDialog as AdapterPickerDialog<ModelType?>?

    protected fun getAdapter(): EasyRecyclerAdapter<ModelType> {
        if (adapter == null) adapter = initializeAdapter()
        return adapter!!
    }

    protected override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    protected override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
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

    fun addValidationCheck(validationCallback: ValidationCheck<ModelType>) {
        validationCallbacks.add(validationCallback)
    }

    fun setDialogTitle(dialogTitle: String?) {
        this.dialogTitle = dialogTitle
    }

    fun setDialogMessage(dialogMessage: String?) {
        this.dialogMessage = dialogMessage
    }

    fun setItemDecoration(itemDecoration: RecyclerView.ItemDecoration?) {
        this.itemDecoration = itemDecoration
    }

    val selectedItems: List<ModelType>
        get() = getAdapter().getItems(selection)

    fun selectItemAt(selection: Int) {
        val newSelection = intArrayOf(selection)
        if (dialog == null) selectInternally(newSelection, true) else dialog!!.setSelection(newSelection)
    }

    fun setSelection(selection: IntArray) {
        if (dialog == null) selectInternally(selection, true) else dialog!!.setSelection(selection)
    }

    fun selectItem(item: ModelType?) {
        if (item == null) return
        val position = getAdapter().getPosition(item)
        if (position != -1) selectItemAt(position)
    }

    var data: List<ModelType>?
        get() = ArrayList(getAdapter().getItems())
        set(data) {
            getAdapter().setCollection(data!!)
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