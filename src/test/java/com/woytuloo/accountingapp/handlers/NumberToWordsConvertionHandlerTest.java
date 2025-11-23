package com.woytuloo.accountingapp.handlers;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.Mockito;

import java.io.PrintStream;

public class NumberToWordsConvertionHandlerTest {

    @Test
    public void testNumberToWords_ThousandPluralizationVariants() {
        assertEquals("tysiąc", NumberToWordsConvertionHandler.numberToWords(1000));
        assertEquals("dwa tysiące", NumberToWordsConvertionHandler.numberToWords(2000));
        assertEquals("trzy tysiące", NumberToWordsConvertionHandler.numberToWords(3000));
        assertEquals("cztery tysiące", NumberToWordsConvertionHandler.numberToWords(4000));
        assertEquals("pięć tysięcy", NumberToWordsConvertionHandler.numberToWords(5000));
        assertEquals("dziesięć tysięcy", NumberToWordsConvertionHandler.numberToWords(10000));
        assertEquals("dwanaście tysięcy", NumberToWordsConvertionHandler.numberToWords(12000)); // teen exception
        assertEquals("dwadzieścia dwa tysiące", NumberToWordsConvertionHandler.numberToWords(22000));
    }

    @Test
    public void testNumberToWords_UpTo999AllPatterns() {
        assertEquals("siedem", NumberToWordsConvertionHandler.numberToWords(7));
        assertEquals("dziesięć", NumberToWordsConvertionHandler.numberToWords(10));
        assertEquals("jedenaście", NumberToWordsConvertionHandler.numberToWords(11));
        assertEquals("dziewiętnaście", NumberToWordsConvertionHandler.numberToWords(19));
        assertEquals("dwadzieścia", NumberToWordsConvertionHandler.numberToWords(20));
        assertEquals("dwadzieścia jeden", NumberToWordsConvertionHandler.numberToWords(21));
        assertEquals("sto", NumberToWordsConvertionHandler.numberToWords(100));
        assertEquals("dwieście", NumberToWordsConvertionHandler.numberToWords(200));
        assertEquals("trzysta czterdzieści pięć", NumberToWordsConvertionHandler.numberToWords(345));
        assertEquals("dziewięćset dziewięćdziesiąt dziewięć", NumberToWordsConvertionHandler.numberToWords(999));
    }

    @Test
    public void testNumberToWords_ThousandsWithRemainder() {
        assertEquals("dwa tysiące trzysta czterdzieści pięć", NumberToWordsConvertionHandler.numberToWords(2345));
        assertEquals("tysiąc jeden", NumberToWordsConvertionHandler.numberToWords(1001));
        assertEquals("pięć tysięcy siedem", NumberToWordsConvertionHandler.numberToWords(5007));
    }

    @Test
    public void testNumberToWords_Zero() {
        // Mock System.out to demonstrate Mockito usage and ensure no output during this test
        PrintStream originalOut = System.out;
        PrintStream mockOut = Mockito.mock(PrintStream.class);
        try {
            System.setOut(mockOut);
            assertEquals("zero", NumberToWordsConvertionHandler.numberToWords(0));
            Mockito.verifyNoInteractions(mockOut);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void testNumberToWords_NegativeNumberThrows() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> NumberToWordsConvertionHandler.numberToWords(-1));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> NumberToWordsConvertionHandler.numberToWords(-123));
    }

    @Test
    public void testNumberToWords_MillionAndAboveThrows() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> NumberToWordsConvertionHandler.numberToWords(1_000_000));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> NumberToWordsConvertionHandler.numberToWords(2_000_000));
    }
}
