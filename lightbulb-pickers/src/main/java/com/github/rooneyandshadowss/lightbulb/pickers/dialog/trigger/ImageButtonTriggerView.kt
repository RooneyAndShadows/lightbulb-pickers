package com.github.RooneyAndShadows.lightbulb.pickers.dialog.trigger

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import com.github.RooneyAndShadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.RooneyAndShadows.lightbulb.pickers.dialog.trigger.base.DialogPickerTriggerLayout
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.R

@Suppress("UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")
class ImageButtonTriggerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes), DialogPickerTriggerLayout {
    private lateinit var pickerView: BaseDialogPickerView<*>
    private lateinit var iconButtonView: AppCompatImageButton
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

    //NOT SUPPORTED
    override var triggerText: String
        get() = ""
        set(newTextValue) {
            //NOT SUPPORTED
        }

    init {
        readAttributes(context, attrs)
        initView()
    }

    @Override
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        iconButtonView.isEnabled = enabled
    }

    @Override
    override fun attachTo(pickerView: BaseDialogPickerView<*>) {
        this.pickerView = pickerView
        requirePickerView().apply {
            iconButtonView.setOnClickListener { pickerView.showPickerDialog() }
            iconButtonView.isEnabled = isEnabled
            setTriggerErrorEnabled(errorEnabled)
            if (errorEnabled)
                errorTextView.error = errorText
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
        //NOT SUPPORTED
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
        myState.pickerStartIconColor = pickerStartIconColor
        myState.errorAppearance = errorAppearance
        return myState
    }

    @Override
    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        buttonBackgroundColor = savedState.pickerBackgroundColor
        pickerStartIconColor = savedState.pickerStartIconColor
        errorAppearance = savedState.errorAppearance
        setupViews()
    }

    private fun requirePickerView(): BaseDialogPickerView<*> {
        if (!this::pickerView.isInitialized)
            throw Exception("ButtonTriggerView is not attached to picker.")
        return pickerView
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ImageButtonTriggerView, 0, 0)
        try {
            hasDefinedBoxBackgroundAttribute = attrTypedArray.hasValue(R.styleable.ButtonTriggerView_btv_background_color)
            attrTypedArray.apply {
                getColor(R.styleable.ImageButtonTriggerView_ibtv_BackgroundColor, -1).apply {
                    buttonBackgroundColor = when (this) {
                        -1 -> Color.TRANSPARENT
                        else -> this
                    }
                }
                getResourceId(
                    R.styleable.ImageButtonTriggerView_ibtv_ErrorTextAppearance,
                    R.style.PickerViewErrorTextAppearance
                ).apply {
                    errorAppearance = this
                }
                getColor(R.styleable.ImageButtonTriggerView_ibtv_StartIconColor, -1).apply {
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

    private fun initView() {
        isSaveEnabled = true
        renderLayout()
        setupViews()
    }

    private fun renderLayout() {
        inflate(context, R.layout.dialog_picker_image_button_layout, this) as LinearLayout
        iconButtonView = findViewById(R.id.picker_view_image_button)
        errorTextView = findViewById(R.id.picker_view_error_text_view)
    }

    private fun setupViews() {
        setupBackground()
        setupStartIcon()
        setupErrorAppearance()
    }

    private fun setupBackground() {
        if (hasDefinedBoxBackgroundAttribute && buttonBackgroundColor == Color.TRANSPARENT)
            iconButtonView.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun setupStartIcon() {
        if (pickerIcon != null) pickerIcon!!.setTint(pickerStartIconColor)
        iconButtonView.setImageDrawable(pickerIcon)
    }

    private fun setupErrorAppearance() {
        errorTextView.setTextAppearance(errorAppearance)
    }

    private class SavedState : BaseSavedState {
        var pickerBackgroundColor = -1
        var errorAppearance = -1
        var pickerStartIconColor = -1
        var textColor = -1

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            pickerBackgroundColor = parcel.readInt()
            errorAppearance = parcel.readInt()
            pickerStartIconColor = parcel.readInt()
            textColor = parcel.readInt()
        }

        @Override
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(pickerBackgroundColor)
            out.writeInt(errorAppearance)
            out.writeInt(pickerStartIconColor)
            out.writeInt(textColor)
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