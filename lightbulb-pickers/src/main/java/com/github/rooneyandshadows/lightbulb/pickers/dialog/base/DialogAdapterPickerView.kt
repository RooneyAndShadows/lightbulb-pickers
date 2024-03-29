package com.github.rooneyandshadows.lightbulb.pickers.dialog.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView.*
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogBuilder
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment.*
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogTypes.*
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.adapter.DialogPickerAdapter
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel
import java.util.*

@Suppress("UNCHECKED_CAST", "unused", "MemberVisibilityCanBePrivate", "UnnecessaryVariable")
@JvmSuppressWildcards
abstract class DialogAdapterPickerView<ItemType : EasyAdapterDataModel> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : BaseDialogPickerView<IntArray>(context, attrs, defStyleAttr) {
    override val dialog: AdapterPickerDialog<ItemType>
        get() = super.dialog as AdapterPickerDialog<ItemType>
    protected open val adapter: DialogPickerAdapter<ItemType>
        get() = dialog.adapter!!
    var data: List<ItemType>
        get() = adapter.collection.getItems()
        set(value) = adapter.collection.set(value)
    override var selection: IntArray?
        set(value) = dialog.setSelection(value)
        get() = dialog.getSelection()
    val selectedItems: List<ItemType>
        get() = adapter.collection.getItems(selection ?: intArrayOf())
    override val viewText: String
        get() {
            return selection?.let {
                return@let adapter.collection.getPositionStrings(it)
            } ?: ""
        }

    init {
        isSaveEnabled = true
        readAttributes(context, attrs)
    }

    @Override
    abstract fun initializeDialog(): AdapterPickerDialog<ItemType>

    override fun getDialogBuilder(
        fragmentManager: FragmentManager,
        dialogTag: String,
    ): BaseDialogBuilder<out BasePickerDialogFragment<IntArray>> {
        return AdapterPickerDialogBuilder(dialogTag, fragmentManager, { initializeDialog() })
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

    fun setItemDecoration(itemDecoration: ItemDecoration) {
        dialog.setItemDecoration(itemDecoration)
    }

    fun setSelection(items: List<ItemType>?) {
        if (items == null || items.isEmpty()) {
            selection = null
            return
        }
        val itemPositions = adapter.collection.getPositions(items)
        selection = itemPositions
    }

    fun setSelection(position: Int) {
        selection = intArrayOf(position)
    }

    fun setSelection(item: ItemType?) {
        if (item == null) {
            selection = null
            return
        }
        val position = adapter.collection.getPosition(item)
        if (position != -1) setSelection(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refresh() {
        adapter.notifyDataSetChanged()
        updateTextAndValidate()
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray: TypedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.DialogAdapterPickerView, 0, 0)
        try {
        } finally {
            attrTypedArray.recycle()
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