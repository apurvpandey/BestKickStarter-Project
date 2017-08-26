package com.apurvpandey.payuchallenge.network;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.apurvpandey.payuchallenge.MyApplication;

/**
 * Created by Apurv Pandey on 13/8/17.
 * apurvpandey@rocektmail.com
 * Rewardz Pte Ltd.
 * Contact No. - +91-8377887369
 */

public class VolleySingleton {

    static VolleySingleton instance = null;
    private RequestQueue requestQueue;


    private VolleySingleton() {
        requestQueue = Volley.newRequestQueue(MyApplication.getContext());
    }


    public static VolleySingleton getInstance() {

        if (instance == null)
            instance = new VolleySingleton();

        return instance;
    }


    public RequestQueue getRequestQueue() {
        return requestQueue;
    }
}
