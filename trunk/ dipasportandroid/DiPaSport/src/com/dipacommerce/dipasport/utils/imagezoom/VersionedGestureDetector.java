package com.dipacommerce.dipasport.utils.imagezoom;

import android.content.Context;
import android.os.Build;

public final class VersionedGestureDetector {

    public static GestureDetector newInstance(Context context, OnGestureListener listener) {
        final int sdkVersion = Build.VERSION.SDK_INT;
        GestureDetector detector;

        if (sdkVersion < Build.VERSION_CODES.FROYO) {
            detector = new EclairGestureDetector(context);
        } else {
            detector = new FroyoGestureDetector(context);
        }

        detector.setOnGestureListener(listener);

        return detector;
    }

}