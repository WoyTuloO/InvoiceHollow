package com.woytuloo.accountingapp.handlers;

public class NumberToWordsConverter {
    private static final String[] JEDNOSCI = {
            "", "jeden", "dwa", "trzy", "cztery", "pięć", "sześć", "siedem", "osiem", "dziewięć"
    };

    private static final String[] NASTKI = {
            "dziesięć", "jedenaście", "dwanaście", "trzynaście", "czternaście",
            "piętnaście", "szesnaście", "siedemnaście", "osiemnaście", "dziewiętnaście"
    };

    private static final String[] DZIESIATKI = {
            "", "", "dwadzieścia", "trzydzieści", "czterdzieści",
            "pięćdziesiąt", "sześćdziesiąt", "siedemdziesiąt", "osiemdziesiąt", "dziewięćdziesiąt"
    };

    private static final String[] SETKI = {
            "", "sto", "dwieście", "trzysta", "czterysta",
            "pięćset", "sześćset", "siedemset", "osiemset", "dziewięćset"
    };

    private static final String[][] TYSIACE = {
            {"", "", ""},
            {"tysiąc", "tysiące", "tysięcy"}
    };

    public static String numberToWords(int number) {
        if (number == 0) return "zero";

        StringBuilder output = new StringBuilder();

        if (number >= 1000) {
            int tysiace = number / 1000;
            number %= 1000;

            if (tysiace > 1) output.append(Convert999(tysiace)).append(" ");
            output.append(thousandConv(tysiace)).append(" ");
        }

        output.append(Convert999(number));

        return output.toString().trim();
    }

    private static String Convert999(int number) {
        if (number == 0) return "";

        StringBuilder output = new StringBuilder();

        output.append(SETKI[number / 100]).append(" ");
        number %= 100;

        if (number >= 10 && number < 20) {
            output.append(NASTKI[number - 10]);
        } else {
            output.append(DZIESIATKI[number / 10]).append(" ");
            output.append(JEDNOSCI[number % 10]);
        }

        return output.toString().trim();
    }

    private static String thousandConv(int number) {
        if (number == 1) return TYSIACE[1][0]; // tysiąc
        if (number % 10 >= 2 && number % 10 <= 4 && (number % 100 < 10 || number % 100 >= 20)) {
            return TYSIACE[1][1]; // tysiące
        }
        return TYSIACE[1][2]; // tysięcy
    }

    public static void main(String[] args) {
        System.out.println(numberToWords(1234));
        System.out.println(numberToWords(2050));
        System.out.println(numberToWords(101));
        System.out.println(numberToWords(9999));
    }
}

