package de.dala.network;


import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import de.dala.common.PolygonNetworkWrapper;

public class NetworkCommunication extends AbstractNetworkCommunication implements INetworkCommunication {

    @Override
    public String getResource() {
        return "";
    }

    @Override
    public void getObjectsFromURL(Response.Listener<ArrayList<PolygonNetworkWrapper>> successListener,
                                  Response.ErrorListener errorListener, String url) {
        Type type = new TypeToken<ArrayList<PolygonNetworkWrapper>>(){}.getType();
        Request<?> request = new GsonRequest<ArrayList<PolygonNetworkWrapper>>(Request.Method.POST,
                getUrlWithParams(""), type, successListener,
                errorListener);
        addRequest(request);
    }
}
