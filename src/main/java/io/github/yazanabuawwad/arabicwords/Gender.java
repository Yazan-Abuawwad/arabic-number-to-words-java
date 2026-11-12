package io.github.yazanabuawwad.arabicwords;

/**
 * Grammatical gender of a counted noun (المعدود).
 * <p>
 * In Arabic the form of the number depends on the gender of the thing being counted:
 * {@code ثلاثة دنانير} (masculine noun) but {@code ثلاث ليرات} (feminine noun).
 */
public enum Gender {
    /** مذكر — e.g. دينار، ريال، درهم. */
    MASCULINE,
    /** مؤنث — e.g. ليرة، هللة، بيسة. */
    FEMININE
}
