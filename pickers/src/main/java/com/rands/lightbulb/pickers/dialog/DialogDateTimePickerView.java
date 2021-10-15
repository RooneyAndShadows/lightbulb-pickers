package com.rands.lightbulb.pickers.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.rands.lightbulb.commons.utils.ResourceUtils;
import com.rands.lightbulb.dialogs.picker_dialog_datetime.DateTimePickerDialog;
import com.rands.lightbulb.dialogs.picker_dialog_datetime.DateTimePickerDialogBuilder;
import com.rands.lightbulb.pickers.R;
import com.rands.lightbulb.pickers.dialog.base.BaseDialogPickerView;
import com.rands.java.commons.date.DateUtils;

import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import static com.rands.lightbulb.dialogs.base.BaseDialogFragment.*;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class DialogDateTimePickerView extends BaseDialogPickerView {
    private Date selection;
    private String datePickerFormat;
    private SelectionChangedListener dataBindingListener;
    protected ArrayList<ValidationCheck> validationCallbacks = new ArrayList<>();
    private final ArrayList<SelectionChangedListener> selectionChangedListeners = new ArrayList<>();

    public void addValidationCheck(ValidationCheck validationCallback) {
        validationCallbacks.add(validationCallback);
    }

    public void addSelectionChangedListener(SelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
    }

    public DialogDateTimePickerView(Context context) {
        this(context, null);
    }

    public DialogDateTimePickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void readAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DialogDatePickerView, 0, 0);
        try {
            datePickerFormat = a.getString(R.styleable.DialogDatePickerView_DPV_DateFormat);
            if (datePickerFormat == null || datePickerFormat.equals(""))
                datePickerFormat = "yyyy-MM-dd HH:mm";
        } finally {
            a.recycle();
        }
    }

    @Override
    protected DateTimePickerDialog initializeDialog() {
        return new DateTimePickerDialogBuilder(manager, DIALOG_TAG)
                .withSelection(selection)
                .withCancelOnClickOutsude(true)
                .withPositiveButton(new DialogButtonConfiguration(pickerDialogPositiveButtonText), (view, dialogFragment) -> updateTextAndValidate())
                .withNegativeButton(new DialogButtonConfiguration(pickerDialogNegativeButtonText), (view, dialogFragment) -> updateTextAndValidate())
                .withOnCancelListener(dialogFragment -> updateTextAndValidate())
                .withOnDateSelectedEvent((oldValue, newValue) -> selectInternally(newValue))
                .withAnimations(pickerDialogAnimationType)
                .buildDialog();
    }

    @Override
    protected DateTimePickerDialog getDialog() {
        return (DateTimePickerDialog) pickerDialog;
    }

    @Override
    public boolean validate() {
        boolean isValid = true;
        if (validationEnabled) {
            if (required && !hasSelection()) {
                setErrorEnabled(true);
                setErrorText(pickerRequiredText);
                return false;
            }
            for (ValidationCheck validationCallback : validationCallbacks)
                isValid &= validationCallback.validate(selection);
        }
        if (!isValid) {
            setErrorEnabled(true);
        } else {
            setErrorEnabled(false);
            setErrorText(null);
        }
        return isValid;
    }

    @Override
    protected String getViewText() {
        if (selection == null)
            return ResourceUtils.getPhrase(getContext(), R.string.dialog_date_picker_empty_text);
        return DateUtils.getDateString(datePickerFormat, selection);
    }

    @BindingAdapter("datePickerSelection")
    public static void updatePickerSelectionBinding(DialogDateTimePickerView view, Date selectedDate) {
        view.setSelection(selectedDate);
    }

    @InverseBindingAdapter(attribute = "datePickerSelection", event = "dateSelectionChanged")
    public static Date getText(DialogDateTimePickerView view) {
        return view.getSelection();
    }

    @BindingAdapter("dateSelectionChanged")
    public static void setListeners(DialogDateTimePickerView view, final InverseBindingListener attrChange) {
        view.dataBindingListener = (view1, newValue, oldValue) -> {
            if (view1.compareValues(newValue, oldValue))
                return;
            attrChange.onChange();
        };
    }

    public void setDateFormat(String datePickerFormat) {
        this.datePickerFormat = datePickerFormat;
        updateTextAndValidate();
    }

    public void setSelection(Date date) {
        if (getDialog() == null) selectInternally(date);
        else getDialog().setSelection(date);
    }

    public Date getSelection() {
        return selection;
    }

    public boolean hasSelection() {
        return selection != null;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState myState = new SavedState(superState);
        myState.selection = DateUtils.getDateStringInDefaultFormat(selection);
        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        selection = DateUtils.getDateFromStringInDefaultFormat(savedState.selection);
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    private boolean compareValues(Date v1, Date v2) {
        return DateUtils.isDateEqual(v1, v2, false);
    }

    private void selectInternally(Date newSelection) {
        Date oldSelection = selection;
        selection = newSelection;
        updateTextAndValidate();
        dispatchSelectionChangedEvents(oldSelection, newSelection);
    }

    private void dispatchSelectionChangedEvents(Date oldValue, Date newValue) {
        if (compareValues(oldValue, newValue))
            return;
        for (SelectionChangedListener listener : selectionChangedListeners)
            listener.onSelectionChanged(DialogDateTimePickerView.this, oldValue, newValue);
        if (dataBindingListener != null)
            dataBindingListener.onSelectionChanged(DialogDateTimePickerView.this, oldValue, newValue);
    }

    private static class SavedState extends View.BaseSavedState {
        private String selection;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            selection = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(selection);
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

    public interface ValidationCheck {
        boolean validate(Date currentSelection);
    }

    public interface SelectionChangedListener {
        void onSelectionChanged(DialogDateTimePickerView view, Date oldValue, Date newValue);
    }
}