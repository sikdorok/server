package com.ddd.chulsi.infrastructure.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
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

    /**
     * 해당 연도-월(yearMonth)과 주(week) 값을 받아서 해당 주의 1주일치 날짜 목록을 반환하는 메서드입니다.
     * 주의 첫번째와 마지막 날짜가 7개가 아닐 경우, 이전 달과 다음 달에서 날짜를 채워서 반환합니다.
     *
     * @param yearMonth LocalDate 타입의 연도-월 값
     * @param week int 타입의 주(week) 값
     * @return 해당 주의 1주일치 날짜 목록
     */
    public static List<LocalDate> getWeekDays(LocalDate yearMonth, int week) {
        List<LocalDate> weekDates = new ArrayList<>();
        LocalDate firstDayOfMonth = yearMonth.withDayOfMonth(1);
        LocalDate lastDayOfMonth = yearMonth.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate firstDateOfWeek = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        firstDateOfWeek = firstDateOfWeek.plusWeeks(week - 1);

        for (int i = 0; i < 7; i++) {
            weekDates.add(firstDateOfWeek.plusDays(i));
        }

        // 주의 첫번째 날짜가 이전 달의 날짜인 경우, 이전 달에서 남은 날짜를 채웁니다.
        if (weekDates.get(0).isBefore(firstDayOfMonth)) {
            LocalDate previousMonthLastDay = firstDayOfMonth.minusDays(1);
            for (int i = 0; i < 7 - weekDates.size(); i++) {
                weekDates.add(i, previousMonthLastDay.minusDays(6 - i));
            }
        }

        // 주의 마지막 날짜가 다음 달의 날짜인 경우, 다음 달에서 남은 날짜를 채웁니다.
        if (weekDates.get(weekDates.size() - 1).isAfter(lastDayOfMonth)) {
            LocalDate nextMonthFirstDay = lastDayOfMonth.plusDays(1);
            for (int i = 0; i < 7 - weekDates.size(); i++) {
                weekDates.add(nextMonthFirstDay.plusDays(i));
            }
        }

        return weekDates;
    }

}
