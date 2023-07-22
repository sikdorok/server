package com.ddd.chulsi.infrastructure.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import static java.time.temporal.TemporalAdjusters.nextOrSame;
import static java.time.temporal.TemporalAdjusters.previousOrSame;

public class DateUtil {

    public static final String CREATE_TIMEZONE = "Asia/Seoul";
    public static final String FORMAT_TIMEZONE = "";
    public static final String FORMAT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String FORMAT_TIME_PATTERN = "HH:mm";
    public static final String FORMAT_DATETIME_PATTERN1 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATETIME_PATTERN2 = "yyyy-MM-dd HH:mm";

    public static LocalDate getNowDate() {
        return LocalDate.now(ZoneId.of(CREATE_TIMEZONE));
    }

    public static LocalDateTime getNowDateTime() {
        return LocalDateTime.now(ZoneId.of(CREATE_TIMEZONE));
    }

    public static LocalTime getNowTime() {
        return LocalTime.now(ZoneId.of(CREATE_TIMEZONE));
    }

    public static int getAge(LocalDate birthday) {
        Period period = Period.between(birthday, getNowDate());
        return period.getYears();
    }

    public static int getAge(LocalDate birthday, LocalDate standardDate) {
        Period period = Period.between(birthday, standardDate);
        return period.getYears();
    }

    public static String getYoil(LocalDate date) {
        return date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
    }

    public static LocalDate getWeeksFirstDate(LocalDate date) {
        final LocalDate first = date.with(previousOrSame(DayOfWeek.MONDAY));
        if (!first.getMonth().equals(date.getMonth())) return date.withDayOfMonth(1);
        return first;
    }

    public static LocalDate getWeeksLastDate(LocalDate date) {
        final LocalDate last = date.with(nextOrSame(DayOfWeek.SUNDAY));
        if (!last.getMonth().equals(date.getMonth())) return date.withDayOfMonth(date.lengthOfMonth());
        return last;
    }

    public static String getWeeksPeriod(LocalDate date) {
        return getWeeksFirstDate(date).format(DateTimeFormatter.ofPattern("dd")) + "~" + DateUtil.getWeeksLastDate(date).format(DateTimeFormatter.ofPattern("dd"));
    }

    public static LocalDate getFirstDateOfYear(Year year) {
        return LocalDate.of(year.getValue(), 1, 1);
    }

    public static LocalDate getLastDateOfYear(Year year) {
        return YearMonth.of(year.getValue(), 12).atEndOfMonth();
    }

    public static LocalDate getFirstDateOfMonth(YearMonth yearMonth) {
        return LocalDate.of(yearMonth.getYear(), yearMonth.getMonth(), 1);
    }

    public static LocalDate getLastDateOfMonth(YearMonth yearMonth) {
        return yearMonth.atEndOfMonth();
    }

}
