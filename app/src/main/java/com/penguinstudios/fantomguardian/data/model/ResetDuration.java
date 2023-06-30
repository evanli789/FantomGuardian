package com.penguinstudios.fantomguardian.data.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ResetDuration {

    NO_DATE_SELECTED("Select reset duration", -1),
    ONE_HOUR("One hour", 0),
    ONE_DAY("One day", 1),
    THREE_MONTHS("Three months", 90),
    HALF_YEAR("Half year", 182),
    ONE_YEAR("One year", 365);

    private static final Map<Integer, ResetDuration> map;
    private final String name;
    private final int numDays;

    static {
        Map<Integer, ResetDuration> occupationMap = Arrays.stream(ResetDuration.values())
                .collect(Collectors.toMap(s -> s.numDays, Function.identity()));
        map = Collections.unmodifiableMap(occupationMap);
    }

    ResetDuration(String name, int numDays) {
        this.name = name;
        this.numDays = numDays;
    }

    public String getName() {
        return name;
    }

    public int getNumDays() {
        return numDays;
    }

    public static Optional<ResetDuration> of(int numDays) {
        return Optional.ofNullable(map.get(numDays));
    }
}
