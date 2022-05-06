package com.github.rooneyandshadows.lightbulb.pickers.dialog;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;

import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialog;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialogBuilder;
import com.github.rooneyandshadows.lightbulb.pickers.R;
import com.github.rooneyandshadows.lightbulb.pickers.dialog.base.BaseDialogPickerView;

import java.time.OffsetDateTime;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.github.rooneyandshadows.lightbulb.dialogs.base.BaseDialogFragment.*;


@SuppressWarnings({"unused", "UnusedReturnValue"})
public class DialogDateRangePickerView extends BaseDialogPickerView {
    private String datePickerFormat;
    private String datePickerFromText;
    private String datePickerToText;
    private OffsetDateTime[] selection;
    private SelectionChangedListener internalSelectionChangedListeners;
    protected final ArrayList<ValidationCheck> validationCallbacks = new ArrayList<>();
    private final ArrayList<SelectionChangedListener> selectionChangedListeners = new ArrayList<>();

    public void addValidationCheck(ValidationCheck validationCallback) {
        validationCallbacks.add(validationCallback);
    }

    public void addSelectionChangedListener(SelectionChangedListener listener) {
        selectionChangedListeners.add(listener);
    }

    public DialogDateRangePickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDatePickerFormat(String datePickerFormat) {
        this.datePickerFormat = datePickerFormat;
    }

    public void setDatePickerFromText(String datePickerFromText) {
        this.datePickerFromText = datePickerFromText;
    }

    public void setDatePickerToText(String datePickerToText) {
        this.datePickerToText = datePickerToText;
    }

    public void setPickerRange(OffsetDateTime dateFrom, OffsetDateTime dateTo) {
        if (dateFrom == null || dateTo == null) {
            dateFrom = null;
            dateTo = null;
        } else {
            if (DateUtilsOffsetDate.isDateBefore(dateTo, dateFrom)) {
                OffsetDateTime temp = dateFrom;
                dateFrom = dateTo;
                dateTo = temp;
            }
        }
        if (getDialog() == null) selectInternally(dateFrom, dateTo);
        else getDialog().setSelection(dateFrom, dateTo);
    }

    public OffsetDateTime[] getPickerRange() {
        return selection;
    }

    public boolean hasSelection() {
        return selection != null && selection[0] != null && selection[1] != null;
    }

    @Override
    protected String getViewText() {
        if (selection == null || !hasSelection())
            return ResourceUtils.getPhrase(getContext(), R.string.dialog_date_picker_empty_text);
        String from = DateUtilsOffsetDate.getDateString(datePickerFormat, selection[0]);
        String to = DateUtilsOffsetDate.getDateString(datePickerFormat, selection[1]);
        String viewTextFormat = "{from} - {to}";
        return viewTextFormat.replace("{from}", from).replace("{to}", to);
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
        if (!isValid)
            setErrorEnabled(true);
        else {
            setErrorEnabled(false);
            setErrorText(null);
        }
        return isValid;
    }

    @Override
    protected void readAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DialogDateRangePickerView, 0, 0);
        try {
            if (selection == null)
                selection = new OffsetDateTime[2];
            datePickerFormat = a.getString(R.styleable.DialogDateRangePickerView_DRPV_DateFormat);
            datePickerFromText = a.getString(R.styleable.DialogDateRangePickerView_DRPV_TextFrom);
            datePickerToText = a.getString(R.styleable.DialogDateRangePickerView_DRPV_TextTo);
            if (datePickerFormat == null || datePickerFormat.equals(""))
                datePickerFormat = "yyyy/MM/dd";
            if (datePickerFromText == null || datePickerFromText.equals(""))
                datePickerFromText = "FROM";
            if (datePickerToText == null || datePickerToText.equals(""))
                datePickerToText = "TO";
        } finally {
            a.recycle();
        }
    }

    @Override
    protected DateRangePickerDialog initializeDialog() {
        return new DateRangePickerDialogBuilder(manager, DIALOG_TAG)
                .withSelection(selection)
                .withCancelOnClickOutsude(true)
                .withTextFrom(datePickerFromText)
                .withTextTo(datePickerToText)
                .withPositiveButton(new DialogButtonConfiguration(pickerDialogPositiveButtonText), (view, dialogFragment) -> updateTextAndValidate())
                .withNegativeButton(new DialogButtonConfiguration(pickerDialogNegativeButtonText), (view, dialogFragment) -> updateTextAndValidate())
                .withOnCancelListener(dialogFragment -> updateTextAndValidate())
                .withOnDateSelectedEvent((oldValue, newValue) -> selectInternally(newValue[0], newValue[1]))
                .withAnimations(pickerDialogAnimationType)
                .buildDialog();
    }

    @Override
    protected DateRangePickerDialog getDialog() {
        return (DateRangePickerDialog) pickerDialog;
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
        myState.selectionFrom = DateUtilsOffsetDate.getDateString(DateUtilsOffsetDate.defaultFormatWithTimeZone, selection[0]);
        myState.selectionTo = DateUtilsOffsetDate.getDateString(DateUtilsOffsetDate.defaultFormatWithTimeZone, selection[1]);
        return myState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        OffsetDateTime currentFrom = DateUtilsOffsetDate.getDateFromString(DateUtilsOffsetDate.defaultFormatWithTimeZone, savedState.selectionFrom);
        OffsetDateTime currentTo = DateUtilsOffsetDate.getDateFromString(DateUtilsOffsetDate.defaultFormatWithTimeZone, savedState.selectionTo);
        selection = new OffsetDateTime[]{currentFrom, currentTo};
        super.onRestoreInstanceState(savedState.getSuperState());
    }

    private void selectInternally(OffsetDateTime dateFrom, OffsetDateTime dateTo) {
        selection = new OffsetDateTime[]{dateFrom, dateTo};
        updateTextAndValidate();
        dispatchRangeSelectionChangedEvent();
    }

    private void dispatchRangeSelectionChangedEvent() {
        for (SelectionChangedListener publicChangedListener : selectionChangedListeners)
            publicChangedListener.onSelectionChanged(this, selection);
    }

    private static class SavedState extends View.BaseSavedState {
        private String selectionFrom;
        private String selectionTo;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            selectionFrom = in.readString();
            selectionTo = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(selectionFrom);
            out.writeString(selectionTo);
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

    public interface SelectionChangedListener {
        void onSelectionChanged(DialogDateRangePickerView view, OffsetDateTime[] newRange);
    }

    public interface ValidationCheck {
        boolean validate(OffsetDateTime[] currentSelection);
    }
}