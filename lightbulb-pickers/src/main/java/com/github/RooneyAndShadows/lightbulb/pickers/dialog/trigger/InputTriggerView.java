package com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.LinearLayout;

import com.github.rooneyandshadows.java.commons.string.StringUtils;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.github.rooneyandshadows.lightbulb.pickers.R;
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView;
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.DialogPickerTriggerLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;

@SuppressWarnings("unused")
public class InputTriggerView extends LinearLayout implements DialogPickerTriggerLayout {
    private final String INPUT_LAYOUT_TAG = ResourceUtils.getPhrase(getContext(), R.string.DP_InputLayoutTag);
    private final String INPUT_EDIT_TEXT_TAG = ResourceUtils.getPhrase(getContext(), R.string.DP_InputEditTextTag);
    private LinearLayout rootView;
    private TextInputLayout textInputLayout;
    private TextInputEditText textInputEditText;
    private InputTypes inputType;
    protected Drawable pickerIcon;
    protected Integer pickerBackgroundColor;
    protected Integer pickerInputBoxStrokeColor;
    protected int pickerStartIconColor;
    protected int defaultIconColor;
    protected int hintAppearance;
    protected int errorAppearance;
    protected boolean startIconUseAlpha;

    public InputTriggerView(@NonNull Context context) {
        this(context, null);
    }

