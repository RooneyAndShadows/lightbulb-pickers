package com.github.rooneyandshadowss.lightbulb.pickersdemo.fragments.date_picker;

import com.github.rooneyandshadows.java.commons.date.DateUtilsOffsetDate;
import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel;

import java.time.OffsetDateTime;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class VMDatePickerDemo extends BaseObservableViewModel {
    private OffsetDateTime boxedSelection;
    private OffsetDateTime outlinedSelection;
    private OffsetDateTime buttonSelection;
    private OffsetDateTime imageButtonSelection;

    public void initialize() {
        boxedSelection = DateUtilsOffsetDate.nowLocal();
        outlinedSelection = DateUtilsOffsetDate.nowLocal();
        buttonSelection = DateUtilsOffsetDate.nowLocal();
        imageButtonSelection = DateUtilsOffsetDate.nowLocal();
    }

    public void setBoxedSelection(OffsetDateTime boxedSelection) {
        this.boxedSelection = boxedSelection;
        notifyPropertyChanged(BR.boxedSelection);
    }

    public void setOutlinedSelection(OffsetDateTime outlinedSelection) {
        this.outlinedSelection = outlinedSelection;
        notifyPropertyChanged(BR.outlinedSelection);
    }

    public void setButtonSelection(OffsetDateTime buttonSelection) {
        this.buttonSelection = buttonSelection;
        notifyPropertyChanged(BR.buttonSelection);
    }

    public void setImageButtonSelection(OffsetDateTime imageButtonSelection) {
        this.imageButtonSelection = imageButtonSelection;
        notifyPropertyChanged(BR.imageButtonSelection);
    }

    @Bindable
    public OffsetDateTime getBoxedSelection() {
        return boxedSelection;
    }

    @Bindable
    public OffsetDateTime getOutlinedSelection() {
        return outlinedSelection;
    }

    @Bindable
    public OffsetDateTime getButtonSelection() {
        return buttonSelection;
    }

    @Bindable
    public OffsetDateTime getImageButtonSelection() {
        return imageButtonSelection;
    }
}
