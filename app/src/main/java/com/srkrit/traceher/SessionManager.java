package com.srkrit.traceher;

import android.app.Activity;
import android.content.Context;


public class SessionManager {
    private static Session ourInstance = null;

    public static Session getInstance(Activity activity) {
        if (ourInstance == null)
            ourInstance = new Session(activity);
        return ourInstance;
    }

    public static Session getInstance(Context activity) {
        if (ourInstance == null)
            ourInstance = new Session(activity);
        return ourInstance;
    }

    private SessionManager() {
    }
}