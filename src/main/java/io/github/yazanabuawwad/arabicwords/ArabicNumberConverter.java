package io.github.yazanabuawwad.arabicwords;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Converts numbers and money amounts into Arabic words (تفقيط).
 *
 * <pre>{@code
 * ArabicNumberConverter.toWords(1234);                              // ألف ومئتان وأربعة وثلاثون
 * ArabicNumberConverter.toWords(new BigDecimal("3.25"));            // ثلاثة فاصلة خمسة وعشرون
 * ArabicNumberConverter.toCurrencyWords("1250.750", ArabicCurrency.JOD);
 *                                    // ألف ومئتان وخمسون دينارًا وسبعمائة وخمسون فلسًا
 * }</pre>
 *
 * Supports integers up to {@link #MAX_VALUE} (36 digits). All methods are static, stateless and thread-safe.
 *
 * @author github.com/Yazan-Abuawwad
 */
public final class ArabicNumberConverter {

    /** Largest supported absolute value: 10^36 - 1 (just under one undecillion). */
    public static final BigInteger MAX_VALUE = BigInteger.TEN.pow(36).subtract(BigInteger.ONE);

    private static final String ZERO = "صفر";
    private static final String NEGATIVE = "سالب";
    private static final String DECIMAL_POINT = "فاصلة";
    private static final String AND = " و";

    private static final BigInteger THOUSAND = BigInteger.valueOf(1000);
    private static final BigInteger HUNDRED = BigInteger.valueOf(100);

    private static final String[] ONES_MASCULINE = {
            "", "واحد", "اثنان", "ثلاثة", "أربعة", "خمسة", "ستة", "سبعة", "ثمانية", "تسعة", "عشرة",
            "أحد عشر", "اثنا عشر", "ثلاثة عشر", "أربعة عشر", "خمسة عشر",
            "ستة عشر", "سبعة عشر", "ثمانية عشر", "تسعة عشر"};

    private static final String[] ONES_FEMININE = {
            "", "واحدة", "اثنتان", "ثلاث", "أربع", "خمس", "ست", "سبع", "ثماني", "تسع", "عشر",
            "إحدى عشرة", "اثنتا عشرة", "ثلاث عشرة", "أربع عشرة", "خمس عشرة",
            "ست عشرة", "سبع عشرة", "ثماني عشرة", "تسع عشرة"};

    private static final String[] TENS = {
            "", "", "عشرون", "ثلاثون", "أربعون", "خمسون", "ستون", "سبعون", "ثمانون", "تسعون"};

    private static final String[] HUNDREDS = {
            "", "مائة", "مئتان", "ثلاثمائة", "أربعمائة", "خمسمائة", "ستمائة", "سبعمائة", "ثمانمائة", "تسعمائة"};

    private static final String HUNDREDS_TWO_CONSTRUCT = "مئتا";

    /** Index = power of 1000. Index 0 (units) has no scale word. */
    private static final ArabicNoun[] SCALES = {
            null,
            scale("ألف", "ألفان", "آلاف", "ألفًا"),
            scale("مليون", "مليونان", "ملايين", "مليونًا"),
            scale("مليار", "ملياران", "مليارات", "مليارًا"),
            scale("تريليون", "تريليونان", "تريليونات", "تريليونًا"),
            scale("كوادريليون", "كوادريليونان", "كوادريليونات", "كوادريليونًا"),
            scale("كوينتليون", "كوينتليونان", "كوينتليونات", "كوينتليونًا"),
            scale("سكستليون", "سكستليونان", "سكستليونات", "سكستليونًا"),
            scale("سبتليون", "سبتليونان", "سبتليونات", "سبتليونًا"),
            scale("أوكتليون", "أوكتليونان", "أوكتليونات", "أوكتليونًا"),
            scale("نونليون", "نونليونان", "نونليونات", "نونليونًا"),
            scale("ديسيليون", "ديسيليونان", "ديسيليونات", "ديسيليونًا"),
    };

    private ArabicNumberConverter() {
    }

    // ------------------------------------------------------------------ plain numbers

    /** Converts an integer to Arabic words, e.g. {@code 25 → خمسة وعشرون}. */
    public static String toWords(long number) {
        return toWords(BigInteger.valueOf(number), Gender.MASCULINE);
    }

    /**
     * Converts an integer to Arabic words in the given gender,
     * e.g. {@code 3, FEMININE → ثلاث}, {@code 3, MASCULINE → ثلاثة}.
     */
    public static String toWords(long number, Gender gender) {
        return toWords(BigInteger.valueOf(number), gender);
    }

    /** Converts an integer of any size (up to {@link #MAX_VALUE}) to Arabic words. */
    public static String toWords(BigInteger number) {
        return toWords(number, Gender.MASCULINE);
    }

    /**
     * Converts an integer to Arabic words using the given gender,
     * e.g. {@code 3, FEMININE → ثلاث}, {@code 3, MASCULINE → ثلاثة}.
     */
    public static String toWords(BigInteger number, Gender gender) {
        Objects.requireNonNull(number, "number");
        Objects.requireNonNull(gender, "gender");
        checkRange(number);
        if (number.signum() == 0) {
            return ZERO;
        }
        String words = integerWords(number.abs(), gender, false);
        return number.signum() < 0 ? NEGATIVE + " " + words : words;
    }

    /**
     * Converts a decimal number to Arabic words. The fraction is read after the word "فاصلة"
     * with leading zeros spelled out: {@code 12.05 → اثنا عشر فاصلة صفر خمسة}.
     */
    public static String toWords(BigDecimal number) {
        return toWords(number, Gender.MASCULINE);
    }

    /**
     * Same as {@link #toWords(BigDecimal)} with the given gender for both the whole part and the fraction:
     * {@code 3.1, FEMININE → ثلاث فاصلة واحدة}.
     */
    public static String toWords(BigDecimal number, Gender gender) {
        Objects.requireNonNull(number, "number");
        Objects.requireNonNull(gender, "gender");
        BigDecimal abs = number.abs().stripTrailingZeros();
        BigInteger whole = abs.toBigInteger();
        checkRange(whole);

        String words = whole.signum() == 0 ? ZERO : integerWords(whole, gender, false);
        if (abs.scale() > 0) {
            String fraction = abs.subtract(new BigDecimal(whole)).toPlainString();
            fraction = fraction.substring(fraction.indexOf('.') + 1);
            StringBuilder sb = new StringBuilder(words).append(' ').append(DECIMAL_POINT);
            int i = 0;
            while (i < fraction.length() - 1 && fraction.charAt(i) == '0') {
                sb.append(' ').append(ZERO);
                i++;
            }
            sb.append(' ').append(toWords(new BigInteger(fraction.substring(i)), gender));
            words = sb.toString();
        }
        return number.signum() < 0 ? NEGATIVE + " " + words : words;
    }

    /**
     * Parses and converts a number given as text. Accepts Western ({@code 0-9}) and
     * Arabic-Indic ({@code ٠-٩}) digits, an optional sign, a decimal point ({@code .} or {@code ٫})
     * and thousands separators ({@code ,} {@code ٬} {@code _} or spaces).
     *
     * @throws NumberFormatException if the text is not a number
     */
    public static String toWords(String number) {
        return toWords(number, Gender.MASCULINE);
    }

    /** Same as {@link #toWords(String)} with the given gender. */
    public static String toWords(String number, Gender gender) {
        BigDecimal value = parse(number);
        return value.stripTrailingZeros().scale() > 0 ? toWords(value, gender) : toWords(value.toBigInteger(), gender);
    }

    // ------------------------------------------------------------------ counted nouns & money

    /**
     * Converts a count followed by the counted noun, applying all Arabic agreement rules:
     * <pre>
     *   1   → كتاب واحد        2   → كتابان
     *   3   → ثلاثة كتب        11  → أحد عشر كتابًا
     *   100 → مائة كتاب        2000 → ألفا كتاب
     * </pre>
     */
    public static String toWords(BigInteger count, ArabicNoun noun) {
        Objects.requireNonNull(count, "count");
        Objects.requireNonNull(noun, "noun");
        checkRange(count);
        String words = count.signum() == 0
                ? ZERO + " " + noun.singular()
                : countedNoun(count.abs(), noun, true, false);
        return count.signum() < 0 ? NEGATIVE + " " + words : words;
    }

    /** Same as {@link #toWords(BigInteger, ArabicNoun)}. */
    public static String toWords(long count, ArabicNoun noun) {
        return toWords(BigInteger.valueOf(count), noun);
    }

    /**
     * Converts a money amount to words. The fraction is rounded (half-up) to a whole number of
     * sub-units, e.g. {@code 5.25 JOD → خمسة دنانير ومئتان وخمسون فلسًا}. Numbers agree with the
     * gender of the unit and sub-unit: {@code 3 LBP → ثلاث ليرات}, {@code 3 EGP → ثلاثة جنيهات}.
     */
    public static String toCurrencyWords(BigDecimal amount, ArabicCurrency currency) {
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(currency, "currency");

        BigDecimal abs = amount.abs();
        BigInteger whole = abs.toBigInteger();
        BigInteger perUnit = BigInteger.valueOf(currency.subunitsPerUnit());
        BigInteger sub = abs.subtract(new BigDecimal(whole)).multiply(new BigDecimal(perUnit))
                .setScale(0, RoundingMode.HALF_UP).toBigInteger();
        if (sub.equals(perUnit)) { // e.g. 9.999 USD rounds up to 10 دولارات
            whole = whole.add(BigInteger.ONE);
            sub = BigInteger.ZERO;
        }
        checkRange(whole);

        if (whole.signum() == 0 && sub.signum() == 0) {
            return ZERO + " " + currency.unit().singular();
        }
        List<String> parts = new ArrayList<>(2);
        if (whole.signum() > 0) {
            parts.add(countedNoun(whole, currency.unit(), true, false));
        }
        if (sub.signum() > 0) {
            parts.add(countedNoun(sub, currency.subunit(), true, false));
        }
        String words = String.join(AND, parts);
        return amount.signum() < 0 ? NEGATIVE + " " + words : words;
    }

    /** Same as {@link #toCurrencyWords(BigDecimal, ArabicCurrency)}, parsing the amount like {@link #toWords(String)}. */
    public static String toCurrencyWords(String amount, ArabicCurrency currency) {
        return toCurrencyWords(parse(amount), currency);
    }

    /**
     * Same as {@link #toCurrencyWords(String, ArabicCurrency)}, looking the currency up by
     * ISO 4217 code, e.g. {@code toCurrencyWords("12.5", "OMR")}.
     *
     * @throws IllegalArgumentException if the code is not one of {@link ArabicCurrency#predefined()}
     */
    public static String toCurrencyWords(String amount, String currencyCode) {
        return toCurrencyWords(amount, ArabicCurrency.forCode(currencyCode));
    }

    /** Same as {@link #toCurrencyWords(BigDecimal, ArabicCurrency)}. Prefer {@code BigDecimal} for money. */
    public static String toCurrencyWords(double amount, ArabicCurrency currency) {
        return toCurrencyWords(BigDecimal.valueOf(amount), currency);
    }

    /** Same as {@link #toCurrencyWords(BigDecimal, ArabicCurrency)} using the Jordanian dinar. */
    public static String toCurrencyWords(BigDecimal amount) {
        return toCurrencyWords(amount, ArabicCurrency.JOD);
    }

    /** Same as {@link #toCurrencyWords(String, ArabicCurrency)} using the Jordanian dinar. */
    public static String toCurrencyWords(String amount) {
        return toCurrencyWords(amount, ArabicCurrency.JOD);
    }

    // ------------------------------------------------------------------ internals

    /**
     * Number words for a positive integer.
     *
     * @param construct true when a noun follows directly, so a trailing dual takes its
     *                  construct form (مئتان → مئتا دينار, ألفان → ألفا دينار)
     */
    private static String integerWords(BigInteger number, Gender gender, boolean construct) {
        List<Integer> groups = new ArrayList<>();
        BigInteger rest = number;
        while (rest.signum() > 0) {
            BigInteger[] qr = rest.divideAndRemainder(THOUSAND);
            groups.add(qr[1].intValue());
            rest = qr[0];
        }

        int lowestNonZero = 0;
        while (groups.get(lowestNonZero) == 0) {
            lowestNonZero++;
        }

        List<String> parts = new ArrayList<>();
        for (int level = groups.size() - 1; level >= 0; level--) {
            int group = groups.get(level);
            if (group == 0) {
                continue;
            }
            boolean groupConstruct = construct && level == lowestNonZero;
            parts.add(level == 0
                    ? groupWords(group, gender, groupConstruct)
                    : countedNoun(BigInteger.valueOf(group), SCALES[level], false, groupConstruct));
        }
        return String.join(AND, parts);
    }

    /** Words for 1..999. */
    private static String groupWords(int number, Gender gender, boolean construct) {
        int hundreds = number / 100;
        int rest = number % 100;
        String[] ones = gender == Gender.FEMININE ? ONES_FEMININE : ONES_MASCULINE;

        List<String> parts = new ArrayList<>(3);
        if (hundreds > 0) {
            parts.add(hundreds == 2 && rest == 0 && construct ? HUNDREDS_TWO_CONSTRUCT : HUNDREDS[hundreds]);
        }
        if (rest > 0 && rest < 20) {
            parts.add(ones[rest]);
        } else if (rest >= 20) {
            int unit = rest % 10;
            if (unit > 0) {
                // In compounds the feminine "one" is إحدى (إحدى وعشرون), not واحدة.
                parts.add(unit == 1 && gender == Gender.FEMININE ? "إحدى" : ones[unit]);
            }
            parts.add(TENS[rest / 10]);
        }
        return String.join(AND, parts);
    }

    /**
     * A positive count followed by its noun.
     *
     * @param mentionOne true to say "دينار واحد" for 1; false to say just "ألف"
     * @param construct  true when another noun follows (ألفا دينار, اثنا عشر ألف دينار)
     */
    private static String countedNoun(BigInteger count, ArabicNoun noun, boolean mentionOne, boolean construct) {
        if (count.equals(BigInteger.ONE)) {
            return mentionOne ? noun.singular() + " " + groupWords(1, noun.gender(), false) : noun.singular();
        }
        if (count.equals(BigInteger.valueOf(2))) {
            return construct ? noun.dualConstruct() : noun.dual();
        }
        int lastTwo = count.mod(HUNDRED).intValue();
        if (lastTwo >= 3 && lastTwo <= 10) {
            return integerWords(count, noun.gender(), false) + " " + noun.plural();
        }
        if (lastTwo >= 11) {
            return integerWords(count, noun.gender(), false) + " " + (construct ? noun.singular() : noun.accusative());
        }
        // 100, 200, 1000, 101, 102 ...: the noun stays singular; a trailing dual is in construct state.
        return integerWords(count, noun.gender(), lastTwo == 0) + " " + noun.singular();
    }

    private static ArabicNoun scale(String singular, String dual, String plural, String accusative) {
        return ArabicNoun.of(singular, dual, plural, accusative, Gender.MASCULINE);
    }

    private static void checkRange(BigInteger number) {
        if (number.abs().compareTo(MAX_VALUE) > 0) {
            throw new IllegalArgumentException("Number is too large; maximum supported value is 10^36 - 1");
        }
    }

    private static BigDecimal parse(String text) {
        Objects.requireNonNull(text, "number");
        StringBuilder sb = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c >= '٠' && c <= '٩') {
                sb.append((char) ('0' + (c - '٠')));
            } else if (c >= '۰' && c <= '۹') {
                sb.append((char) ('0' + (c - '۰')));
            } else if (c == '٫') {
                sb.append('.');
            } else if (c != ',' && c != '٬' && c != '_' && !Character.isWhitespace(c)) {
                sb.append(c);
            }
        }
        try {
            return new BigDecimal(sb.toString());
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Not a number: \"" + text + "\"");
        }
    }
}
