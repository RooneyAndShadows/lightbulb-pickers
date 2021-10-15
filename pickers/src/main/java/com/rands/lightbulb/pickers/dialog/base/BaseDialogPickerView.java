package com.rands.lightbulb.pickers.dialog.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.rands.lightbulb.dialogs.base.BasePickerDialogFragment;
import com.rands.lightbulb.pickers.R;

import java.util.ArrayList;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import static com.rands.lightbulb.dialogs.base.BaseDialogFragment.DialogAnimationTypes;

@SuppressWarnings({"unused", "UnusedReturnValue", "rawtypes"})
public abstract class BaseDialogPickerView extends LinearLayout {
    protected DialogPickerTriggerLayout triggerView;
    protected String DIALOG_TAG;
    protected String pickerHintText;
    protected String errorText;
    protected String pickerDialogPositiveButtonText;
    protected String pickerDialogNegativeButtonText;
    protected String pickerRequiredText;
    protected boolean errorEnabled;
    protected boolean validationEnabled;
    protected boolean required;
    protected boolean showSelectedTextValue;
    protected boolean pickerDialogCancelable;
    protected Drawable pickerIcon;
    protected DialogAnimationTypes pickerDialogAnimationType;
    protected FragmentManager manager;
    protected BasePickerDialogFragment pickerDialog;
    private final ArrayList<TriggerAttachedCallback> triggerAttachedCallback = new ArrayList<>();

    public BaseDialogPickerView(Context context) {
        this(context, null);
    }

