package com.us.spaces.hr;

import com.us.framework.netty.Router;
import com.us.framework.netty.WebServer;

public class UserMain {

    public static void main(String[] args) throws InterruptedException {
        var container = new DependencyContainerImpl();

        var router = new Router(container.getRoutes());

        new WebServer(router, 8080).start();
    }
}
