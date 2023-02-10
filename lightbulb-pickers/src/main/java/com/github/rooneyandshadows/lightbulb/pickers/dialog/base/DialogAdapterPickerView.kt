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
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment.*
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogTypes.*
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialogBuilder
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter
import java.util.*

@Suppress("UNCHECKED_CAST", "unused", "MemberVisibilityCanBePrivate", "UnnecessaryVariable")
@JvmSuppressWildcards
abstract class DialogAdapterPickerView<ItemType : EasyAdapterDataModel> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseDialogPickerView<IntArray>(context, attrs, defStyleAttr) {
    private val dialog: AdapterPickerDialog<ItemType>
        get() = (pickerDialog as AdapterPickerDialog<ItemType>)
    protected open val adapter: EasyRecyclerAdapter<ItemType>
        get() = dialog.adapter
    var data: List<ItemType>
        get() = adapter.getItems()
        set(value) = adapter.setCollection(value)
    override var selection: IntArray?
        set(value) = dialog.setSelection(value)
        get() = pickerDialog.getSelection()
    val selectedItems: List<ItemType>
        get() = adapter.getItems(selection)
    override val viewText: String
        get() {
            return selection?.let {
                return@let adapter.getPositionStrings(it)
            } ?: ""
        }

    init {
        readAttributes(context, attrs)
    }

    @Override
    abstract override fun initializeDialog(): AdapterPickerDialog<ItemType>

    override fun getDialogBuilder(
        fragmentManager: FragmentManager,
        fragmentTag: String,
    ): BaseDialogBuilder<out BasePickerDialogFragment<IntArray>> {
        return AdapterPickerDialogBuilder(
            null,
            fragmentManager,
            fragmentTag,
            object : AdapterPickerDialogBuilder.AdapterPickerDialogInitializer<AdapterPickerDialog<ItemType>> {
                override fun initialize(): AdapterPickerDialog<ItemType> {
                    return initializeDialog()
                }
            })
    }

    @Suppress("UNCHECKED_CAST")
    @Override
    override fun onDialogInitialized(dialog: BasePickerDialogFragment<IntArray>) {
        super.onDialogInitialized(dialog)
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