    public BaseDialogPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSaveEnabled(true);
        readBaseAttributes(context, attrs);
        readAttributes(context, attrs);
        if (!isInEditMode())
            manager = ((FragmentActivity) context).getSupportFragmentManager();
        setOrientation(VERTICAL);
    }

    public abstract boolean validate();

    protected abstract BasePickerDialogFragment initializeDialog();

    protected abstract BasePickerDialogFragment getDialog();

    protected abstract String getViewText();

    protected abstract void readAttributes(Context context, AttributeSet attrs);

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (!(child instanceof DialogPickerTriggerLayout)) {
            Log.w(BaseDialogPickerView.class.getName(), "Picker view child is ignored. All child views must implement com.rands.lightbulb.pickers.dialog.base.TriggerView");
            return;
        }
        if (getChildCount() > 0) {
            Log.w(BaseDialogPickerView.class.getName(), "Picker can have only one trigger view.");
            return;
        }
        super.addView(child, index, params);
        triggerView = (DialogPickerTriggerLayout) child;
        triggerView.attachTo(this);
        for (TriggerAttachedCallback callback : triggerAttachedCallback)
            callback.onAttached(this.triggerView, this);
    }

    private void readBaseAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PickerView, 0, 0);
        try {
            pickerHintText = a.getString(R.styleable.PickerView_PV_HintText);
            errorText = a.getString(R.styleable.PickerView_PV_ErrorText);
            pickerRequiredText = a.getString(R.styleable.PickerView_PV_RequiredText);
            pickerDialogPositiveButtonText = a.getString(R.styleable.PickerView_PV_DialogButtonPosText);
            pickerDialogNegativeButtonText = a.getString(R.styleable.PickerView_PV_DialogButtonNegText);
            DIALOG_TAG = a.getString(R.styleable.PickerView_PV_DialogTag);
            if (pickerHintText == null || pickerHintText.equals(""))
                pickerHintText = "";
            if (errorText == null || errorText.equals(""))
                errorText = "Error";
            if (pickerDialogPositiveButtonText == null || pickerDialogPositiveButtonText.equals(""))
                pickerDialogPositiveButtonText = "OK";
            if (pickerDialogNegativeButtonText == null || pickerDialogNegativeButtonText.equals(""))
                pickerDialogNegativeButtonText = "CANCEL";
            if (pickerRequiredText == null || pickerRequiredText.equals(""))
                pickerRequiredText = "Field is required";
            if (DIALOG_TAG == null || DIALOG_TAG.equals(""))
                DIALOG_TAG = "DEFAULT_PICKER_DIALOG_TAG";
            pickerDialogAnimationType = DialogAnimationTypes.valueOf(a.getInt(R.styleable.PickerView_PV_DialogAnimation, 1));
            errorEnabled = a.getBoolean(R.styleable.PickerView_PV_ErrorEnabled, false);
            required = a.getBoolean(R.styleable.PickerView_PV_Required, false);
            validationEnabled = a.getBoolean(R.styleable.PickerView_PV_ValidationEnabled, false);
            pickerDialogCancelable = a.getBoolean(R.styleable.PickerView_PV_DialogCancelable, true);
            showSelectedTextValue = a.getBoolean(R.styleable.PickerView_PV_ShowSelectedText, true);
        } finally {
            a.recycle();
        }
    }

    protected final void updateTextAndValidate() {
        updateText();
        validate();
    }

    protected final void updateText() {
        if (!showSelectedTextValue)
            return;
        String newTextValue = this.getViewText();
        triggerView.setTriggerText(newTextValue);
    }

    public final void showPickerDialog() {
        if (pickerDialog == null)
            pickerDialog = initializeDialog();
        pickerDialog.show();
    }

    public void addOnTriggerAttachedCallback(TriggerAttachedCallback callback) {
        triggerAttachedCallback.add(callback);
    }

    public final void setTriggerView(View triggerView) {
        if (!(triggerView instanceof DialogPickerTriggerLayout)) {
            Log.w(BaseDialogPickerView.class.getName(), "Trigger view must implement com.rands.lightbulb.pickers.dialog.base.TriggerView");
            return;
        }
        removeAllViews();
        addView(triggerView);
        this.triggerView = (DialogPickerTriggerLayout) triggerView;
        this.triggerView.attachTo(this);
        for (TriggerAttachedCallback callback : triggerAttachedCallback)
            callback.onAttached(this.triggerView, this);
    }

    public final void setValidationEnabled(boolean validationEnabled) {
        this.validationEnabled = validationEnabled;
    }

    public final void setDialogTag(String dialogTag) {
        if (pickerDialog == null)
            this.DIALOG_TAG = dialogTag;
    }

    public final void setDialogAnimation(DialogAnimationTypes animation) {
        this.pickerDialogAnimationType = animation;
    }

    public final void setRequired(boolean required) {
        this.required = required;
        if (required)
            validate();
    }

    public final void setErrorText(String error) {
        errorText = error;
        triggerView.setTriggerErrorText(errorText);
    }

    public final void setHintText(String hintText) {
        if (!showSelectedTextValue)
            return;
        pickerHintText = hintText;
        triggerView.setTriggerHintText(pickerHintText);
    }

    public final void setPickerIcon(Drawable icon, Integer color) {
        pickerIcon = icon;
        triggerView.setTriggerIcon(icon, color);
    }

    public final void setPickerIcon(Drawable icon) {
        setPickerIcon(icon, null);
    }

    public final void setShowSelectedTextValue(boolean showSelectedTextValue) {
        this.showSelectedTextValue = showSelectedTextValue;
        invalidate();
    }

    protected final void setErrorEnabled(boolean errorEnabled) {
        if (this.errorEnabled != errorEnabled) {
            this.errorEnabled = errorEnabled;
            triggerView.setTriggerErrorEnabled(this.errorEnabled);
        }
    }

    protected final boolean isPickerDialogShowing() {
        return pickerDialog != null && pickerDialog.isDialogShown();
    }

    public final boolean isShowSelectedTextValue() {
        return showSelectedTextValue;
    }

    public final boolean isValidationEnabled() {
        return validationEnabled;
    }

    public final boolean isErrorEnabled() {
        return this.errorEnabled;
    }

    public final String getPickerHintText() {
        return pickerHintText;
    }

    public final String getErrorText() {
        return errorText;
    }

    public final String getText() {
        return triggerView.getTriggerText();
    }

    public final DialogPickerTriggerLayout getTriggerView() {
        return triggerView;
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState myState = new SavedState(superState);
        myState.isDialogShown = isPickerDialogShowing();
        myState.pickerHintText = pickerHintText;
        myState.pickerErrorText = errorText;
        myState.pickerRequiredText = pickerRequiredText;
        myState.pickerIsRequired = required;
        myState.pickerIsErrorEnabled = errorEnabled;
        myState.pickerIsValidationEnabled = validationEnabled;
        myState.pickerShowSelectedTextValue = showSelectedTextValue;
        myState.pickerDialogAnimationType = pickerDialogAnimationType.getValue();
        myState.pickerDialogPositiveButtonText = pickerDialogPositiveButtonText;
        myState.pickerDialogNegativeButtonText = pickerDialogNegativeButtonText;
        myState.pickerDialogCancelable = pickerDialogCancelable;
        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        boolean isPickerDialogShowing = savedState.isDialogShown;
        pickerHintText = savedState.pickerHintText;
        errorText = savedState.pickerErrorText;
        pickerRequiredText = savedState.pickerRequiredText;
        required = savedState.pickerIsRequired;
        errorEnabled = savedState.pickerIsErrorEnabled;
        validationEnabled = savedState.pickerIsValidationEnabled;
        showSelectedTextValue = savedState.pickerShowSelectedTextValue;
        pickerDialogAnimationType = DialogAnimationTypes.valueOf(savedState.pickerDialogAnimationType);
        pickerDialogPositiveButtonText = savedState.pickerDialogPositiveButtonText;
        pickerDialogNegativeButtonText = savedState.pickerDialogNegativeButtonText;
        pickerDialogCancelable = savedState.pickerDialogCancelable;
        updateTextAndValidate();
        if (isPickerDialogShowing)
            showPickerDialog();
    }

    private static class SavedState extends BaseSavedState {
        private boolean isDialogShown;
        private String pickerHintText;
        private String pickerErrorText;
        private String pickerRequiredText;
        private boolean pickerIsRequired;
        private boolean pickerIsErrorEnabled;
        private boolean pickerIsValidationEnabled;
        private boolean pickerShowSelectedTextValue;
        private int pickerDialogAnimationType;
        private int pickerDialogAnimationDuration;
        private String pickerDialogPositiveButtonText;
        private String pickerDialogNegativeButtonText;
        private boolean pickerDialogCancelable;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            isDialogShown = in.readByte() != 0;
            pickerHintText = in.readString();
            pickerErrorText = in.readString();
            pickerRequiredText = in.readString();
            pickerIsRequired = in.readByte() != 0;
            pickerIsErrorEnabled = in.readByte() != 0;
            pickerIsValidationEnabled = in.readByte() != 0;
            pickerShowSelectedTextValue = in.readByte() != 0;
            pickerDialogAnimationType = in.readInt();
            pickerDialogAnimationDuration = in.readInt();
            pickerDialogPositiveButtonText = in.readString();
            pickerDialogNegativeButtonText = in.readString();
            pickerDialogCancelable = in.readByte() != 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeByte((byte) (isDialogShown ? 1 : 0));
            out.writeString(pickerHintText);
            out.writeString(pickerErrorText);
            out.writeString(pickerRequiredText);
            out.writeByte((byte) (pickerIsRequired ? 1 : 0));
            out.writeByte((byte) (pickerIsErrorEnabled ? 1 : 0));
            out.writeByte((byte) (pickerIsValidationEnabled ? 1 : 0));
            out.writeByte((byte) (pickerShowSelectedTextValue ? 1 : 0));
            out.writeInt(pickerDialogAnimationType);
            out.writeInt(pickerDialogAnimationDuration);
            out.writeString(pickerDialogPositiveButtonText);
            out.writeString(pickerDialogNegativeButtonText);
            out.writeByte((byte) (pickerDialogCancelable ? 1 : 0));
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

    public interface TriggerAttachedCallback {
        void onAttached(DialogPickerTriggerLayout triggerView, BaseDialogPickerView pickerView);
    }
}