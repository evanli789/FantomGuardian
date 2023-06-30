package com.penguinstudios.fantomguardian.util;

import java.util.Map;

public class MapToStringConverter {

    public static String convert(Map<Integer, CharSequence> map) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (CharSequence value : map.values()) {
            sb.append(value);
            if (i < map.size() - 1) {
                sb.append(" ");
            }
            i++;
        }
        return sb.toString();
    }
}
