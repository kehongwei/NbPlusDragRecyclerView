package com.xk.nblm.vlayout.extend;

import android.view.View;

public interface ViewLifeCycleListener {
    void onAppearing(View view);

    void onDisappearing(View view);

    void onAppeared(View view);

    void onDisappeared(View view);
}
