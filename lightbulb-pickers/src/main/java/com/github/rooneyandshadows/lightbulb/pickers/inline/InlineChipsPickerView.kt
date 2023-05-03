package com.github.rooneyandshadows.lightbulb.pickers.inline

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.setPadding
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rooneyandshadows.lightbulb.commons.utils.DrawableUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.data.EasyAdapterDataModel
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.collection.ExtendedCollection
import com.github.rooneyandshadows.lightbulb.textinputview.TextInputView
import com.github.rooneyandshadows.lightbulb.textinputview.TextInputView.TextChangedCallback
import com.nex3z.flowlayout.FlowLayout
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "unused", "ObjectLiteralToLambda")
abstract class InlineChipsPickerView<ModelType : EasyAdapterDataModel> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    //private PopupWindow popupWindow;
    lateinit var adapter: SelectableFilterOptionAdapter<ModelType>
        private set
    private var recyclerView: RecyclerView? = null
    private var filterInput: TextInputView? = null
    private var flowLayout: FlowLayout? = null
    private var pickerDefaultIconColor = 0
    private var pickerBackgroundColor = 0
    private var pickerCornerRadius = 0
    private var pickerGroupPadding = 0
    private var internalOnShowListener: OnShowListener? = null
    private val onShowListeners: MutableList<OnShowListener> = mutableListOf()
    private val validationCallbacks: MutableList<ValidationCheck<ModelType>> = mutableListOf()
    private val selectionChangedListeners: MutableList<SelectionChangedListener> = mutableListOf()
    private val onHideListeners: MutableList<OnHideListener> = mutableListOf()
    private val onOptionCreatedListeners: MutableList<OnOptionCreatedListener<ModelType>> = mutableListOf()
    private val textWatcher = TextChangedCallback { newValue: String, _: String? ->
        handleAddOptionVisibility()
        filterOptions(newValue)
    }
    var pickerRequiredText: String? = null
    var pickerErrorText: String? = null
        set(value) {
            field = value
            filterInput?.error = field
        }
    var pickerIcon: Drawable? = null
        set(value) {
            field = value
            field?.setTint(pickerDefaultIconColor)
            filterInput?.setStartIcon(field)
        }
    var isPickerRequired = false
    var pickerAllowOptionAddition: Boolean = false
        set(value) {
            field = value
            setupAddButton()
        }
    var pickerHintText: String? = null
        set(value) {
            field = value
            filterInput?.setHintText(field)
        }
    var selection: List<ModelType>
        set(value) {
            val positions = adapter.collection.getPositions(value)
            adapter.collection.selectPositions(
                positions = positions,
                newState = true,
                incremental = false
            )
        }
        get() = adapter.collection.selectedItems
    var selectedPositions: IntArray
        set(value) = adapter.collection.selectPositions(
            positions = value,
            newState = true,
            incremental = false
        )
        get() = adapter.collection.selectedPositionsAsArray
    var options: List<ModelType>
        get() = adapter.collection.getItems()
        set(value) = adapter.collection.set(value)
    val isPickerShowing: Boolean
        get() = recyclerView?.visibility == VISIBLE
    val hasSelection: Boolean
        get() = adapter.collection.hasSelection
    abstract val optionCreator: AdapterOptionCreator<ModelType>

    init {
        isSaveEnabled = true
        orientation = VERTICAL
        readAttributes(context, attrs)
        initializeAdapter()
        renderLayout()
        initializeViews()
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
    public override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.apply {
            hintText = pickerHintText
            errorText = pickerErrorText
            requiredText = pickerRequiredText
            isShowing = isPickerShowing
            isRequired = isPickerRequired
            allowToAddNewOptions = pickerAllowOptionAddition
            iconColor = pickerDefaultIconColor
            colorBackground = pickerBackgroundColor
            groupPadding = pickerGroupPadding
            cornerRadius = pickerCornerRadius
            filterInputState = filterInput?.onSaveInstanceState()
            pickerAdapterState = adapter.saveAdapterState()
        }
        return myState
    }

    @Override
    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        savedState.apply {
            pickerHintText = hintText
            pickerErrorText = errorText
            pickerRequiredText = requiredText
            isPickerRequired = isRequired
            pickerBackgroundColor = colorBackground
            pickerGroupPadding = groupPadding
            pickerCornerRadius = cornerRadius
            pickerDefaultIconColor = iconColor
            pickerAllowOptionAddition = allowToAddNewOptions
            filterInput?.onRestoreInstanceState(filterInputState)
            adapter.restoreAdapterState(pickerAdapterState!!)
            initializeViews()
            if (isShowing) showPicker()
        }
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

    fun addValidationCheck(validationCallback: ValidationCheck<ModelType>) {
        validationCallbacks.add(validationCallback)
    }

    fun attachToScrollingParent(parent: ViewGroup) {
        when (parent) {
            is ScrollView -> {
                internalOnShowListener = object : OnShowListener {
                    override fun execute() {
                        parent.post { parent.smoothScrollTo(0, bottom) }
                    }
                }
                return
            }
            is NestedScrollView -> {
                internalOnShowListener = object : OnShowListener {
                    override fun execute() {
                        parent.post { parent.smoothScrollTo(0, bottom) }
                    }
                }
            }
            else -> {
                Log.w(
                    InlineChipsPickerView::class.java.name, "Scrolling parent ignored. Parent type must be one of " +
                            "ScrollView|NestedScrollView"
                )
            }
        }
    }

    fun validate(): Boolean {
        var isValid = true
        if (isPickerRequired && !hasSelection) {
            pickerErrorText = pickerRequiredText
            return false
        }
        validationCallbacks.forEach {
            isValid = isValid and it.validate(selection)
            if (!isValid) return@forEach
        }
        pickerErrorText = if (isValid) null
        else pickerErrorText
        return isValid
    }

    fun showPicker() {
        if (isPickerShowing) return
        if (!filterInput!!.hasFocus())
            filterInput!!.requestFocus()
        recyclerView!!.visibility = VISIBLE
        dispatchOnShowEvent()
    }

    fun hidePicker() {
        if (!isPickerShowing) return
        if (filterInput!!.hasFocus())
            filterInput!!.clearFocus()
        recyclerView!!.visibility = GONE
        dispatchOnHideEvent()
    }

    fun addOption(option: ModelType) {
        adapter.collection.add(option)
    }

    fun selectPositions(positions: List<Int>) {
        selectedPositions = positions.toIntArray()
    }

    fun selectPositions(positions: IntArray) {
        selectedPositions = positions
    }

    fun selectItemAt(selection: Int) {
        selectedPositions = intArrayOf(selection)
    }

    fun selectItem(item: ModelType) {
        val position = adapter.collection.getPosition(item)
        if (position != -1) selectItemAt(position)
    }

    fun setRequired(required: Boolean) {
        this.isPickerRequired = required
        if (required) validate()
    }

    fun setPickerIcon(icon: Drawable?, color: Int) {
        pickerIcon = icon
        pickerIcon?.setTint(color)
    }

    fun getErrorText(): String? {
        return pickerErrorText
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.InlineChipsPickerView, 0, 0)
        try {
            attrTypedArray.apply {
                val defaultColorBackground = ResourceUtils.getColorByAttribute(getContext(), R.attr.colorOnSurface)
                val defaultCornerRadius = ResourceUtils.getDimenPxById(
                    getContext(),
                    com.google.android.material.R.dimen.mtrl_textinput_box_corner_radius_medium
                )
                val defaultChipGroupPadding = ResourceUtils.getDimenPxById(
                    getContext(),
                    R.dimen.inline_chips_picker_chip_group_padding
                )
                getString(R.styleable.InlineChipsPickerView_icpv_hint_text).apply {
                    val default = ResourceUtils.getPhrase(context, R.string.picker_chips_default_hint)
                    pickerHintText = this ?: default
                }
                getString(R.styleable.InlineChipsPickerView_icpv_required_text).apply {
                    val default = ResourceUtils.getPhrase(context, R.string.picker_default_required_text)
                    pickerRequiredText = this ?: default
                }
                getColor(R.styleable.InlineChipsPickerView_icpv_background_color, defaultColorBackground).apply {
                    val withAlpha = ColorUtils.setAlphaComponent(this, 30)
                    pickerBackgroundColor = withAlpha
                }
                getDimensionPixelSize(
                    R.styleable.InlineChipsPickerView_icpv_background_corner_radius,
                    defaultCornerRadius
                ).apply {
                    pickerCornerRadius = this
                }
                getDimensionPixelSize(
                    R.styleable.InlineChipsPickerView_cpv_chip_group_padding,
                    defaultChipGroupPadding
                ).apply {
                    pickerGroupPadding = this
                }
                isPickerRequired = getBoolean(R.styleable.InlineChipsPickerView_icpv_required, false)
                pickerAllowOptionAddition = getBoolean(R.styleable.InlineChipsPickerView_icpv_allow_to_add_new_options, true)
                ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface).apply {
                    pickerDefaultIconColor = ColorUtils.setAlphaComponent(this, 140)
                }
            }
        } finally {
            attrTypedArray.recycle()
        }
    }

    private fun initializeAdapter() {
        adapter = SelectableFilterOptionAdapter<ModelType>().apply {
            collection.addOnSelectionChangeListener(object : ExtendedCollection.SelectionChangeListener {
                override fun onChanged(newSelection: IntArray?) {
                    setupChips()
                    validate()
                    dispatchSelectionChangedEvent()
                }
            })
        }
    }

    private fun initializeViews() {
        setupBackground(pickerBackgroundColor)
        setupAddButton()
        setupInput()
        setupChips()
        setupRecyclerView()
        hidePicker()
    }

    private fun renderLayout() {
        inflate(context, R.layout.inline_chips_picker_layout, this) as LinearLayoutCompat
        flowLayout = findViewById(R.id.picker_flow_layout)
        filterInput = findViewById(R.id.picker_filter_input_view)
    }

    private fun setupInput() {
        filterInput!!.removeTextChangedCallback(textWatcher)
        filterInput!!.addTextChangedCallback(textWatcher)
        filterInput!!.setHintText(pickerHintText)
        filterInput!!.onFocusChangeListener = object : OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus && !isPickerShowing) showPicker()
                else if (!hasFocus && isPickerShowing) hidePicker()
            }
        }
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById<RecyclerView?>(R.id.picker_recycler_view).apply {
            setPadding(pickerGroupPadding)
            itemAnimator = null
            layoutManager = LinearLayoutManager(context)
            adapter = this@InlineChipsPickerView.adapter
        }
    }

    private fun setupAddButton() {
        val icon = ResourceUtils.getDrawable(context, R.drawable.inline_chip_picker_add_icon)
        icon!!.setTint(ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface))
        filterInput?.setEndIcon(icon) {
            if (!pickerAllowOptionAddition) return@setEndIcon
            val newOptionName = filterInput!!.text
            val newOption = optionCreator.createOption(newOptionName)
            adapter.collection.add(newOption)
            filterInput?.text = ""
            dispatchOptionCreatedEvent(newOption)
        }
        handleAddOptionVisibility()
    }

    private fun setupBackground(newColor: Int) {
        pickerBackgroundColor = newColor
        val backgroundDrawable: Drawable = DrawableUtils.getLayeredRoundedCornersDrawable(
            ResourceUtils.getColorByAttribute(context, R.attr.colorSurface),
            pickerBackgroundColor, pickerCornerRadius
        )
        background = backgroundDrawable
    }

    private fun handleAddOptionVisibility() {
        if (!pickerAllowOptionAddition) {
            filterInput?.setEndIconVisible(false)
            return
        }
        val text = filterInput?.text
        val showAddOption = !text.isNullOrBlank() && !adapter.hasItemWithName(text)
        filterInput?.setEndIconVisible(showAddOption)
    }

    private fun filterOptions(queryText: String) {
        adapter.collection.filter.filter(queryText)
    }

    private fun setupChips() {
        clearChips()
        buildChips()
    }

    private fun clearChips() {
        flowLayout!!.removeAllViews()
    }

    private fun buildChips() {
        if (selection.isEmpty()) {
            flowLayout!!.setPadding(0)
            return
        }
        val bottomPadding = ResourceUtils.getDimenPxById(context, R.dimen.inline_chips_picker_spacing_size)
        flowLayout!!.setPadding(pickerGroupPadding, pickerGroupPadding, pickerGroupPadding, bottomPadding)
        selection.forEach {
            val chipView = buildChip(it)
            flowLayout!!.addView(chipView)
        }
    }

    private fun buildChip(targetItem: ModelType): View {
        val itemName = targetItem.itemName
        val layoutInflater = LayoutInflater.from(context)
        val layoutId = R.layout.inline_chips_picker_chip
        return layoutInflater.inflate(layoutId, null, false).apply {
            background = ResourceUtils.getDrawable(context, R.drawable.inline_chip_picker_item_bg)
            findViewById<AppCompatImageButton>(R.id.picker_chip_item_remove_button).apply {
                val chipRemoveIcon = ResourceUtils.getDrawable(context, R.drawable.inline_chip_picker_remove_icon).apply {
                    this!!.setTint(ResourceUtils.getColorByAttribute(context, R.attr.colorPrimary))
                }
                background = ResourceUtils.getDrawable(context, R.drawable.inline_chip_picker_remove_icon_bg)
                setImageDrawable(chipRemoveIcon)
                setOnClickListener { adapter.collection.selectItem(targetItem, false) }
            }
            findViewById<TextView>(R.id.chip_item_text_view).apply {
                text = itemName
                setTextColor(ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface))
                compoundDrawablePadding = ResourceUtils.getDimenPxById(context, R.dimen.inline_chips_picker_spacing_size)
            }
        }
    }

    private fun compareValues(v1: IntArray?, v2: IntArray?): Boolean {
        return Arrays.equals(v1, v2)
    }


    private fun dispatchSelectionChangedEvent() {
        selectionChangedListeners.forEach {
            it.execute(selectedPositions)
        }
    }

    private fun dispatchOptionCreatedEvent(newOption: ModelType) {
        onOptionCreatedListeners.forEach {
            it.execute(newOption)
        }
    }

    private fun dispatchOnShowEvent() {
        internalOnShowListener?.execute()
        onShowListeners.forEach {
            it.execute()
        }
    }

    private fun dispatchOnHideEvent() {
        onHideListeners.forEach {
            it.execute()
        }
    }

    private class SavedState : BaseSavedState {
        var hintText: String? = null
        var errorText: String? = null
        var requiredText: String? = null
        var isShowing: Boolean = false
        var isRequired: Boolean = false
        var allowToAddNewOptions: Boolean = false
        var colorBackground = -1
        var iconColor = -1
        var groupPadding = -1
        var cornerRadius = -1
        var filterInputState: Parcelable? = null
        var pickerAdapterState: Bundle? = null

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            ParcelUtils.apply {
                hintText = readString(parcel)
                errorText = readString(parcel)
                requiredText = readString(parcel)
                isShowing = readBoolean(parcel)!!
                isRequired = readBoolean(parcel)!!
                allowToAddNewOptions = readBoolean(parcel)!!
                colorBackground = readInt(parcel)!!
                iconColor = readInt(parcel)!!
                groupPadding = readInt(parcel)!!
                cornerRadius = readInt(parcel)!!
                filterInputState = readParcelable(parcel, Bundle::class.java)
                pickerAdapterState = parcel.readBundle(this::class.java.classLoader)
            }
        }

        @Override
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            ParcelUtils.apply {
                writeString(out, hintText)
                writeString(out, errorText)
                writeString(out, requiredText)
                writeBoolean(out, isShowing)
                writeBoolean(out, isRequired)
                writeBoolean(out, allowToAddNewOptions)
                writeInt(out, colorBackground)
                writeInt(out, iconColor)
                writeInt(out, groupPadding)
                writeInt(out, cornerRadius)
                writeParcelable(out, filterInputState)
                out.writeBundle(pickerAdapterState)
            }
        }

        @Override
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
        fun execute(newSelection: IntArray?)
    }

    interface ValidationCheck<ModelType : EasyAdapterDataModel> {
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
}