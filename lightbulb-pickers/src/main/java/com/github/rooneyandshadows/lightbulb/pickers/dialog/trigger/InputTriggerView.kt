package com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.LinearLayout
import androidx.core.graphics.ColorUtils
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.base.DialogPickerTriggerLayout
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.InputTriggerView.InputTypes.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

@Suppress("MemberVisibilityCanBePrivate")
class InputTriggerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes), DialogPickerTriggerLayout {
    private lateinit var pickerView: BaseDialogPickerView<*>
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var textInputEditText: TextInputEditText
    private lateinit var inputType: InputTypes
    private val defaultIconColor: Int
        get() = ResourceUtils.getColorByAttribute(context, R.attr.colorOnPrimary)
    var pickerIcon: Drawable? = null
        set(value) {
            field = value
            setupStartIcon()
        }
    var triggerBackgroundColor: Int = -1
        set(value) {
            field = value
            setupBackground()
        }
    var pickerStartIconColor = -1
        set(value) {
            field = value
            setupStartIcon()
        }
    var errorAppearance = 0
        set(value) {
            field = value
            setupTextAppearances()
        }
    var hintAppearance = 0
        set(value) {
            field = value
            setupTextAppearances()
        }
    var startIconUseAlpha = false
        set(value) {
            field = value
            setupStartIcon()
        }
    var pickerInputBoxStrokeColor: Int = -1
        set(value) {
            field = value
            setupStroke()
        }

    override var triggerText: String
        get() = if (textInputEditText.text != null) textInputEditText.text.toString() else ""
        set(newTextValue) {
            textInputEditText.setText(newTextValue)
        }

    init {
        readAttributes(context, attrs)
        initializeView()
    }

