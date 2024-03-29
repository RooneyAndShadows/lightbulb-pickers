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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.FragmentManager
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogBuilder
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment.Buttons.Companion.cancelSelectionButton
import com.github.rooneyandshadows.lightbulb.dialogs.base.BasePickerDialogFragment.Buttons.Companion.confirmSelectionButton
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogAnimationTypes
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogAnimationTypes.NO_ANIMATION
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogButton
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogTypes
import com.github.rooneyandshadows.lightbulb.dialogs.base.internal.DialogTypes.NORMAL
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView.ButtonTypes.NEGATIVE
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView.ButtonTypes.POSITIVE
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.base.DialogTriggerView

@Suppress("unused", "MemberVisibilityCanBePrivate")
@JvmSuppressWildcards
abstract class BaseDialogPickerView<SelectionType> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    private lateinit var fragmentManager: FragmentManager
    private lateinit var dialogTag: String
    private val validationCallbacks: MutableList<ValidationCheck<SelectionType>> = mutableListOf()
    private val selectionChangedListeners: MutableList<SelectionChangedListener<SelectionType>> = mutableListOf()
    private val triggerAttachedCallback: MutableList<TriggerAttachedCallback<SelectionType>> = mutableListOf()
    private val onDialogReadyListeners: MutableList<DialogReadyListener<SelectionType>> = mutableListOf()
    private val dialogInitializer = lazy {
        return@lazy getDialogBuilder(fragmentManager, dialogTag).buildDialog().apply dialog@{
            onDialogReadyListeners.apply {
                forEach { it.execute(this@dialog) }
                clear()
            }
        }
    }
    private val pickerDialog: BasePickerDialogFragment<SelectionType> by dialogInitializer
    private var positiveButtonText: String = ""
    private var negativeButtonText: String = ""
    protected var dataBindingListener: SelectionChangedListener<SelectionType>? = null
    protected var triggerView: DialogTriggerView? = null
        private set
    var showSelectedTextValue = false
        protected set
    var requiredText: String? = null
    var isValidationEnabled = false
        private set
    var isRequired = false
        private set
    val isDialogShown: Boolean
        get() = pickerDialog.isDialogShown
    val text: String
        get() = triggerView?.text ?: ""
    val hasSelection: Boolean
        get() = pickerDialog.hasSelection()
    protected open val dialog: BasePickerDialogFragment<SelectionType>
        get() = pickerDialog
    open var selection: SelectionType?
        set(value) = pickerDialog.setSelection(value)
        get() = pickerDialog.getSelection()
    protected abstract val viewText: String

    protected abstract fun getDialogBuilder(
        fragmentManager: FragmentManager,
        dialogTag: String,
    ): BaseDialogBuilder<out BasePickerDialogFragment<SelectionType>>

    init {
        isSaveEnabled = true
        orientation = VERTICAL
        if (!isInEditMode) fragmentManager = getFragmentManager(context)!!
        readBaseAttributes(context, attrs)
        whenDialogReady { dialog ->
            dialog.apply {
                if (negativeButtonText.isNotBlank()) {
                    addButton(generateButtonConfig(NEGATIVE))
                }
                if (positiveButtonText.isNotBlank()) {
                    addButton(generateButtonConfig(POSITIVE))
                }
                addOnCancelListener { updateTextAndValidate() }
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
    }

    fun validate(): Boolean {
        var isValid = true
        if (isValidationEnabled) {
            if (isRequired && !hasSelection) {
                setErrorEnabled(true)
                setErrorText(requiredText)
                return false
            }
            validationCallbacks.forEach {
                isValid = isValid and it.validate(selection)
                if (!isValid) return@forEach
            }
        }
        if (!isValid) setErrorEnabled(true)
        else {
            setErrorEnabled(false)
            setErrorText(null)
        }
        return isValid
    }

    private fun setErrorEnabled(errorEnabled: Boolean) {
        triggerView?.setErrorEnabled(errorEnabled)
    }

    private fun getErrorEnabled(): Boolean {
        return triggerView?.errorEnabled ?: false
    }

    @Override
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        pickerDialog.apply {}//init dialog
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
        val addButton: Boolean = !buttonText.isNullOrBlank()
        positiveButtonText = buttonText ?: ""
        val button = generateButtonConfig(POSITIVE)
        if (addButton) pickerDialog.addButton(button)
        else pickerDialog.removeButton(button)
    }

    fun setDialogNegativeButtonText(buttonText: String?) {
        val addButton: Boolean = !buttonText.isNullOrBlank()
        negativeButtonText = buttonText ?: ""
        val button = generateButtonConfig(NEGATIVE)
        if (addButton) pickerDialog.addButton(button)
        else pickerDialog.removeButton(button)
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

    fun setErrorText(errorText: String?) {
        triggerView?.apply {
            post {
                setErrorText(errorText)
            }
        }
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

    fun isErrorEnabled(): Boolean {
        return triggerView?.errorEnabled ?: false
    }

    fun getErrorText(): String? {
        return triggerView?.errorText
    }

    @JvmOverloads
    fun setRequired(required: Boolean, validateOnCall: Boolean = true) {
        this.isRequired = required
        if (validateOnCall) validate()
    }

    fun setValidationEnabled(newValue: Boolean) {
        isValidationEnabled = newValue
        validate()
    }

    @JvmOverloads
    fun setPickerIcon(icon: Drawable?, color: Int = -1) {
        triggerView?.setIcon(icon, color)
    }

    protected fun whenDialogReady(action: DialogReadyListener<SelectionType>) {
        if (dialogInitializer.isInitialized()) {
            action.execute(pickerDialog)
            return
        }
        onDialogReadyListeners.add(action)
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
            setText(viewText)
            attachTo(this@BaseDialogPickerView)
            triggerAttachedCallback.forEach {
                it.onAttached(this, this@BaseDialogPickerView)
            }
        }
    }

    private fun generateButtonConfig(buttonType: ButtonTypes): DialogButton {
        return when (buttonType) {
            POSITIVE -> confirmSelectionButton(positiveButtonText) { _, _ -> validate() }
            NEGATIVE -> cancelSelectionButton(negativeButtonText) { _, _ -> validate() }
        }
    }

    private fun readBaseAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.BaseDialogPickerView, 0, 0)
        try {
            attrTypedArray.apply {
                getString(R.styleable.BaseDialogPickerView_pv_dialog_tag).apply {
                    dialogTag = let {
                        val default = ResourceUtils.getPhrase(context, R.string.picker_default_dialog_tag_text)
                        return@let if (it.isNullOrBlank()) default else it
                    }
                }
                getString(R.styleable.BaseDialogPickerView_pv_dialog_title).apply {
                    val default = ""
                    whenDialogReady {
                        it.setDialogTitle(this ?: default)
                    }
                }
                getString(R.styleable.BaseDialogPickerView_pv_dialog_message).apply {
                    val default = ""
                    whenDialogReady {
                        it.setDialogMessage(this ?: default)
                    }
                }
                getString(R.styleable.BaseDialogPickerView_pv_required_text).apply {
                    val default = ResourceUtils.getPhrase(context, R.string.picker_default_required_text)
                    requiredText = this ?: default
                }
                getString(R.styleable.BaseDialogPickerView_pv_dialog_button_positive_text).apply {
                    positiveButtonText = let {
                        val default = ResourceUtils.getPhrase(context, R.string.picker_default_positive_button_text)
                        return@let if (it.isNullOrBlank()) default else it
                    }
                }
                getString(R.styleable.BaseDialogPickerView_pv_dialog_button_negative_text).apply {
                    negativeButtonText = let {
                        val default = ResourceUtils.getPhrase(context, R.string.picker_default_negative_button_text)
                        return@let if (it.isNullOrBlank()) default else it
                    }
                }
                getInt(R.styleable.BaseDialogPickerView_pv_dialog_type, NORMAL.value).apply {
                    whenDialogReady {
                        it.dialogType = DialogTypes.valueOf(this)
                    }
                }
                getInt(R.styleable.BaseDialogPickerView_pv_dialog_animation, NO_ANIMATION.value).apply {
                    whenDialogReady {
                        it.dialogAnimationType = DialogAnimationTypes.valueOf(this)
                    }
                }
                getBoolean(R.styleable.BaseDialogPickerView_pv_dialog_cancelable, true).apply {
                    whenDialogReady {
                        it.isCancelable = this
                    }
                }
                isRequired = getBoolean(R.styleable.BaseDialogPickerView_pv_required, false)
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
            requiredText = view.requiredText
            isRequired = view.isRequired
            isValidationEnabled = view.isValidationEnabled
            showSelectedTextValue = view.showSelectedTextValue
            dialogState = pickerDialog.saveDialogState()
            triggerState = triggerView?.onSaveInstanceState()
        }
        return myState
    }

    @Override
    public override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        state.apply {
            val view = this@BaseDialogPickerView
            view.requiredText = requiredText
            view.isRequired = isRequired
            view.isValidationEnabled = isValidationEnabled
            view.showSelectedTextValue = showSelectedTextValue
            view.pickerDialog.restoreDialogState(dialogState)
            triggerState?.apply {
                view.triggerView?.onRestoreInstanceState(this)
            }
        }
    }

    fun interface SelectionChangedListener<SelectionType> {
        fun execute(newSelection: SelectionType?, oldSelection: SelectionType?)
    }

    fun interface ValidationCheck<SelectionType> {
        fun validate(currentSelection: SelectionType?): Boolean
    }

    fun interface TriggerAttachedCallback<SelectionType> {
        fun onAttached(triggerView: DialogTriggerView, pickerView: BaseDialogPickerView<SelectionType>)
    }

    fun interface DialogReadyListener<SelectionType> {
        fun execute(dialog: BasePickerDialogFragment<SelectionType>)
    }

    private class SavedState : BaseSavedState {
        var positiveButtonText: String = ""
        var negativeButtonText: String = ""
        var isDialogShown = false
        var requiredText: String? = null
        var isRequired = false
        var isValidationEnabled = false
        var showSelectedTextValue = false
        var dialogState: Bundle? = null
        var triggerState: Parcelable? = null

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            parcel.apply {
                positiveButtonText = ParcelUtils.readString(this)!!
                negativeButtonText = ParcelUtils.readString(this)!!
                isDialogShown = ParcelUtils.readBoolean(this)!!
                isRequired = ParcelUtils.readBoolean(this)!!
                isValidationEnabled = ParcelUtils.readBoolean(this)!!
                showSelectedTextValue = ParcelUtils.readBoolean(this)!!
                requiredText = ParcelUtils.readString(this)
                dialogState = ParcelUtils.readParcelable(this, Bundle::class.java)
                triggerState = ParcelUtils.readParcelable(this, Parcelable::class.java)
            }
        }

        @Override
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            super.writeToParcel(parcel, flags)
            parcel.apply {
                ParcelUtils.writeBoolean(this, isDialogShown)
                    .writeString(this, positiveButtonText)
                    .writeString(this, negativeButtonText)
                    .writeBoolean(this, isRequired)
                    .writeBoolean(this, isValidationEnabled)
                    .writeBoolean(this, showSelectedTextValue)
                    .writeString(this, requiredText)
                    .writeParcelable(this, dialogState)
                    .writeParcelable(this, triggerState)
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

    private enum class ButtonTypes(val value: Int) {
        POSITIVE(0),
        NEGATIVE(1)
    }
}