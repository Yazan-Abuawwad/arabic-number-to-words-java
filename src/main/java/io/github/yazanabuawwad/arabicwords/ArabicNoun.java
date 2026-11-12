package io.github.yazanabuawwad.arabicwords;

import java.util.Objects;

/**
 * A countable Arabic noun (المعدود) with every form needed to place it after a number.
 *
 * <pre>
 *   1        → singular     دينار واحد
 *   2        → dual         ديناران
 *   3..10    → plural       ثلاثة دنانير
 *   11..99   → accusative   أحد عشر دينارًا
 *   100, 1000 ... → singular مائة دينار
 * </pre>
 *
 * Instances are immutable and thread-safe.
 */
public final class ArabicNoun {

    private final String singular;
    private final String dual;
    private final String plural;
    private final String accusative;
    private final Gender gender;

    private ArabicNoun(String singular, String dual, String plural, String accusative, Gender gender) {
        this.singular = requireText(singular, "singular");
        this.dual = requireText(dual, "dual");
        this.plural = requireText(plural, "plural");
        this.accusative = requireText(accusative, "accusative");
        this.gender = Objects.requireNonNull(gender, "gender");
    }

    /**
     * Creates a noun.
     *
     * @param singular   المفرد, e.g. {@code دينار}
     * @param dual       المثنى, e.g. {@code ديناران}
     * @param plural     الجمع (used with 3..10), e.g. {@code دنانير}
     * @param accusative التمييز المنصوب (used with 11..99), e.g. {@code دينارًا}
     * @param gender     the noun's grammatical gender
     */
    public static ArabicNoun of(String singular, String dual, String plural, String accusative, Gender gender) {
        return new ArabicNoun(singular, dual, plural, accusative, gender);
    }

    public String singular() {
        return singular;
    }

    public String dual() {
        return dual;
    }

    /**
     * The dual in construct state (المثنى المضاف), used when another noun follows:
     * {@code ألفان} → {@code ألفا دينار}. Derived by dropping the final ن.
     */
    public String dualConstruct() {
        return dual.endsWith("ن") ? dual.substring(0, dual.length() - 1) : dual;
    }

    public String plural() {
        return plural;
    }

    public String accusative() {
        return accusative;
    }

    public Gender gender() {
        return gender;
    }

    private static String requireText(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
        return value.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArabicNoun)) return false;
        ArabicNoun that = (ArabicNoun) o;
        return singular.equals(that.singular) && dual.equals(that.dual) && plural.equals(that.plural)
                && accusative.equals(that.accusative) && gender == that.gender;
    }

    @Override
    public int hashCode() {
        return Objects.hash(singular, dual, plural, accusative, gender);
    }

    @Override
    public String toString() {
        return singular;
    }
}
