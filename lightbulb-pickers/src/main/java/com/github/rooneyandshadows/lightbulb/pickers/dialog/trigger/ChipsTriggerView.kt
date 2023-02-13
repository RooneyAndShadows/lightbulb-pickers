package com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.View.MeasureSpec.*
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.graphics.ColorUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.DrawableUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ParcelUtils
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.R
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.base.DialogTriggerView
import com.google.android.material.color.MaterialColors
import com.nex3z.flowlayout.FlowLayout
import kotlin.math.min

@Suppress("MemberVisibilityCanBePrivate", "unused")
class ChipsTriggerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : DialogTriggerView(context, attrs, defStyleAttr) {
    private lateinit var flowLayoutContainer: RelativeLayout
    private lateinit var flowLayout: FlowLayout
    private lateinit var errorTextView: AppCompatTextView
    private lateinit var hintTextView: AppCompatTextView
    private lateinit var titleTextView: AppCompatTextView
    private var nMoreItemsFormat: String = ""
    private var titleTextAppearance: Int = -1
    private var title: String = ""
    private var maxRows = DEFAULT_MAX_ROWS
    private val emptyLayoutHeight by lazy {
        return@lazy inflateChip("Chip").let {
            val widthMeasureSpec = makeMeasureSpec(0, UNSPECIFIED)
            val heightMeasureSpec = makeMeasureSpec(0, UNSPECIFIED)
            it.measure(widthMeasureSpec, heightMeasureSpec)
            return@let it.measuredHeight + flowLayout.paddingTop + flowLayout.paddingBottom
        }
    }
    var hintText: String? = ""
        private set
    var hintTextAppearance = 0
        private set

    companion object {
        private const val DEFAULT_MAX_ROWS = 2
    }

    init {
        isSaveEnabled = true
        readAttributes(context, attrs)
        inflateView()
        initView()
        setupHintTextAppearance()
        setupHintText()
    }

    @Override
    override fun isClickable(): Boolean {
        return true
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
        //Not supported
    }

    @Override
    override fun onIconColorChange() {
        //Not supported
    }

    @Override
    override fun onErrorEnabledChange() {
        errorTextView.apply {
            visibility = if (errorEnabled) VISIBLE else GONE
        }
    }

