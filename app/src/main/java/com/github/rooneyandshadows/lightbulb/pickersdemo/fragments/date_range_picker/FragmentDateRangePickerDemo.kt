package com.github.RooneyAndShadows.lightbulb.pickersdemo.fragments.date_range_picker

import android.view.View
import com.github.RooneyAndShadows.lightbulb.application.activity.BaseActivity

class FragmentDateRangePickerDemo : BaseFragment() {
    private var viewModel: VMDateRangePickerDemo? = null
    private var pickerViewBoxed: DialogDateRangePickerView? = null
    private var pickerViewOutlined: DialogDateRangePickerView? = null
    private var pickerViewButton: DialogDateRangePickerView? = null
    private var pickerViewImageButton: DialogDateRangePickerView? = null
    protected fun configureFragment(): BaseFragmentConfiguration {
        val homeDrawable = ShowMenuDrawable(getContextActivity())
        homeDrawable.setEnabled(false)
        homeDrawable.setProgress(1)
        return BaseFragmentConfiguration()
            .withLeftDrawer(false)
            .withActionBarConfiguration(
                ActionBarConfiguration(R.id.toolbar)
                    .withActionButtons(true)
                    .attachToDrawer(false)
                    .withHomeIcon(homeDrawable)
                    .withSubTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.date_picker_demo_text))
                    .withTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.app_name))
            )
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