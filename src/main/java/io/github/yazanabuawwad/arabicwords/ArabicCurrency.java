package io.github.yazanabuawwad.arabicwords;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * A currency: a main unit (e.g. دينار), a sub-unit (e.g. فلس) and how many sub-units make one unit.
 * <p>
 * The currencies of every Arab League country are predefined (plus USD, EUR and GBP), each with
 * the correct grammatical gender of its unit and sub-unit, e.g. ليرة and هللة are feminine.
 * Look them up by ISO 4217 code with {@link #forCode(String)}, or define your own with
 * {@link #of(String, ArabicNoun, ArabicNoun, int)}. Instances are immutable and thread-safe.
 */
public final class ArabicCurrency {

    // ------------------------------------------------------------------ nouns shared by several currencies

    private static final ArabicNoun DINAR = masculine("دينار", "ديناران", "دنانير", "دينارًا");
    private static final ArabicNoun RIYAL = masculine("ريال", "ريالان", "ريالات", "ريالًا");
    private static final ArabicNoun DIRHAM = masculine("درهم", "درهمان", "دراهم", "درهمًا");
    private static final ArabicNoun POUND = masculine("جنيه", "جنيهان", "جنيهات", "جنيهًا");
    private static final ArabicNoun LIRA = feminine("ليرة", "ليرتان", "ليرات", "ليرةً");
    private static final ArabicNoun FRANC = masculine("فرنك", "فرنكان", "فرنكات", "فرنكًا");
    private static final ArabicNoun FILS = masculine("فلس", "فلسان", "فلوس", "فلسًا");
    private static final ArabicNoun PIASTRE = masculine("قرش", "قرشان", "قروش", "قرشًا");
    private static final ArabicNoun CENTIME = masculine("سنتيم", "سنتيمان", "سنتيمات", "سنتيمًا");
    private static final ArabicNoun CENT = masculine("سنت", "سنتان", "سنتات", "سنتًا");

    private static final Map<String, ArabicCurrency> BY_CODE = new LinkedHashMap<>();

    // ------------------------------------------------------------------ Arab League currencies

    /** Jordanian dinar — 1 دينار = 1000 فلس. */
    public static final ArabicCurrency JOD = register("JOD", DINAR, FILS, 1000);
    /** Kuwaiti dinar — 1 دينار = 1000 فلس. */
    public static final ArabicCurrency KWD = register("KWD", DINAR, FILS, 1000);
    /** Bahraini dinar — 1 دينار = 1000 فلس. */
    public static final ArabicCurrency BHD = register("BHD", DINAR, FILS, 1000);
    /** Iraqi dinar — 1 دينار = 1000 فلس. */
    public static final ArabicCurrency IQD = register("IQD", DINAR, FILS, 1000);
    /** Libyan dinar — 1 دينار = 1000 درهم. */
    public static final ArabicCurrency LYD = register("LYD", DINAR, DIRHAM, 1000);
    /** Tunisian dinar — 1 دينار = 1000 مليم. */
    public static final ArabicCurrency TND = register("TND", DINAR,
            masculine("مليم", "مليمان", "مليمات", "مليمًا"), 1000);
    /** Algerian dinar — 1 دينار = 100 سنتيم. */
    public static final ArabicCurrency DZD = register("DZD", DINAR, CENTIME, 100);

    /** Saudi riyal — 1 ريال = 100 هللة (feminine). */
    public static final ArabicCurrency SAR = register("SAR", RIYAL,
            feminine("هللة", "هللتان", "هللات", "هللةً"), 100);
    /** Qatari riyal — 1 ريال = 100 درهم. */
    public static final ArabicCurrency QAR = register("QAR", RIYAL, DIRHAM, 100);
    /** Omani rial — 1 ريال = 1000 بيسة (feminine). */
    public static final ArabicCurrency OMR = register("OMR", RIYAL,
            feminine("بيسة", "بيستان", "بيسات", "بيسةً"), 1000);
    /** Yemeni rial — 1 ريال = 100 فلس. */
    public static final ArabicCurrency YER = register("YER", RIYAL, FILS, 100);

    /** UAE dirham — 1 درهم = 100 فلس. */
    public static final ArabicCurrency AED = register("AED", DIRHAM, FILS, 100);
    /** Moroccan dirham — 1 درهم = 100 سنتيم. */
    public static final ArabicCurrency MAD = register("MAD", DIRHAM, CENTIME, 100);

    /** Egyptian pound — 1 جنيه = 100 قرش. */
    public static final ArabicCurrency EGP = register("EGP", POUND, PIASTRE, 100);
    /** Sudanese pound — 1 جنيه = 100 قرش. */
    public static final ArabicCurrency SDG = register("SDG", POUND, PIASTRE, 100);
    /** Lebanese pound — 1 ليرة (feminine) = 100 قرش. */
    public static final ArabicCurrency LBP = register("LBP", LIRA, PIASTRE, 100);
    /** Syrian pound — 1 ليرة (feminine) = 100 قرش. */
    public static final ArabicCurrency SYP = register("SYP", LIRA, PIASTRE, 100);

    /** Mauritanian ouguiya — 1 أوقية (feminine) = 5 خمس. */
    public static final ArabicCurrency MRU = register("MRU",
            feminine("أوقية", "أوقيتان", "أوقيات", "أوقيةً"),
            masculine("خمس", "خمسان", "أخماس", "خمسًا"), 5);
    /** Somali shilling — 1 شلن = 100 سنت. */
    public static final ArabicCurrency SOS = register("SOS",
            masculine("شلن", "شلنان", "شلنات", "شلنًا"), CENT, 100);
    /** Djiboutian franc — 1 فرنك = 100 سنتيم. */
    public static final ArabicCurrency DJF = register("DJF", FRANC, CENTIME, 100);
    /** Comorian franc — 1 فرنك = 100 سنتيم. */
    public static final ArabicCurrency KMF = register("KMF", FRANC, CENTIME, 100);

    // ------------------------------------------------------------------ other common currencies

    /** US dollar — 1 دولار = 100 سنت. */
    public static final ArabicCurrency USD = register("USD",
            masculine("دولار", "دولاران", "دولارات", "دولارًا"), CENT, 100);
    /** Euro — 1 يورو = 100 سنت. */
    public static final ArabicCurrency EUR = register("EUR",
            masculine("يورو", "يوروان", "يوروهات", "يورو"), CENT, 100);
    /** Pound sterling — 1 جنيه إسترليني = 100 بنس. */
    public static final ArabicCurrency GBP = register("GBP",
            masculine("جنيه إسترليني", "جنيهان إسترلينيان", "جنيهات إسترلينية", "جنيهًا إسترلينيًا"),
            masculine("بنس", "بنسان", "بنسات", "بنسًا"), 100);

    private final String code;
    private final ArabicNoun unit;
    private final ArabicNoun subunit;
    private final int subunitsPerUnit;

    private ArabicCurrency(String code, ArabicNoun unit, ArabicNoun subunit, int subunitsPerUnit) {
        this.code = Objects.requireNonNull(code, "code").trim().toUpperCase(Locale.ROOT);
        this.unit = Objects.requireNonNull(unit, "unit");
        this.subunit = Objects.requireNonNull(subunit, "subunit");
        if (subunitsPerUnit < 2) {
            throw new IllegalArgumentException("subunitsPerUnit must be at least 2, got " + subunitsPerUnit);
        }
        this.subunitsPerUnit = subunitsPerUnit;
    }

    /**
     * Creates a custom currency. It is not added to {@link #forCode(String)}.
     *
     * @param code            an identifier such as an ISO 4217 code, e.g. {@code "ILS"}
     * @param unit            the main unit, e.g. دينار
     * @param subunit         the fractional unit, e.g. فلس
     * @param subunitsPerUnit how many sub-units make one unit, e.g. 1000 (at least 2)
     */
    public static ArabicCurrency of(String code, ArabicNoun unit, ArabicNoun subunit, int subunitsPerUnit) {
        return new ArabicCurrency(code, unit, subunit, subunitsPerUnit);
    }

    /**
     * Returns the predefined currency for an ISO 4217 code (case-insensitive), e.g. {@code "SAR"}.
     *
     * @throws IllegalArgumentException if no predefined currency has that code
     */
    public static ArabicCurrency forCode(String code) {
        Objects.requireNonNull(code, "code");
        ArabicCurrency currency = BY_CODE.get(code.trim().toUpperCase(Locale.ROOT));
        if (currency == null) {
            throw new IllegalArgumentException("Unknown currency code: " + code + ". Known codes: " + BY_CODE.keySet());
        }
        return currency;
    }

    /** All predefined currencies keyed by ISO 4217 code, in declaration order. */
    public static Map<String, ArabicCurrency> predefined() {
        return Collections.unmodifiableMap(BY_CODE);
    }

    public String code() {
        return code;
    }

    public ArabicNoun unit() {
        return unit;
    }

    public ArabicNoun subunit() {
        return subunit;
    }

    public int subunitsPerUnit() {
        return subunitsPerUnit;
    }

    @Override
    public String toString() {
        return code + " (" + unit.singular() + " / " + subunit.singular() + ")";
    }

    private static ArabicCurrency register(String code, ArabicNoun unit, ArabicNoun subunit, int subunitsPerUnit) {
        ArabicCurrency currency = new ArabicCurrency(code, unit, subunit, subunitsPerUnit);
        BY_CODE.put(currency.code, currency);
        return currency;
    }

    private static ArabicNoun masculine(String singular, String dual, String plural, String accusative) {
        return ArabicNoun.of(singular, dual, plural, accusative, Gender.MASCULINE);
    }

    private static ArabicNoun feminine(String singular, String dual, String plural, String accusative) {
        return ArabicNoun.of(singular, dual, plural, accusative, Gender.FEMININE);
    }
}
