package com.github.RooneyAndShadows.lightbulb.pickersdemo.activity

class Router {
    var router: AppRouter? = null

    companion object {
        @get:Synchronized
        var instance: Router? = null
            get() {
                if (null == field) {
                    field = Router()
                }
                return field
            }
            private set
    }
}