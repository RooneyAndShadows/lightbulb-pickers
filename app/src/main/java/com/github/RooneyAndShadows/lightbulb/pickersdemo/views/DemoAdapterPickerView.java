package com.github.rooneyandshadows.lightbulb.pickersdemo.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.util.AttributeSet;

import com.github.rooneyandshadows.lightbulb.commons.utils.DrawableUtils;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.pickers.dialog.DialogAdapterPickerView;
import com.github.rooneyandshadows.lightbulb.pickers.dialog.trigger.InputTriggerView;
import com.github.rooneyandshadows.lightbulb.pickersdemo.R;
import com.github.rooneyandshadows.lightbulb.pickersdemo.models.DemoModel;
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils;
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIcons;
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIconsUi;
import com.github.rooneyandshadows.lightbulb.recycleradapters.abstraction.EasyRecyclerAdapter;
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.CheckBoxSelectableAdapter;
import com.github.rooneyandshadows.lightbulb.recycleradapters.implementation.RadioButtonSelectableAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;
import androidx.recyclerview.widget.DividerItemDecoration;

public class DemoAdapterPickerView extends DialogAdapterPickerView<DemoModel> {
    private SelectionModes selectionMode;

    public DemoAdapterPickerView(Context context) {
        this(context, null);
    }

    public DemoAdapterPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DemoAdapterPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DemoAdapterPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        addSelectionChangedListener((oldPositions, newPositions) -> setupIcon());
        addOnTriggerAttachedCallback((triggerView1, pickerView) -> setupIcon());
        setItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    @NonNull
    @Override
    protected EasyRecyclerAdapter<DemoModel> initializeAdapter() {
        if (selectionMode.equals(SelectionModes.SINGLE)) {
            return new RadioButtonSelectableAdapter<DemoModel>() {
                @Override
                protected Drawable getItemIcon(DemoModel item) {
                    return AppIconUtils.getIconWithAttributeColor(getContext(), item.getIcon(), R.attr.colorOnSurface, R.dimen.ICON_SIZE_RECYCLER_ITEM);
                }

                @Override
                protected Drawable getItemIconBackground(DemoModel item) {
                    return DrawableUtils.getRoundedCornersDrawable(item.getIconBackgroundColor().getColor(), ResourceUtils.dpToPx(100));
                }
            };
        } else {
            return new CheckBoxSelectableAdapter<DemoModel>() {
                @Override
                protected Drawable getItemIcon(DemoModel item) {
                    return AppIconUtils.getIconWithAttributeColor(getContext(), item.getIcon(), R.attr.colorOnSurface, R.dimen.ICON_SIZE_RECYCLER_ITEM);
                }

                @Override
                protected Drawable getItemIconBackground(DemoModel item) {
                    return DrawableUtils.getRoundedCornersDrawable(item.getIconBackgroundColor().getColor(), ResourceUtils.dpToPx(100));
                }
            };
        }
    }

    @Override
    protected void readAttributes(Context context, AttributeSet attrs) {
        super.readAttributes(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DemoAdapterPickerView, 0, 0);
        try {
            selectionMode = SelectionModes.valueOf(a.getInt(R.styleable.DemoAdapterPickerView_DAPV_SelectionMode, SelectionModes.SINGLE.value));
        } finally {
            a.recycle();
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        setupIcon();
    }

    @InverseBindingAdapter(attribute = "pickerSelection", event = "pickerSelectionChanged")
    public static UUID getSingleSelection(DialogAdapterPickerView<DemoModel> view) {
        return view.hasSelection() ? view.getSelectedItems().get(0).getId() : null;
    }

    @InverseBindingAdapter(attribute = "pickerSelection", event = "pickerSelectionChanged")
    public static List<UUID> getMultipleSelection(DialogAdapterPickerView<DemoModel> view) {
        if (!view.hasSelection())
            return new ArrayList<>();
        return view.getSelectedItems()
                .stream()
                .map(DemoModel::getId)
                .collect(Collectors.toList());
    }

    @BindingAdapter(value = {"pickerSelection"})
    public static void setSingleSelection(DialogAdapterPickerView<DemoModel> view, UUID newSelection) {
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

    @BindingAdapter(value = {"pickerSelection"})
    public static void setMultipleSelection(DialogAdapterPickerView<DemoModel> view, List<UUID> newSelection) {
        if (newSelection == null)
            return;
        if (view.hasSelection()) {
            List<UUID> currentSelection = view.getSelectedItems()
                    .stream()
                    .map(DemoModel::getId)
                    .collect(Collectors.toList());
            if (currentSelection.size() == newSelection.size() && currentSelection.containsAll(newSelection))
                return;
        }
        List<Integer> positionsToSelect = new ArrayList<>();
        for (int i = 0; i < view.getData().size(); i++) {
            DemoModel model = view.getData().get(i);
            if (newSelection.contains(model.getId()))
                positionsToSelect.add(i);
        }
        int[] selection = new int[positionsToSelect.size()];
        for (int i = 0; i < positionsToSelect.size(); i++)
            selection[i] = positionsToSelect.get(i);
        view.setSelection(selection);
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

    public enum SelectionModes {
        SINGLE(1),
        MULTIPLE(2);

        private final int value;
        private static final Map<Integer, SelectionModes> map = new HashMap<>();

        private SelectionModes(int value) {
            this.value = value;
        }

        static {
            for (SelectionModes selectionMode : SelectionModes.values()) {
                map.put(selectionMode.value, selectionMode);
            }
        }

        public static SelectionModes valueOf(int selectionMode) {
            return (SelectionModes) map.get(selectionMode);
        }

        public int getValue() {
            return value;
        }
    }
}