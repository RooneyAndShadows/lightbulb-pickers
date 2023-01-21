package com.github.rooneyandshadows.lightbulb.pickers.inline

import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogFragment.show
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogFragment.isDialogShown
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerAdapter.getDrawable
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.selectedItems
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.getPositionStrings
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerDialogBuilder.withSelection
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerDialogBuilder.withDialogType
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerDialogBuilder.withAnimations
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.saveAdapterState
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.restoreAdapterState
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.selectPositions
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.positionExists
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment.setSelection
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.getPosition
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_icon.IconPickerAdapter.setCollection
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.hasSelection
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.getItems
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_time.TimePickerDialogBuilder.withInitialTime
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_time.TimePickerDialogBuilder.buildDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerDialogBuilder.withSelection
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerDialogBuilder.withDialogType
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerDialogBuilder.withAnimations
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter.setCollection
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.getItem
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter.getColorDrawable
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter.ColorModel.colorHex
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withMinYear
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withMaxYear
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withDisabledMonths
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withEnabledMonths
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withPositiveButton
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withNegativeButton
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withOnCancelListener
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withOnDateSelectedEvent
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withAnimations
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.withSelection
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialogBuilder.buildDialog
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialog.setSelection
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialog.setCalendarBounds
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialog.setDisabledMonths
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_month.MonthPickerDialog.setEnabledMonths
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialogBuilder.withSelection
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialogBuilder.withDialogType
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_adapter.AdapterPickerDialogBuilder.withAnimations
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.setCollection
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_datetime.DateTimePickerDialogBuilder.withSelection
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_datetime.DateTimePickerDialog.setSelection
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialog.setSelection
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialogBuilder.withSelection
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.addOnSelectionChangedListener
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.selectedPositionsAsArray
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel.itemName
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.selectItem
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.addItem
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter.isItemSelected
import android.widget.LinearLayout
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.DialogPickerTriggerLayout
import android.graphics.drawable.Drawable
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView.TriggerAttachedCallback
import kotlin.jvm.JvmOverloads
import androidx.fragment.app.FragmentActivity
import android.view.ViewGroup
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import android.content.res.TypedArray
import com.github.rooneyandshadows.lightbulb.pickers.R
import android.util.SparseArray
import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.InputTriggerView.InputTypes
import android.content.res.ColorStateList
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.InputTriggerView
import com.google.android.material.button.MaterialButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.AppCompatImageButton
import android.os.Bundle
import com.github.rooneyandshadows.lightbulb.pickers.dialog.DialogIconPickerView
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingListener
import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate
import com.github.rooneyandshadows.lightbulb.pickers.dialog.DialogTimePickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.DialogColorPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.DialogMonthPickerView
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import com.github.rooneyandshadows.lightbulb.pickers.dialog.DialogAdapterPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.DialogDateTimePickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.DialogDateRangePickerView
import androidx.appcompat.widget.LinearLayoutCompat
import com.github.rooneyandshadows.lightbulb.textinputview.TextInputView
import com.github.rooneyandshadows.lightbulb.pickers.inline.SelectableFilterOptionAdapter
import com.github.rooneyandshadows.lightbulb.pickers.inline.ChipsPickerView.AdapterOptionCreator
import com.github.rooneyandshadows.lightbulb.pickers.inline.ChipsPickerView.OnHideListener
import com.github.rooneyandshadows.lightbulb.pickers.inline.ChipsPickerView.OnOptionCreatedListener
import com.github.rooneyandshadows.lightbulb.textinputview.TextInputView.TextChangedCallback
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import com.github.rooneyandshadows.lightbulb.pickers.inline.ChipsPickerView
import android.view.View.OnFocusChangeListener
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Filterable
import android.widget.RelativeLayout
import com.github.rooneyandshadows.lightbulb.pickers.inline.SelectableFilterOptionAdapter.ChipVH
import android.widget.Filter.FilterResults
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.java.commons.string.StringUtils
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.callbacks.EasyAdapterSelectionChangedListener
import com.nex3z.flowlayout.FlowLayout
import java.util.*

