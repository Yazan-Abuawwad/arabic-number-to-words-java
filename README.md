# Arabic Number Words — تفقيط الأرقام

A small, dependency-free Java library that turns numbers and money amounts into
grammatically correct Arabic words (تفقيط). It handles very large numbers (up to 36 digits),
both grammatical genders (masculine and feminine), decimals, and **the currency of every Arab country**
(plus USD, EUR and GBP), with the correct terms for each unit and sub-unit already built in.

```java
ArabicNumberConverter.toWords(1234);
// ألف ومئتان وأربعة وثلاثون

ArabicNumberConverter.toWords(1234, Gender.FEMININE);
// ألف ومئتان وأربع وثلاثون

ArabicNumberConverter.toCurrencyWords("1250.750");            // Jordanian dinar by default
// ألف ومئتان وخمسون دينارًا وسبعمائة وخمسون فلسًا

ArabicNumberConverter.toCurrencyWords("100.25", ArabicCurrency.SAR);
// مائة ريال وخمس وعشرون هللةً

ArabicNumberConverter.toCurrencyWords("22", "LBP");            // look up by ISO code
// اثنتان وعشرون ليرةً
```

- **Java 8+** (compiled with `--release 8`, so it works on Java 8, 11, 17, 21 ...)
- **No runtime dependencies**
- **Thread-safe**: every method is static and stateless, so you can call it from anywhere without creating an object

---

## Table of contents

