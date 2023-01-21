package com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.DialogPickerTriggerLayout
import com.google.android.material.button.MaterialButton

@Suppress("MemberVisibilityCanBePrivate")
class ButtonTriggerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes), DialogPickerTriggerLayout {
    private lateinit var pickerView: BaseDialogPickerView<*>
    private lateinit var buttonView: MaterialButton
    private lateinit var errorTextView: AppCompatTextView
    private var hasDefinedBoxBackgroundAttribute = false
    var pickerIcon: Drawable? = null
        set(value) {
            field = value
            setupStartIcon()
        }
    var buttonBackgroundColor: Int = -1
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
            setupErrorAppearance()
        }
    var cornerRadius = 0
        set(value) {
            field = value
            setupCornerRadius()
        }
    override var triggerText: String
        get() = buttonView.text.toString()
        set(newTextValue) {
            buttonView.text = newTextValue
        }

    init {
        readAttributes(context, attrs)
        initializeView()
    }

    @Override
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        buttonView.isEnabled = enabled
    }

    @Override
    override fun attachTo(pickerView: BaseDialogPickerView<*>) {
        this.pickerView = pickerView
        buttonView.setOnClickListener { requirePickerView().showPickerDialog() }
        requirePickerView().apply {
            setTriggerErrorEnabled(errorEnabled)
            if (errorEnabled) buttonView.error = errorText
            buttonView.isEnabled = isEnabled
            setTriggerHintText(pickerHintText)
        }
    }

    @Override
    override fun setTriggerIcon(icon: Drawable?, color: Int?) {
        pickerIcon = icon
        pickerStartIconColor = color ?: -1
    }

    @Override
    override fun setTriggerErrorText(errorText: String?) {
        errorTextView.text = errorText
    }

    @Override
    override fun setTriggerHintText(hintText: String?) {
        buttonView.text = hintText
    }

    @Override
    override fun setTriggerErrorEnabled(errorEnabled: Boolean) {
        errorTextView.visibility = if (errorEnabled) VISIBLE else GONE
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
        myState.pickerBackgroundColor = buttonBackgroundColor
        myState.backgroundRadius = cornerRadius
        myState.startIconColor = pickerStartIconColor
        myState.errorAppearance = errorAppearance
        return myState
    }

    @Override
    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        buttonBackgroundColor = savedState.pickerBackgroundColor
        cornerRadius = savedState.backgroundRadius
        pickerStartIconColor = savedState.startIconColor
        errorAppearance = savedState.errorAppearance
        setupInputLayout()
    }

    private fun requirePickerView(): BaseDialogPickerView<*> {
        if (!this::pickerView.isInitialized)
            throw Exception("ButtonTriggerView is not attached to picker.")
        return pickerView
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ButtonTriggerView, 0, 0)
        try {
            hasDefinedBoxBackgroundAttribute = attrTypedArray.hasValue(R.styleable.ButtonTriggerView_BTV_BackgroundColor)
            attrTypedArray.apply {
                getColor(R.styleable.ButtonTriggerView_BTV_BackgroundColor, -1).apply {
                    buttonBackgroundColor = when (this) {
                        -1 -> Color.TRANSPARENT
                        else -> this
                    }
                }
                getInt(R.styleable.ButtonTriggerView_BTV_BackgroundRadius, 5).apply {
                    cornerRadius = ResourceUtils.dpToPx(this)
                }
                getResourceId(R.styleable.ButtonTriggerView_BTV_ErrorTextAppearance,
                    R.style.PickerViewErrorTextAppearance).apply {
                    errorAppearance = this
                }
                getColor(R.styleable.ButtonTriggerView_BTV_StartIconColor, -1).apply {
                    pickerStartIconColor = when (this) {
                        -1 -> ResourceUtils.getColorByAttribute(context, R.attr.colorOnPrimary)
                        else -> this
                    }
                }
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
        inflate(context, R.layout.dialog_picker_button_layout, this) as LinearLayout
        buttonView = findViewById(R.id.picker_view_button)
        errorTextView = findViewById(R.id.picker_view_error_text_view)
    }

    private fun setupInputLayout() {
        setupBackground()
        setupCornerRadius()
        setupStartIcon()
        setupErrorAppearance()
    }

    private fun setupBackground() {
        if (hasDefinedBoxBackgroundAttribute && buttonBackgroundColor == Color.TRANSPARENT)
            buttonView.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun setupCornerRadius() {
        buttonView.cornerRadius = cornerRadius
    }

    private fun setupStartIcon() {
        buttonView.apply {
            iconTint = ColorStateList(arrayOf(intArrayOf()), intArrayOf(pickerStartIconColor))
            icon = pickerIcon
        }
    }

    private fun setupErrorAppearance() {
        errorTextView.setTextAppearance(errorAppearance)
    }

    private class SavedState : BaseSavedState {
        var pickerBackgroundColor = -1
        var errorAppearance = -1
        var backgroundRadius = -1
        var startIconColor = -1

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            pickerBackgroundColor = parcel.readInt()
            errorAppearance = parcel.readInt()
            backgroundRadius = parcel.readInt()
            startIconColor = parcel.readInt()
        }

        @Override
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(pickerBackgroundColor)
            out.writeInt(errorAppearance)
            out.writeInt(backgroundRadius)
            out.writeInt(startIconColor)
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
}