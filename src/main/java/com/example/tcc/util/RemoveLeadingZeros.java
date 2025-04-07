package com.example.tcc.util;

public class RemoveLeadingZeros {
    public static String removeLeadingZeros(String numberStr) {
        if (numberStr == null || numberStr.isEmpty()) {
            return numberStr;
        }
        return numberStr.replaceFirst("^0+(?!$)", "");
    }
}
