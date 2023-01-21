package com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.widget.LinearLayout
import androidx.core.graphics.ColorUtils
import com.github.rooneyandshadows.java.commons.string.StringUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.DialogPickerTriggerLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class InputTriggerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes), DialogPickerTriggerLayout {
    private val INPUT_LAYOUT_TAG = ResourceUtils.getPhrase(getContext(), R.string.DP_InputLayoutTag)
    private val INPUT_EDIT_TEXT_TAG = ResourceUtils.getPhrase(getContext(), R.string.DP_InputEditTextTag)
    private var rootView: LinearLayout? = null
    private var textInputLayout: TextInputLayout? = null
    private var textInputEditText: TextInputEditText? = null
    private var inputType: InputTypes? = null
    protected var pickerIcon: Drawable? = null
    protected var pickerBackgroundColor: Int? = null
    protected var pickerInputBoxStrokeColor: Int? = null
    protected var pickerStartIconColor = 0
    protected var defaultIconColor = 0
    protected var hintAppearance = 0
    protected var errorAppearance = 0
    protected var startIconUseAlpha = false

    init {
        readAttributes(context, attrs)
        initializeView()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (textInputEditText != null) textInputEditText!!.isEnabled = enabled
        if (textInputLayout != null) textInputLayout!!.isEnabled = enabled
    }

    override fun attachTo(pickerView: BaseDialogPickerView) {
        textInputLayout!!.setEndIconOnClickListener { v: View? -> pickerView.showPickerDialog() }
        textInputLayout!!.isErrorEnabled = pickerView.isErrorEnabled
        if (textInputLayout!!.isErrorEnabled) textInputLayout!!.error = pickerView.errorText
        if (!pickerView.isShowSelectedTextValue && StringUtils.isNullOrEmptyString(pickerView.pickerHintText)) {
            textInputEditText!!.compoundDrawablePadding = -textInputEditText!!.paddingLeft
            textInputEditText!!.minWidth = 0
            textInputEditText!!.width = 0
        }
        textInputLayout!!.isEnabled = isEnabled
        textInputEditText!!.isEnabled = isEnabled
        setTriggerHintText(pickerView.pickerHintText)
    }

    override fun setTriggerIcon(icon: Drawable?, color: Int?) {
        pickerIcon = icon
        setPickerStartIconColor(color)
    }

    override fun setTriggerErrorText(errorText: String?) {
        textInputLayout!!.error = errorText
    }

    override fun setTriggerHintText(hintText: String?) {
        val hasHint = !StringUtils.isNullOrEmptyString(hintText)
        textInputLayout!!.isHintEnabled = hasHint
        textInputLayout!!.hint = hintText
        textInputEditText.setHint(null)
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

    override fun setTriggerErrorEnabled(errorEnabled: Boolean) {
        textInputLayout!!.isErrorEnabled = errorEnabled
    }

    override var triggerText: String
        get() = if (textInputEditText!!.text != null) textInputEditText!!.text.toString() else ""
        set(newTextValue) {
            if (textInputEditText!!.text != null) if (newTextValue != textInputEditText!!.text.toString()) textInputEditText!!.setText(
                newTextValue)
        }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>) {
        dispatchThawSelfOnly(container)
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.pickerBackgroundColor = pickerBackgroundColor!!
        myState.pickerInputBoxStrokeColor = pickerInputBoxStrokeColor!!
        myState.pickerStartIconColor = pickerStartIconColor
        myState.defaultIconColor = defaultIconColor
        myState.iconUseAlpha = startIconUseAlpha
        myState.editTextSavedState = textInputEditText!!.onSaveInstanceState()
        return myState
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        pickerBackgroundColor = savedState.pickerBackgroundColor
        pickerInputBoxStrokeColor = savedState.pickerInputBoxStrokeColor
        pickerStartIconColor = savedState.pickerStartIconColor
        defaultIconColor = savedState.defaultIconColor
        startIconUseAlpha = savedState.iconUseAlpha
        textInputEditText!!.onRestoreInstanceState(savedState.editTextSavedState)
        setupInputLayout()
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val a = context.theme.obtainStyledAttributes(attrs, R.styleable.InputTriggerView, 0, 0)
        try {
            pickerInputBoxStrokeColor = ColorUtils.setAlphaComponent(a.getColor(R.styleable.InputTriggerView_ITV_StrokeColor,
                ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface)), 140)
            pickerBackgroundColor = ColorUtils.setAlphaComponent(a.getColor(R.styleable.InputTriggerView_ITV_BackgroundColor,
                ResourceUtils.getColorByAttribute(getContext(), R.attr.colorOnSurface)), 30)
            inputType = InputTypes.valueOf(a.getInt(R.styleable.InputTriggerView_ITV_LayoutType, 1))
            errorAppearance =
                a.getResourceId(R.styleable.InputTriggerView_ITV_ErrorTextAppearance, R.style.PickerViewErrorTextAppearance)
            hintAppearance =
                a.getResourceId(R.styleable.InputTriggerView_ITV_HintTextAppearance, R.style.PickerViewHintTextAppearance)
            startIconUseAlpha = a.getBoolean(R.styleable.InputTriggerView_ITV_IconUseAlpha, true)
            defaultIconColor = ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface)
            pickerStartIconColor = a.getColor(R.styleable.InputTriggerView_ITV_StartIconColor, defaultIconColor)
        } finally {
            a.recycle()
        }
    }

    private fun initializeView() {
        isSaveEnabled = true
        renderLayout()
        setupInputLayout()
    }

    private fun renderLayout() {
        when (inputType) {
            InputTypes.BOXED -> rootView = inflate(context, R.layout.dialog_picker_boxed_layout, this) as LinearLayout
            InputTypes.OUTLINED -> rootView = inflate(context, R.layout.dialog_picker_outlined_layout, this) as LinearLayout
        }
        textInputLayout = rootView!!.findViewWithTag(INPUT_LAYOUT_TAG)
        textInputEditText = findViewWithTag(INPUT_EDIT_TEXT_TAG)
    }

    private fun setupInputLayout() {
        setupBackground()
        setupStroke()
        setupStartIcon()
        setupEndIcon()
        setupTextAppearances()
    }

    fun setStartIconUseAlpha(startIconUseAlpha: Boolean) {
        this.startIconUseAlpha = startIconUseAlpha
        setupStartIcon()
    }

    fun setPickerBackgroundColor(pickerBackgroundColor: Int?) {
        this.pickerBackgroundColor = pickerBackgroundColor
        setupBackground()
    }

    fun setPickerInputBoxStrokeColor(pickerInputBoxStrokeColor: Int?) {
        this.pickerInputBoxStrokeColor = pickerInputBoxStrokeColor
        setupStroke()
    }

    fun setPickerStartIconColor(pickerStartIconColor: Int?) {
        this.pickerStartIconColor = pickerStartIconColor ?: defaultIconColor
        setupStartIcon()
    }

    fun setHintAppearance(hintAppearance: Int) {
        this.hintAppearance = hintAppearance
        setupTextAppearances()
    }

    fun setErrorAppearance(errorAppearance: Int) {
        this.errorAppearance = errorAppearance
        setupTextAppearances()
    }

    private fun setupBackground() {
        if (inputType == InputTypes.BOXED) textInputLayout!!.boxBackgroundColor = pickerBackgroundColor!!
    }

    private fun setupStroke() {
        textInputLayout!!.setBoxStrokeColorStateList(ColorStateList(arrayOf(intArrayOf(android.R.attr.state_focused),
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_focused, android.R.attr.state_pressed),
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf()), intArrayOf(
            pickerInputBoxStrokeColor!!,
            pickerInputBoxStrokeColor!!,
            pickerInputBoxStrokeColor!!,
            pickerInputBoxStrokeColor!!,
            pickerInputBoxStrokeColor!!
        )))
    }

    private fun setupStartIcon() {
        val alpha = if (startIconUseAlpha) 140 else 255
        val iconColor: Int
        iconColor = ColorUtils.setAlphaComponent(pickerStartIconColor, alpha)
        textInputLayout!!.setStartIconTintList(ColorStateList(arrayOf(intArrayOf(android.R.attr.state_focused,
            android.R.attr.state_pressed), intArrayOf(-android.R.attr.state_enabled), intArrayOf()), intArrayOf(
            iconColor,
            iconColor,
            iconColor
        )))
        textInputLayout!!.startIconDrawable = pickerIcon
    }

    private fun setupEndIcon() {
        val endIcon = ResourceUtils.getDrawable(context, R.drawable.dropdown_icon)
        endIcon!!.setTint(ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface))
        textInputLayout!!.endIconMode = TextInputLayout.END_ICON_CUSTOM
        textInputLayout!!.endIconDrawable = endIcon
        textInputLayout!!.isEndIconVisible = true
        textInputLayout.setErrorIconDrawable(null)
    }

    private fun setupTextAppearances() {
        textInputLayout!!.setHintTextAppearance(hintAppearance)
        textInputLayout!!.setErrorTextAppearance(errorAppearance)
    }

    private class SavedState : BaseSavedState {
        var pickerBackgroundColor = 0
        var pickerInputBoxStrokeColor = 0
        var pickerStartIconColor = 0
        var defaultIconColor = 0
        private var hintAppearance = 0
        private var errorAppearance = 0
        var iconUseAlpha = false
        var editTextSavedState: Parcelable? = null

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            pickerBackgroundColor = `in`.readInt()
            pickerInputBoxStrokeColor = `in`.readInt()
            pickerStartIconColor = `in`.readInt()
            defaultIconColor = `in`.readInt()
            hintAppearance = `in`.readInt()
            errorAppearance = `in`.readInt()
            iconUseAlpha = `in`.readByte().toInt() != 0
            editTextSavedState = `in`.readParcelable(InputTriggerView::class.java.classLoader)
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(pickerBackgroundColor)
            out.writeInt(pickerInputBoxStrokeColor)
            out.writeInt(pickerStartIconColor)
            out.writeInt(defaultIconColor)
            out.writeInt(hintAppearance)
            out.writeInt(errorAppearance)
            out.writeByte((if (iconUseAlpha) 1 else 0).toByte())
            out.writeParcelable(editTextSavedState, 0)
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

    protected enum class InputTypes(val value: Int) {
        BOXED(1), OUTLINED(2);

        companion object {
            private val values = SparseArray<InputTypes>()

            init {
                for (inputType in values()) {
                    values.put(com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.inputType.value,
                        com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.inputType)
                }
            }

            fun valueOf(inputType: Int): InputTypes {
                return values[inputType]
            }
        }
    }
}