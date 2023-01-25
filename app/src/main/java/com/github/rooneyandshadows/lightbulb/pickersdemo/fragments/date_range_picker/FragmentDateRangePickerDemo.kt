package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.date_range_picker

import android.os.Bundle
import android.view.View
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentConfiguration
import com.github.rooneyandshadows.lightbulb.annotation_processors.annotations.FragmentScreen
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity
import com.github.rooneyandshadows.lightbulb.application.fragment.base.BaseFragmentWithViewModelAndDataBinding
import com.github.rooneyandshadows.lightbulb.application.fragment.cofiguration.ActionBarConfiguration
import com.github.rooneyandshadows.lightbulb.commons.utils.ResourceUtils
import com.github.rooneyandshadows.lightbulb.dialogs.picker_dialog_date_range.DateRangePickerDialog
import com.github.rooneyandshadows.lightbulb.pickersdemo.R
import com.github.rooneyandshadows.lightbulb.pickersdemo.databinding.FragmentDatePickerDemoBinding
import com.github.rooneyandshadows.lightbulb.pickersdemo.databinding.FragmentDateRangePickerDemoBinding
import com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.date_picker.VMDatePickerDemo
import com.github.rooneyandshadows.lightbulb.pickersdemo.getShowMenuDrawable
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.AppIconUtils
import com.github.rooneyandshadows.lightbulb.pickersdemo.utils.icon.icons.DemoIconsUi

@FragmentScreen(screenName = "DateRange", screenGroup = "Demo")
@FragmentConfiguration(layoutName = "fragment_date_range_picker_demo")
class FragmentDateRangePickerDemo :
    BaseFragmentWithViewModelAndDataBinding<FragmentDateRangePickerDemoBinding, VMDateRangePickerDemo>() {

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
    override fun initializeViewModel(viewModel: VMDateRangePickerDemo, savedInstanceState: Bundle?) {
        super.initializeViewModel(viewModel, savedInstanceState)
        if (savedInstanceState != null) return
        viewModel.initialize()
    }

    @Override
    override fun doOnViewBound(viewBinding: FragmentDateRangePickerDemoBinding, savedInstanceState: Bundle?) {
        super.doOnViewBound(viewBinding, savedInstanceState)
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

    protected fun create(savedInstanceState: Bundle?) {
        super.create(savedInstanceState)
        viewModel = ViewModelProvider(this).get(VMDateRangePickerDemo::class.java)
        if (savedInstanceState == null) viewModel!!.initialize()
    }

    fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentDateRangePickerDemoBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_date_range_picker_demo, container, false)
        binding.setModel(viewModel)
        return binding.getRoot()
    }

    protected fun viewCreated(fragmentView: View, savedInstanceState: Bundle?) {
        super.viewCreated(fragmentView, savedInstanceState)
        if (getFragmentState() === FragmentStates.CREATED) {
            BaseActivity.updateMenuConfiguration(
                getContextActivity(),
                MainActivity::class.java
            ) { obj: MenuConfigurations?, activity: BaseActivity? -> MenuConfigurations.getConfiguration(activity) }
        }
        setupViews()
    }

    protected fun selectViews() {
        super.selectViews()
        if (getView() == null) return
        pickerViewBoxed = getView().findViewById(R.id.pickerViewBoxed)
        pickerViewOutlined = getView().findViewById(R.id.pickerViewOutlined)
        pickerViewButton = getView().findViewById(R.id.pickerViewButton)
        pickerViewImageButton = getView().findViewById(R.id.pickerViewImageButton)
    }

    private fun setupViews() {
        val color: Int = ResourceUtils.getColorByAttribute(getContextActivity(), R.attr.colorOnPrimary)
        pickerViewBoxed.pickerIcon =
            AppIconUtils.getIconWithAttributeColor(getContext(), DemoIconsUi.ICON_DATE_PICKER_INDICATOR)
        pickerViewOutlined.pickerIcon =
            AppIconUtils.getIconWithAttributeColor(getContext(), DemoIconsUi.ICON_DATE_PICKER_INDICATOR)
        pickerViewButton.setPickerIcon(
            AppIconUtils.getIconWithAttributeColor(
                getContext(),
                DemoIconsUi.ICON_DATE_PICKER_INDICATOR
            ), color
        )
        pickerViewImageButton.setPickerIcon(
            AppIconUtils.getIconWithAttributeColor(
                getContext(),
                DemoIconsUi.ICON_DATE_PICKER_INDICATOR
            ), color
        )
    }

    companion object {
        val newInstance: FragmentDateRangePickerDemo
            get() = FragmentDateRangePickerDemo()
    }
}