1. [Installation (use it as a dependency)](#1-installation-use-it-as-a-dependency)
2. [Quick start](#2-quick-start)
3. [API reference](#3-api-reference)
4. [Currencies](#4-currencies)
5. [Counting any noun](#5-counting-any-noun)
6. [Accepted input formats](#6-accepted-input-formats)
7. [Grammar rules applied](#7-grammar-rules-applied)
8. [Supported range, limits and errors](#8-supported-range-limits-and-errors)
9. [Migrating from the old `UtilityTOArabic` class](#9-migrating-from-the-old-utilitytoarabic-class)
10. [Building and testing this project](#10-building-and-testing-this-project)
11. [Project structure](#11-project-structure)

---

## 1. Installation (use it as a dependency)

Library coordinates:

| groupId                   | artifactId            | version |
|---------------------------|-----------------------|---------|
| `io.github.yazan-abuawwad` | `arabic-number-words` | `1.0.0` |

Pick **one** of the options below.

### Option A: install into your local Maven repository (simplest, works offline)

Clone this repo and install it once:

```bash
git clone https://github.com/Yazan-Abuawwad/Convert_Big_numbers_to_Arabic_words.git
cd Convert_Big_numbers_to_Arabic_words
mvn install
```

This puts the jar into `~/.m2/repository`. Any project on the same machine can now use it.

**Maven** (`pom.xml` of your other project):

```xml
<dependency>
    <groupId>io.github.yazan-abuawwad</groupId>
    <artifactId>arabic-number-words</artifactId>
    <version>1.0.0</version>
</dependency>
```

**Gradle** (`build.gradle`):

```groovy
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation 'io.github.yazan-abuawwad:arabic-number-words:1.0.0'
}
```

### Option B: pull straight from GitHub with JitPack (no manual install)

[JitPack](https://jitpack.io) builds the library from this GitHub repository when someone asks for it.
Push a tag first (for example `git tag v1.0.0 && git push origin v1.0.0`), then in the other project:

**Maven:**

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.github.Yazan-Abuawwad</groupId>
    <artifactId>Convert_Big_numbers_to_Arabic_words</artifactId>
    <version>v1.0.0</version>
</dependency>
```

**Gradle:**

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.Yazan-Abuawwad:Convert_Big_numbers_to_Arabic_words:v1.0.0'
}
```

> With JitPack the groupId/artifactId are `com.github.<user>` / `<repo name>`, and the version is the git tag.
> The `jitpack.yml` file in this repo tells JitPack to build with JDK 17.

### Option C: copy the jar

Run `mvn package` and copy `target/arabic-number-words-1.0.0.jar` into the other project's `libs/` folder
(or add it as a library in your IDE). Use this if the other project does not use Maven or Gradle.

---

## 2. Quick start

```java
import io.github.yazanabuawwad.arabicwords.ArabicCurrency;
import io.github.yazanabuawwad.arabicwords.ArabicNumberConverter;
import io.github.yazanabuawwad.arabicwords.Gender;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Example {
    public static void main(String[] args) {
        // Plain integers
        System.out.println(ArabicNumberConverter.toWords(25));          // خمسة وعشرون
        System.out.println(ArabicNumberConverter.toWords(3_000_000));   // ثلاثة ملايين

        // Huge numbers
        System.out.println(ArabicNumberConverter.toWords(new BigInteger("1000000000000000000")));
        // كوينتليون

        // Masculine (default) and feminine forms
        System.out.println(ArabicNumberConverter.toWords(13, Gender.MASCULINE)); // ثلاثة عشر
        System.out.println(ArabicNumberConverter.toWords(13, Gender.FEMININE));  // ثلاث عشرة

        // Decimals
        System.out.println(ArabicNumberConverter.toWords(new BigDecimal("12.05")));
        // اثنا عشر فاصلة صفر خمسة

        // Money (Jordanian dinar is the default)
        System.out.println(ArabicNumberConverter.toCurrencyWords("5.250"));
        // خمسة دنانير ومئتان وخمسون فلسًا

        // Money in another currency
        System.out.println(ArabicNumberConverter.toCurrencyWords(new BigDecimal("3.99"), ArabicCurrency.USD));
        // ثلاثة دولارات وتسعة وتسعون سنتًا

        // A currency with a feminine unit: the numbers switch gender automatically
        System.out.println(ArabicNumberConverter.toCurrencyWords("3.05", ArabicCurrency.SYP));
        // ثلاث ليرات وخمسة قروش
    }
}
```

Tip: to get the "فقط ... لا غير" style used on cheques and invoices, add the words yourself:

```java
String text = "فقط " + ArabicNumberConverter.toCurrencyWords(amount) + " لا غير";
```

---

## 3. API reference

All methods live in `ArabicNumberConverter` and are `public static`.

### Numbers

| Method | What it does | Example → result |
|---|---|---|
| `toWords(long n)` | Integer to words (masculine) | `21` → `واحد وعشرون` |
| `toWords(BigInteger n)` | Integer of any size up to 36 digits | `10^33` → `ديسيليون` |
| `toWords(BigDecimal n)` | Decimal number, fraction read after "فاصلة" | `3.5` → `ثلاثة فاصلة خمسة` |
| `toWords(String n)` | Parses text, then converts as integer or decimal | `"1,250,000"` → `مليون ومئتان وخمسون ألفًا` |

Every method above also has a version that takes a `Gender` as its last argument.
Without one, the masculine form is used:

| Method | Example → result |
|---|---|
| `toWords(long n, Gender g)` | `21, FEMININE` → `إحدى وعشرون` |
| `toWords(BigInteger n, Gender g)` | `12, FEMININE` → `اثنتا عشرة` |
| `toWords(BigDecimal n, Gender g)` | `3.1, FEMININE` → `ثلاث فاصلة واحدة` |
| `toWords(String n, Gender g)` | `"8", FEMININE` → `ثماني` |

You don't pass a gender for money or counted nouns. It comes from the noun itself (see [section 7](#7-grammar-rules-applied)).

### Counted nouns

| Method | What it does | Example → result |
|---|---|---|
| `toWords(long count, ArabicNoun noun)` | A number followed by a noun, with agreement | `11, book` → `أحد عشر كتابًا` |
| `toWords(BigInteger count, ArabicNoun noun)` | Same, for big counts | |

### Money

| Method | What it does |
|---|---|
| `toCurrencyWords(BigDecimal amount, ArabicCurrency c)` | Amount in currency `c` (**recommended**) |
| `toCurrencyWords(String amount, ArabicCurrency c)` | Parses the text first |
| `toCurrencyWords(String amount, String isoCode)` | Looks up a built-in currency by code, e.g. `"OMR"` |
| `toCurrencyWords(double amount, ArabicCurrency c)` | Convenience overload; prefer `BigDecimal` for money |
| `toCurrencyWords(BigDecimal amount)` | Jordanian dinar |
| `toCurrencyWords(String amount)` | Jordanian dinar |

How amounts are handled:

- The fraction is **rounded half-up** to a whole number of sub-units (1/1000 for the dinar, 1/100 for the riyal
  and dollar, 1/5 for the ouguiya). For example `2.0004` JOD becomes `ديناران` and `9.999` USD becomes `عشرة دولارات`.
- If there is no whole part, only the sub-unit is written: `0.500` → `خمسمائة فلس`.
- If there is no fraction, only the main unit is written: `7` → `سبعة دنانير`.
- Zero is `صفر دينار`; negative amounts start with `سالب`.

### Constant

- `ArabicNumberConverter.MAX_VALUE`: the largest supported absolute value, `10^36 - 1`.

---

## 4. Currencies

All 22 Arab League countries are covered, plus USD, EUR and GBP. Each one is a constant on
`ArabicCurrency`, and `ArabicCurrency.forCode("...")` looks it up by ISO 4217 code (case-insensitive).
Every term below is built in with all its forms (singular, dual, plural, accusative) and its gender.
(m) = masculine, **(f) = feminine**.

| Code | Country | Unit | Sub-unit | Sub-units per unit |
|---|---|---|---|---|
| `JOD` | Jordan · الأردن | دينار (m) | فلس (m) | 1000 |
| `KWD` | Kuwait · الكويت | دينار (m) | فلس (m) | 1000 |
| `BHD` | Bahrain · البحرين | دينار (m) | فلس (m) | 1000 |
| `IQD` | Iraq · العراق | دينار (m) | فلس (m) | 1000 |
| `LYD` | Libya · ليبيا | دينار (m) | درهم (m) | 1000 |
| `TND` | Tunisia · تونس | دينار (m) | مليم (m) | 1000 |
| `DZD` | Algeria · الجزائر | دينار (m) | سنتيم (m) | 100 |
| `SAR` | Saudi Arabia · السعودية | ريال (m) | **هللة (f)** | 100 |
| `QAR` | Qatar · قطر | ريال (m) | درهم (m) | 100 |
| `OMR` | Oman · عُمان | ريال (m) | **بيسة (f)** | 1000 |
| `YER` | Yemen · اليمن | ريال (m) | فلس (m) | 100 |
| `AED` | UAE · الإمارات | درهم (m) | فلس (m) | 100 |
| `MAD` | Morocco · المغرب | درهم (m) | سنتيم (m) | 100 |
| `EGP` | Egypt · مصر | جنيه (m) | قرش (m) | 100 |
| `SDG` | Sudan · السودان | جنيه (m) | قرش (m) | 100 |
| `LBP` | Lebanon · لبنان | **ليرة (f)** | قرش (m) | 100 |
| `SYP` | Syria · سوريا | **ليرة (f)** | قرش (m) | 100 |
| `MRU` | Mauritania · موريتانيا | **أوقية (f)** | خمس (m) | 5 |
| `SOS` | Somalia · الصومال | شلن (m) | سنت (m) | 100 |
| `DJF` | Djibouti · جيبوتي | فرنك (m) | سنتيم (m) | 100 |
| `KMF` | Comoros · جزر القمر | فرنك (m) | سنتيم (m) | 100 |
| `USD` | United States | دولار (m) | سنت (m) | 100 |
| `EUR` | Eurozone | يورو (m) | سنت (m) | 100 |
| `GBP` | United Kingdom | جنيه إسترليني (m) | بنس (m) | 100 |

Palestine has no currency of its own (it uses JOD and ILS); ILS is shown as a custom currency below.

Plural forms used: دنانير، فلوس، دراهم، مليمات، سنتيمات، ريالات، هللات، بيسات، جنيهات، قروش، ليرات، أوقيات، أخماس، شلنات، فرنكات، دولارات، يوروهات، سنتات، بنسات.

Examples:

```java
ArabicNumberConverter.toCurrencyWords("5.008", ArabicCurrency.OMR);  // خمسة ريالات وثماني بيسات
ArabicNumberConverter.toCurrencyWords("11", ArabicCurrency.LBP);     // إحدى عشرة ليرةً
ArabicNumberConverter.toCurrencyWords("3.4", ArabicCurrency.MRU);    // ثلاث أوقيات وخمسان
ArabicNumberConverter.toCurrencyWords("7.005", "TND");               // سبعة دنانير وخمسة مليمات

ArabicCurrency.predefined();  // Map<String, ArabicCurrency> of every built-in currency
```

The built-in nouns use plain names (`دينار`, not `دينار أردني`). If you need the country name in the
text, define your own currency as shown below. Multi-word forms are fine, as `GBP` shows.

### Defining your own currency

Each noun needs five things: singular, dual, plural (used for 3–10), accusative (used for 11–99), and gender.

```java
import io.github.yazanabuawwad.arabicwords.*;

ArabicCurrency ils = ArabicCurrency.of("ILS",
        ArabicNoun.of("شيكل", "شيكلان", "شواكل", "شيكلًا", Gender.MASCULINE),   // unit
        ArabicNoun.of("أغورة", "أغورتان", "أغورات", "أغورةً", Gender.FEMININE),  // sub-unit
        100);                                                                  // 1 شيكل = 100 أغورة

ArabicNumberConverter.toCurrencyWords("3.03", ils);
// ثلاثة شواكل وثلاث أغورات

ArabicCurrency jodFull = ArabicCurrency.of("JOD",
        ArabicNoun.of("دينار أردني", "ديناران أردنيان", "دنانير أردنية", "دينارًا أردنيًا", Gender.MASCULINE),
        ArabicNoun.of("فلس", "فلسان", "فلوس", "فلسًا", Gender.MASCULINE),
        1000);
```

`subunitsPerUnit` must be at least 2 (it can be any number, not only 10/100/1000). Custom currencies
are not added to `forCode`.

Store your custom currency in a `static final` field and reuse it. Like all classes here, it is immutable and thread-safe.

---

## 5. Counting any noun

The rules that apply to currencies work for any noun, not just money:

```java
ArabicNoun book = ArabicNoun.of("كتاب", "كتابان", "كتب", "كتابًا", Gender.MASCULINE);

ArabicNumberConverter.toWords(1, book);     // كتاب واحد
ArabicNumberConverter.toWords(2, book);     // كتابان
ArabicNumberConverter.toWords(7, book);     // سبعة كتب
ArabicNumberConverter.toWords(15, book);    // خمسة عشر كتابًا
ArabicNumberConverter.toWords(100, book);   // مائة كتاب
ArabicNumberConverter.toWords(2000, book);  // ألفا كتاب
```

---

## 6. Accepted input formats

The `String` overloads accept:

| Input | Meaning |
|---|---|
| `1234`, `-1234`, `+1234` | Plain integers with an optional sign |
| `1234.5` | Decimal point `.` |
| `1,234,567` · `1_234_567` · `1 234 567` · `1٬234` | Thousands separators are ignored |
| `١٢٣٤` · `۱۲۳۴` | Arabic-Indic and Persian digits |
| `٣٫٥` | Arabic decimal separator `٫` |
| `1E6` | Scientific notation (anything `BigDecimal` accepts) |

Anything else throws `NumberFormatException`.

---

## 7. Grammar rules applied

The library follows standard Modern Standard Arabic rules for numbers and counted nouns (العدد والمعدود).

**Gender agreement.** The numbers 1 and 2 agree with the noun's gender. The numbers 3–10 take the
*opposite* gender (عكس المعدود):

| | Masculine noun (دينار) | Feminine noun (ليرة) |
|---|---|---|
| 1 | دينار واحد | ليرة واحدة |
| 2 | ديناران | ليرتان |
| 3 | ثلاثة دنانير | ثلاث ليرات |
| 11 | أحد عشر دينارًا | إحدى عشرة ليرةً |
| 12 | اثنا عشر دينارًا | اثنتا عشرة ليرةً |
| 21 | واحد وعشرون دينارًا | إحدى وعشرون ليرةً |

All the number words that change with gender (everything else, like the tens, مائة and ألف, is the same in both):

| n | Masculine | Feminine | n | Masculine | Feminine |
|---|---|---|---|---|---|
| 1 | واحد | واحدة (إحدى in 21, 31 …) | 11 | أحد عشر | إحدى عشرة |
| 2 | اثنان | اثنتان | 12 | اثنا عشر | اثنتا عشرة |
| 3 | ثلاثة | ثلاث | 13 | ثلاثة عشر | ثلاث عشرة |
| 4 | أربعة | أربع | 14 | أربعة عشر | أربع عشرة |
| 5 | خمسة | خمس | 15 | خمسة عشر | خمس عشرة |
| 6 | ستة | ست | 16 | ستة عشر | ست عشرة |
| 7 | سبعة | سبع | 17 | سبعة عشر | سبع عشرة |
| 8 | ثمانية | ثماني | 18 | ثمانية عشر | ثماني عشرة |
| 9 | تسعة | تسع | 19 | تسعة عشر | تسع عشرة |
| 10 | عشرة | عشر | | | |

Here "Masculine"/"Feminine" means *the gender of the counted noun* (or the `Gender` you pass to `toWords`).
The scale words ألف، مليون، مليار … are masculine nouns, so the count in front of them always uses the
masculine column, even when the currency is feminine: `3000 ليرة → ثلاثة آلاف ليرة`.

**Form of the noun.** The last two digits of the count decide the noun's form:

| Last two digits | Noun form | Example |
|---|---|---|
| exactly 1 | singular, followed by "واحد" | دينار واحد |
| exactly 2 | dual | ديناران |
| 3–10 | plural | خمسة دنانير / خمسة آلاف |
| 11–99 | singular accusative (تمييز منصوب) | خمسة وعشرون دينارًا / عشرون ألفًا |
| 00, or 01–02 after a hundred | singular | مائة دينار / مائة وواحد دينار |

**Construct state (الإضافة).** When a noun follows a dual, the dual drops its ن:
`مئتان` → `مئتا دينار`, `ألفان` → `ألفا دينار`, `مليونان` → `مليونا دينار`.
In the same way the accusative loses its tanween: `اثنا عشر ألفًا` → `اثنا عشر ألف دينار`.

**Joining.** Parts are joined with "و" attached to the next word, from the largest scale down:
`مليون ومئتان وخمسون ألفًا وثلاثة`.

**Scale words** (powers of 1000):

| 10^3 | 10^6 | 10^9 | 10^12 | 10^15 | 10^18 | 10^21 | 10^24 | 10^27 | 10^30 | 10^33 |
|---|---|---|---|---|---|---|---|---|---|---|
| ألف | مليون | مليار | تريليون | كوادريليون | كوينتليون | سكستليون | سبتليون | أوكتليون | نونليون | ديسيليون |

---

## 8. Supported range, limits and errors

### The range at a glance

| | Value | Digits |
|---|---|---|
| **Smallest number** | `-999,999,999,999,999,999,999,999,999,999,999,999` (`-(10^36 - 1)`) | 36 |
| **Smallest positive whole number** | `1` | 1 |
| **Smallest positive fraction** | No fixed limit, e.g. `0.000000001` (up to 36 significant digits after the zeros) | — |
| **Smallest money amount** | One sub-unit, e.g. `0.001` JOD = `فلس واحد`, `0.01` LBP = `قرش واحد` | — |
| **Largest number** | `999,999,999,999,999,999,999,999,999,999,999,999` (`10^36 - 1`, `ArabicNumberConverter.MAX_VALUE`) | 36 |

Anything outside `-(10^36 - 1)` … `10^36 - 1` throws `IllegalArgumentException`.

### From the smallest number to the largest

Every output below was produced by the library itself.

**Negative numbers** (the prefix is `سالب`):

| Input | Output |
|---|---|
| `-(10^36 - 1)` (the smallest) | سالب تسعمائة وتسعة وتسعون ديسيليونًا وتسعمائة وتسعة وتسعون نونليونًا … وتسعمائة وتسعة وتسعون ألفًا وتسعمائة وتسعة وتسعون |
| `-2.5` | سالب اثنان فاصلة خمسة |
| `-1` | سالب واحد |

**Zero and fractions** (read after `فاصلة`, with leading zeros spelled out):

| Input | Output |
|---|---|
| `0` | صفر |
| `0.000000001` | صفر فاصلة صفر صفر صفر صفر صفر صفر صفر صفر واحد |
| `0.001` | صفر فاصلة صفر صفر واحد |
| `0.05` | صفر فاصلة صفر خمسة |
| `0.5` | صفر فاصلة خمسة |
| `3.14159` | ثلاثة فاصلة أربعة عشر ألفًا ومائة وتسعة وخمسون |

**Units, tens and hundreds** (1 to 999):

| Input | Output |
|---|---|
| `1` | واحد |
| `2` | اثنان |
| `3` | ثلاثة |
| `10` | عشرة |
| `11` | أحد عشر |
| `19` | تسعة عشر |
| `20` | عشرون |
| `21` | واحد وعشرون |
| `99` | تسعة وتسعون |
| `100` | مائة |
| `101` | مائة وواحد |
| `200` | مئتان |
| `999` | تسعمائة وتسعة وتسعون |

**Thousands:**

| Input | Output |
|---|---|
| `1,000` | ألف |
| `2,000` | ألفان |
| `3,000` | ثلاثة آلاف |
| `11,000` | أحد عشر ألفًا |
| `100,000` | مائة ألف |
| `999,999` | تسعمائة وتسعة وتسعون ألفًا وتسعمائة وتسعة وتسعون |

**Every scale up to the largest** (each one is 1000 times the one before):

| Value | Digits | Output |
|---|---|---|
| `10^6`  = 1,000,000 | 7 | مليون |
| `10^9`  = 1,000,000,000 | 10 | مليار |
| `10^12` = 1,000,000,000,000 | 13 | تريليون |
| `10^15` | 16 | كوادريليون |
| `10^18` | 19 | كوينتليون |
| `10^21` | 22 | سكستليون |
| `10^24` | 25 | سبتليون |
| `10^27` | 28 | أوكتليون |
| `10^30` | 31 | نونليون |
| `10^33` | 34 | ديسيليون |

**A full 36-digit number:**

```
123456789012345678901234567890123456
```

> مائة وثلاثة وعشرون ديسيليونًا وأربعمائة وستة وخمسون نونليونًا وسبعمائة وتسعة وثمانون أوكتليونًا
> واثنا عشر سبتليونًا وثلاثمائة وخمسة وأربعون سكستليونًا وستمائة وثمانية وسبعون كوينتليونًا
> وتسعمائة وواحد كوادريليون ومئتان وأربعة وثلاثون تريليونًا وخمسمائة وسبعة وستون مليارًا
> وثمانمائة وتسعون مليونًا ومائة وثلاثة وعشرون ألفًا وأربعمائة وستة وخمسون

**The largest number, `ArabicNumberConverter.MAX_VALUE`:**

```
999999999999999999999999999999999999
```

> تسعمائة وتسعة وتسعون ديسيليونًا وتسعمائة وتسعة وتسعون نونليونًا وتسعمائة وتسعة وتسعون أوكتليونًا
> وتسعمائة وتسعة وتسعون سبتليونًا وتسعمائة وتسعة وتسعون سكستليونًا وتسعمائة وتسعة وتسعون كوينتليونًا
> وتسعمائة وتسعة وتسعون كوادريليونًا وتسعمائة وتسعة وتسعون تريليونًا وتسعمائة وتسعة وتسعون مليارًا
> وتسعمائة وتسعة وتسعون مليونًا وتسعمائة وتسعة وتسعون ألفًا وتسعمائة وتسعة وتسعون

**One past the largest**, `10^36`, throws:

```
java.lang.IllegalArgumentException: Number is too large; maximum supported value is 10^36 - 1
```

### The same range for money

The whole part of an amount has the same limit (`10^36 - 1`). The smallest non-zero amount is one sub-unit.

| Input | Currency | Output |
|---|---|---|
| `0` | JOD | صفر دينار |
| `0.001` (smallest) | JOD | فلس واحد |
| `0.010` | JOD | عشرة فلوس |
| `0.999` | JOD | تسعمائة وتسعة وتسعون فلسًا |
| `1` | JOD | دينار واحد |
| `1.001` | JOD | دينار واحد وفلس واحد |
| `1000000.500` | JOD | مليون دينار وخمسمائة فلس |
| `0.01` (smallest) | LBP | قرش واحد |
| `1` | LBP | ليرة واحدة |
| `1000000000` | LBP | مليار ليرة |
| `999999999999999999999999999999999999.999` (largest) | JOD | تسعمائة وتسعة وتسعون ديسيليونًا … وتسعمائة وتسعة وتسعون دينارًا وتسعمائة وتسعة وتسعون فلسًا |

Amounts below half a sub-unit round to zero (`0.0004` JOD → `صفر دينار`). Rounding up can't go past the
limit: `999…999.9995` JOD rounds up to `10^36` dinars and throws.

### Errors

| Situation | Behaviour |
|---|---|
| absolute value > `10^36 - 1` | `IllegalArgumentException` |
| Text that is not a number | `NumberFormatException` |
| `null` argument | `NullPointerException` |
| `subunitsPerUnit` below 2, or empty noun forms | `IllegalArgumentException` |
| Unknown code in `forCode` / `toCurrencyWords(amount, code)` | `IllegalArgumentException` (the message lists the known codes) |

---

## 9. Migrating from the old `UtilityTOArabic` class

Earlier versions of this repo exposed `com.converter.UtilityTOArabic`. It was replaced because it kept
mutable state per instance (not safe to share between threads), only knew the dinar, parsed money
through `double` (which loses precision), and made a few grammar mistakes such as `ثلاث دنانير`
instead of `ثلاثة دنانير`.

| Old | New |
|---|---|
| `new UtilityTOArabic().numberToArabicWords("12.5")` | `ArabicNumberConverter.toCurrencyWords("12.5")` |
| `printNumberToArabicWords(12.5)` (map with `dinar`/`fals`) | `toCurrencyWords(BigDecimal.valueOf(12), ArabicCurrency.JOD)` and `toWords(500, ArabicCurrency.JOD.subunit())` |
| `numberToArabicWordsWithOutCurrency(12.5)` | `toWords(12)` and `toWords(500)` |
| `isFeminine = true` | `toWords(n, Gender.FEMININE)` |

Output differences you may notice:

- "و" is now attached to the next word (`خمسة وعشرون` instead of `خمسة و عشرون`) and the output has no leading or trailing spaces.
- Gender agreement and noun forms follow the rules in [section 7](#7-grammar-rules-applied).

---

## 10. Building and testing this project

Requirements: JDK 9 or newer to build (the output still runs on Java 8), and Maven 3.6+.

```bash
mvn test       # run the unit tests
mvn package    # build target/arabic-number-words-1.0.0.jar (+ -sources and -javadoc jars)
mvn install    # build and install into your local Maven repository (~/.m2)
```

To release a new version: change `<version>` in `pom.xml`, commit, then tag it (`git tag v1.1.0`) and
push the tag so JitPack can build it.

In IntelliJ IDEA open the folder (or `pom.xml`) and choose **Open as Project**; the IDE configures itself from the pom.

---

## 11. Project structure

```
.
├── pom.xml                         Maven build: coordinates, Java 8 target, plugins
├── jitpack.yml                     Tells JitPack to build with JDK 17
├── README.md
└── src
    ├── main/java/io/github/yazanabuawwad/arabicwords
    │   ├── ArabicNumberConverter.java   Entry point: all toWords / toCurrencyWords methods
    │   ├── ArabicCurrency.java          Currency = unit + sub-unit + ratio; built-in JOD, SAR, USD ...
    │   ├── ArabicNoun.java              A countable noun with singular/dual/plural/accusative forms
    │   └── Gender.java                  MASCULINE / FEMININE
    └── test/java/io/github/yazanabuawwad/arabicwords
        └── ArabicNumberConverterTest.java   Unit tests
```

### How the conversion works

1. **Split into groups of three digits.** `1,250,003` becomes `[1] [250] [003]`. Each group's position is its scale (units, ألف, مليون ...).
2. **Convert each group (0–999)** to words: hundreds (`مائة` … `تسعمائة`), then 1–19 from a lookup table, or *unit + tens* for 20–99 (`خمسة وعشرون`).
3. **Attach the scale word** to each group using the same counted-noun rules that apply to currencies.
   A scale word like ألف is just a masculine noun: `1 → ألف`, `2 → ألفان`, `3 → ثلاثة آلاف`, `11 → أحد عشر ألفًا`.
4. **Join** the groups with "و", largest first.
5. **For money**, round to the currency's decimals, split into the whole part and the sub-units, run each through
   step 3 with the currency's noun, and join them with "و".

---

Created by [Yazan Abuawwad](https://github.com/Yazan-Abuawwad).