    @Override
    override fun onTextChange() {
        syncChips()
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
    override fun onEnabledChange() {
        // buttonView.isEnabled = isEnabled
    }

    @Override
    override fun attachTo(pickerView: BaseDialogPickerView<*>) {
        this.pickerView = pickerView
        flowLayoutContainer.apply {
            setOnClickListener {
                requirePickerView().showPickerDialog()
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
        myState.title = title
        myState.nMoreFormat = nMoreItemsFormat
        myState.titleTextAppearance = titleTextAppearance
        myState.maxRows = maxRows
        myState.hintText = hintText
        myState.hintTextAppearance = hintTextAppearance
        return myState
    }

    @Override
    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        title = savedState.title
        nMoreItemsFormat = savedState.nMoreFormat
        titleTextAppearance = savedState.titleTextAppearance
        maxRows = savedState.maxRows
        setHintTextAppearance(savedState.hintTextAppearance)
        setHintText(savedState.hintText)
        initView()
    }

    fun setHintTextAppearance(hintTextAppearance: Int) {
        if (this.hintTextAppearance == hintTextAppearance) return
        this.hintTextAppearance = hintTextAppearance
        setupHintTextAppearance()
    }

    fun setHintText(hintText: String?) {
        if (this.hintText == hintText) return
        this.hintText = hintText
        setupHintText()
    }

    fun setTitleTextAppearance(textAppearance: Int) {
        this.titleTextAppearance = textAppearance
        this.titleTextView.setTextAppearance(textAppearance)
    }

    fun setTitle(title: String?) {
        this.title = title ?: ""
        if (this.title.isBlank()) {
            titleTextView.visibility = GONE
            return
        }
        titleTextView.text = this.title
    }

    private fun inflateView() {
        orientation = VERTICAL
        inflate(context, R.layout.dialog_picker_chips_layout, this) as LinearLayoutCompat
        flowLayoutContainer = findViewById(R.id.flowLayoutContainer)
        flowLayout = findViewById(R.id.chipsContainer)
        errorTextView = findViewById(R.id.picker_view_error_text_view)
        hintTextView = findViewById(R.id.picker_view_hint_text_view)
        titleTextView = findViewById(R.id.picker_view_title_text_view)
    }

    private fun initView() {
        setupTextView()
        setupFlowLayoutContainer()
    }

    private fun setupTextView() {
        titleTextView.visibility = if (title.isBlank()) GONE else VISIBLE
        titleTextView.text = title
    }

    private fun setupFlowLayoutContainer() {
        val boxBackgroundColor = ColorUtils.setAlphaComponent(
            ResourceUtils.getColorByAttribute(
                context,
                com.github.rooneyandshadows.lightbulb.textinputview.R.attr.colorOnSurface
            ), 30
        )
        val surfaceLayerColor = MaterialColors.getColor(this, R.attr.colorSurface, Color.TRANSPARENT)
        val fgColor = MaterialColors.layer(surfaceLayerColor, boxBackgroundColor)
        val radius = ResourceUtils.getDimenPxById(context, R.dimen.trigger_view_chips_corner_radius)
        val strokeWidth = ResourceUtils.dpToPx(1)
        val strokeColor = ColorUtils.setAlphaComponent(
            ResourceUtils.getColorByAttribute(
                context,
                com.github.rooneyandshadows.lightbulb.textinputview.R.attr.colorOnSurface
            ), 140
        )
        flowLayoutContainer.background = DrawableUtils.getRoundedBorderedShape(
            fgColor,
            strokeColor,
            0,
            0,
            0,
            strokeWidth,
            radius.toFloat(),
            0F,
            radius.toFloat(),
            0F
        )
    }

    private fun readAttributes(context: Context, attrs: AttributeSet?) {
        val attrTypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ChipsTriggerView, 0, 0)
        try {
            attrTypedArray.apply {
                getString(R.styleable.ChipsTriggerView_cpv_hint_text).apply {
                    val default = ""
                    hintText = this ?: default
                }
                getInt(R.styleable.ChipsTriggerView_cpv_max_rows, DEFAULT_MAX_ROWS).apply {
                    maxRows = this
                }
                getResourceId(
                    R.styleable.ChipsTriggerView_cpv_title_text_appearance,
                    R.style.PickerViewTitleTextAppearance
                ).apply {
                    titleTextAppearance = this
                }
                getString(R.styleable.ChipsTriggerView_cpv_hidden_items_format_text).apply {
                    val default = ResourceUtils.getPhrase(context, R.string.picker_chips_n_more_items_format_text)
                    nMoreItemsFormat = if (isNullOrBlank()) default
                    else this
                }
                getString(R.styleable.ChipsTriggerView_cpv_title_text).apply {
                    val default = ResourceUtils.getPhrase(context, R.string.picker_chips_title_text)
                    title = this ?: default
                }
                getResourceId(
                    R.styleable.ChipsTriggerView_cpv_hint_text_appearance,
                    R.style.PickerViewChipsHintTextAppearance
                ).apply {
                    hintTextAppearance = this
                }
            }
        } finally {
            attrTypedArray.recycle()
        }
    }

    private fun syncChips() {
        flowLayout.apply flowLayout@{
            removeAllViews()
            text.apply chipTitle@{
                if (isNullOrBlank()) {
                    hintTextView.visibility = VISIBLE
                    flowLayoutContainer.layoutParams.height = emptyLayoutHeight
                    return@chipTitle
                }
                flowLayoutContainer.layoutParams.height = WRAP_CONTENT
                hintTextView.visibility = GONE
                split(",").apply {
                    generateViewsForFlowLayout(this)
                }
            }
        }
    }


    private fun setupHintTextAppearance() {
        hintTextView.apply {
            setTextAppearance(hintTextAppearance)
        }
    }

    private fun setupHintText() {
        hintTextView.apply {
            text = hintText
        }
    }

    private fun generateViewsForFlowLayout(elements: List<String>) {
        val maxWidth = flowLayout.measuredWidth - flowLayout.paddingStart - flowLayout.paddingEnd
        val spacing = ResourceUtils.getDimenPxById(context, R.dimen.trigger_view_chips_items_spacing)
        var currentRow = 1
        var currentRowWidth = 0
        var fitChildren = 0
        elements.apply {
            for (i in 0 until size) {
                val chipTitle = this[i].trim()
                val chipView = inflateChip(chipTitle)
                val nHiddenChipsView = inflateNMoreItemsView(size - i + 1)
                val requiredWidthForChip = min((chipView.measuredWidth + spacing), maxWidth)
                val requiredWidthForNview = min((nHiddenChipsView.measuredWidth + spacing), maxWidth)
                if (currentRowWidth + requiredWidthForChip > maxWidth) {
                    currentRow++
                    currentRowWidth = 0
                }
                currentRowWidth += requiredWidthForChip
                if ((i + 1 < size && currentRow == maxRows && (currentRowWidth + requiredWidthForNview) > maxWidth) || currentRow > maxRows) {
                    break
                }
                flowLayout.addView(chipView)
                fitChildren++
            }
        }
        val removeChildrenCount = elements.size - fitChildren
        if (removeChildrenCount > 0) flowLayout.addView(inflateNMoreItemsView(removeChildrenCount))
    }

    @SuppressLint("InflateParams")
    private fun inflateChip(title: String): View {
        return LayoutInflater.from(context).inflate(R.layout.dialog_picker_chip_item, null).apply {
            val widthMeasureSpec = makeMeasureSpec(0, UNSPECIFIED)
            val heightMeasureSpec = makeMeasureSpec(0, UNSPECIFIED)
            val titleTextView: TextView = findViewById(R.id.chip_item_text_view)
            titleTextView.text = title
            measure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private fun inflateNMoreItemsView(hiddenItemsCount: Int): View {
        val widthMeasureSpec = makeMeasureSpec(0, UNSPECIFIED)
        val heightMeasureSpec = makeMeasureSpec(0, UNSPECIFIED)
        return TextView(context).apply {
            val paddingVer = ResourceUtils.getDimenPxById(context, R.dimen.spacing_size_tiny)
            val nMoreItemsText = nMoreItemsFormat.format(hiddenItemsCount)
            setPadding(0, paddingVer, 0, paddingVer)
            text = nMoreItemsText
            measure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    private class SavedState : BaseSavedState {
        var hintText: String? = null
        var nMoreFormat = ""
        var title = ""
        var titleTextAppearance = -1
        var maxRows = -1
        var hintTextAppearance: Int = 0

        constructor(superState: Parcelable?) : super(superState)

        private constructor(parcel: Parcel) : super(parcel) {
            parcel.apply {
                hintText = ParcelUtils.readString(this)
                nMoreFormat = ParcelUtils.readString(this)!!
                title = ParcelUtils.readString(this)!!
                titleTextAppearance = ParcelUtils.readInt(this)!!
                hintTextAppearance = ParcelUtils.readInt(this)!!
                maxRows = ParcelUtils.readInt(this)!!
            }
        }

        @Override
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.apply {
                ParcelUtils.writeString(this, hintText)
                ParcelUtils.writeString(this, nMoreFormat)
                ParcelUtils.writeString(this, title)
                ParcelUtils.writeInt(this, titleTextAppearance)
                ParcelUtils.writeInt(this, hintTextAppearance)
                ParcelUtils.writeInt(this, maxRows)
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