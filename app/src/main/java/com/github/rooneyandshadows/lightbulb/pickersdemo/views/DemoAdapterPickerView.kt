package com.github.rooneyandshadows.lightbulb.pickersdemo.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.*
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.adapter.DialogPickerRadioButtonAdapter
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.DialogAdapterPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.InputTriggerView
import com.github.rooneyandshadows.lightbulb.pickersdemo.R
import com.github.rooneyandshadows.lightbulb.pickersdemo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIconsUi
import com.github.rooneyandshadows.lightbulb.pickersdemo.views.dialogs.DemoSingleSelectionDialog
import java.util.*
import java.util.stream.Collectors

@Suppress("unused")
class DemoAdapterPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : DialogAdapterPickerView<DemoModel>(context, attrs, defStyleAttr) {
    override val adapter: DialogPickerRadioButtonAdapter<DemoModel>
        get() = super.adapter as DialogPickerRadioButtonAdapter<DemoModel>

    init {
        readAttributes(context, attrs)
        addOnTriggerAttachedListener { _, _ -> setupIcon() }
        addSelectionChangedListener { _, _ -> setupIcon() }
        whenDialogReady {
            val adapterDialog = it as DemoSingleSelectionDialog
            val newDecor = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            adapterDialog.setItemDecoration(newDecor)
        }
    }

    @Override
    override fun initializeDialog(): AdapterPickerDialog<DemoModel> {
        return DemoSingleSelectionDialog()
    }

    @Override
    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        setupIcon()
    }

    private fun setupIcon() {
        val selectedPositions = adapter.collection.getPositions(selectedItems)
        if (selectedPositions.isEmpty()) {
            val icon: Drawable = AppIconUtils.getIconWithAttributeColor(
                context,
                DemoIconsUi.ICON_ADAPTER_PICKER_INDICATOR,
                R.attr.colorOnSurface,
                R.dimen.ICON_SIZE_MEDIUM
            )
            if (triggerView is InputTriggerView) (triggerView as InputTriggerView).setStartIconUseAlpha(true)
            setPickerIcon(icon)
            return
        }
        val selectedItem = adapter.collection.getItem(selectedPositions[0])!!
        val iconType = selectedItem.icon
        val icon: Drawable = AppIconUtils.getIconWithAttributeColor(
            context,
            iconType,
            R.attr.colorOnSurface,
            R.dimen.ICON_SIZE_RECYCLER_ITEM
        )
        val color = selectedItem.iconBackgroundColor.color
        if (triggerView is InputTriggerView) (triggerView as InputTriggerView).setStartIconUseAlpha(false)
        setPickerIcon(icon, color)
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DemoAdapterPickerView, 0, 0)
        try {
        } finally {
            a.recycle()
        }
    }

    object Databinding {
        @JvmStatic
        @InverseBindingAdapter(attribute = "pickerSelection", event = "pickerSelectionChanged")
        fun getSingleSelection(view: DemoAdapterPickerView): UUID? {
            return if (view.hasSelection) view.selectedItems[0].id else null
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "pickerSelection", event = "pickerSelectionChanged")
        fun getMultipleSelection(view: DemoAdapterPickerView): List<UUID> {
            return if (!view.hasSelection) listOf() else view.selectedItems
                .stream()
                .map { obj: DemoModel -> obj.id }
                .collect(Collectors.toList())
        }

        @JvmStatic
        @BindingAdapter(value = ["pickerSelection"])
        fun setSingleSelection(view: DemoAdapterPickerView, newSelection: UUID?) {
            if (newSelection == null) return
            if (view.hasSelection) {
                val currentSelection = view.selectedItems[0]
                if (currentSelection.id == newSelection) return
            }
            for (selectableTransactionTypeModel in view.data) if (newSelection == selectableTransactionTypeModel.id) {
                view.setSelection(selectableTransactionTypeModel)
                break
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["pickerSelection"])
        fun setMultipleSelection(view: DemoAdapterPickerView, newSelection: List<UUID>?) {
            if (newSelection.isNullOrEmpty()) {
                if (view.hasSelection) view.selection = null
                return
            }
            if (view.hasSelection) {
                val currentSelection = view.selectedItems.map { return@map it.id }
                if (currentSelection.size == newSelection.size && currentSelection.containsAll(newSelection)) return
            }

            val positionsToSelect: MutableList<Int> = mutableListOf()
            view.data.map { return@map it.id }.forEachIndexed { index, uuid ->
                if (newSelection.contains(uuid)) {
                    positionsToSelect.add(index)
                }
            }
            val selection = positionsToSelect.toIntArray()
            if (selection.isEmpty() && !view.hasSelection) return
            view.selection = selection
        }

        @JvmStatic
        @BindingAdapter(value = ["pickerSelectionChanged"], requireAll = false)
        fun bindPickerEvent(view: DemoAdapterPickerView, bindingListener: InverseBindingListener) {
            if (view.hasSelection) bindingListener.onChange()
            view.dataBindingListener = SelectionChangedListener { _, _ ->
                bindingListener.onChange()
            }
        }
    }
}