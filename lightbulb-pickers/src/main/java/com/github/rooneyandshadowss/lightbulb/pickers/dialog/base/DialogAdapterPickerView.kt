package com.github.RooneyAndShadows.lightbulb.pickers.dialog.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView.*
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment.*
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogTypes.*
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialog
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import java.util.*

@Suppress("UNCHECKED_CAST", "unused", "MemberVisibilityCanBePrivate", "UnnecessaryVariable")
abstract class DialogAdapterPickerView<ItemType : EasyAdapterDataModel> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : BaseDialogPickerView<IntArray>(context, attrs, defStyleAttr, defStyleRes) {
    protected open val adapter: EasyRecyclerAdapter<ItemType>
        get() {
            val dialog = (pickerDialog as AdapterPickerDialog<ItemType>)
            return dialog.adapter
        }
    var itemDecoration: ItemDecoration? = null
        set(value) {
            field = value
            (pickerDialog as AdapterPickerDialog<ItemType>).setItemDecoration(field)
        }
    var data: List<ItemType>
        get() {
            return adapter.getItems()
        }
        set(data) {
            adapter.setCollection(data)
        }
    override var selection: IntArray?
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

    @Override
    abstract override fun initializeDialog(fragmentManager: FragmentManager): BasePickerDialogFragment<IntArray>

    @Suppress("UNCHECKED_CAST")
    @Override
    override fun onDialogInitialized(dialog: BasePickerDialogFragment<IntArray>) {
        super.onDialogInitialized(dialog)
        val adapterDialog = dialog as AdapterPickerDialog<ItemType>
        adapterDialog.apply {
            setItemDecoration(itemDecoration)
        }
    }

    @Override
    override fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray: TypedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.DialogAdapterPickerView, 0, 0)
        try {
        } finally {
            attrTypedArray.recycle()
        }
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

    fun selectItemAt(position: Int) {
        selection = intArrayOf(position)
    }

    fun selectItem(item: ItemType?) {
        if (item == null) return
        val position = adapter.getPosition(item)
        if (position != -1) selectItemAt(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        adapter.notifyDataSetChanged()
        updateTextAndValidate()
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