class ChipsPickerView<ModelType : EasyAdapterDataModel?> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayoutCompat(context, attrs) {
    private val CHIPS_GROUP_TAG = ResourceUtils.getPhrase(getContext(), R.string.CP_GroupTag)
    private val CHIPS_FILTER_CONTAINER_TAG = ResourceUtils.getPhrase(getContext(), R.string.CP_FilterTag)
    private val CHIPS_FILTER_INPUT_TAG = ResourceUtils.getPhrase(getContext(), R.string.CP_InputEditTextTag)
    private val CHIPS_OPTIONS_RECYCLER_TAG = ResourceUtils.getPhrase(getContext(), R.string.CP_OptionsRecyclerTag)
    private val CHIPS_CHIP_ITEM_TEXT_TAG = ResourceUtils.getPhrase(getContext(), R.string.CP_ChipItemTextTag)
    private val CHIPS_CHIP_ITEM_REMOVE_TAG = ResourceUtils.getPhrase(getContext(), R.string.CP_ChipItemRemoveTag)

    //private PopupWindow popupWindow;
    private var rootLayout: LinearLayoutCompat? = null
    private var recyclerView: RecyclerView? = null
    private var chipGroupInput: TextInputView? = null
    private var chipGroup: FlowLayout? = null
    var pickerHintText: String? = null
        private set
    private var errorText: String? = null
    private var pickerRequiredText: String? = null
    private var pickerIcon: Drawable? = null
    private var required = false
    private var allowToAddNewOptions = false
    private var closeOnLostFocus = false
    private var defaultIconColor = 0
    private var backgroundColor = 0
    private var backgroundCornerRadius = 0
    private var chipsGroupPadding = 0
    private var selection: IntArray?
    var recyclerAdapter: SelectableFilterOptionAdapter<ModelType?>? = null
        private set
    private var optionCreator: AdapterOptionCreator<ModelType>? = null
    private var internalOnShowListener: OnShowListener? = null
    private val onShowListeners = ArrayList<OnShowListener>()
    private val validationCallbacks = ArrayList<ValidationCheck<ModelType?>>()
    private val selectionChangedListeners = ArrayList<SelectionChangedListener>()
    private val onHideListeners = ArrayList<OnHideListener>()
    private val onOptionCreatedListeners = ArrayList<OnOptionCreatedListener<ModelType>>()
    private val textWatcher = TextChangedCallback { newValue: String, oldValue: String? ->
        handleAddOptionVisibility()
        filterOptions(newValue)
    }

    init {
        isSaveEnabled = true
        readAttributes(context, attrs)
        initLayout()
    }

    fun addOnShowListener(onShowListener: OnShowListener) {
        onShowListeners.add(onShowListener)
    }

    fun addOnHideListener(onHideListener: OnHideListener) {
        onHideListeners.add(onHideListener)
    }

    fun addOnOptionCreatedListener(onOptionCreatedListener: OnOptionCreatedListener<ModelType>) {
        onOptionCreatedListeners.add(onOptionCreatedListener)
    }

    fun addSelectionChangedListener(changedCallback: SelectionChangedListener) {
        selectionChangedListeners.add(changedCallback)
    }

    fun addValidationCheck(validationCallback: ValidationCheck<ModelType?>) {
        validationCallbacks.add(validationCallback)
    }

    fun selectItems(positions: IntArray) {
        selectInternally(positions, true)
    }

    fun selectItemAt(selection: Int) {
        val newSelection = intArrayOf(selection)
        selectInternally(newSelection, true)
    }

    fun validate(): Boolean {
        var isValid = true
        if (required && !hasSelection()) {
            setErrorText(pickerRequiredText)
            return false
        }
        for (validationCallback in validationCallbacks) isValid = isValid and validationCallback.validate(
            selectedItems)
        if (isValid) setErrorText(null) else {
            setErrorText(errorText)
        }
        return isValid
    }

    fun enableOptionCreation(optionCreator: AdapterOptionCreator<ModelType>?) {
        this.optionCreator = optionCreator
    }

    fun addOption(option: ModelType) {
        if (recyclerAdapter == null) return
        recyclerAdapter!!.addItem(option)
    }

    fun setSelection(selection: IntArray) {
        selectInternally(selection, true)
    }

    fun setAllowToAddNewOptions(allowToAddNewOptions: Boolean) {
        this.allowToAddNewOptions = allowToAddNewOptions
        setupAddButton()
    }

    fun setRequired(required: Boolean) {
        this.required = required
        if (required) validate()
    }

    fun setPickerIcon(icon: Drawable?, color: Int?) {
        pickerIcon = icon
        if (color != null) pickerIcon!!.setTint(color)
        chipGroupInput!!.setStartIcon(pickerIcon)
    }

