package com.wearltnow.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class DateTimeFormatter {
    Map<Long, Function<Instant, String>> strategyMap = new LinkedHashMap<>();

    public DateTimeFormatter() {
        strategyMap.put(60L, this::formatInSecond);        // dưới 60 giây
        strategyMap.put(3600L, this::formatInMinute);      // dưới 1 giờ
        strategyMap.put(86400L, this::formatInHour);       // dưới 1 ngày
        strategyMap.put(604800L, this::formatInDay);       // dưới 1 tuần (7 ngày)
        strategyMap.put(2629743L, this::formatInWeek);     // dưới 1 tháng (~30 ngày)
        strategyMap.put(31556926L, this::formatInMonth);   // dưới 1 năm (~365 ngày)
        strategyMap.put(Long.MAX_VALUE, this::formatInYear); // trên 1 năm
    }

    private String formatInSecond(Instant instant) {
        long elapSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());
        return elapSeconds + " giây trước";
    }

    private String formatInMinute(Instant instant) {
        long elapseMinutes = ChronoUnit.MINUTES.between(instant, Instant.now());
        return elapseMinutes + " phút trước";
    }

    private String formatInHour(Instant instant) {
        long elapseHours = ChronoUnit.HOURS.between(instant, Instant.now());
        return elapseHours + " giờ trước";
    }

    private String formatInDay(Instant instant) {
        long elapseDays = ChronoUnit.DAYS.between(instant, Instant.now());
        return elapseDays + " ngày trước";
    }

    private String formatInWeek(Instant instant) {
        long elapseWeeks = ChronoUnit.WEEKS.between(instant, Instant.now());
        return elapseWeeks + " tuần trước";
    }

    private String formatInMonth(Instant instant) {
        long elapseMonths = ChronoUnit.MONTHS.between(instant, Instant.now());
        return elapseMonths + " tháng trước";
    }

    private String formatInYear(Instant instant) {
        long elapseYears = ChronoUnit.YEARS.between(instant, Instant.now());
        return elapseYears + " năm trước";
    }

    public String format(Instant instant) {
        long elapSeconds = ChronoUnit.SECONDS.between(instant, Instant.now());
        var strategy = strategyMap.entrySet()
                .stream()
                .filter(longFunctionEntry -> elapSeconds < longFunctionEntry.getKey())
                .findFirst()
                .get();
        return strategy.getValue().apply(instant);
    }
}
