package com.github.rooneyandshadows.lightbulb.pickersdemo.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.DividerItemDecoration.*
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.DialogAdapterPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.base.DialogPickerTriggerLayout
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.InputTriggerView
import com.github.rooneyandshadows.lightbulb.pickersdemo.models.DemoModel
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIconsUi
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogsdemo.dialogs.DemoSingleSelectionDialog
import com.github.rooneyandshadows.lightbulb.pickersdemo.R
import java.util.*
import java.util.stream.Collectors

class DemoAdapterPickerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : DialogAdapterPickerView<DemoModel>(context, attrs, defStyleAttr, defStyleRes) {

    init {
        addSelectionChangedListener(object : SelectionChangedListener<IntArray> {
            override fun execute(newSelection: IntArray?, oldSelection: IntArray?) {
                setupIcon()
            }
        })
        addOnTriggerAttachedListener(object : TriggerAttachedCallback<IntArray> {
            override fun onAttached(triggerView: DialogPickerTriggerLayout, pickerView: BaseDialogPickerView<IntArray>) {
                setupIcon()
            }
        })
        itemDecoration = DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL)
    }
    @Override
    override fun initializeDialog(fragmentManager: FragmentManager): BasePickerDialogFragment<IntArray> {
        return DemoSingleSelectionDialog()
    }
    @Override
    override fun readAttributes(context: Context, attrs: AttributeSet?) {
        super.readAttributes(context, attrs)
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.DemoAdapterPickerView, 0, 0)
        try {
        } finally {
            a.recycle()
        }
    }

    @Override
    override fun onRestoreInstanceState(state: Parcelable) {
        super.onRestoreInstanceState(state)
        setupIcon()
    }

    private fun setupIcon() {
        val selectedPositions = adapter.getPositions(selectedItems)
        if (selectedPositions.isEmpty()) {
            val icon: Drawable = AppIconUtils.getIconWithAttributeColor(
                context,
                DemoIconsUi.ICON_ADAPTER_PICKER_INDICATOR,
                R.attr.colorOnSurface,
                R.dimen.ICON_SIZE_MEDIUM
            )
            if (triggerView is InputTriggerView) (triggerView as InputTriggerView).startIconUseAlpha = true
            pickerIcon = icon
            return
        }
        val selectedItem = adapter.getItem(selectedPositions[0])!!
        val iconType = selectedItem.icon
        val icon: Drawable = AppIconUtils.getIconWithAttributeColor(
            context,
            iconType,
            R.attr.colorOnSurface,
            R.dimen.ICON_SIZE_RECYCLER_ITEM
        )
        val color = selectedItem.iconBackgroundColor.color
        if (triggerView is InputTriggerView) (triggerView as InputTriggerView).startIconUseAlpha = false
        setPickerIcon(icon, color)
    }

    companion object {
        @InverseBindingAdapter(attribute = "pickerSelection", event = "pickerSelectionChanged")
        fun getSingleSelection(view: DialogAdapterPickerView<DemoModel>): UUID? {
            return if (view.hasSelection) view.selectedItems[0].id else null
        }

        @InverseBindingAdapter(attribute = "pickerSelection", event = "pickerSelectionChanged")
        fun getMultipleSelection(view: DialogAdapterPickerView<DemoModel>): List<UUID?> {
            return if (!view.hasSelection) ArrayList() else view.selectedItems
                .stream()
                .map { obj: DemoModel -> obj.id }
                .collect(Collectors.toList())
        }

        @BindingAdapter(value = ["pickerSelection"])
        fun setSingleSelection(view: DialogAdapterPickerView<DemoModel>, newSelection: UUID?) {
            if (newSelection == null) return
            if (view.hasSelection) {
                val currentSelection = view.selectedItems[0]
                if (currentSelection.id == newSelection) return
            }
            for (selectableTransactionTypeModel in view.data) if (newSelection == selectableTransactionTypeModel.id) {
                view.selectItem(selectableTransactionTypeModel)
                break
            }
        }

        @BindingAdapter(value = ["pickerSelection"])
        fun setMultipleSelection(view: DialogAdapterPickerView<DemoModel>, newSelection: List<UUID?>?) {
            if (newSelection == null) return
            if (view.hasSelection) {
                val currentSelection = view.selectedItems
                    .stream()
                    .map { obj: DemoModel -> obj.id }
                    .collect(Collectors.toList())
                if (currentSelection.size == newSelection.size && currentSelection.containsAll(newSelection)) return
            }
            val positionsToSelect: MutableList<Int> = ArrayList()
            for (i in view.data.indices) {
                val model = view.data[i]
                if (newSelection.contains(model.id)) positionsToSelect.add(i)
            }
            val selection = IntArray(positionsToSelect.size)
            for (i in positionsToSelect.indices) selection[i] = positionsToSelect[i]
            view.selection = selection
        }

        @BindingAdapter(value = ["pickerSelectionChanged"], requireAll = false)
        fun bindPickerEvent(view: DialogAdapterPickerView<DemoModel>, bindingListener: InverseBindingListener) {
            if (view.hasSelection) bindingListener.onChange()
            view.
            view.addSelectionChangedListener(SelectionChangedListener<IntArray> { oldPositions: IntArray?, newPositions: IntArray? -> bindingListener.onChange() })
        }
    }
}