    @Override
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        textInputEditText.isEnabled = enabled
        textInputLayout.isEnabled = enabled
    }

    @Override
    override fun attachTo(pickerView: BaseDialogPickerView<*>) {
        this.pickerView = pickerView
        requirePickerView().apply {
            textInputLayout.setEndIconOnClickListener { showPickerDialog() }
            textInputLayout.isErrorEnabled = errorEnabled
            if (textInputLayout.isErrorEnabled) textInputLayout.error = errorText
            if (!pickerView.showSelectedTextValue && pickerHintText.isNullOrBlank()) {
                textInputEditText.compoundDrawablePadding = -textInputEditText.paddingLeft
                textInputEditText.minWidth = 0
                textInputEditText.width = 0
            }
            textInputLayout.isEnabled = isEnabled
            textInputEditText.isEnabled = isEnabled
            setTriggerHintText(pickerHintText)
        }
    }

    @Override
    override fun setTriggerIcon(icon: Drawable?, color: Int?) {
        pickerStartIconColor = color ?: defaultIconColor
        pickerIcon = icon
    }

    @Override
    override fun setTriggerErrorText(errorText: String?) {
        textInputLayout.error = errorText
    }

    @Override
    override fun setTriggerHintText(hintText: String?) {
        val hasHint = !hintText.isNullOrBlank()
        textInputLayout.isHintEnabled = hasHint
        textInputLayout.hint = hintText
        textInputEditText.hint = null
        /*int paddingTop = (int) (textInputEditText.getPaddingTop() * 1.1);
        if (textInputLayout.getBoxBackgroundMode() == TextInputLayout.BOX_BACKGROUND_FILLED) {
            if (hasHint)
                textInputEditText.setPadding(textInputEditText.getPaddingLeft(), paddingTop, textInputEditText.getPaddingRight(), textInputEditText.getPaddingBottom());
            else {
                int paddingVertical = (paddingTop + textInputEditText.getPaddingBottom()) / 2;
                textInputEditText.setPadding(textInputEditText.getPaddingLeft(), paddingVertical, textInputEditText.getPaddingRight(), paddingVertical);
            }
        }*/
    }

    @Override
    override fun setTriggerErrorEnabled(errorEnabled: Boolean) {
        textInputLayout.isErrorEnabled = errorEnabled
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
        myState.pickerBackgroundColor = triggerBackgroundColor
        myState.pickerInputBoxStrokeColor = pickerInputBoxStrokeColor
        myState.pickerStartIconColor = pickerStartIconColor
        myState.iconUseAlpha = startIconUseAlpha
        myState.editTextSavedState = textInputEditText.onSaveInstanceState()
        return myState
    }

    @Override
    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        triggerBackgroundColor = savedState.pickerBackgroundColor
        pickerInputBoxStrokeColor = savedState.pickerInputBoxStrokeColor
        pickerStartIconColor = savedState.pickerStartIconColor
        startIconUseAlpha = savedState.iconUseAlpha
        textInputEditText.onRestoreInstanceState(savedState.editTextSavedState)
        setupInputLayout()
    }

    private fun requirePickerView(): BaseDialogPickerView<*> {
        if (!this::pickerView.isInitialized)
            throw Exception("ButtonTriggerView is not attached to picker.")
        return pickerView
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.InputTriggerView, 0, 0)
        try {
            attrTypedArray.apply {
                getColor(R.styleable.InputTriggerView_itv_stroke_color, -1).apply {
                    val default = ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface)
                    pickerInputBoxStrokeColor = when (this) {
                        -1 -> default
                        else -> this
                    }.let {
                        return@let ColorUtils.setAlphaComponent(this, 140)
                    }
                }
                getColor(R.styleable.InputTriggerView_itv_background_color, -1).apply {
                    val default = ResourceUtils.getColorByAttribute(getContext(), R.attr.colorOnSurface)
                    triggerBackgroundColor = when (this) {
                        -1 -> default
                        else -> this
                    }.let {
                        return@let ColorUtils.setAlphaComponent(this, 30)
                    }
                }
                getInt(R.styleable.InputTriggerView_itv_layout_type, BOXED.value).apply {
                    inputType = InputTypes.valueOf(this)
                }
                getResourceId(
                    R.styleable.InputTriggerView_itv_error_text_appearance,
                    R.style.PickerViewErrorTextAppearance
                ).apply {
                    errorAppearance = this
                }
                getResourceId(
                    R.styleable.InputTriggerView_itv_hint_text_appearance,
                    R.style.PickerViewHintTextAppearance
                ).apply {
                    hintAppearance = this
                }
                getColor(R.styleable.InputTriggerView_itv_start_icon_color, -1).apply {
                    pickerStartIconColor = when (this) {
                        -1 -> ResourceUtils.getColorByAttribute(context, R.attr.colorAccent)
                        else -> this
                    }
                }
                startIconUseAlpha = getBoolean(R.styleable.InputTriggerView_itv_icon_use_alpha, true)
            }
        } finally {
            attrTypedArray.recycle()
        }
    }

    private fun initializeView() {
        isSaveEnabled = true
        renderLayout()
        setupInputLayout()
    }

    private fun renderLayout() {
        when (inputType) {
            BOXED -> inflate(context, R.layout.dialog_picker_boxed_layout, this) as LinearLayout
            OUTLINED -> inflate(context, R.layout.dialog_picker_outlined_layout, this) as LinearLayout
        }
        textInputLayout = findViewById(R.id.picker_view_input_layout_view)
        textInputEditText = findViewById(R.id.picker_view_input_edit_text_view)
    }

    private fun setupInputLayout() {
        setupBackground()
        setupStroke()
        setupStartIcon()
        setupEndIcon()
        setupTextAppearances()
    }

    private fun setupBackground() {
        if (inputType == BOXED)
            textInputLayout.boxBackgroundColor = triggerBackgroundColor
    }

    private fun setupStroke() {
        textInputLayout.setBoxStrokeColorStateList(
            ColorStateList(
                arrayOf(
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_focused, android.R.attr.state_pressed),
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf()
                ), intArrayOf(
                    pickerInputBoxStrokeColor,
                    pickerInputBoxStrokeColor,
                    pickerInputBoxStrokeColor,
                    pickerInputBoxStrokeColor,
                    pickerInputBoxStrokeColor
                )
            )
        )
    }

    private fun setupStartIcon() {
        val alpha = if (startIconUseAlpha) 140 else 255
        val iconColor: Int = ColorUtils.setAlphaComponent(pickerStartIconColor, alpha)
        textInputLayout.setStartIconTintList(
            ColorStateList(
                arrayOf(
                    intArrayOf(
                        android.R.attr.state_focused,
                        android.R.attr.state_pressed
                    ), intArrayOf(-android.R.attr.state_enabled), intArrayOf()
                ), intArrayOf(
                    iconColor,
                    iconColor,
                    iconColor
                )
            )
        )
        textInputLayout.startIconDrawable = pickerIcon
    }

    private fun setupEndIcon() {
        val endIcon = ResourceUtils.getDrawable(context, R.drawable.dropdown_icon)
        endIcon!!.setTint(ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface))
        textInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
        textInputLayout.endIconDrawable = endIcon
        textInputLayout.isEndIconVisible = true
        textInputLayout.errorIconDrawable = null
    }

    private fun setupTextAppearances() {
        textInputLayout.setHintTextAppearance(hintAppearance)
        textInputLayout.setErrorTextAppearance(errorAppearance)
    }

    private class SavedState : BaseSavedState {
        var pickerBackgroundColor = 0
        var pickerInputBoxStrokeColor = 0
        var pickerStartIconColor = 0
        var hintAppearance = 0
        var errorAppearance = 0
        var iconUseAlpha = false
        var editTextSavedState: Parcelable? = null

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            pickerBackgroundColor = parcel.readInt()
            pickerInputBoxStrokeColor = parcel.readInt()
            pickerStartIconColor = parcel.readInt()
            hintAppearance = parcel.readInt()
            errorAppearance = parcel.readInt()
            iconUseAlpha = parcel.readByte().toInt() != 0
            editTextSavedState = ParcelUtils.readParcelable(parcel, Bundle::class.java)
        }

        @Override
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(pickerBackgroundColor)
            out.writeInt(pickerInputBoxStrokeColor)
            out.writeInt(pickerStartIconColor)
            out.writeInt(hintAppearance)
            out.writeInt(errorAppearance)
            out.writeByte((if (iconUseAlpha) 1 else 0).toByte())
            ParcelUtils.writeParcelable(out, editTextSavedState)
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

    enum class InputTypes(val value: Int) {
        BOXED(1), OUTLINED(2);

        companion object {
            fun valueOf(value: Int) = values().first { it.value == value }
        }
    }
}