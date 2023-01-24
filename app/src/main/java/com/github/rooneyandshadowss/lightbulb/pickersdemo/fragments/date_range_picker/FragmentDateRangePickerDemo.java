package com.github.rooneyandshadowss.lightbulb.pickersdemo.fragments.date_range_picker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity;
import com.github.rooneyandshadows.lightbulb.application.activity.slidermenu.drawable.ShowMenuDrawable;
import com.github.rooneyandshadows.lightbulb.application.fragment.BaseFragment;
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.BaseFragmentConfiguration;
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils;
import com.github.rooneyandshadows.lightbulb.pickers.dialog.DialogDateRangePickerView;
import com.github.rooneyandshadows.lightbulb.pickers.dialog.DialogDateTimePickerView;
import com.github.rooneyandshadows.lightbulb.pickersdemo.R;
import com.github.rooneyandshadowss.lightbulb.pickersdemo.activity.MainActivity;
import com.github.rooneyandshadowss.lightbulb.pickersdemo.activity.MenuConfigurations;
import com.github.rooneyandshadows.lightbulb.pickersdemo.databinding.FragmentDatePickerDemoBinding;
import com.github.rooneyandshadows.lightbulb.pickersdemo.databinding.FragmentDateRangePickerDemoBinding;
import com.github.rooneyandshadowss.lightbulb.pickersdemo.utils.icon.AppIconUtils;
import com.github.rooneyandshadowss.lightbulb.pickersdemo.utils.icon.icons.DemoIconsUi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

public class FragmentDateRangePickerDemo extends BaseFragment {
    private VMDateRangePickerDemo viewModel;
    private DialogDateRangePickerView pickerViewBoxed;
    private DialogDateRangePickerView pickerViewOutlined;
    private DialogDateRangePickerView pickerViewButton;
    private DialogDateRangePickerView pickerViewImageButton;

    public static FragmentDateRangePickerDemo getNewInstance() {
        return new FragmentDateRangePickerDemo();
    }

    @NonNull
    @Override
    protected BaseFragmentConfiguration configureFragment() {
        ShowMenuDrawable homeDrawable = new ShowMenuDrawable(getContextActivity());
        homeDrawable.setEnabled(false);
        homeDrawable.setProgress(1);
        return new BaseFragmentConfiguration()
                .withLeftDrawer(false)
                .withActionBarConfiguration(new BaseFragmentConfiguration.ActionBarConfiguration(R.id.toolbar)
                        .withActionButtons(true)
                        .attachToDrawer(false)
                        .withHomeIcon(homeDrawable)
                        .withSubTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.date_picker_demo_text))
                        .withTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.app_name))
                );
    }

    @Override
    protected void create(@Nullable Bundle savedInstanceState) {
        super.create(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(VMDateRangePickerDemo.class);
        if (savedInstanceState == null)
            viewModel.initialize();
    }

    @Override
    public View createView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentDateRangePickerDemoBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_date_range_picker_demo, container, false);
        binding.setModel(viewModel);
        return binding.getRoot();
    }

    @Override
    protected void viewCreated(@NonNull View fragmentView, @Nullable Bundle savedInstanceState) {
        super.viewCreated(fragmentView, savedInstanceState);
        if (getFragmentState() == FragmentStates.CREATED) {
            BaseActivity.updateMenuConfiguration(
                    getContextActivity(),
                    MainActivity.class,
                    MenuConfigurations::getConfiguration
            );
        }
        setupViews();
    }

    @Override
    protected void selectViews() {
        super.selectViews();
        if (getView() == null)
            return;
        pickerViewBoxed = getView().findViewById(R.id.pickerViewBoxed);
        pickerViewOutlined = getView().findViewById(R.id.pickerViewOutlined);
        pickerViewButton = getView().findViewById(R.id.pickerViewButton);
        pickerViewImageButton = getView().findViewById(R.id.pickerViewImageButton);
    }

    private void setupViews() {
        int color = ResourceUtils.getColorByAttribute(getContextActivity(), R.attr.colorOnPrimary);
        pickerViewBoxed.setPickerIcon(AppIconUtils.getIconWithAttributeColor(getContext(), DemoIconsUi.ICON_DATE_PICKER_INDICATOR));
        pickerViewOutlined.setPickerIcon(AppIconUtils.getIconWithAttributeColor(getContext(), DemoIconsUi.ICON_DATE_PICKER_INDICATOR));
        pickerViewButton.setPickerIcon(AppIconUtils.getIconWithAttributeColor(getContext(), DemoIconsUi.ICON_DATE_PICKER_INDICATOR), color);
        pickerViewImageButton.setPickerIcon(AppIconUtils.getIconWithAttributeColor(getContext(), DemoIconsUi.ICON_DATE_PICKER_INDICATOR), color);
    }
}
