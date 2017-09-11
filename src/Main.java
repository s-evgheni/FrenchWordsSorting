import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.text.CollationKey;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Main {

    private static final Locale TEST_LOCALE = Locale.FRENCH;
    /*
        Sorting rules:
            1. Lowercase letters and capital letters are equal
            2. Non accented letters come before accented letters and small letters come before capital letters.
            3. Regarding the precedence of accents, the rule is:
                    a comes before à, which comes before â
                    e comes before é, which comes before è, which comes before ê, which comes before ë
                    i comes before î, which comes before ï
                    u comes before ù, which comes before û, which comes before ü
            4. 'æ','Æ' and 'œ', 'Œ' are considered as separate letters, i.e.'ae', 'AE' and 'oe', 'OE'
            5. Numbers and symbols like @ sort before letters.
            6. The common French alphabetizing order including all extended character used
                in the French language is as follows :
                a, A, à, À, â, Â, æ, Æ, b, B, c, C, ç, Ç, d,
                D, e, E, é, É, è, È, ê, Ê, ë, Ë, f, F, g, G, h, H, i, I, î, Î, ï, Ï, j, J, k, K, l, L, m, M, n, N,
                o, O, ô, Ô, ö, Ö, œ, Œ, p, P, q, Q, r, R, s, S, t, T, u, U, ù, Ù, û, Û, v, V, w, W, x,
                X, y, Y, ÿ, Ÿ, z, Z
     */

    private static final String CUSTOM_RULES =  "< '@'='!'='#'='$'='%'='&'='*' " +
                                                "< 0 < 1 < 2 < 3 < 4 < 5 < 6 < 7 < 8 < 9 " +
                                                "< a<A < à<À < â<Â < æ<Æ " +
                                                "< b<B < c<C < ç<Ç < d<D " +
                                                "< e<E < é<É < è<È < ê<Ê < ë<Ë < f<F < g<G " +
                                                "< h<H < i<I; î<Î < ï<Ï < j<J < k<K < l<L " +
                                                "< m<M < n<N < o<O < ô<Ô < ö<Ö < œ<Œ < p<P " +
                                                "< q<Q < r<R < s<S < t<T < u<U < ù<Ù < û<Û " +
                                                "< v,V < w<W < x<X < y<Y < ÿ<Ÿ < z<Z ";

    private static final List<String> alphabetInOrder = Arrays.asList("a", "A", "à", "À", "â", "Â", "æ", "Æ", "b", "B", "c", "C", "ç", "Ç", "d",
                                                                      "D", "e", "E", "é", "É", "è", "È", "ê", "Ê", "ë", "Ë", "f", "F", "g", "G",
                                                                      "h", "H", "i", "I", "î", "Î", "ï", "Ï", "j", "J", "k", "K", "l", "L", "m", "M", "n", "N",
                                                                      "o", "O", "ô", "Ô", "ö", "Ö", "œ", "Œ", "p", "P", "q", "Q", "r", "R", "s",
                                                                      "S", "t", "T", "u", "U", "ù", "Ù", "û", "Û", "v", "V", "w", "W", "x", "X", "y", "Y", "ÿ", "Ÿ", "z", "Z");


    private static final List<String> sortedWordSet = Arrays.asList("@", "$","1",
                                                              "aàâ", "aâà",
                                                              "Aàâ", "Aâà",
                                                              "àaâ", "àAâ", "àâa",  "àâA",
                                                              "âaà", "âAà", "âàa", "âàA",
                                                              "æaA", "ÆaA",
                                                              "bâà", "Bàâ");

    private static final List<String> sortedWordSet2 = Arrays.asList("@","1","Aaron", "àAron",
                                                               "chaque","chemin","cote", "coté", "côte",
                                                               "côté", "lie", "lire", "pint", "pylon", "savoir",
                                                               "yen", "yuan","yucca", "zoo","Zürich");

    public static void main(String... aArguments) throws ParseException {
        System.out.println("\n\n --- Human Sorted Data Set 1 --- \n" );
        sortedWordSet.forEach(word -> System.out.print(word + " | "));
        System.out.println("\n Data Set to sort:" );
        Collections.shuffle(sortedWordSet);
        sortedWordSet.forEach(word -> System.out.print(word + " | "));

        List<String> customSortResult = customSortLocaleSensitiveList(sortedWordSet, CUSTOM_RULES);
        System.out.println("\n\n --- Custom sorting rules results --- \n");
        customSortResult.forEach(word -> System.out.print(word + " | "));

        List<String> builtInSortResult = sortLocaleSensitiveList(sortedWordSet, TEST_LOCALE);
        System.out.println("\n\n --- Java Built-In sorting rules results --- \n");
        builtInSortResult.forEach(word -> System.out.print(word + " | "));

        System.out.println("\n\n --- Human Sorted Data Set 2 --- \n" );
        sortedWordSet2.forEach(word -> System.out.print(word + " | "));
        System.out.println("\n Data Set to sort:" );
        Collections.shuffle(sortedWordSet2);
        sortedWordSet2.forEach(word -> System.out.print(word + " | "));

        customSortResult = customSortLocaleSensitiveList(sortedWordSet2, CUSTOM_RULES);
        System.out.println("\n\n --- Custom sorting results --- \n");
        customSortResult.forEach(word -> System.out.print(word + " | "));

        builtInSortResult = sortLocaleSensitiveList(sortedWordSet2, TEST_LOCALE);
        System.out.println("\n\n --- Java Built-In sorting results --- \n");
        builtInSortResult.forEach(word -> System.out.print(word + " | "));

        System.out.println("\n\n --- Human sorted Data Set 3 --- \n" );
        alphabetInOrder.forEach(word -> System.out.print(word + " | "));
        System.out.println("\n Data Set to sort:" );
        Collections.shuffle(alphabetInOrder);
        alphabetInOrder.forEach(word -> System.out.print(word + " | "));

        customSortResult = customSortLocaleSensitiveList(alphabetInOrder, CUSTOM_RULES);
        System.out.println("\n\n --- Custom sorting results --- \n");
        customSortResult.forEach(word -> System.out.print(word + " | "));

        builtInSortResult = sortLocaleSensitiveList(alphabetInOrder, TEST_LOCALE);
        System.out.println("\n\n --- Java Built-In sorting results --- \n");
        builtInSortResult.forEach(word -> System.out.print(word + " | "));

    }

    /**
     * Compares the source string to the target string according to the Java default built-in collation rules.
     *
     * @param source - source string
     * @param target - target string
     * @param locale - source and target strings locale origin, if not provided current default locale will be used instead
     *
     * @return Returns an integer value. Value is less than zero if source is less than target, value is zero if source and target are equal, value is greater than zero if source is greater than target.
     */
    public static int compareLocaleSensitiveWords(String source, String target, Locale locale) {
        Optional<Integer> blankValueComparisonResult = compareBlankStrings(source, target);
        Collator collator = locale != null ? Collator.getInstance(locale): Collator.getInstance();
        collator.setStrength(Collator.TERTIARY);
        return blankValueComparisonResult.orElseGet(() -> collator.compare(source, target));
    }

    /**
     * Compares the source string to the target string according to the custom set of rules, provided as an input parameter to the method.
     *
     * @param source - source string
     * @param target - target string
     * @param rule - see JavaDoc for java.text.RuleBasedCollator for examples on how to create a valid parsable rule set
     *
     * @return Returns an integer value. Value is less than zero if source is less than target, value is zero if source and target are equal, value is greater than zero if source is greater than target.
     *
     * @throws ParseException - A format exception will be thrown if the build process of the rules fails. For example, build rule "a < ? < d" will cause the constructor to throw the ParseException because the '?' is not quoted.
     */
    public static int customCompareLocaleSensitiveWords(String source, String target, String rule) throws ParseException {
        if(StringUtils.isBlank(rule)) {
            throw new ParseException("Build rules empty", 0);
        }

        Optional<Integer> blankValueComparisonResult = compareBlankStrings(source, target);

        RuleBasedCollator ruleBasedCollator = new RuleBasedCollator(rule);
        ruleBasedCollator.setStrength(Collator.TERTIARY);
        return blankValueComparisonResult.orElseGet(() -> ruleBasedCollator.compare(source, target));
    }

    /**
     * Sort provided locale sensitive list according to the Java default built-in collation rules.
     *
     * @param wordsList - a list with locale sensitive words which needs to be sorted
     * @param locale - locale origin of the provided wordsList, if not provided current default locale will be used instead
     * @return - list sorted based on the locale's collation strategy
     */
    public static List<String> sortLocaleSensitiveList(List<String> wordsList, Locale locale) {
        List<String> result = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(wordsList)) {
            Collator collator = locale != null ? Collator.getInstance(locale) : Collator.getInstance();
            collator.setStrength(Collator.SECONDARY);

            //prepare for optimal sorting
            CollationKey[] collationKeys = new CollationKey[wordsList.size()];
            for (String word : wordsList) {
                collationKeys[wordsList.indexOf(word)] = collator.getCollationKey(word);
            }

            //sort
            Arrays.sort(collationKeys);

            //assemble final result
            result = new ArrayList<>(collationKeys.length);
            for (CollationKey key : collationKeys) {
                result.add(key.getSourceString());
            }
        }

        return result;
    }

    /**
     * Sort provided locale sensitive list according to the custom collation rule set which must be provided as an input parameter to the method
     *
     * @param wordsList - a list with locale sensitive words which needs to be sorted
     * @param rule - see JavaDoc for java.text.RuleBasedCollator for examples on how to create a valid parsable rule set
     *
     * @return list sorted based on the collation rule strategy
     *
     * @throws ParseException A format exception will be thrown if the build process of the rules fails. For example, build rule "a < ? < d" will cause the constructor to throw the ParseException because the '?' is not quoted.
     */
    public static List<String> customSortLocaleSensitiveList(List<String> wordsList, String rule) throws ParseException {
        if(StringUtils.isBlank(rule)) {
            throw new ParseException("Build rules empty", 0);
        }

        List<String> result = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(wordsList)) {
            RuleBasedCollator ruleBasedCollator = new RuleBasedCollator(rule);
            CollationKey[] collationKeys = new CollationKey[wordsList.size()];

            //prepare for optimal sorting
            for (String word : wordsList) {
                collationKeys[wordsList.indexOf(word)] = ruleBasedCollator.getCollationKey(word);
            }

            //sort
            Arrays.sort(collationKeys);

            //assemble final result
            result = new ArrayList<>(collationKeys.length);
            for (CollationKey key : collationKeys) {
                result.add(key.getSourceString());
            }
        }

        return result;
    }


    private static Optional<Integer> compareBlankStrings(String source, String target) {
        if(StringUtils.isBlank(source) && StringUtils.isBlank(target) )
            return Optional.of(0);
        if(StringUtils.isBlank(source) && StringUtils.isNotBlank(target) )
            return Optional.of(-1);
        if(StringUtils.isNotBlank(source) && StringUtils.isBlank(target) )
            return Optional.of(1);

        return Optional.empty();
    }
}
