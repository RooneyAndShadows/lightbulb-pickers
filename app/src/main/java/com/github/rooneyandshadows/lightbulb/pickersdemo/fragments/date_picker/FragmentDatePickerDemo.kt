package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.date_picker

import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragment
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragmentWithViewModelAndDataBinding
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickers.dialog.DialogDateTimePickerView
import com.github.rooneyandshadows.lightbulb.pickersdemo.R
import com.github.rooneyandshadows.lightbulb.pickersdemo.databinding.FragmentAdapterPickerDemoBinding
import com.github.rooneyandshadows.lightbulb.pickersdemo.databinding.FragmentDatePickerDemoBinding
import com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.adapter_picker.VMAdapterPickerDemo
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIconsUi

@FragmentScreen(screenName = "DateAndTime", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_date_picker_demo")
class FragmentDatePickerDemo : BaseFragmentWithViewModelAndDataBinding<FragmentDatePickerDemoBinding, VMDatePickerDemo>() {
    @Override
    override fun getViewModelClass(): Class<VMDatePickerDemo> {
        return VMDatePickerDemo::class.java
    }

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        val title = ResourceUtils.getPhrase(requireContext(), R.string.app_name)
        val subTitle = ResourceUtils.getPhrase(requireContext(), R.string.date_picker_demo_text)
        return ActionBarConfiguration(R.id.toolbar)
            .withActionButtons(true)
            .withTitle(title)
            .withSubTitle(subTitle)
    }

    @Override
    override fun initializeViewModel(viewModel: VMDatePickerDemo) {
        super.initializeViewModel(viewModel)
        if (getFragmentState() == FragmentStates.CREATED) viewModel.initialize()
    }

    @Override
    override fun doOnViewBound(viewBinding: FragmentDatePickerDemoBinding) {
        super.doOnViewBound(viewBinding)
        val color: Int = ResourceUtils.getColorByAttribute(requireContext(), R.attr.colorOnPrimary)
        viewBinding.pickerViewBoxed.apply {
            val icon = AppIconUtils.getIconWithAttributeColor(context, DemoIconsUi.ICON_DATE_PICKER_INDICATOR)
            setPickerIcon(icon, color)
        }
        viewBinding.pickerViewOutlined.apply {
            val icon = AppIconUtils.getIconWithAttributeColor(context, DemoIconsUi.ICON_DATE_PICKER_INDICATOR)
            setPickerIcon(icon, color)
        }
        viewBinding.pickerViewButton.apply {
            val icon = AppIconUtils.getIconWithAttributeColor(context, DemoIconsUi.ICON_DATE_PICKER_INDICATOR)
            setPickerIcon(icon, color)
        }
        viewBinding.pickerViewImageButton.apply {
            val icon = AppIconUtils.getIconWithAttributeColor(context, DemoIconsUi.ICON_DATE_PICKER_INDICATOR)
            setPickerIcon(icon, color)
        }
    }

    @Override
    override fun doOnCreate(savedInstanceState: Bundle?, viewModel: VMDatePickerDemo) {
        super.doOnCreate(savedInstanceState, viewModel)
        viewBinding.model = viewModel
    }
}