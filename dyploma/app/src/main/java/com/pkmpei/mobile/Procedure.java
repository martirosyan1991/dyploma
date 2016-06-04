package com.pkmpei.mobile;

/**
 * Created by Harry on 04.06.2016.
 */
public interface Procedure<T> extends Callback<T> {

    public void onError(T input);
}
