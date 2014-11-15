package com.dipacommerce.dipasport.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeUtils {

    public static final String DATE_TIME_PATTERN = "MM/dd/yyyy hh:mm a";

    public static String ConvertToFullDateTime(long _milliSecconds, String _pattern) {
        String _datetime = new SimpleDateFormat(_pattern, Locale.getDefault()).format(new Date(_milliSecconds));
        return _datetime;
    }
}
