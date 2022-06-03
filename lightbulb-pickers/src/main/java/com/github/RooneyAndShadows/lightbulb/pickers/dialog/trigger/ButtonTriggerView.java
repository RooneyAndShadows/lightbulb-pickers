package com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger;

import android.content.Context;
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
import com.google.android.material.button.MaterialButton;
import com.github.rooneyandshadows.lightbulb.pickers.R;
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView;
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.DialogPickerTriggerLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class ButtonTriggerView extends LinearLayout implements DialogPickerTriggerLayout {
    private final String BUTTON_TAG = ResourceUtils.getPhrase(getContext(), R.string.DP_ButtonTag);
    private final String BUTTON_ERROR_TEXT_TAG = ResourceUtils.getPhrase(getContext(), R.string.DP_ErrorTextTag);
    private BaseDialogPickerView pickerView;
    private LinearLayout rootView;
    private MaterialButton buttonView;
    protected AppCompatTextView errorTextView;
    protected Drawable pickerIcon;
    protected Integer pickerBackgroundColor;
    protected int pickerStartIconColor;
    protected int defaultIconColor;
    protected int errorAppearance;
    protected int cornerRadius;
    private boolean hasDefinedBoxBackgroundAttribute;

    public ButtonTriggerView(@NonNull Context context) {
        this(context, null);
    }

    public ButtonTriggerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        readAttributes(context, attrs);
        initializeView();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (buttonView != null)
            buttonView.setEnabled(enabled);
    }

    @Override
    public void attachTo(@NonNull BaseDialogPickerView pickerView) {
        this.pickerView = pickerView;
        buttonView.setOnClickListener(v -> pickerView.showPickerDialog());
        boolean errorEnabled = pickerView.isErrorEnabled();
        setTriggerErrorEnabled(errorEnabled);
        if (errorEnabled)
            buttonView.setError(pickerView.getErrorText());
        buttonView.setEnabled(isEnabled());
        setTriggerHintText(pickerView.getPickerHintText());
    }

    @Override
    public void setTriggerIcon(Drawable icon, Integer iconColor) {
        pickerIcon = icon;
        setPickerStartIconColor(iconColor);
    }

    @Override
    public void setTriggerText(String newTextValue) {
        if (StringUtils.isNullOrEmptyString(newTextValue))
            buttonView.setText(pickerView.getPickerHintText());
        else
            buttonView.setText(newTextValue);
    }

    @Override
    public void setTriggerErrorText(String errorText) {
        errorTextView.setText(errorText);
    }

    @Override
    public void setTriggerHintText(String hintText) {
        buttonView.setText(hintText);
    }

    @Override
    public void setTriggerErrorEnabled(boolean errorEnabled) {
        errorTextView.setVisibility(errorEnabled ? VISIBLE : GONE);
    }

    @Override
    public String getTriggerText() {
        return buttonView.getText().toString();
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
        myState.defaultIconColor = defaultIconColor;
        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        pickerBackgroundColor = savedState.pickerBackgroundColor;
        defaultIconColor = savedState.defaultIconColor;
        setupInputLayout();
    }

    private void readAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ButtonTriggerView, 0, 0);
        try {
            pickerBackgroundColor = ResourceUtils.readNullableColorAttributeFromTypedArray(getContext(), a, R.styleable.ButtonTriggerView_BTV_BackgroundColor);
            hasDefinedBoxBackgroundAttribute = a.hasValue(R.styleable.ButtonTriggerView_BTV_BackgroundColor);
            if (pickerBackgroundColor == null)
                pickerBackgroundColor = Color.TRANSPARENT;
            cornerRadius = ResourceUtils.dpToPx(a.getInt(R.styleable.ButtonTriggerView_BTV_BackgroundRadius, 5));
            errorAppearance = a.getResourceId(R.styleable.ButtonTriggerView_BTV_ErrorTextAppearance, R.style.PickerViewErrorTextAppearance);
            defaultIconColor = ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface);
            pickerStartIconColor = a.getColor(R.styleable.ButtonTriggerView_BTV_StartIconColor, defaultIconColor);
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
        rootView = (LinearLayout) inflate(getContext(), R.layout.dialog_picker_button_layout, this);
        buttonView = rootView.findViewWithTag(BUTTON_TAG);
        errorTextView = rootView.findViewWithTag(BUTTON_ERROR_TEXT_TAG);
    }

    private void setupInputLayout() {
        setupBackground();
        setupStartIcon();
        setupErrorAppearance();
    }

    public void setPickerStartIconColor(Integer pickerStartIconColor) {
        this.pickerStartIconColor = pickerStartIconColor == null ? defaultIconColor : pickerStartIconColor;
        setupStartIcon();
    }

    public void setPickerBackgroundColor(Integer pickerBackgroundColor) {
        this.pickerBackgroundColor = pickerBackgroundColor;
        setupBackground();
    }

    public void setErrorAppearance(int errorAppearance) {
        this.errorAppearance = errorAppearance;
        setupErrorAppearance();
    }

    private void setupBackground() {
        if (hasDefinedBoxBackgroundAttribute && pickerBackgroundColor == Color.TRANSPARENT)
            buttonView.setBackgroundColor(Color.TRANSPARENT);
        buttonView.setCornerRadius(cornerRadius);
    }

    private void setupStartIcon() {
        if (pickerIcon != null)
            pickerIcon.setTint(pickerStartIconColor);
        buttonView.setIcon(pickerIcon);
    }

    private void setupErrorAppearance() {
        errorTextView.setTextAppearance(getContext(), errorAppearance);
    }

    private static class SavedState extends BaseSavedState {
        private int pickerBackgroundColor;
        private int errorAppearance;
        private int backgroundRadius;
        private int startIconColor;
        private int defaultIconColor;
        private int textColor;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            pickerBackgroundColor = in.readInt();
            errorAppearance = in.readInt();
            backgroundRadius = in.readInt();
            startIconColor = in.readInt();
            defaultIconColor = in.readInt();
            textColor = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(pickerBackgroundColor);
            out.writeInt(errorAppearance);
            out.writeInt(backgroundRadius);
            out.writeInt(startIconColor);
            out.writeInt(defaultIconColor);
            out.writeInt(textColor);
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
}
