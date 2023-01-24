package com.github.RooneyAndShadows.lightbulb.pickersdemo.activity

import com.github.rooneyandshadows.lightbulb.application.activity.BaseActivity

class MainActivity : BaseActivity() {
    private var router: AppRouter? = null
    protected fun initializeRouter(fragmentContainerId: Int): BaseApplicationRouter? {
        router = AppRouter(this, fragmentContainerId)
        Router.Companion.getInstance().setRouter(router)
        return router
    }

    protected fun beforeCreate(savedInstanceState: Bundle?) {
        super.beforeCreate(savedInstanceState)
        setTheme(R.style.DemoTheme)
    }

    protected fun create(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) router!!.toAdapterPickerDemo(BaseApplicationRouter.NavigationCommands.NAVIGATE_TO_AND_CLEAR_BACKSTACK)
    }
}