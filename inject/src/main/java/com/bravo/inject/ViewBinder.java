package com.bravo.inject;

import android.app.Activity;

/**
 * Created by bravo.lee on 2017/10/24.
 */

public interface ViewBinder<T> {
    void bind(T target);
}
