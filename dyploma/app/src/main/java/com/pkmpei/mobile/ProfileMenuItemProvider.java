package com.pkmpei.mobile;

import android.content.Context;
import android.view.ActionProvider;
import android.view.MenuItem;
import android.view.View;

import com.pkmpei.mobile.Utils.Utils;

/**
 * Created by Harry on 06.08.2016.
 */
public class ProfileMenuItemProvider extends ActionProvider {
    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public ProfileMenuItemProvider(Context context) {
        super(context);
    }

    @Override
    public View onCreateActionView() {
        System.out.println("ggg onCreateActionView");
        return null;
    }

    @Override
    public boolean isVisible() {
        System.out.println("ggg isVisible = " + UserPreferences.getInstance().getImei());
        return Utils.isEmpty(UserPreferences.getInstance().getImei());
    }

    @Override
    public View onCreateActionView(MenuItem forItem) {
        System.out.println("ggg onCreateActionView 2");
        return super.onCreateActionView(forItem);
    }
}