    public InputTriggerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        readAttributes(context, attrs);
        initializeView();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (textInputEditText != null)
            textInputEditText.setEnabled(enabled);
        if (textInputLayout != null)
            textInputLayout.setEnabled(enabled);
    }

    @Override
    public void attachTo(@NonNull BaseDialogPickerView pickerView) {
        textInputLayout.setEndIconOnClickListener(v -> pickerView.showPickerDialog());
        textInputLayout.setErrorEnabled(pickerView.isErrorEnabled());
        if (textInputLayout.isErrorEnabled())
            textInputLayout.setError(pickerView.getErrorText());
        if (!pickerView.isShowSelectedTextValue() && StringUtils.isNullOrEmptyString(pickerView.getPickerHintText())) {
            textInputEditText.setCompoundDrawablePadding(-textInputEditText.getPaddingLeft());
            textInputEditText.setMinWidth(0);
            textInputEditText.setWidth(0);
        }
        textInputLayout.setEnabled(isEnabled());
        textInputEditText.setEnabled(isEnabled());
        setTriggerHintText(pickerView.getPickerHintText());
    }

    @Override
    public void setTriggerIcon(Drawable icon, Integer color) {
        pickerIcon = icon;
        setPickerStartIconColor(color);
    }

    @Override
    public void setTriggerText(String newTextValue) {
        if (textInputEditText.getText() != null)
            if (!newTextValue.equals(textInputEditText.getText().toString()))
                textInputEditText.setText(newTextValue);
    }

    @Override
    public void setTriggerErrorText(String errorText) {
        textInputLayout.setError(errorText);
    }

    @Override
    public void setTriggerHintText(String hintText) {
        boolean hasHint = !StringUtils.isNullOrEmptyString(hintText);
        textInputLayout.setHintEnabled(hasHint);
        textInputLayout.setHint(hintText);
        textInputEditText.setHint(null);
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
    public void setTriggerErrorEnabled(boolean errorEnabled) {
        textInputLayout.setErrorEnabled(errorEnabled);
    }

    @Override
    public String getTriggerText() {
        if (textInputEditText.getText() != null)
            return textInputEditText.getText().toString();
        return "";
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(@NonNull SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState myState = new SavedState(superState);
        myState.pickerBackgroundColor = pickerBackgroundColor;
        myState.pickerInputBoxStrokeColor = pickerInputBoxStrokeColor;
        myState.pickerStartIconColor = pickerStartIconColor;
        myState.defaultIconColor = defaultIconColor;
        myState.iconUseAlpha = startIconUseAlpha;
        myState.editTextSavedState = textInputEditText.onSaveInstanceState();
        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        pickerBackgroundColor = savedState.pickerBackgroundColor;
        pickerInputBoxStrokeColor = savedState.pickerInputBoxStrokeColor;
        pickerStartIconColor = savedState.pickerStartIconColor;
        defaultIconColor = savedState.defaultIconColor;
        startIconUseAlpha = savedState.iconUseAlpha;
        textInputEditText.onRestoreInstanceState(savedState.editTextSavedState);
        setupInputLayout();
    }

    private void readAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.InputTriggerView, 0, 0);
        try {
            pickerInputBoxStrokeColor = ColorUtils.setAlphaComponent(a.getColor(R.styleable.InputTriggerView_ITV_StrokeColor, ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface)), 140);
            pickerBackgroundColor = ColorUtils.setAlphaComponent(a.getColor(R.styleable.InputTriggerView_ITV_BackgroundColor, ResourceUtils.getColorByAttribute(getContext(), R.attr.colorOnSurface)), 30);
            inputType = InputTypes.valueOf(a.getInt(R.styleable.InputTriggerView_ITV_LayoutType, 1));
            errorAppearance = a.getResourceId(R.styleable.InputTriggerView_ITV_ErrorTextAppearance, R.style.PickerViewErrorTextAppearance);
            hintAppearance = a.getResourceId(R.styleable.InputTriggerView_ITV_HintTextAppearance, R.style.PickerViewHintTextAppearance);
            startIconUseAlpha = a.getBoolean(R.styleable.InputTriggerView_ITV_IconUseAlpha, true);
            defaultIconColor = ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface);
            pickerStartIconColor = a.getColor(R.styleable.InputTriggerView_ITV_StartIconColor, defaultIconColor);
        } finally {
            a.recycle();
        }
    }

    private void initializeView() {
        setSaveEnabled(true);
        renderLayout();
        setupInputLayout();
    }

    private void renderLayout() {
        switch (inputType) {
            case BOXED:
                rootView = (LinearLayout) inflate(getContext(), R.layout.dialog_picker_boxed_layout, this);
                break;
            case OUTLINED:
                rootView = (LinearLayout) inflate(getContext(), R.layout.dialog_picker_outlined_layout, this);
                break;
        }
        textInputLayout = rootView.findViewWithTag(INPUT_LAYOUT_TAG);
        textInputEditText = findViewWithTag(INPUT_EDIT_TEXT_TAG);
    }

    private void setupInputLayout() {
        setupBackground();
        setupStroke();
        setupStartIcon();
        setupEndIcon();
        setupTextAppearances();
    }

    public void setStartIconUseAlpha(boolean startIconUseAlpha) {
        this.startIconUseAlpha = startIconUseAlpha;
        setupStartIcon();
    }

    public void setPickerBackgroundColor(Integer pickerBackgroundColor) {
        this.pickerBackgroundColor = pickerBackgroundColor;
        setupBackground();
    }

    public void setPickerInputBoxStrokeColor(Integer pickerInputBoxStrokeColor) {
        this.pickerInputBoxStrokeColor = pickerInputBoxStrokeColor;
        setupStroke();
    }

    public void setPickerStartIconColor(Integer pickerStartIconColor) {
        this.pickerStartIconColor = pickerStartIconColor == null ? defaultIconColor : pickerStartIconColor;
        setupStartIcon();
    }

    public void setHintAppearance(int hintAppearance) {
        this.hintAppearance = hintAppearance;
        setupTextAppearances();
    }

    public void setErrorAppearance(int errorAppearance) {
        this.errorAppearance = errorAppearance;
        setupTextAppearances();
    }

    private void setupBackground() {
        if (inputType.equals(InputTypes.BOXED))
            textInputLayout.setBoxBackgroundColor(pickerBackgroundColor);
    }

    private void setupStroke() {
        textInputLayout.setBoxStrokeColorStateList(new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_enabled},
                        new int[]{android.R.attr.state_focused, android.R.attr.state_pressed},
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{}
                },
                new int[]{
                        pickerInputBoxStrokeColor,
                        pickerInputBoxStrokeColor,
                        pickerInputBoxStrokeColor,
                        pickerInputBoxStrokeColor,
                        pickerInputBoxStrokeColor
                }));
    }

    private void setupStartIcon() {
        int alpha = startIconUseAlpha ? 140 : 255;
        int iconColor;
        iconColor = ColorUtils.setAlphaComponent(pickerStartIconColor, alpha);
        textInputLayout.setStartIconTintList(new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_focused, android.R.attr.state_pressed},
                        new int[]{-android.R.attr.state_enabled},
                        new int[]{}
                },
                new int[]{
                        iconColor,
                        iconColor,
                        iconColor
                }));
        textInputLayout.setStartIconDrawable(pickerIcon);
    }

    private void setupEndIcon() {
        Drawable endIcon = ResourceUtils.getDrawable(getContext(), R.drawable.dropdown_icon);
        endIcon.setTint(ResourceUtils.getColorByAttribute(getContext(), R.attr.colorOnSurface));
        textInputLayout.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
        textInputLayout.setEndIconDrawable(endIcon);
        textInputLayout.setEndIconVisible(true);
        textInputLayout.setErrorIconDrawable(null);
    }

    private void setupTextAppearances() {
        textInputLayout.setHintTextAppearance(hintAppearance);
        textInputLayout.setErrorTextAppearance(errorAppearance);
    }

    private static class SavedState extends BaseSavedState {
        private int pickerBackgroundColor;
        private int pickerInputBoxStrokeColor;
        private int pickerStartIconColor;
        private int defaultIconColor;
        private int hintAppearance;
        private int errorAppearance;
        private boolean iconUseAlpha;
        private Parcelable editTextSavedState;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            pickerBackgroundColor = in.readInt();
            pickerInputBoxStrokeColor = in.readInt();
            pickerStartIconColor = in.readInt();
            defaultIconColor = in.readInt();
            hintAppearance = in.readInt();
            errorAppearance = in.readInt();
            iconUseAlpha = in.readByte() != 0;
            editTextSavedState = in.readParcelable(InputTriggerView.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(pickerBackgroundColor);
            out.writeInt(pickerInputBoxStrokeColor);
            out.writeInt(pickerStartIconColor);
            out.writeInt(defaultIconColor);
            out.writeInt(hintAppearance);
            out.writeInt(errorAppearance);
            out.writeByte((byte) (iconUseAlpha ? 1 : 0));
            out.writeParcelable(editTextSavedState, 0);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    protected enum InputTypes {
        BOXED(1),
        OUTLINED(2);

        private final int value;
        private static final SparseArray<InputTypes> values = new SparseArray<>();

        InputTypes(int value) {
            this.value = value;
        }

        static {
            for (InputTypes inputType : InputTypes.values()) {
                values.put(inputType.value, inputType);
            }
        }

        public static InputTypes valueOf(int inputType) {
            return values.get(inputType);
        }

        public int getValue() {
            return value;
        }
    }
}