    fun setPickerIcon(icon: Drawable?) {
        setPickerIcon(icon, defaultIconColor)
    }

    fun setErrorText(error: String?) {
        errorText = error
        chipGroupInput!!.error = errorText
    }

    fun setHintText(hintText: String?) {
        pickerHintText = hintText
        chipGroupInput!!.setHintText(pickerHintText)
    }

    fun setRecyclerAdapter(adapter: SelectableFilterOptionAdapter<ModelType>?) {
        recyclerAdapter = adapter
        recyclerAdapter!!.addOnSelectionChangedListener(EasyAdapterSelectionChangedListener { newSelection: IntArray ->
            selectInternally(newSelection, false)
            chipGroupInput!!.text = ""
            hidePicker()
        })
        recyclerView = findViewWithTag(CHIPS_OPTIONS_RECYCLER_TAG)
        recyclerView.setPadding(chipsGroupPadding, chipsGroupPadding, chipsGroupPadding, chipsGroupPadding)
        recyclerView.setItemAnimator(null)
        recyclerView.setLayoutManager(LinearLayoutManager(context))
        recyclerView.setAdapter(recyclerAdapter)
        hidePicker()
    }

    fun hasSelection(): Boolean {
        return if (recyclerAdapter == null) false else recyclerAdapter!!.hasSelection()
    }

    val selectedItems: List<ModelType?>
        get() = if (recyclerAdapter == null) ArrayList() else recyclerAdapter!!.getItems(selection)
    val selectedPositions: IntArray
        get() = if (recyclerAdapter == null) intArrayOf() else recyclerAdapter!!.selectedPositionsAsArray
    var options: List<ModelType?>?
        get() = if (recyclerAdapter == null) ArrayList() else recyclerAdapter!!.getItems()
        set(option) {
            if (recyclerAdapter == null) return
            recyclerAdapter!!.setCollection(option!!)
        }

    fun attachToScrollingParent(parent: ViewGroup) {
        if (parent is ScrollView) {
            internalOnShowListener = OnShowListener { parent.post { parent.smoothScrollTo(0, bottom) } }
            return
        }
        if (parent is NestedScrollView) {
            internalOnShowListener = OnShowListener { parent.post { parent.smoothScrollTo(0, bottom) } }
        }
        Log.w(ChipsPickerView::class.java.name, "Scrolling parent type must be one of ScrollView|NestedScrollView")
    }

    fun getErrorText(): String? {
        return errorText
    }

    fun selectItem(item: ModelType?) {
        if (item == null || recyclerAdapter == null) return
        val position = recyclerAdapter!!.getPosition(item)
        if (position != -1) selectItemAt(position)
    }

    /*private void showPopupWindow() {
        RecyclerView rc = popupWindow.getContentView().findViewWithTag("pickerRecycler");
        popupWindow.getContentView().setBackgroundColor(ResourceUtils.getColorByAttribute(getContext(), android.R.attr.colorBackground));
        popupWindow.setFocusable(false);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setOutsideTouchable(true);
        rc.setItemAnimator(null);
        rc.setLayoutManager(new LinearLayoutManager(getContext()));
        rc.setAdapter(recyclerAdapter);
        popupWindow.showAsDropDown(chipGroupInput, 0, 0);
    }*/
    fun showPicker() {
        if (isPickerShown) return
        if (!chipGroupInput!!.hasFocus()) chipGroupInput!!.requestFocus()
        recyclerView!!.visibility = VISIBLE
        if (internalOnShowListener != null) internalOnShowListener!!.execute()
        for (onShowListener in onShowListeners) onShowListener.execute()
    }

    fun hidePicker() {
        if (!isPickerShown) return
        if (chipGroupInput!!.hasFocus()) chipGroupInput!!.clearFocus()
        recyclerView!!.visibility = GONE
        for (onHideListener in onHideListeners) onHideListener.execute()
    }

