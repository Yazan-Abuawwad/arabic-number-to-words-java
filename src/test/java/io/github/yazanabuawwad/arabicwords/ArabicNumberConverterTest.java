package io.github.yazanabuawwad.arabicwords;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.math.BigInteger;

import static io.github.yazanabuawwad.arabicwords.ArabicNumberConverter.toCurrencyWords;
import static io.github.yazanabuawwad.arabicwords.ArabicNumberConverter.toWords;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArabicNumberConverterTest {

    @ParameterizedTest
    @CsvSource({
            "0, صفر",
            "1, واحد",
            "2, اثنان",
            "3, ثلاثة",
            "10, عشرة",
            "11, أحد عشر",
            "12, اثنا عشر",
            "19, تسعة عشر",
            "20, عشرون",
            "21, واحد وعشرون",
            "99, تسعة وتسعون",
            "100, مائة",
            "101, مائة وواحد",
            "200, مئتان",
            "250, مئتان وخمسون",
            "999, تسعمائة وتسعة وتسعون",
            "1000, ألف",
            "1001, ألف وواحد",
            "2000, ألفان",
            "3000, ثلاثة آلاف",
            "10000, عشرة آلاف",
            "11000, أحد عشر ألفًا",
            "100000, مائة ألف",
            "102000, مائة واثنان ألف",
            "200000, مئتا ألف",
            "1234, ألف ومئتان وأربعة وثلاثون",
            "1000000, مليون",
            "2000000, مليونان",
            "5000000, خمسة ملايين",
            "1000000000, مليار",
            "1000001, مليون وواحد",
            "-15, سالب خمسة عشر",
    })
    void integers(long number, String expected) {
        assertEquals(expected, toWords(number));
    }

    @Test
    void veryLargeNumbers() {
        assertEquals("ديسيليون", toWords(BigInteger.TEN.pow(33)));
        assertEquals("تسعمائة وتسعة وتسعون ديسيليونًا وتسعمائة وتسعة وتسعون نونليونًا",
                toWords(new BigInteger("999999000000000000000000000000000000")));
        assertThrows(IllegalArgumentException.class, () -> toWords(BigInteger.TEN.pow(36)));
    }

    @ParameterizedTest
    @CsvSource({
            "1, واحدة",
            "2, اثنتان",
            "3, ثلاث",
            "11, إحدى عشرة",
            "21, إحدى وعشرون",
            "38, ثماني وثلاثون",
            "1003, ألف وثلاث",
    })
    void feminine(long number, String expected) {
        assertEquals(expected, toWords(BigInteger.valueOf(number), Gender.FEMININE));
    }

    @ParameterizedTest
    @CsvSource({
            "3.5, ثلاثة فاصلة خمسة",
            "12.05, اثنا عشر فاصلة صفر خمسة",
            "0.25, صفر فاصلة خمسة وعشرون",
            "7.00, سبعة",
            "-1.5, سالب واحد فاصلة خمسة",
    })
    void decimals(String number, String expected) {
        assertEquals(expected, toWords(number));
        assertEquals(expected, toWords(new BigDecimal(number)));
    }

    @Test
    void parsesFormattedText() {
        assertEquals("مليون ومئتان وخمسون ألفًا", toWords("1,250,000"));
        assertEquals("مائة وخمسة وعشرون", toWords("١٢٥"));
        assertEquals("ثلاثة فاصلة خمسة", toWords("٣٫٥"));
        assertThrows(NumberFormatException.class, () -> toWords("abc"));
    }

    @ParameterizedTest
    @CsvSource({
            "0, صفر دينار",
            "1, دينار واحد",
            "2, ديناران",
            "3, ثلاثة دنانير",
            "10, عشرة دنانير",
            "11, أحد عشر دينارًا",
            "25, خمسة وعشرون دينارًا",
            "100, مائة دينار",
            "101, مائة وواحد دينار",
            "200, مئتا دينار",
            "1000, ألف دينار",
            "2000, ألفا دينار",
            "12000, اثنا عشر ألف دينار",
            "1000000, مليون دينار",
            "1.000, دينار واحد",
            "0.500, خمسمائة فلس",
            "0.001, فلس واحد",
            "0.002, فلسان",
            "0.005, خمسة فلوس",
            "5.250, خمسة دنانير ومئتان وخمسون فلسًا",
            "1250.750, ألف ومئتان وخمسون دينارًا وسبعمائة وخمسون فلسًا",
            "2.0004, ديناران",
            "-7.5, سالب سبعة دنانير وخمسمائة فلس",
    })
    void jordanianDinar(String amount, String expected) {
        assertEquals(expected, toCurrencyWords(amount));
    }

    @Test
    void otherCurrencies() {
        assertEquals("مائة ريال وخمس وعشرون هللةً", toCurrencyWords("100.25", ArabicCurrency.SAR));
        assertEquals("ريالان وهللة واحدة", toCurrencyWords("2.01", ArabicCurrency.SAR));
        assertEquals("ثلاثة دولارات وتسعة وتسعون سنتًا", toCurrencyWords(3.99, ArabicCurrency.USD));
        assertEquals("عشرة دولارات", toCurrencyWords("9.999", ArabicCurrency.USD));
    }

    @Test
    void customNoun() {
        ArabicNoun lira = ArabicNoun.of("ليرة", "ليرتان", "ليرات", "ليرةً", Gender.FEMININE);
        assertEquals("ليرة واحدة", toWords(1, lira));
        assertEquals("ليرتان", toWords(2, lira));
        assertEquals("ثلاث ليرات", toWords(3, lira));
        assertEquals("إحدى عشرة ليرةً", toWords(11, lira));
        assertEquals("مئتا ليرة", toWords(200, lira));
    }

    @ParameterizedTest
    @CsvSource({
            "1, واحد, واحدة",
            "2, اثنان, اثنتان",
            "8, ثمانية, ثماني",
            "10, عشرة, عشر",
            "12, اثنا عشر, اثنتا عشرة",
            "18, ثمانية عشر, ثماني عشرة",
            "31, واحد وثلاثون, إحدى وثلاثون",
            "103, مائة وثلاثة, مائة وثلاث",
            "3005, ثلاثة آلاف وخمسة, ثلاثة آلاف وخمس",
    })
    void bothGenders(long number, String masculine, String feminine) {
        assertEquals(masculine, toWords(number, Gender.MASCULINE));
        assertEquals(feminine, toWords(number, Gender.FEMININE));
        assertEquals(feminine, toWords(String.valueOf(number), Gender.FEMININE));
    }

    @Test
    void feminineDecimals() {
        assertEquals("ثلاث فاصلة واحدة", toWords(new BigDecimal("3.1"), Gender.FEMININE));
    }

    @ParameterizedTest
    @CsvSource({
            // feminine unit (ليرة) with masculine sub-unit (قرش)
            "LBP, 1, ليرة واحدة",
            "LBP, 2, ليرتان",
            "LBP, 3, ثلاث ليرات",
            "LBP, 11, إحدى عشرة ليرةً",
            "LBP, 22, اثنتان وعشرون ليرةً",
            "LBP, 200, مئتا ليرة",
            "LBP, 3000, ثلاثة آلاف ليرة",
            "SYP, 2.03, ليرتان وثلاثة قروش",
            // masculine unit with feminine sub-unit
            "OMR, 0.001, بيسة واحدة",
            "OMR, 0.002, بيستان",
            "OMR, 5.008, خمسة ريالات وثماني بيسات",
            "SAR, 0.12, اثنتا عشرة هللةً",
            // feminine unit with a non-decimal sub-unit (1 أوقية = 5 خمس)
            "MRU, 1.2, أوقية واحدة وخمس واحد",
            "MRU, 3.4, ثلاث أوقيات وخمسان",
            "MRU, 0.99, أوقية واحدة",
            // the other Arab currencies
            "BHD, 4.100, أربعة دنانير ومائة فلس",
            "IQD, 25000, خمسة وعشرون ألف دينار",
            "LYD, 1.250, دينار واحد ومئتان وخمسون درهمًا",
            "TND, 7.005, سبعة دنانير وخمسة مليمات",
            "DZD, 1.50, دينار واحد وخمسون سنتيمًا",
            "QAR, 10.10, عشرة ريالات وعشرة دراهم",
            "YER, 3, ثلاثة ريالات",
            "MAD, 2.5, درهمان وخمسون سنتيمًا",
            "SDG, 11.11, أحد عشر جنيهًا وأحد عشر قرشًا",
            "SOS, 6, ستة شلنات",
            "DJF, 1000, ألف فرنك",
            "KMF, 2, فرنكان",
            "GBP, 1.01, جنيه إسترليني واحد وبنس واحد",
            "GBP, 15, خمسة عشر جنيهًا إسترلينيًا",
            "EUR, 4, أربعة يوروهات",
    })
    void predefinedCurrencies(String code, String amount, String expected) {
        assertEquals(expected, toCurrencyWords(amount, code));
    }

    @Test
    void everyPredefinedCurrencyWorks() {
        assertEquals(24, ArabicCurrency.predefined().size());
        for (ArabicCurrency currency : ArabicCurrency.predefined().values()) {
            assertEquals(currency, ArabicCurrency.forCode(currency.code().toLowerCase()));
            toCurrencyWords("1234567.89", currency);
        }
        assertThrows(IllegalArgumentException.class, () -> ArabicCurrency.forCode("XYZ"));
    }

    @Test
    void customCurrency() {
        ArabicCurrency ils = ArabicCurrency.of("ILS",
                ArabicNoun.of("شيكل", "شيكلان", "شواكل", "شيكلًا", Gender.MASCULINE),
                ArabicNoun.of("أغورة", "أغورتان", "أغورات", "أغورةً", Gender.FEMININE),
                100);
        assertEquals("ثلاثة شواكل وثلاث أغورات", toCurrencyWords("3.03", ils));

        ArabicNoun noun = ArabicNoun.of("x", "y", "z", "w", Gender.MASCULINE);
        assertThrows(IllegalArgumentException.class, () -> ArabicCurrency.of("X", noun, noun, 1));
    }
}
