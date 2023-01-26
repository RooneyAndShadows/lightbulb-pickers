package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.date_range_picker

import android.os.Bundle
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragmentWithViewModelAndViewBinding
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.pickersdemo.R
import com.github.rooneyandshadows.lightbulb.pickersdemo.databinding.FragmentDateRangePickerDemoBinding
import com.github.rooneyandshadows.lightbulb.pickersdemo.getShowMenuDrawable
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIconsUi

@FragmentScreen(screenName = "DateRange", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_date_range_picker_demo")
class FragmentDateRangePickerDemo :
    BaseFragmentWithViewModelAndViewBinding<FragmentDateRangePickerDemoBinding, VMDateRangePickerDemo>() {
    override val viewModelClass: Class<VMDateRangePickerDemo> = VMDateRangePickerDemo::class.java

    @Override
    override fun configureActionBar(): ActionBarConfiguration {
        val ctx = requireContext()
        val title = ResourceUtils.getPhrase(ctx, R.string.app_name)
        val subTitle = ResourceUtils.getPhrase(ctx, R.string.date_range_picker_demo_text)
        val showMenuDrawable = getShowMenuDrawable(ctx)
        return ActionBarConfiguration(R.id.toolbar).apply {
            withHomeIcon(showMenuDrawable)
            withActionButtons(true)
            withTitle(title)
            withSubTitle(subTitle)
        }
    }

    @Override
    override fun doOnViewBound(
        viewBinding: FragmentDateRangePickerDemoBinding,
        viewModel: VMDateRangePickerDemo,
        savedInstanceState: Bundle?
    ) {
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
        viewBinding.model = viewModel
    }
}