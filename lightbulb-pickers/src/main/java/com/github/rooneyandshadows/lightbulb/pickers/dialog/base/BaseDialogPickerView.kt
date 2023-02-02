package com.github.rooneyandshadows.lightbulb.pickers.dialog.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.Log
import android.util.SparseArray
import android.view.ContextThemeWrapper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogAnimationTypes
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogAnimationTypes.NO_ANIMATION
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogButtonConfiguration
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogTypes
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogTypes.NORMAL
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.callbacks.DialogButtonClickListener
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.callbacks.DialogCancelListener
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.base.DialogTriggerView

@Suppress("unused", "MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")
abstract class BaseDialogPickerView<SelectionType> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
    private lateinit var fragmentManager: FragmentManager
    private val validationCallbacks: MutableList<ValidationCheck<SelectionType>> = mutableListOf()
    private val selectionChangedListeners: MutableList<SelectionChangedListener<SelectionType>> = mutableListOf()
    private val triggerAttachedCallback: MutableList<TriggerAttachedCallback<SelectionType>> = mutableListOf()
    protected var dataBindingListener: SelectionChangedListener<SelectionType>? = null
    protected var triggerView: DialogTriggerView? = null
        private set
    var showSelectedTextValue = false
        protected set
    protected lateinit var pickerDialog: BasePickerDialogFragment<SelectionType>
        private set
    private var errorEnabled: Boolean = false
        set(value) = triggerView?.setErrorEnabled(field) ?: Unit
        get() = triggerView?.errorEnabled ?: false
    private var requiredText: String? = null
    private var isValidationEnabled = false
    private var required = false
    private var hintText: String? = null
    private var errorText: String? = null
    val isDialogShown: Boolean
        get() = pickerDialog.isDialogShown
    val text: String
        get() = triggerView?.text ?: ""
    val hasSelection: Boolean
        get() = pickerDialog.hasSelection()
    open var selection: SelectionType?
        set(value) = pickerDialog.setSelection(value)
        get() = pickerDialog.getSelection()
    protected abstract val viewText: String
    protected abstract fun initializeDialog(
        fragmentManager: FragmentManager,
        fragmentTag: String,
    ): BasePickerDialogFragment<SelectionType>

    protected abstract fun readAttributes(context: Context, attrs: AttributeSet?)

    protected open fun validate(): Boolean {
        var isValid = true
        if (isValidationEnabled) {
            if (required && !hasSelection) {
                errorEnabled = true
                errorText = requiredText
                return false
            }
            validationCallbacks.forEach {
                isValid = isValid and it.validate(selection)
                if (!isValid) return@forEach
            }
        }
        if (!isValid) errorEnabled = true
        else {
            errorEnabled = false
            errorText = null
        }
        return isValid
    }

    protected open fun onDialogInitialized(dialog: BasePickerDialogFragment<SelectionType>) {
        dialog.apply {
            addOnPositiveClickListener(object : DialogButtonClickListener {
                override fun doOnClick(buttonView: View?, dialogFragment: BaseDialogFragment) {
                    updateTextAndValidate()
                }
            })
            addOnNegativeClickListeners(object : DialogButtonClickListener {
                override fun doOnClick(buttonView: View?, dialogFragment: BaseDialogFragment) {
                    updateTextAndValidate()
                }
            })
            addOnCancelListener(object : DialogCancelListener {
                override fun doOnCancel(dialogFragment: BaseDialogFragment) {
                    updateTextAndValidate()
                }
            })
            addOnSelectionChangedListener(object : BasePickerDialogFragment.SelectionChangedListener<SelectionType> {
                override fun onSelectionChanged(
                    dialog: BasePickerDialogFragment<SelectionType>,
                    newValue: SelectionType?,
                    oldValue: SelectionType?,
                ) {
                    updateTextAndValidate()
                    dispatchSelectionChangedEvents(newValue, oldValue)
                }
            })
        }
    }

    init {
        isSaveEnabled = true
        orientation = VERTICAL
        if (!isInEditMode) fragmentManager = getFragmentManager(context)!!
        readAttrs(context, attrs)
    }


    @Override
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        triggerView?.isEnabled = enabled
    }

    @Override
    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        addViewInternally(child, index, params)
    }

    fun addSelectionChangedListener(listener: SelectionChangedListener<SelectionType>) {
        selectionChangedListeners.add(listener)
    }

    fun removeSelectionChangedListener(listener: SelectionChangedListener<SelectionType>) {
        selectionChangedListeners.add(listener)
    }

    fun removeAllSelectionChangedListeners() {
        selectionChangedListeners.clear()
    }

    fun addValidationCheck(validationCheck: ValidationCheck<SelectionType>) {
        validationCallbacks.add(validationCheck)
    }

    fun removeValidationCheck(validationCheck: ValidationCheck<SelectionType>) {
        validationCallbacks.remove(validationCheck)
    }

    fun removeAllValidationChecks() {
        validationCallbacks.clear()
    }

    fun addOnTriggerAttachedListener(callback: TriggerAttachedCallback<SelectionType>) {
        triggerAttachedCallback.add(callback)
    }

    fun showPickerDialog() {
        pickerDialog.show()
    }


    fun setDialogTag(dialogTag: String) {
        pickerDialog.setDialogTag(dialogTag)
    }

    fun setDialogTitle(dialogTitle: String?) {
        pickerDialog.setDialogTitle(dialogTitle)
    }

    fun setDialogMessage(dialogMessage: String?) {
        pickerDialog.setDialogMessage(dialogMessage)
    }

    fun setDialogPositiveButtonText(buttonText: String?) {
        pickerDialog.setDialogPositiveButton(generateButtonConfig(buttonText))
    }

    fun setDialogNegativeButtonText(buttonText: String?) {
        pickerDialog.setDialogNegativeButton(generateButtonConfig(buttonText))
    }

    fun setDialogCancelable(isCancelable: Boolean) {
        pickerDialog.isCancelable = isCancelable
    }

    fun setDialogType(dialogType: DialogTypes) {
        pickerDialog.dialogType = dialogType
    }

    fun setDialogAnimationType(dialogAnimationType: DialogAnimationTypes) {
        pickerDialog.dialogAnimationType = dialogAnimationType
    }

    fun setHintText(hintText: String?) {
        this.hintText = hintText
        if (!showSelectedTextValue) return
        triggerView?.setHintText(hintText)
    }

    fun setErrorText(errorText: String?) {
        this.errorText = errorText
        triggerView?.setErrorText(errorText)
    }

    fun getDialogTag(): String? {
        return pickerDialog.tag
    }

    fun getDialogTitle(): String? {
        return pickerDialog.dialogTitle
    }

    fun getDialogMessage(): String? {
        return pickerDialog.dialogMessage
    }

    fun isDialogCancelable(): Boolean {
        return pickerDialog.isCancelable
    }

    fun getDialogType(): DialogTypes {
        return pickerDialog.dialogType
    }

    fun getDialogAnimationType(): DialogAnimationTypes {
        return pickerDialog.dialogAnimationType
    }

    fun getHintText(): String? {
        return triggerView?.hintText
    }

    fun getErrorText(): String? {
        return triggerView?.errorText
    }

    @JvmOverloads
    fun setRequired(required: Boolean, validateOnCall: Boolean = true) {
        this.required = required
        if (validateOnCall) validate()
    }

    fun setValidationEnabled(newValue: Boolean) {
        isValidationEnabled = newValue
        validate()
    }

    fun setRequiredText(text: String) {
        requiredText = text
    }

    @JvmOverloads
    fun setPickerIcon(icon: Drawable?, color: Int = -1) {
        triggerView?.setIcon(icon, color)
    }

    protected fun updateTextAndValidate() {
        updateText()
        validate()
    }

    protected fun setShowSelectionText(showSelectedTextValue: Boolean) {
        this.showSelectedTextValue = showSelectedTextValue
        invalidate()
    }

    private fun getFragmentManager(context: Context?): FragmentManager? {
        return when (context) {
            is AppCompatActivity -> context.supportFragmentManager
            is ContextThemeWrapper -> getFragmentManager(context.baseContext)
            else -> null
        }
    }

    private fun updateText() {
        if (!showSelectedTextValue) return
        val newTextValue = viewText
        triggerView?.setText(newTextValue)
    }

    private fun addViewInternally(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child !is DialogTriggerView) {
            Log.w(
                BaseDialogPickerView::class.java.name,
                "Picker view child is ignored. All child views must implement com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.base.DialogTriggerView"
            )
            return
        }
        removeAllViews()
        triggerView = child
        super.addView(child, index, params)
        triggerView!!.apply {
            isEnabled = this@BaseDialogPickerView.isEnabled
            attachTo(this@BaseDialogPickerView)
            triggerAttachedCallback.forEach {
                it.onAttached(this, this@BaseDialogPickerView)
            }
        }
    }

    private fun generateButtonConfig(buttonText: String?): DialogButtonConfiguration? {
        if (buttonText.isNullOrBlank()) return null
        return DialogButtonConfiguration(buttonTitle = buttonText, buttonEnabled = true, closeDialogOnClick = true)
    }

    private fun readAttrs(context: Context, attrs: AttributeSet?) {
        readBaseAttributes(context, attrs)
        readAttributes(context, attrs)
    }

    private fun readBaseAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.BaseDialogPickerView, 0, 0)
        try {
            attrTypedArray.apply {
                getString(R.styleable.BaseDialogPickerView_pv_dialog_tag).apply {
                    val dialogTag = let {
                        val default = ResourceUtils.getPhrase(context, R.string.picker_default_dialog_tag_text)
                        return@let if (it.isNullOrBlank()) default else it
                    }
                    pickerDialog = initializeDialog(fragmentManager, dialogTag)
                    onDialogInitialized(pickerDialog)
                }
                getString(R.styleable.BaseDialogPickerView_pv_dialog_title).apply {
                    val default = ""
                    pickerDialog.setDialogTitle(this ?: default)
                }
                getString(R.styleable.BaseDialogPickerView_pv_dialog_message).apply {
                    val default = ""
                    pickerDialog.setDialogMessage(this ?: default)
                }

                getString(R.styleable.BaseDialogPickerView_pv_required_text).apply {
                    val default = ResourceUtils.getPhrase(context, R.string.picker_default_required_text)
                    requiredText = this ?: default
                }
                getString(R.styleable.BaseDialogPickerView_pv_dialog_button_positive_text).apply {
                    val positiveButtonText = let {
                        val default = ResourceUtils.getPhrase(context, R.string.picker_default_positive_button_text)
                        return@let if (it.isNullOrBlank()) default else it
                    }
                    pickerDialog.setDialogPositiveButton(generateButtonConfig(positiveButtonText))
                }
                getString(R.styleable.BaseDialogPickerView_pv_dialog_button_negative_text).apply {
                    val negativeButtonText = let {
                        val default = ResourceUtils.getPhrase(context, R.string.picker_default_negative_button_text)
                        return@let if (it.isNullOrBlank()) default else it
                    }
                    pickerDialog.setDialogNegativeButton(generateButtonConfig(negativeButtonText))
                }
                getInt(R.styleable.BaseDialogPickerView_pv_dialog_type, NORMAL.value).apply {
                    pickerDialog.dialogType = DialogTypes.valueOf(this)
                }
                getInt(R.styleable.BaseDialogPickerView_pv_dialog_animation, NO_ANIMATION.value).apply {
                    pickerDialog.dialogAnimationType = DialogAnimationTypes.valueOf(this)
                }
                pickerDialog.isCancelable = getBoolean(R.styleable.BaseDialogPickerView_pv_dialog_cancelable, true)
                required = getBoolean(R.styleable.BaseDialogPickerView_pv_required, false)
                isValidationEnabled = getBoolean(R.styleable.BaseDialogPickerView_pv_validation_enabled, false)
                showSelectedTextValue = getBoolean(R.styleable.BaseDialogPickerView_pv_show_selected_text, true)
            }
        } finally {
            attrTypedArray.recycle()
        }
    }

    private fun dispatchSelectionChangedEvents(newValue: SelectionType?, oldValue: SelectionType?) {
        selectionChangedListeners.forEach {
            it.execute(newValue, oldValue)
        }
        dataBindingListener?.execute(newValue, oldValue)
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
    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val myState = SavedState(superState)
        myState.apply {
            val view = this@BaseDialogPickerView
            isDialogShown = view.isDialogShown
            pickerRequiredText = view.requiredText
            pickerIsRequired = view.required
            pickerIsValidationEnabled = view.isValidationEnabled
            pickerShowSelectedTextValue = view.showSelectedTextValue
            dialogState = pickerDialog.saveDialogState()
        }
        return myState
    }

    @Override
    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        requiredText = savedState.pickerRequiredText
        required = savedState.pickerIsRequired
        isValidationEnabled = savedState.pickerIsValidationEnabled
        showSelectedTextValue = savedState.pickerShowSelectedTextValue
        pickerDialog.restoreDialogState(savedState.dialogState)
        updateTextAndValidate()
    }

    interface SelectionChangedListener<SelectionType> {
        fun execute(newSelection: SelectionType?, oldSelection: SelectionType?)
    }

    interface ValidationCheck<SelectionType> {
        fun validate(currentSelection: SelectionType?): Boolean
    }

    interface TriggerAttachedCallback<SelectionType> {
        fun onAttached(triggerView: DialogTriggerView, pickerView: BaseDialogPickerView<SelectionType>)
    }

    private class SavedState : BaseSavedState {
        var isDialogShown = false
        var pickerRequiredText: String? = null
        var pickerIsRequired = false
        var pickerIsValidationEnabled = false
        var pickerShowSelectedTextValue = false
        var dialogState: Bundle? = null

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            parcel.apply {
                isDialogShown = ParcelUtils.readBoolean(this)!!
                pickerIsRequired = ParcelUtils.readBoolean(this)!!
                pickerIsValidationEnabled = ParcelUtils.readBoolean(this)!!
                pickerShowSelectedTextValue = ParcelUtils.readBoolean(this)!!
                pickerRequiredText = ParcelUtils.readString(this)
                dialogState = readBundle(Bundle::class.java.classLoader)
            }
        }

        @Override
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.apply {
                ParcelUtils.writeBoolean(this, isDialogShown)
                    .writeBoolean(this, pickerIsRequired)
                    .writeBoolean(this, pickerIsValidationEnabled)
                    .writeBoolean(this, pickerShowSelectedTextValue)
                    .writeString(this, pickerRequiredText)
                writeBundle(dialogState)
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