package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.date_picker

import android.os.Bundle
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragmentWithViewModelAndDataBinding
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickersdemo.R
import com.github.rooneyandshadows.lightbulb.pickersdemo.databinding.FragmentDatePickerDemoBinding
import com.github.rooneyandshadows.lightbulb.pickersdemo.getShowMenuDrawable
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIconsUi.ICON_DATE_PICKER_INDICATOR

@FragmentScreen(screenName = "DateAndTime", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_date_picker_demo")
class FragmentDatePickerDemo : BaseFragmentWithViewModelAndDataBinding<FragmentDatePickerDemoBinding, VMDatePickerDemo>() {

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        val ctx = requireContext()
        val title = ResourceUtils.getPhrase(ctx, R.string.app_name)
        val subTitle = ResourceUtils.getPhrase(ctx, R.string.date_picker_demo_text)
        val showMenuDrawable = getShowMenuDrawable(ctx)
        return ActionBarConfiguration(R.id.toolbar).apply {
            withHomeIcon(showMenuDrawable)
            withActionButtons(true)
            withTitle(title)
            withSubTitle(subTitle)
        }
    }

    @Override
    override fun initializeViewModel(viewModel: VMDatePickerDemo, savedInstanceState: Bundle?) {
        super.initializeViewModel(viewModel, savedInstanceState)
        if (savedInstanceState != null) return
        viewModel.initialize()
    }

    @Override
    override fun doOnViewBound(viewBinding: FragmentDatePickerDemoBinding, savedInstanceState: Bundle?) {
        super.doOnViewBound(viewBinding, savedInstanceState)
        val color: Int = ResourceUtils.getColorByAttribute(requireContext(), R.attr.colorOnPrimary)
        viewBinding.pickerViewBoxed.apply {
            val icon = AppIconUtils.getIconWithAttributeColor(context, ICON_DATE_PICKER_INDICATOR)
            setPickerIcon(icon, color)
        }
        viewBinding.pickerViewOutlined.apply {
            val icon = AppIconUtils.getIconWithAttributeColor(context, ICON_DATE_PICKER_INDICATOR)
            setPickerIcon(icon, color)
        }
        viewBinding.pickerViewButton.apply {
            val icon = AppIconUtils.getIconWithAttributeColor(context, ICON_DATE_PICKER_INDICATOR)
            setPickerIcon(icon, color)
        }
        viewBinding.pickerViewImageButton.apply {
            val icon = AppIconUtils.getIconWithAttributeColor(context, ICON_DATE_PICKER_INDICATOR)
            setPickerIcon(icon, color)
        }
    }

    @Override
    override fun doOnCreate(savedInstanceState: Bundle?, viewModel: VMDatePickerDemo) {
        super.doOnCreate(savedInstanceState, viewModel)
        viewBinding.model = viewModel
    }
}