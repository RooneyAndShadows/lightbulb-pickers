package com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.LinearLayout
import androidx.core.graphics.ColorUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.InputTriggerView.InputTypes.*
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.base.DialogTriggerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


@Suppress("MemberVisibilityCanBePrivate")
class InputTriggerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : DialogTriggerView(context, attrs, defStyleAttr, defStyleRes) {
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var textInputEditText: TextInputEditText
    private lateinit var inputType: InputTypes
    var inputBackgroundColor: Int = -1
        private set
    var startIconUseAlpha = false
        private set
    var pickerInputBoxStrokeColor: Int = -1
        private set

    init {
        isSaveEnabled = true
        readAttributes(context, attrs)
        inflateView()
        setupBackground()
        setupStroke()
        setupEndIcon()
    }

    @Override
    override fun initializeDefaultIconColor(): Int {
        return ResourceUtils.getColorByAttribute(context, R.attr.colorOnBackground)
    }

    @Override
    override fun onIconChange() {
        setupStartIcon()
    }

    @Override
    override fun onIconColorChange() {
        setupStartIcon()
    }

    @Override
    override fun onErrorEnabledChange() {
        textInputLayout.apply {
            post {
                isErrorEnabled = errorEnabled
            }
        }
    }

    @Override
    override fun onTextChange() {
        textInputEditText.apply {
            setText(this@InputTriggerView.text)
        }
    }

    @Override
    override fun onHintTextChange() {
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
    override fun onErrorTextChange() {
        textInputLayout.apply {
            error = errorText
        }
    }

    @Override
    override fun onErrorTextAppearanceChange() {
        textInputLayout.setErrorTextAppearance(errorTextAppearance)
    }

    @Override
    override fun onHintTextAppearanceChange() {
        textInputLayout.setHintTextAppearance(hintTextAppearance)
    }

    @Override
    override fun onEnabledChange() {
        textInputEditText.isEnabled = isEnabled
        textInputLayout.isEnabled = isEnabled
    }

    @Override
    override fun attachTo(pickerView: BaseDialogPickerView<*>) {
        this.pickerView = pickerView
        requirePickerView().apply {
            textInputLayout.setEndIconOnClickListener { showPickerDialog() }
            if (!pickerView.showSelectedTextValue && hintText.isNullOrBlank()) {
                textInputEditText.compoundDrawablePadding = -textInputEditText.paddingLeft
                textInputEditText.minWidth = 0
                textInputEditText.width = 0
            }
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
    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.apply {
            val view = this@InputTriggerView
            inputBackgroundColor = view.inputBackgroundColor
            pickerInputBoxStrokeColor = view.pickerInputBoxStrokeColor
            startIconUseAlpha = view.startIconUseAlpha
            editTextSavedState = textInputEditText.onSaveInstanceState()
        }

        return myState
    }

    @Override
    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        savedState.apply {
            setInputBackgroundColor(savedState.inputBackgroundColor)
            setInputBoxStrokeColor(savedState.pickerInputBoxStrokeColor)
            setStartIconUseAlpha(savedState.startIconUseAlpha)
            textInputEditText.onRestoreInstanceState(savedState.editTextSavedState)
        }
    }

    fun setInputBackgroundColor(inputBackgroundColor: Int) {
        this.inputBackgroundColor = inputBackgroundColor
        setupBackground()
    }

    fun setStartIconUseAlpha(useAlpha: Boolean) {
        this.startIconUseAlpha = useAlpha
        setupStartIcon()
    }

    fun setInputBoxStrokeColor(strokeColor: Int) {
        this.pickerInputBoxStrokeColor = strokeColor
        setupStroke()
    }

    private fun inflateView() {
        when (inputType) {
            BOXED -> inflate(context, R.layout.dialog_picker_boxed_layout, this) as LinearLayout
            OUTLINED -> inflate(context, R.layout.dialog_picker_outlined_layout, this) as LinearLayout
        }
        textInputLayout = findViewById(R.id.picker_view_input_layout_view)
        textInputEditText = findViewById(R.id.picker_view_input_edit_text_view)
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.InputTriggerView, 0, 0)
        try {
            attrTypedArray.apply {
                val defaultStrokeColor = ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface)
                val defaultBackgroundColor = ResourceUtils.getColorByAttribute(getContext(), R.attr.colorOnSurface)
                getColor(R.styleable.InputTriggerView_itv_stroke_color, defaultStrokeColor).apply {
                    pickerInputBoxStrokeColor = this.let {
                        return@let ColorUtils.setAlphaComponent(this, 140)
                    }
                }
                getColor(R.styleable.InputTriggerView_itv_background_color, defaultBackgroundColor).apply {
                    inputBackgroundColor = this.let {
                        return@let ColorUtils.setAlphaComponent(this, 30)
                    }
                }
                getInt(R.styleable.InputTriggerView_itv_layout_type, BOXED.value).apply {
                    inputType = InputTypes.valueOf(this)
                }
                startIconUseAlpha = getBoolean(R.styleable.InputTriggerView_itv_icon_use_alpha, true)
            }
        } finally {
            attrTypedArray.recycle()
        }
    }

    private fun setupBackground() {
        if (inputType == BOXED)
            textInputLayout.boxBackgroundColor = inputBackgroundColor
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
        val color = if (iconColor == -1) defaultIconColor else iconColor
        color.apply {
            val alpha = if (startIconUseAlpha) 140 else 255
            val newColor = ColorUtils.setAlphaComponent(this, alpha)
            textInputLayout.setStartIconTintList(
                ColorStateList(
                    arrayOf(
                        intArrayOf(
                            android.R.attr.state_focused,
                            android.R.attr.state_pressed
                        ), intArrayOf(-android.R.attr.state_enabled), intArrayOf()
                    ), intArrayOf(
                        newColor,
                        newColor,
                        newColor
                    )
                )
            )
            textInputLayout.startIconDrawable = icon
        }
    }

    private fun setupEndIcon() {
        val endIcon = ResourceUtils.getDrawable(context, R.drawable.dropdown_icon)
        endIcon!!.setTint(ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface))
        textInputLayout.endIconMode = TextInputLayout.END_ICON_CUSTOM
        textInputLayout.endIconDrawable = endIcon
        textInputLayout.isEndIconVisible = true
        textInputLayout.errorIconDrawable = null
    }

    private class SavedState : BaseSavedState {
        var inputBackgroundColor = 0
        var pickerInputBoxStrokeColor = 0
        var startIconUseAlpha = false
        var editTextSavedState: Parcelable? = null

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            parcel.apply {
                inputBackgroundColor = ParcelUtils.readInt(this)!!
                pickerInputBoxStrokeColor = ParcelUtils.readInt(this)!!
                startIconUseAlpha = ParcelUtils.readBoolean(this)!!
                editTextSavedState = ParcelUtils.readParcelable(this, Bundle::class.java)
            }
        }

        @Override
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.apply {
                ParcelUtils.writeInt(this, inputBackgroundColor)
                ParcelUtils.writeInt(this, pickerInputBoxStrokeColor)
                ParcelUtils.writeBoolean(this, startIconUseAlpha)
                ParcelUtils.writeParcelable(this, editTextSavedState)
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

    enum class InputTypes(val value: Int) {
        BOXED(1), OUTLINED(2);

        companion object {
            fun valueOf(value: Int) = values().first { it.value == value }
        }
    }
}