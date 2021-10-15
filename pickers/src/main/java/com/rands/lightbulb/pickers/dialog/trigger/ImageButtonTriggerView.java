package com.rands.lightbulb.pickers.dialog.trigger;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.LinearLayout;

import com.rands.lightbulb.commons.utils.ResourceUtils;
import com.rands.lightbulb.pickers.R;
import com.rands.lightbulb.pickers.dialog.base.BaseDialogPickerView;
import com.rands.lightbulb.pickers.dialog.base.DialogPickerTriggerLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;

public class ImageButtonTriggerView extends LinearLayout implements DialogPickerTriggerLayout {
    private final String IMAGE_BUTTON_TAG = ResourceUtils.getPhrase(getContext(), R.string.DP_ImageButtonTag);
    private final String IMAGE_BUTTON_ERROR_TEXT_TAG = ResourceUtils.getPhrase(getContext(), R.string.DP_ErrorTextTag);
    private BaseDialogPickerView pickerView;
    private LinearLayout rootView;
    protected AppCompatImageButton iconButtonView;
    protected AppCompatTextView errorTextView;
    protected Drawable pickerIcon;
    protected Integer pickerBackgroundColor;
    protected int pickerStartIconColor;
    protected int defaultIconColor;
    protected int errorAppearance;
    private boolean hasDefinedBoxBackgroundAttribute;

    public ImageButtonTriggerView(@NonNull Context context) {
        this(context, null);
    }

    public ImageButtonTriggerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        readAttributes(context, attrs);
        initView();
    }

    @Override
    public void attachTo(@NonNull BaseDialogPickerView pickerView) {
        this.pickerView = pickerView;
        iconButtonView.setOnClickListener(v -> pickerView.showPickerDialog());
        boolean errorEnabled = pickerView.isErrorEnabled();
        setTriggerErrorEnabled(errorEnabled);
        if (errorEnabled)
            errorTextView.setError(pickerView.getErrorText());
        setTriggerHintText(pickerView.getPickerHintText());
    }

    @Override
    public void setTriggerIcon(Drawable icon, Integer iconColor) {
        pickerIcon = icon;
        setPickerStartIconColor(iconColor);
    }

    @Override
    public void setTriggerText(String newTextValue) {
        //NOT SUPPORTED
    }

    @Override
    public void setTriggerErrorText(String errorText) {
        errorTextView.setText(errorText);
    }

    @Override
    public void setTriggerHintText(String hintText) {
        //NOT SUPPORTED
    }

    @Override
    public void setTriggerErrorEnabled(boolean errorEnabled) {
        errorTextView.setVisibility(errorEnabled ? VISIBLE : GONE);
    }

    @Override
    public String getTriggerText() {
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
        myState.pickerStartIconColor = pickerStartIconColor;
        myState.defaultIconColor = defaultIconColor;
        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        pickerBackgroundColor = savedState.pickerBackgroundColor;
        pickerStartIconColor = savedState.pickerStartIconColor;
        defaultIconColor = savedState.defaultIconColor;
        setupViews();
    }

    private void readAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ImageButtonTriggerView, 0, 0);
        try {
            pickerBackgroundColor = ResourceUtils.readNullableColorAttributeFromTypedArray(getContext(), a, R.styleable.ImageButtonTriggerView_IBTV_BackgroundColor);
            hasDefinedBoxBackgroundAttribute = a.hasValue(R.styleable.ImageButtonTriggerView_IBTV_BackgroundColor);
            if (pickerBackgroundColor == null)
                pickerBackgroundColor = Color.TRANSPARENT;
            errorAppearance = a.getResourceId(R.styleable.ImageButtonTriggerView_IBTV_ErrorTextAppearance, R.style.PickerViewErrorTextAppearance);
            defaultIconColor = ResourceUtils.getColorByAttribute(context, R.attr.colorOnSurface);
            pickerStartIconColor = a.getColor(R.styleable.ImageButtonTriggerView_IBTV_StartIconColor, defaultIconColor);

        } finally {
            a.recycle();
        }
    }

    private void initView() {
        setSaveEnabled(true);
        renderLayout();
        setupViews();
    }

    private void renderLayout() {
        rootView = (LinearLayout) inflate(getContext(), R.layout.dialog_picker_image_button_layout, this);
        iconButtonView = rootView.findViewWithTag(IMAGE_BUTTON_TAG);
        errorTextView = rootView.findViewWithTag(IMAGE_BUTTON_ERROR_TEXT_TAG);
    }

    private void setupViews() {
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
            iconButtonView.setBackgroundColor(Color.TRANSPARENT);
    }

    private void setupStartIcon() {
        if (pickerIcon != null)
            pickerIcon.setTint(pickerStartIconColor);
        iconButtonView.setImageDrawable(pickerIcon);
    }

    private void setupErrorAppearance() {
        errorTextView.setTextAppearance(getContext(), errorAppearance);
    }

    private static class SavedState extends BaseSavedState {
        private int pickerBackgroundColor;
        private int errorAppearance;
        private int backgroundRadius;
        private int pickerStartIconColor;
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
            pickerStartIconColor = in.readInt();
            defaultIconColor = in.readInt();
            textColor = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(pickerBackgroundColor);
            out.writeInt(errorAppearance);
            out.writeInt(backgroundRadius);
            out.writeInt(pickerStartIconColor);
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
