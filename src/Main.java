import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.text.CollationKey;
import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Main {

    private static final Locale TEST_LOCALE = Locale.CANADA_FRENCH;
    private static final String CUSTOM_RULES = "< A,À,Á,Â,Ä,Æ,Ã,Å,Ā,a,à,á,â,ä,æ,ã,å,ā < B,b < C,Ç,Ć,Č,c,ç,ć,č " +
                                        "< D,d < E,È,É,Ê,Ë,Ē,Ė,Ę,e,è,é,ê,ë,ē,ė,ę < F,f < G,g < H,h " +
                                        "< I,Î,Ï,Í,Ī,Į,Ì,i,î,ï,í,ī,į,ì < J,j < K,k < L,Ł,l,ł < M,m " +
                                        "< N,Ñ,Ń,n,ñ,ń < O,Ô,Ö,Ò,Ó,Œ,Ø,Ō,Õ,o,ô,ö,ò,ó,œ,ø,ō,õ < P,p < Q,q " +
                                        "< R,r < S,Ś,Š,s,ś,š < T,t < U,Û,Ü,Ù,Ú,Ū,u,û,ü,ù,ú,ū < W,w " +
                                        "< X,x < Y,Ÿ,y,ÿ < Z,Ž,Ź,Ż,z,ž,ź,ż";

    private static final List<String> words = Arrays.asList("Äbc", "äbc", "Àbc", "àbc", "Abc", "abc", "ABC", "BED",
                                                            "ébárquér", "end", "zebra", "San","Mr. Antone Jorge",
                                                            "ábrquér", "žebra", "Žebra", "žebrá", "Antone Jorge",
                                                            "antone jorge", "Àntóné Jorge", "àntóné jorge", "Mr. Àntóné Jorge");

    public static void main(String... aArguments) throws ParseException {
        System.out.println(" --- Original Data --- \n" + words);
        List<String> builtInSortResult = sortLocaleSensitiveList(words, TEST_LOCALE);
        List<String> customSortResult = customSortLocaleSensitiveList(words, CUSTOM_RULES);
        System.out.println("\n --- Built-In sorting results --- \n");
        builtInSortResult.stream().forEach(word -> System.out.print(word + " "));
        System.out.println("\n\n --- Custom sorting results --- \n");
        customSortResult.stream().forEach(word -> System.out.print(word + " "));
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
        collator.setStrength(Collator.SECONDARY);
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
