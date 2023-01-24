package com.github.rooneyandshadowss.lightbulb.pickersdemo.activity;

public class Router {
    private static Router instance = null;
    private AppRouter router;

    public static synchronized Router getInstance() {
        if (null == instance) {
            instance = new Router();
        }
        return instance;
    }

    public AppRouter getRouter() {
        return router;
    }

    public void setRouter(AppRouter router) {
        this.router = router;
    }
}