    protected val isPickerShown: Boolean
        protected get() = recyclerView != null && recyclerView!!.visibility == VISIBLE

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ChipsPickerView, 0, 0)
        try {
            pickerHintText = a.getString(R.styleable.ChipsPickerView_CPV_HintText)
            pickerRequiredText = a.getString(R.styleable.ChipsPickerView_CPV_RequiredText)
            pickerHintText = StringUtils.getOrDefault(pickerHintText, "...")
            pickerRequiredText = StringUtils.getOrDefault(pickerRequiredText, "Field is required")
            backgroundColor = a.getColor(R.styleable.ChipsPickerView_CPV_BackgroundColor,
                ColorUtils.setAlphaComponent(ResourceUtils.getColorByAttribute(getContext(), R.attr.colorOnSurface), 30))
            backgroundCornerRadius = a.getDimensionPixelSize(R.styleable.ChipsPickerView_CPV_BackgroundCornerRadius,
                ResourceUtils.getDimenPxById(getContext(),
                    com.google.android.material.R.dimen.mtrl_textinput_box_corner_radius_medium))
            backgroundCornerRadius = a.getDimensionPixelSize(R.styleable.ChipsPickerView_CPV_BackgroundCornerRadius,
                ResourceUtils.getDimenPxById(getContext(),
                    com.google.android.material.R.dimen.mtrl_textinput_box_corner_radius_medium))
            chipsGroupPadding = a.getDimensionPixelSize(R.styleable.ChipsPickerView_CPV_ChipGroupPadding,
                ResourceUtils.getDimenPxById(getContext(), R.dimen.chips_picker_chip_group_padding))
            required = a.getBoolean(R.styleable.ChipsPickerView_CPV_Required, false)
            closeOnLostFocus = a.getBoolean(R.styleable.ChipsPickerView_CPV_CloseOnLostFocus, true)
            allowToAddNewOptions = a.getBoolean(R.styleable.ChipsPickerView_CPV_AllowToAddNewOptions, true)
            defaultIconColor =
                ColorUtils.setAlphaComponent(ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface), 140)
        } finally {
            a.recycle()
        }
    }

    private fun initLayout() {
        orientation = VERTICAL
        renderLayout()
        setupViews()
    }

    private fun renderLayout() {
        rootLayout = inflate(context, R.layout.chips_picker_layout, this) as LinearLayoutCompat
        chipGroup = findViewWithTag(CHIPS_GROUP_TAG)
        chipGroupInput = findViewWithTag(CHIPS_FILTER_INPUT_TAG)
    }

    private fun setupViews() {
        setupBackground(backgroundColor)
        setHintText(pickerHintText)
        setErrorText(errorText)
        setupInput()
        setupChips()
        setupAddButton()
    }

    private fun setupInput() {
        chipGroupInput!!.removeTextChangedCallback(textWatcher)
        chipGroupInput!!.addTextChangedCallback(textWatcher)
        chipGroupInput!!.onFocusChangeListener =
            OnFocusChangeListener { v: View?, hasFocus: Boolean -> if (hasFocus && !isPickerShown) showPicker() else if (closeOnLostFocus && !hasFocus && isPickerShown) hidePicker() }
    }

    private fun setupAddButton() {
        val icon = ResourceUtils.getDrawable(context, R.drawable.chip_picker_add_icon)
        icon!!.setTint(ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface))
        chipGroupInput!!.setEndIcon(icon) { v: View? ->
            if (!allowToAddNewOptions || recyclerAdapter == null || optionCreator == null) return@setEndIcon
            val newOptionName = chipGroupInput!!.text
            val newOption = optionCreator!!.createOption(newOptionName)
            recyclerAdapter!!.addItem(newOption)
            for (optionCreatedListener in onOptionCreatedListeners) optionCreatedListener.execute(newOption)
        }
        handleAddOptionVisibility()
    }

    private fun setupBackground(newColor: Int) {
        backgroundColor = newColor
        val backgroundDrawable: Drawable =
            getLayeredRoundedCornersDrawable.getLayeredRoundedCornersDrawable(ResourceUtils.getColorByAttribute(
                context, R.attr.colorSurface), backgroundColor, backgroundCornerRadius)
        background = backgroundDrawable
    }

    private fun handleAddOptionVisibility() {
        if (!allowToAddNewOptions || recyclerAdapter == null || optionCreator == null) {
            chipGroupInput!!.setEndIconVisible(false)
            return
        }
        val text = chipGroupInput!!.text
        chipGroupInput!!.setEndIconVisible(!StringUtils.isNullOrEmptyString(text) && !recyclerAdapter!!.hasItemWithName(text))
    }

    private fun filterOptions(queryText: String) {
        if (recyclerAdapter != null) recyclerAdapter.getFilter().filter(queryText)
    }

    private fun setupChips() {
        clearChips()
        buildChips()
    }

    private fun clearChips() {
        val childCount = chipGroup!!.childCount
        var child = chipGroup!!.getChildAt(0)
        while (child != null) {
            val tag = child.tag
            if (tag != null && tag == CHIPS_FILTER_CONTAINER_TAG) break
            chipGroup!!.removeView(child)
            child = chipGroup!!.getChildAt(0)
        }
    }

    private fun buildChips() {
        if (recyclerAdapter == null || recyclerAdapter!!.selectedItems.size <= 0) {
            chipGroup!!.setPadding(0, 0, 0, 0)
            return
        }
        chipGroup!!.setPadding(chipsGroupPadding,
            chipsGroupPadding,
            chipsGroupPadding,
            ResourceUtils.getDimenPxById(context, R.dimen.chips_picker_spacing_size))
        val selectedItems = recyclerAdapter!!.selectedItems
        for (position in selectedItems.indices) buildChip(selectedItems[position])
    }

    private fun buildChip(targetItem: ModelType?) {
        val itemName = targetItem!!.itemName
        val chipLayout = LayoutInflater.from(context).inflate(R.layout.chips_picker_chip, this, false) as LinearLayoutCompat
        val chipTextView = chipLayout.findViewWithTag<TextView>(CHIPS_CHIP_ITEM_TEXT_TAG)
        val chipRemoveButton = chipLayout.findViewWithTag<AppCompatImageButton>(CHIPS_CHIP_ITEM_REMOVE_TAG)
        val chipBackground: Drawable = getRoundedCornersDrawable.getRoundedCornersDrawable(ResourceUtils.getColorByAttribute(
            context, R.attr.colorOnSurface), ResourceUtils.getDimenPxById(context, R.dimen.chips_picker_chip_height))
        val chipRemoveIcon = ResourceUtils.getDrawable(context, R.drawable.chip_picker_remove_icon)
        chipRemoveIcon!!.setTint(ResourceUtils.getColorByAttribute(context, R.attr.colorPrimary))
        chipLayout.background = ResourceUtils.getDrawable(context, R.drawable.bg_chip_picker_item)
        chipRemoveButton.setImageDrawable(chipRemoveIcon)
        chipRemoveButton.setOnClickListener { v: View? -> recyclerAdapter!!.selectItem(targetItem, false) }
        chipRemoveButton.setBackgroundDrawable(ResourceUtils.getDrawable(context, R.drawable.bg_chip_picker_remove_icon))
        chipTextView.text = itemName
        chipTextView.setTextColor(ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface))
        chipTextView.compoundDrawablePadding = ResourceUtils.getDimenPxById(context, R.dimen.chips_picker_spacing_size)
        val childrenCount = chipGroup!!.childCount
        val index: Int
        index = if (childrenCount > 1) childrenCount - 1 else 0
        chipGroup!!.addView(chipLayout, index)
    }

    private fun selectInternally(newSelection: IntArray, selectInAdapter: Boolean) {
        val oldSelection = selection
        ensureAndApplySelection(newSelection)
        if (recyclerAdapter != null && selectInAdapter) recyclerAdapter!!.selectPositions(newSelection, true, false)
        setupChips()
        validate()
        dispatchSelectionChangedEvents(oldSelection, selection)
    }

    private fun ensureAndApplySelection(newSelection: IntArray) {
        val positionsToSelect: MutableList<Int> = ArrayList()
        for (positionToSelect in newSelection) {
            if (!recyclerAdapter!!.positionExists(positionToSelect)) continue
            positionsToSelect.add(positionToSelect)
        }
        selection = IntArray(positionsToSelect.size)
        if (positionsToSelect.size <= 0) return
        for (i in positionsToSelect.indices) {
            selection!![i] = positionsToSelect[i]
        }
    }

    private fun compareValues(v1: IntArray?, v2: IntArray?): Boolean {
        return Arrays.equals(v1, v2)
    }

    private fun dispatchSelectionChangedEvents(oldValue: IntArray?, newValue: IntArray?) {
        if (compareValues(newValue, oldValue)) return
        for (selectionChangedListener in selectionChangedListeners) selectionChangedListener.execute(oldValue, newValue)
    }

    /*private void buildPopup() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View popupView = inflater.inflate(R.layout.popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        // popupWindow.showAtLocation(this, Gravity.CENTER, 0, 0);


        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //popupWindow.dismiss();
                return true;
            }
        });
    }*/
    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.pickerIsShowing = isPickerShown
        myState.selection = selection
        myState.pickerHintText = pickerHintText
        myState.pickerErrorText = errorText
        myState.pickerRequiredText = pickerRequiredText
        myState.pickerIsRequired = required
        myState.pickerCloseOnLostFocus = closeOnLostFocus
        myState.pickerBackgroundColor = backgroundColor
        myState.pickerDefaultIconColor = defaultIconColor
        myState.pickerChipGroupPadding = chipsGroupPadding
        myState.pickerBackgroundCornerRadius = backgroundCornerRadius
        myState.pickerAllowToAddNewOptions = allowToAddNewOptions
        myState.pickerInputState = chipGroupInput!!.onSaveInstanceState()
        return myState
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        val isPickerShowing = savedState.pickerIsShowing
        selection = savedState.selection
        pickerHintText = savedState.pickerHintText
        errorText = savedState.pickerErrorText
        pickerRequiredText = savedState.pickerRequiredText
        required = savedState.pickerIsRequired
        closeOnLostFocus = savedState.pickerCloseOnLostFocus
        backgroundColor = savedState.pickerBackgroundColor
        chipsGroupPadding = savedState.pickerChipGroupPadding
        backgroundCornerRadius = savedState.pickerBackgroundCornerRadius
        defaultIconColor = savedState.pickerDefaultIconColor
        allowToAddNewOptions = savedState.pickerAllowToAddNewOptions
        chipGroupInput!!.onRestoreInstanceState(savedState.pickerInputState)
        setupViews()
        if (isPickerShowing) showPicker()
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    private class SavedState : BaseSavedState {
        var selection: IntArray?
        var pickerHintText: String? = null
        var pickerErrorText: String? = null
        var pickerRequiredText: String? = null
        var pickerIsShowing = false
        var pickerIsRequired = false
        private var pickerIsErrorEnabled = false
        private var pickerShowSelectedTextValue = false
        var pickerCloseOnLostFocus = false
        var pickerAllowToAddNewOptions = false
        var pickerBackgroundColor = 0
        var pickerDefaultIconColor = 0
        var pickerChipGroupPadding = 0
        var pickerBackgroundCornerRadius = 0
        var pickerInputState: Parcelable? = null

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            selection = `in`.createIntArray()
            pickerHintText = `in`.readString()
            pickerErrorText = `in`.readString()
            pickerRequiredText = `in`.readString()
            pickerIsShowing = `in`.readByte().toInt() != 0
            pickerIsRequired = `in`.readByte().toInt() != 0
            pickerIsErrorEnabled = `in`.readByte().toInt() != 0
            pickerShowSelectedTextValue = `in`.readByte().toInt() != 0
            pickerCloseOnLostFocus = `in`.readByte().toInt() != 0
            pickerAllowToAddNewOptions = `in`.readByte().toInt() != 0
            pickerBackgroundColor = `in`.readInt()
            pickerDefaultIconColor = `in`.readInt()
            pickerChipGroupPadding = `in`.readInt()
            pickerBackgroundCornerRadius = `in`.readInt()
            pickerInputState = `in`.readParcelable(javaClass.classLoader)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            if (selection != null) out.writeIntArray(selection)
            out.writeString(pickerHintText)
            out.writeString(pickerErrorText)
            out.writeString(pickerRequiredText)
            out.writeByte((if (pickerIsShowing) 1 else 0).toByte())
            out.writeByte((if (pickerIsRequired) 1 else 0).toByte())
            out.writeByte((if (pickerIsErrorEnabled) 1 else 0).toByte())
            out.writeByte((if (pickerShowSelectedTextValue) 1 else 0).toByte())
            out.writeByte((if (pickerCloseOnLostFocus) 1 else 0).toByte())
            out.writeByte((if (pickerAllowToAddNewOptions) 1 else 0).toByte())
            out.writeInt(pickerBackgroundColor)
            out.writeInt(pickerDefaultIconColor)
            out.writeInt(pickerChipGroupPadding)
            out.writeInt(pickerBackgroundCornerRadius)
            out.writeParcelable(pickerInputState, flags)
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

    interface SelectionChangedListener {
        fun execute(oldSelection: IntArray?, newSelection: IntArray?)
    }

    interface ValidationCheck<ModelType : EasyAdapterDataModel?> {
        fun validate(selectedItems: List<ModelType>?): Boolean
    }

    interface AdapterOptionCreator<ModelType> {
        fun createOption(newOptionName: String?): ModelType
    }

    interface OnShowListener {
        fun execute()
    }

    interface OnHideListener {
        fun execute()
    }

    interface OnOptionCreatedListener<ModelType> {
        fun execute(option: ModelType)
    }
}