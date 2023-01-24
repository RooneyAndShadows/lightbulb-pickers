package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.month_picker

import android.view.View
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity

class FragmentMonthPickerDemo : BaseFragment() {
    private var viewModel: VMMonthPickerDemo? = null
    private var pickerViewBoxed: DialogMonthPickerView? = null
    private var pickerViewOutlined: DialogMonthPickerView? = null
    private var pickerViewButton: DialogMonthPickerView? = null
    private var pickerViewImageButton: DialogMonthPickerView? = null
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
                    .withSubTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.month_picker_demo_text))
                    .withTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.app_name))
            )
    }

    protected fun create(savedInstanceState: Bundle?) {
        super.create(savedInstanceState)
        viewModel = ViewModelProvider(this).get(VMMonthPickerDemo::class.java)
        if (savedInstanceState == null) viewModel!!.initialize()
    }

    fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentMonthPickerDemoBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_month_picker_demo, container, false)
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
            AppIconUtils.getIconWithAttributeColor(getContext(), DemoIconsUi.ICON_MONTH_PICKER_INDICATOR)
        pickerViewOutlined.pickerIcon =
            AppIconUtils.getIconWithAttributeColor(getContext(), DemoIconsUi.ICON_MONTH_PICKER_INDICATOR)
        pickerViewButton.setPickerIcon(
            AppIconUtils.getIconWithAttributeColor(
                getContext(),
                DemoIconsUi.ICON_MONTH_PICKER_INDICATOR
            ), color
        )
        pickerViewImageButton.setPickerIcon(
            AppIconUtils.getIconWithAttributeColor(
                getContext(),
                DemoIconsUi.ICON_MONTH_PICKER_INDICATOR
            ), color
        )
    }

    companion object {
        val newInstance: FragmentMonthPickerDemo
            get() = FragmentMonthPickerDemo()
    }
}