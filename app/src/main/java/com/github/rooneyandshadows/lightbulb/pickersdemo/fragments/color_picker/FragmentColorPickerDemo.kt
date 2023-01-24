package com.github.RooneyAndShadows.lightbulb.pickersdemo.fragments.color_picker

import android.view.View
import com.github.RooneyAndShadows.lightbulb.application.activity.BaseActivity

class FragmentColorPickerDemo : BaseFragment() {
    private var viewModel: VMColorPickerDemo? = null
    private var pickerViewBoxed: DialogColorPickerView? = null
    private var pickerViewOutlined: DialogColorPickerView? = null
    private var pickerViewButton: DialogColorPickerView? = null
    private var pickerViewImageButton: DialogColorPickerView? = null
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
                    .withSubTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.color_picker_demo_text))
                    .withTitle(ResourceUtils.getPhrase(getContextActivity(), R.string.app_name))
            )
    }

    protected fun create(savedInstanceState: Bundle?) {
        super.create(savedInstanceState)
        viewModel = ViewModelProvider(this).get(VMColorPickerDemo::class.java)
        if (savedInstanceState == null) viewModel!!.initialize()
    }

    fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: FragmentColorPickerDemoBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_color_picker_demo, container, false)
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
        val newInstance: FragmentColorPickerDemo
            get() = FragmentColorPickerDemo()
    }
}