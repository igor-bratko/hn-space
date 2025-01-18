package com.us.spaces.hr;

import com.us.framework.model.DefaultHttpResponse;
import com.us.framework.model.DependencyContainer;
import com.us.framework.model.HttpRoute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DependencyContainerImpl implements DependencyContainer {

    @Override
    public List<HttpRoute> getRoutes() {
        List<HttpRoute> routes = new ArrayList<>();

        routes.add(new HttpRoute("GET", "/api/{param}", r -> new DefaultHttpResponse("first router")));
        routes.add(new HttpRoute("POST", "/api/{param}/resource", r -> {
            var body = r.getBody(Map.class);
            return new DefaultHttpResponse(body);
        }));

        return routes;
    }
}
