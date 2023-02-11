package com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.postDelayed
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView

@Suppress("MemberVisibilityCanBePrivate")
abstract class DialogTriggerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    protected lateinit var pickerView: BaseDialogPickerView<*>
    private var needToSyncUiAfterRestore = false
    var icon: Drawable? = null
        private set
    var errorEnabled: Boolean = false
        private set
    var text: String? = ""
        private set
    var hintText: String? = ""
        private set
    var errorText: String? = ""
        private set
    var iconColor: Int = -1
        private set
    var errorTextAppearance = 0
        private set
    var hintTextAppearance = 0
        private set
    protected val defaultIconColor: Int by lazy {
        initializeDefaultIconColor()
    }

    protected abstract fun onIconChange()
    protected abstract fun onIconColorChange()
    protected abstract fun onErrorEnabledChange()
    protected abstract fun onTextChange()
    protected abstract fun onHintTextChange()
    protected abstract fun onErrorTextChange()
    protected abstract fun onErrorTextAppearanceChange()
    protected abstract fun onHintTextAppearanceChange()
    protected abstract fun onEnabledChange()
    abstract fun attachTo(pickerView: BaseDialogPickerView<*>)

    protected open fun initializeDefaultIconColor(): Int {
        return ResourceUtils.getColorByAttribute(
            context,
            R.attr.colorPrimary
        )
    }

    init {
        isSaveEnabled = true
        readBaseAttributes(context, attrs)
    }

    @Override
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        syncUserInterface()
    }

    @Override
    override fun isEnabled(): Boolean {
        return super.isEnabled()
    }

    @Override
    override fun setEnabled(enabled: Boolean) {
        if (this.isEnabled == enabled) return
        super.setEnabled(enabled)
        onEnabledChange()
    }

    @JvmOverloads
    fun setIcon(icon: Drawable?, iconColor: Int = -1) {
        this.iconColor = iconColor
        this.icon = icon?.apply {
            val color = if (iconColor == -1) defaultIconColor else iconColor
            setTint(color)
        }
        onIconChange()
    }

    fun setErrorEnabled(errorEnabled: Boolean) {
        if (this.errorEnabled == errorEnabled) return
        this.errorEnabled = errorEnabled
        onErrorEnabledChange()
    }

    fun setText(text: String?) {
        if (this.text == text)
            return
        this.text = text
        onTextChange()
    }

    fun setHintText(hintText: String?) {
        if (this.hintText == hintText) return
        this.hintText = hintText
        onHintTextChange()
    }

    fun setErrorText(errorText: String?) {
        if (this.errorText == errorText) return
        this.errorText = errorText
        onErrorTextChange()
    }

    fun setIconColor(iconColor: Int) {
        if (this.iconColor == iconColor) return
        this.iconColor = iconColor
        onIconColorChange()
    }

    fun setErrorTextAppearance(errorTextAppearance: Int) {
        if (this.errorTextAppearance == errorTextAppearance) return
        this.errorTextAppearance = errorTextAppearance
        onErrorTextAppearanceChange()
    }

    fun setHintTextAppearance(hintTextAppearance: Int) {
        if (this.hintTextAppearance == hintTextAppearance) return
        this.hintTextAppearance = hintTextAppearance
        onHintTextAppearanceChange()
    }

    fun requirePickerView(): BaseDialogPickerView<*> {
        if (!this::pickerView.isInitialized)
            throw Exception("ButtonTriggerView is not attached to picker.")
        return pickerView
    }

    protected fun syncUserInterface() {
        onIconChange()
        onIconColorChange()
        onErrorEnabledChange()
        onTextChange()
        onHintTextChange()
        onErrorTextChange()
        onErrorTextAppearanceChange()
        onHintTextAppearanceChange()
        onEnabledChange()
    }

    private fun readBaseAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialogTriggerView, 0, 0)
        try {
            attrTypedArray.apply {
                errorEnabled = getBoolean(R.styleable.DialogTriggerView_dtv_error_enabled, false)
                getString(R.styleable.DialogTriggerView_dtv_hint_text).apply {
                    val default = ""
                    hintText = this ?: default
                }
                getResourceId(
                    R.styleable.DialogTriggerView_dtv_error_text_appearance,
                    R.style.PickerViewErrorTextAppearance
                ).apply {
                    errorTextAppearance = this
                }
                getResourceId(
                    R.styleable.DialogTriggerView_dtv_hint_text_appearance,
                    R.style.PickerViewHintTextAppearance
                ).apply {
                    hintTextAppearance = this
                }
            }
        } finally {
            attrTypedArray.recycle()
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
    public override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.apply {
            val view = this@DialogTriggerView
            errorEnabled = view.errorEnabled
            text = view.text
            hintText = view.hintText
            errorText = view.errorText
            errorTextAppearance = view.errorTextAppearance
            hintTextAppearance = view.hintTextAppearance
            iconColor = view.iconColor
        }
        return myState
    }

    @Override
    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        savedState.apply {
            needToSyncUiAfterRestore = true
            val view = this@DialogTriggerView
            view.errorEnabled = errorEnabled
            view.text = text
            view.hintText = hintText
            view.errorText = errorText
            view.errorTextAppearance = errorTextAppearance
            view.hintTextAppearance = hintTextAppearance
            view.iconColor = iconColor
        }
    }

    @Override
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (needToSyncUiAfterRestore) {
            needToSyncUiAfterRestore = false
            syncUserInterface()
        }
    }

    private class SavedState : BaseSavedState {
        var errorEnabled: Boolean = false
        var text: String? = null
        var hintText: String? = null
        var errorText: String? = null
        var errorTextAppearance: Int = -1
        var hintTextAppearance: Int = -1
        var iconColor: Int = -1

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            parcel.apply {
                errorEnabled = ParcelUtils.readBoolean(this)!!
                text = ParcelUtils.readString(this)
                hintText = ParcelUtils.readString(this)
                errorText = ParcelUtils.readString(this)
                errorTextAppearance = ParcelUtils.readInt(this)!!
                hintTextAppearance = ParcelUtils.readInt(this)!!
                iconColor = ParcelUtils.readInt(this)!!
            }
        }

        @Override
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.apply {
                ParcelUtils.writeBoolean(this, errorEnabled)
                    .writeString(this, text)
                    .writeString(this, hintText)
                    .writeString(this, errorText)
                    .writeInt(this, errorTextAppearance)
                    .writeInt(this, hintTextAppearance)
                    .writeInt(this, iconColor)
            }
        }

        @Override
        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}