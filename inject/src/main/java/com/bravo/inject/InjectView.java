package com.bravo.inject;

import android.app.Activity;

/**
 * Created by bravo.lee on 2017/10/24.
 */

public class InjectView {
    public  static  void bind(Activity activity)
    {
        String clsName=activity.getClass().getName();
        try {
            Class<?> viewBidClass=Class.forName(clsName+"$$ViewBinder");
            ViewBinder viewBinder= (ViewBinder) viewBidClass.newInstance();
            viewBinder.bind(activity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
