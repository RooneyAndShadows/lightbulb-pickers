package com.github.rooneyandshadows.lightbulb.pickersdemo.fragments.adapter_picker

import android.view.View
import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity

class FragmentAdapterPickerDemo : BaseFragment() {
    private var viewModel: VMAdapterPickerDemo? = null
    private var pickerViewBoxed: DemoAdapterPickerView? = null
    private var pickerViewOutlined: DemoAdapterPickerView? = null
    private var pickerViewButton: DemoAdapterPickerView? = null
    private var pickerViewImageButton: DemoAdapterPickerView? = null
    protected fun configureFragment(): BaseFragmentConfiguration {
        val homeDrawable = ShowMenuDrawable(getContextActivity())
        homeDrawable.setEnabled(false)
        return BaseFragmentConfiguration()
            .withLeftDrawer(true)
            .withActionBarConfiguration(
                ActionBarConfiguration(R.id.toolbar)
                    .withActionButtons(true)
                    .attachToDrawer(true)
                    .withHomeIcon(homeDrawable)
                    .withSubTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.adapter_picker_demo_text))
                    .withTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.app_name))
            )
    }

    protected fun create(savedInstanceState: Bundle?) {
        super.create(savedInstanceState)
        viewModel = ViewModelProvider(this).get(VMAdapterPickerDemo::class.java)
        if (savedInstanceState == null) viewModel!!.initialize()
    }

    fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentAdapterPickerDemoBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_adapter_picker_demo, container, false)
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
        if (savedInstanceState == null) {
            pickerViewBoxed.data = viewModel!!.dataSets[0]
            pickerViewOutlined.data = viewModel!!.dataSets[1]
            pickerViewButton.data = viewModel!!.dataSets[2]
            pickerViewImageButton.data = viewModel!!.dataSets[3]
        }
    }

    protected fun selectViews() {
        super.selectViews()
        if (getView() == null) return
        pickerViewBoxed = getView().findViewById(R.id.pickerViewBoxed)
        pickerViewOutlined = getView().findViewById(R.id.pickerViewOutlined)
        pickerViewButton = getView().findViewById(R.id.pickerViewButton)
        pickerViewImageButton = getView().findViewById(R.id.pickerViewImageButton)
    }

    companion object {
        val newInstance: FragmentAdapterPickerDemo
            get() = FragmentAdapterPickerDemo()
    }
}