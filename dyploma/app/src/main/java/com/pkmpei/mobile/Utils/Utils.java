package com.pkmpei.mobile.Utils;

import android.content.Context;
import android.util.Log;

import com.pkmpei.mobile.News;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static final String TAG = "Utils";

    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() <= 0;
    }
}
