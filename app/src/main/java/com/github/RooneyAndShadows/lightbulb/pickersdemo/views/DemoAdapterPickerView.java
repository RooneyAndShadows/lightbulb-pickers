package com.github.rooneyandshadows.lightbulb.pickersdemo.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;

import com.github.rooneyandshadows.lightbulb.commons.utils.DrawableUtils;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.pickers.dialog.DialogAdapterPickerView;
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.InputTriggerView;
import com.github.rooneyandshadows.lightbulb.pickersdemo.R;
import com.github.rooneyandshadows.lightbulb.pickersdemo.models.DemoModel;
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils;
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIcons;
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIconsUi;
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.RadioButtonSelectableAdapter;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.recyclerview.widget.DividerItemDecoration;

public class DemoAdapterPickerView extends DialogAdapterPickerView<DemoModel> {
    public DemoAdapterPickerView(@NonNull Context context) {
        this(context, null);
    }

    public DemoAdapterPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setAdapter(new RadioButtonSelectableAdapter<DemoModel>() {
            @Override
            protected Drawable getItemIcon(DemoModel item) {
                return AppIconUtils.getIconWithAttributeColor(getContext(), item.getIcon(), R.attr.colorOnSurface, R.dimen.ICON_SIZE_RECYCLER_ITEM);
            }

            @Override
            protected Drawable getItemIconBackground(DemoModel item) {
                return DrawableUtils.getRoundedCornersDrawable(item.getIconBackgroundColor().getColor(), ResourceUtils.dpToPx(100));
            }
        });
        addSelectionChangedListener((oldPositions, newPositions) -> setupIcon());
        addOnTriggerAttachedCallback((triggerView1, pickerView) -> setupIcon());
        setItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        setupIcon();
    }

    @InverseBindingAdapter(attribute = "pickerSelection", event = "pickerSelectionChanged")
    public static UUID getSelectedValue(DialogAdapterPickerView<DemoModel> view) {
        return view.hasSelection() ? view.getSelectedItems().get(0).getId() : null;
    }

    @BindingAdapter(value = {"pickerSelection"})
    public static void setPickerSelection(DialogAdapterPickerView<DemoModel> view, UUID newSelection) {
        if (newSelection == null)
            return;
        if (view.hasSelection()) {
            DemoModel currentSelection = view.getSelectedItems().get(0);
            if (currentSelection.getId().equals(newSelection))
                return;
        }
        for (DemoModel selectableTransactionTypeModel : view.getData())
            if (newSelection.equals(selectableTransactionTypeModel.getId())) {
                view.selectItem(selectableTransactionTypeModel);
                break;
            }
    }

    @BindingAdapter(value = {"pickerSelectionChanged"}, requireAll = false)
    public static void bindPickerEvent(DialogAdapterPickerView<DemoModel> view, final InverseBindingListener bindingListener) {
        if (view.hasSelection()) bindingListener.onChange();
        view.addSelectionChangedListener((oldPositions, newPositions) -> bindingListener.onChange());
    }

    private void setupIcon() {
        int[] selectedPositions = getAdapter().getPositions(getSelectedItems());
        if (selectedPositions.length <= 0) {
            Drawable icon = AppIconUtils.getIconWithAttributeColor(getContext(), DemoIconsUi.ICON_PICKER_INDICATOR, R.attr.colorOnSurface, R.dimen.ICON_SIZE_MEDIUM);
            if (getTriggerView() instanceof InputTriggerView)
                ((InputTriggerView) getTriggerView()).setStartIconUseAlpha(true);
            setPickerIcon(icon);
            return;
        }
        DemoModel selectedItem = getAdapter().getItem(selectedPositions[0]);
        DemoIcons iconType = selectedItem.getIcon();
        Drawable icon = AppIconUtils.getIconWithAttributeColor(getContext(), iconType, R.attr.colorOnSurface, R.dimen.ICON_SIZE_RECYCLER_ITEM);
        int color = selectedItem.getIconBackgroundColor().getColor();
        if (getTriggerView() instanceof InputTriggerView)
            ((InputTriggerView) getTriggerView()).setStartIconUseAlpha(false);
        setPickerIcon(icon, color);
    }
}