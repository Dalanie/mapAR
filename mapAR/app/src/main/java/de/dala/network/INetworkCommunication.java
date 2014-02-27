package de.dala.network;


import com.android.volley.Response;

import java.util.ArrayList;

import de.dala.common.PolygonNetworkWrapper;

public interface INetworkCommunication {
    void getObjectsFromURL(Response.Listener<ArrayList<PolygonNetworkWrapper>> successListener,
                           Response.ErrorListener errorListener, String url);
}
