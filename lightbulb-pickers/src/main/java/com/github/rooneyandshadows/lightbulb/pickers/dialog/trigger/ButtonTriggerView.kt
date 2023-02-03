package com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.base.DialogTriggerView
import com.google.android.material.button.MaterialButton

@Suppress("MemberVisibilityCanBePrivate")
class ButtonTriggerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : DialogTriggerView(context, attrs, defStyleAttr, defStyleRes) {
    private lateinit var buttonView: MaterialButton
    private lateinit var errorTextView: AppCompatTextView
    var buttonBackgroundColor: Int = -1
        private set
    var buttonTextColor: Int = -1
        private set
    var buttonBackgroundCornerRadius = 0
        private set

    init {
        isSaveEnabled = true
        readAttributes(context, attrs)
        inflateView()
        syncUserInterface()
        setupBackground()
        setupCornerRadius()
        setupButtonTextColor()
    }

    @Override
    override fun initializeDefaultIconColor(): Int {
        return ResourceUtils.getColorByAttribute(
            context,
            R.attr.colorOnPrimary
        )
    }


    @Override
    override fun onIconChange() {
        buttonView.apply {
            iconTint = ColorStateList(arrayOf(intArrayOf()), intArrayOf(iconColor))
            icon = this@ButtonTriggerView.icon
        }
    }

    @Override
    override fun onIconColorChange() {
        iconColor.apply color@{
            buttonView.apply {
                val colorToSet = if (this@color == -1) defaultIconColor else this@color
                iconTint = ColorStateList(arrayOf(intArrayOf()), intArrayOf(colorToSet))
            }
        }
    }

    @Override
    override fun onErrorEnabledChange() {
        errorTextView.apply {
            visibility = if (errorEnabled) VISIBLE else GONE
        }
    }

    @Override
    override fun onTextChange() {
        buttonView.apply {
            text = this@ButtonTriggerView.text
        }
    }

    @Override
    override fun onHintTextChange() {
        buttonView.apply {
            text = hintText
        }
    }

    @Override
    override fun onErrorTextChange() {
        errorTextView.apply {
            text = errorText
        }
    }

    @Override
    override fun onErrorTextAppearanceChange() {
        errorTextView.apply {
            setTextAppearance(errorTextAppearance)
        }
    }

    @Override
    override fun onHintTextAppearanceChange() {
        //NOT SUPPORTED
    }

    @Override
    override fun onEnabledChange() {
        buttonView.isEnabled = isEnabled
    }

    @Override
    override fun attachTo(pickerView: BaseDialogPickerView<*>) {
        this.pickerView = pickerView
        buttonView.setOnClickListener { requirePickerView().showPickerDialog() }
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
        myState.buttonBackgroundColor = buttonBackgroundColor
        myState.buttonTextColor = buttonTextColor
        myState.buttonBackgroundCornerRadius = buttonBackgroundCornerRadius
        return myState
    }

    @Override
    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        setButtonBackgroundColor(savedState.buttonBackgroundColor)
        setButtonBackgroundCornerRadius(savedState.buttonBackgroundCornerRadius)
        setButtonTextColor(savedState.buttonTextColor)
    }

    fun setButtonBackgroundColor(backgroundColor: Int) {
        buttonBackgroundColor = backgroundColor
        setupBackground()
    }

    fun setButtonTextColor(textColor: Int) {
        this.buttonTextColor = textColor
        setupButtonTextColor()
    }

    fun setButtonBackgroundCornerRadius(cornerRadius: Int) {
        this.buttonBackgroundCornerRadius = cornerRadius
        setupCornerRadius()
    }

    private fun setupBackground() {
        buttonView.apply {
            if (buttonBackgroundColor == -1) setBackgroundColor(Color.TRANSPARENT)
            else setBackgroundColor(buttonBackgroundColor)
        }
    }

    private fun setupButtonTextColor() {
        buttonView.apply {
            setTextColor(buttonTextColor)
        }
    }

    private fun setupCornerRadius() {
        buttonView.cornerRadius = buttonBackgroundCornerRadius
    }

    private fun inflateView() {
        inflate(context, R.layout.dialog_picker_button_layout, this) as LinearLayout
        buttonView = findViewById(R.id.picker_view_button)
        errorTextView = findViewById(R.id.picker_view_error_text_view)
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ButtonTriggerView, 0, 0)
        try {
            attrTypedArray.apply {
                val defBackgroundColor = ResourceUtils.getColorByAttribute(context, R.attr.colorPrimary)
                val defTextColor = ResourceUtils.getColorByAttribute(context, R.attr.colorOnPrimary)
                getColor(R.styleable.ButtonTriggerView_btv_background_color, defBackgroundColor).apply {
                    buttonBackgroundColor = this
                }
                getColor(R.styleable.ButtonTriggerView_btv_text_color, defBackgroundColor).apply {
                    buttonTextColor = defTextColor
                }
                getInt(R.styleable.ButtonTriggerView_btv_background_corner_radius, 5).apply {
                    buttonBackgroundCornerRadius = ResourceUtils.dpToPx(this)
                }
            }
        } finally {
            attrTypedArray.recycle()
        }
    }

    private class SavedState : BaseSavedState {
        var buttonTextColor = -1
        var buttonBackgroundColor = -1
        var buttonBackgroundCornerRadius = -1

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            parcel.apply {
                buttonTextColor = ParcelUtils.readInt(this)!!
                buttonBackgroundColor = ParcelUtils.readInt(this)!!
                buttonBackgroundCornerRadius = ParcelUtils.readInt(this)!!
            }
        }

        @Override
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.apply {
                ParcelUtils.writeInt(this, buttonTextColor)
                ParcelUtils.writeInt(this, buttonBackgroundColor)
                ParcelUtils.writeInt(this, buttonBackgroundCornerRadius)
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
}