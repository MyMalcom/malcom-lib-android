package com.malcom.library.android.utils;

import android.content.Context;
import android.util.TypedValue;

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
}
