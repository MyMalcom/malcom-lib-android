package com.malcom.library.android.utils;

import android.content.Context;
import android.util.TypedValue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: PedroDuran
 * Date: 07/05/13
 * Time: 18:09
 * To change this template use File | Settings | File Templates.
 */
public class MCMUtils {

    /**
     * Converts pixels in density pixels.
     * @param context - Aplication context
     * @param pixels - mdpi pixels.
     * @return dpi value for current device.
     */
    public static int getDPI(Context context, int pixels) {

        float result = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, pixels, context.getResources().getDisplayMetrics());
        return Math.round(result);
    }

    /**
     * Url encoding for id. If an UnsupportedEncode exception is thrown return id un-encoded
     * @param id
     * @return
     */
    public static String getEncodedUDID(String id) {
        try {
            return URLEncoder.encode(id, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return id;
        }
    }
}
