package com.github.rooneyandshadows.lightbulb.pickers.inline

import android.graphics.drawable.Drawable
import kotlin.jvm.JvmOverloads
import android.view.ViewGroup
import android.util.SparseArray
import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import androidx.appcompat.widget.AppCompatImageButton
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.LinearLayoutCompat
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import android.view.View.OnFocusChangeListener
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.commons.utils.DrawableUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.callbacks.EasyAdapterSelectionChangedListener
import com.github.rooneyandshadows.lightbulb.textinputview.TextInputView
import com.github.rooneyandshadows.lightbulb.textinputview.TextInputView.TextChangedCallback
import com.nex3z.flowlayout.FlowLayout
import java.util.*

class ChipsPickerView<ModelType : EasyAdapterDataModel> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayoutCompat(context, attrs, defStyleAttr, defStyleRes) {
    //private PopupWindow popupWindow;
    private var recyclerView: RecyclerView? = null
    private var filterInput: TextInputView? = null
    private var flowLayout: FlowLayout? = null
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
    var recyclerAdapter: SelectableFilterOptionAdapter<ModelType>? = null
        private set
    private var optionCreator: AdapterOptionCreator<ModelType>? = null
    private var internalOnShowListener: OnShowListener? = null
    private val onShowListeners = ArrayList<OnShowListener>()
    private val validationCallbacks = ArrayList<ValidationCheck<ModelType?>>()
    private val selectionChangedListeners = ArrayList<SelectionChangedListener>()
    private val onHideListeners = ArrayList<OnHideListener>()
    private val onOptionCreatedListeners = ArrayList<OnOptionCreatedListener<ModelType>>()
    private val textWatcher = TextChangedCallback { newValue: String, _: String? ->
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
            selectedItems
        )
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
        filterInput!!.setStartIcon(pickerIcon)
    }

    fun setPickerIcon(icon: Drawable?) {
        setPickerIcon(icon, defaultIconColor)
    }

    fun setErrorText(error: String?) {
        errorText = error
        filterInput!!.error = errorText
    }

    fun setHintText(hintText: String?) {
        pickerHintText = hintText
        filterInput!!.setHintText(pickerHintText)
    }

    fun setRecyclerAdapter(adapter: SelectableFilterOptionAdapter<ModelType>?) {
        recyclerAdapter = adapter
        recyclerAdapter!!.addOnSelectionChangedListener(EasyAdapterSelectionChangedListener { newSelection: IntArray ->
            selectInternally(newSelection, false)
            filterInput!!.text = ""
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
        if (!filterInput!!.hasFocus()) filterInput!!.requestFocus()
        recyclerView!!.visibility = VISIBLE
        if (internalOnShowListener != null) internalOnShowListener!!.execute()
        for (onShowListener in onShowListeners) onShowListener.execute()
    }

    fun hidePicker() {
        if (!isPickerShown) return
        if (filterInput!!.hasFocus()) filterInput!!.clearFocus()
        recyclerView!!.visibility = GONE
        for (onHideListener in onHideListeners) onHideListener.execute()
    }

    protected val isPickerShown: Boolean
        protected get() = recyclerView != null && recyclerView!!.visibility == VISIBLE

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.ChipsPickerView, 0, 0)
        try {
            pickerHintText = a.getString(R.styleable.ChipsPickerView_cpv_hint_text)
            pickerRequiredText = a.getString(R.styleable.ChipsPickerView_cpv_required_text)
            pickerHintText = StringUtils.getOrDefault(pickerHintText, "...")
            pickerRequiredText = StringUtils.getOrDefault(pickerRequiredText, "Field is required")
            backgroundColor = a.getColor(
                R.styleable.ChipsPickerView_cpv_background_color,
                ColorUtils.setAlphaComponent(ResourceUtils.getColorByAttribute(getContext(), R.attr.colorOnSurface), 30)
            )
            backgroundCornerRadius = a.getDimensionPixelSize(
                R.styleable.ChipsPickerView_cpv_background_corner_radius,
                ResourceUtils.getDimenPxById(
                    getContext(),
                    com.google.android.material.R.dimen.mtrl_textinput_box_corner_radius_medium
                )
            )
            chipsGroupPadding = a.getDimensionPixelSize(
                R.styleable.ChipsPickerView_cpv_chip_group_padding,
                ResourceUtils.getDimenPxById(getContext(), R.dimen.chips_picker_chip_group_padding)
            )
            required = a.getBoolean(R.styleable.ChipsPickerView_cpv_required, false)
            closeOnLostFocus = a.getBoolean(R.styleable.ChipsPickerView_cpv_close_on_lost_focus, true)
            allowToAddNewOptions = a.getBoolean(R.styleable.ChipsPickerView_cpv_allow_to_add_new_options, true)
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
        inflate(context, R.layout.chips_picker_layout, this) as LinearLayoutCompat
        flowLayout = findViewWithTag(R.id.picker_flow_layout)
        filterInput = findViewWithTag(R.id.picker_filter_input_view)
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
        filterInput!!.removeTextChangedCallback(textWatcher)
        filterInput!!.addTextChangedCallback(textWatcher)
        filterInput!!.onFocusChangeListener =
            OnFocusChangeListener { v: View?, hasFocus: Boolean -> if (hasFocus && !isPickerShown) showPicker() else if (closeOnLostFocus && !hasFocus && isPickerShown) hidePicker() }
    }

    private fun setupAddButton() {
        val icon = ResourceUtils.getDrawable(context, R.drawable.chip_picker_add_icon)
        icon!!.setTint(ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface))
        filterInput!!.setEndIcon(icon) {
            if (!allowToAddNewOptions || recyclerAdapter == null || optionCreator == null) return@setEndIcon
            val newOptionName = filterInput!!.text
            val newOption = optionCreator!!.createOption(newOptionName)
            recyclerAdapter!!.addItem(newOption)
            for (optionCreatedListener in onOptionCreatedListeners) optionCreatedListener.execute(newOption)
        }
        handleAddOptionVisibility()
    }

    private fun setupBackground(newColor: Int) {
        backgroundColor = newColor
        val backgroundDrawable: Drawable = DrawableUtils.getLayeredRoundedCornersDrawable(
            ResourceUtils.getColorByAttribute(context, R.attr.colorSurface),
            backgroundColor, backgroundCornerRadius
        )
        background = backgroundDrawable
    }

    private fun handleAddOptionVisibility() {
        if (!allowToAddNewOptions || recyclerAdapter == null || optionCreator == null) {
            filterInput!!.setEndIconVisible(false)
            return
        }
        val text = filterInput!!.text
        val showAddOption = !text.isNullOrBlank() && !recyclerAdapter!!.hasItemWithName(text)
        filterInput!!.setEndIconVisible(showAddOption)
    }

    private fun filterOptions(queryText: String) {
        if (recyclerAdapter != null) recyclerAdapter!!.getFilter().filter(queryText)
    }

    private fun setupChips() {
        clearChips()
        buildChips()
    }

    private fun clearChips() {
        flowLayout!!.removeAllViews()
    }

    private fun buildChips() {
        if (recyclerAdapter == null || recyclerAdapter!!.selectedItems.isEmpty()) {
            flowLayout!!.setPadding(0, 0, 0, 0)
            return
        }
        flowLayout!!.setPadding(
            chipsGroupPadding,
            chipsGroupPadding,
            chipsGroupPadding,
            ResourceUtils.getDimenPxById(context, R.dimen.chips_picker_spacing_size)
        )
        val selectedItems = recyclerAdapter!!.selectedItems
        for (position in selectedItems.indices) {
            val chipView = buildChip(selectedItems[position])
            flowLayout!!.addView(chipView)
        }
    }

    private fun buildChip(targetItem: ModelType): View {
        val itemName = targetItem.itemName
        val layoutInflater = LayoutInflater.from(context)
        val layoutId = R.layout.chips_picker_chip
        return layoutInflater.inflate(layoutId, null, false).apply {
            background = ResourceUtils.getDrawable(context, R.drawable.bg_chip_picker_item)
            findViewById<AppCompatImageButton>(R.id.picker_chip_item_remove_button).apply {
                val chipRemoveIcon = ResourceUtils.getDrawable(context, R.drawable.chip_picker_remove_icon).apply {
                    this!!.setTint(ResourceUtils.getColorByAttribute(context, R.attr.colorPrimary))
                }
                background = ResourceUtils.getDrawable(context, R.drawable.bg_chip_picker_remove_icon)
                setImageDrawable(chipRemoveIcon)
                setOnClickListener { recyclerAdapter!!.selectItem(targetItem, false) }
            }
            findViewById<TextView>(R.id.picker_chip_item_text_view).apply {
                text = itemName
                setTextColor(ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface))
                compoundDrawablePadding = ResourceUtils.getDimenPxById(context, R.dimen.chips_picker_spacing_size)
            }
        }
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
    public override fun onSaveInstanceState(): Parcelable {
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
        myState.pickerInputState = filterInput!!.onSaveInstanceState()
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
        filterInput!!.onRestoreInstanceState(savedState.pickerInputState)
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
        var selection: IntArray? = null
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
        private constructor(parcel: Parcel) : super(parcel) {
            selection = parcel.createIntArray()
            pickerHintText = parcel.readString()
            pickerErrorText = parcel.readString()
            pickerRequiredText = parcel.readString()
            pickerIsShowing = parcel.readByte().toInt() != 0
            pickerIsRequired = parcel.readByte().toInt() != 0
            pickerIsErrorEnabled = parcel.readByte().toInt() != 0
            pickerShowSelectedTextValue = parcel.readByte().toInt() != 0
            pickerCloseOnLostFocus = parcel.readByte().toInt() != 0
            pickerAllowToAddNewOptions = parcel.readByte().toInt() != 0
            pickerBackgroundColor = parcel.readInt()
            pickerDefaultIconColor = parcel.readInt()
            pickerChipGroupPadding = parcel.readInt()
            pickerBackgroundCornerRadius = parcel.readInt()
            pickerInputState = parcel.readParcelable(javaClass.classLoader)
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

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
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