/*
 * Copyright (C) 2012-2016 The Android Money Manager Ex Project Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.money.manager.ex.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.DatePicker;

import com.money.manager.ex.Constants;
import com.money.manager.ex.MoneyManagerApplication;
import com.money.manager.ex.R;
import com.money.manager.ex.core.DateRange;
import com.money.manager.ex.core.InfoKeys;
import com.money.manager.ex.servicelayer.InfoService;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Locale;

/**
 * Utilities for DateTime.
 * Most methods specify UTC as the time zone, since time zones play no role in MMEX as we work
 * only with dates, not times. Many exceptions happen in different time zones during DST,
 * especially the ones that transition at midnight.
 * However, using UTC has implications in that the filter date/times are not correct when using Today.
 */
public class MmxDateTimeUtils {

    public static DateTime now() {
        return DateTime.now();
    }

    public static DateTime today() {
        // removing DateTimeZone.UTC
        return new DateTime()
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0)
                .withMillisOfSecond(0);
    }

    public static DateTime from(String isoString) {
        if (TextUtils.isEmpty(isoString)) return null;

        String pattern = Constants.ISO_DATE_FORMAT;
        return from(isoString, pattern);
    }

    public static DateTime from(String dateString, String pattern) {
        if (TextUtils.isEmpty(dateString)) return null;

        DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
//        DateTime dateTime = format.parseDateTime(dateString);
        DateTime dateTime = format // .withZoneUTC()
                .parseDateTime(dateString);
        return dateTime;
    }

    /**
     * Conversion factory.
     * @param calendar The date to use as the base.
     * @return The DateTime instance of the date.
     */
    public static DateTime from(Calendar calendar) {
        if (calendar == null) return null;

        DateTime result = new DateTime(calendar.getTime());
        return result;
    }

    public static DateTime from(int year, int monthOfYear, int dayOfMonth) {
        //DateTimeZone.setDefault(DateTimeZone.UTC); <-- sets the default for JodaTime.
        // DateTimeZone.UTC
        return new DateTime()
                .withYear(year)
                .withMonthOfYear(monthOfYear)
                .withDayOfMonth(dayOfMonth)
                .withHourOfDay(0)
                .withMinuteOfHour(0)
                .withSecondOfMinute(0);
    }

    public static DateTime from(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();

        return new DateTime(year, month, day, 0, 0, 0, 0);
    }

    public static String getDateStringFrom(DateTime dateTime, String pattern) {
        DateTimeFormatter format = DateTimeFormat.forPattern(pattern);
        String result = format.print(dateTime);
        return result;
    }

    public static int getFirstDayOfWeek() {
        Locale appLocale = MoneyManagerApplication.getApp().getAppLocale();
        Calendar cal = Calendar.getInstance(appLocale);
        return cal.getFirstDayOfWeek();
    }

    public static String getIsoStringFrom(DateTime dateTime) {
        if (dateTime == null) return null;

        return dateTime.toString(Constants.ISO_DATE_FORMAT);
    }

    /**
     * Gets the last day of the month in which the given date occurs.
     * @param dateTime The date/time for which to find the last day of the month.
     * @return Last calendar day of the month. Date only - the lesser units are reset to 0.
     */
    public static DateTime getLastDayOfMonth(DateTime dateTime) {
        return dateTime
            .withHourOfDay(0).withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)
            .dayOfMonth().withMaximumValue();
    }

    public static void setDatePicker(DateTime date, DatePicker datePicker) {
        datePicker.updateDate(date.getYear(), date.getMonthOfYear() - 1, date.getDayOfMonth());
    }

    /*
        Non-static methods
     */

    public MmxDateTimeUtils(Context context) {
        this.context = context;
    }

    private Context context;

    private Context getContext() {
        return context;
    }

    /**
     *
     * @param resourceId String Id for name of the period.
     * @return Date range that matches the period selected.
     */
    public DateRange getDateRangeForPeriod(int resourceId) {
        String value = getContext().getString(resourceId);
        return getDateRangeForPeriod(value);
    }

    /**
     * Creates a date range from the period name. Used when selecting a date range from the
     * localized menus.
     * @param period Period name in local language.
     * @return Date Range object.
     */
    public DateRange getDateRangeForPeriod(String period) {
        if (TextUtils.isEmpty(period)) return null;

        DateTime dateFrom;
        DateTime dateTo;

        // we ignore the minutes at the moment, since the field in the db only stores the date value.

        if (period.equalsIgnoreCase(getContext().getString(R.string.all_transaction)) ||
                period.equalsIgnoreCase(context.getString(R.string.all_time))) {
            // All transactions.
            dateFrom = MmxDateTimeUtils.today().minusYears(1000);
            dateTo = MmxDateTimeUtils.today().plusYears(1000);
        } else if (period.equalsIgnoreCase(context.getString(R.string.today))) {
            dateFrom = MmxDateTimeUtils.today();
            dateTo = MmxDateTimeUtils.today();
        } else if (period.equalsIgnoreCase(context.getString(R.string.last7days))) {
            dateFrom = MmxDateTimeUtils.today().minusDays(7);
            dateTo = MmxDateTimeUtils.today();
        } else if (period.equalsIgnoreCase(context.getString(R.string.last15days))) {
            dateFrom = MmxDateTimeUtils.today().minusDays(14);
            dateTo = MmxDateTimeUtils.today();
        } else if (period.equalsIgnoreCase(context.getString(R.string.current_month))) {
            dateFrom = MmxDateTimeUtils.today().dayOfMonth().withMinimumValue();
            dateTo = MmxDateTimeUtils.today().dayOfMonth().withMaximumValue();
        } else if (period.equalsIgnoreCase(context.getString(R.string.last30days))) {
            dateFrom = MmxDateTimeUtils.today().minusDays(30);
            dateTo = MmxDateTimeUtils.today();
        } else if (period.equalsIgnoreCase(context.getString(R.string.last3months))) {
            dateFrom = MmxDateTimeUtils.today().minusMonths(3)
                    .dayOfMonth().withMinimumValue();
            dateTo = MmxDateTimeUtils.today();
        } else if (period.equalsIgnoreCase(context.getString(R.string.last6months))) {
            dateFrom = MmxDateTimeUtils.today().minusMonths(6)
                    .dayOfMonth().withMinimumValue();
            dateTo = MmxDateTimeUtils.today();
        } else if (period.equalsIgnoreCase(context.getString(R.string.current_year))) {
            dateFrom = MmxDateTimeUtils.today().monthOfYear().withMinimumValue()
                    .dayOfMonth().withMinimumValue();
            dateTo = MmxDateTimeUtils.today().monthOfYear().withMaximumValue()
                    .dayOfMonth().withMaximumValue();
        } else if (period.equalsIgnoreCase(context.getString(R.string.future_transactions))) {
            // Future transactions
            dateFrom = MmxDateTimeUtils.today().plusDays(1);
            dateTo = MmxDateTimeUtils.today().plusYears(1000);
        } else {
            dateFrom = null;
            dateTo = null;
        }

        DateRange result = new DateRange(dateFrom, dateTo);
        return result;
    }

    /**
     * Get pattern defined by the user.
     * @return pattern user define
     */
    public String getUserDatePattern() {
        InfoService service = new InfoService(getContext());
        String pattern = service.getInfoValue(InfoKeys.DATEFORMAT);

        if (!TextUtils.isEmpty(pattern)) {
            //replace part of pattern
            pattern = pattern.replace("%d", "dd").replace("%m", "MM")
                    .replace("%y", "yy").replace("%Y", "yyyy")
                    .replace("'", "''");
        }

        // && getContext().getResources().getStringArray(R.array.date_format_mask) != null
        if (TextUtils.isEmpty(pattern)
                && getContext().getResources().getStringArray(R.array.date_format_mask).length > 0){
            pattern= getContext().getResources().getStringArray(R.array.date_format_mask)[0];
            pattern = pattern.replace("%d", "dd").replace("%m", "MM")
                    .replace("%y", "yy").replace("%Y", "yyyy")
                    .replace("'", "''");
        }

        return pattern;
    }

    public String getUserStringFromDateTime(DateTime dateTime) {
        if (dateTime == null) return "";

        String userDatePattern = getUserDatePattern();

        return dateTime.toString(userDatePattern);
    }
}
