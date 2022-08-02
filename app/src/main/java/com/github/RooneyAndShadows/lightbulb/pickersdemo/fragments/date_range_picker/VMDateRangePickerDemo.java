package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.date_range_picker;

import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate;
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class VMDateRangePickerDemo extends BaseObservableViewModel {
    private OffsetDateTime[] boxedSelection;
    private OffsetDateTime[] outlinedSelection;
    private OffsetDateTime[] buttonSelection;
    private OffsetDateTime[] imageButtonSelection;

    public void initialize() {
        OffsetDateTime[] initialSelection = new OffsetDateTime[2];
        initialSelection[0] = DateUtilsOffsetDate.nowLocal();
        initialSelection[1] = initialSelection[0].plus(5, ChronoUnit.DAYS);
        boxedSelection = initialSelection;
        outlinedSelection = initialSelection;
        buttonSelection = initialSelection;
        imageButtonSelection = initialSelection;
    }

    public void setBoxedSelection(OffsetDateTime[] boxedSelection) {
        this.boxedSelection = boxedSelection;
        notifyPropertyChanged(BR.boxedSelection);
    }

    public void setOutlinedSelection(OffsetDateTime[] outlinedSelection) {
        this.outlinedSelection = outlinedSelection;
        notifyPropertyChanged(BR.outlinedSelection);
    }

    public void setButtonSelection(OffsetDateTime[] buttonSelection) {
        this.buttonSelection = buttonSelection;
        notifyPropertyChanged(BR.buttonSelection);
    }

    public void setImageButtonSelection(OffsetDateTime[] imageButtonSelection) {
        this.imageButtonSelection = imageButtonSelection;
        notifyPropertyChanged(BR.imageButtonSelection);
    }

    @Bindable
    public OffsetDateTime[] getBoxedSelection() {
        return boxedSelection;
    }

    @Bindable
    public OffsetDateTime[] getOutlinedSelection() {
        return outlinedSelection;
    }

    @Bindable
    public OffsetDateTime[] getButtonSelection() {
        return buttonSelection;
    }

    @Bindable
    public OffsetDateTime[] getImageButtonSelection() {
        return imageButtonSelection;
    }
}
