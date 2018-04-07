package de.metzgore.rbtvschedule.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateUtils;

import java.util.Date;

public class DateFormatter {

    public static String formatDate(@NonNull Context context, @NonNull Date date) {
        return DateUtils.formatDateTime(context, date.getTime(), DateUtils.FORMAT_SHOW_DATE);
    }
}
