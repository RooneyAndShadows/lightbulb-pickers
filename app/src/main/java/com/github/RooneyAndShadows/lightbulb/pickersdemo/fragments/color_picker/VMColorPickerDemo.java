package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.color_picker;

import com.github.rooneyandshadows.lightbulb.commons.models.BaseObservableViewModel;
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter;
import com.github.rooneyandshadows.lightbulb.pickersdemo.models.DemoModel;
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.color.AppColorUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import static com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_color.ColorPickerAdapter.*;

public class VMColorPickerDemo extends BaseObservableViewModel {
    private String boxedSelection;
    private String outlinedSelection;
    private String buttonSelection;
    private String imageButtonSelection;
    private final Map<Integer, List<ColorModel>> dataSets = new HashMap<>();

    public void initialize() {
        dataSets.put(0, AppColorUtils.getAllForPicker());
        dataSets.put(1, AppColorUtils.getAllForPicker());
        dataSets.put(2, AppColorUtils.getAllForPicker());
        dataSets.put(3, AppColorUtils.getAllForPicker());
        boxedSelection = dataSets.get(0).get(0).getColorExternalName();
        outlinedSelection = dataSets.get(1).get(0).getColorExternalName();
        buttonSelection = dataSets.get(2).get(0).getColorExternalName();
        imageButtonSelection = dataSets.get(3).get(0).getColorExternalName();
    }

    public void setBoxedSelection(String boxedSelection) {
        this.boxedSelection = boxedSelection;
        notifyPropertyChanged(BR.boxedSelection);
    }

    public void setOutlinedSelection(String outlinedSelection) {
        this.outlinedSelection = outlinedSelection;
        notifyPropertyChanged(BR.outlinedSelection);
    }

    public void setButtonSelection(String buttonSelection) {
        this.buttonSelection = buttonSelection;
        notifyPropertyChanged(BR.buttonSelection);
    }

    public void setImageButtonSelection(String imageButtonSelection) {
        this.imageButtonSelection = imageButtonSelection;
        notifyPropertyChanged(BR.imageButtonSelection);
    }

    public Map<Integer, List<ColorModel>> getDataSets() {
        return dataSets;
    }

    @Bindable
    public String getBoxedSelection() {
        return boxedSelection;
    }

    @Bindable
    public String getOutlinedSelection() {
        return outlinedSelection;
    }

    @Bindable
    public String getButtonSelection() {
        return buttonSelection;
    }

    @Bindable
    public String getImageButtonSelection() {
        return imageButtonSelection;
    }
}
