package org.litesoft.externalization.shared;

/**
 * Since there can be many different implementations of E13nResolver, the
 * ability to chain them is not supported as all three "flavors" of the resolve
 * methods will ALWAYS resolve the request. To overcome this chaining problem an
 * implementation of E13nResolver can also implement the NotCompleting interface
 * listed below, and delegate all of the normal three E13nResolver methods to
 * this class, and all the hard work will be done here, by limiting the actual
 * resolving to the NotCompleting method: nonCompletingResolve.
 * <p/>
 * The delegation to this class and the use of nonCompletingResolve(),
 * allows for an E13nResolver implementation (that implements NonCompleting) to
 * chain to other E13nResolver implementation(s) (that also implement
 * NonCompleting).
 *
 * @author georgs
 */
public class HelperE13nResolver {
    private static final int MAX_DEPTH_FLAG_AS_CYCLE = 5;

//    public interface NonCompleting {
//        /**
//         * @param key
//         *            !empty
//         * @return Fully Qualified Key
//         */
//        String getFullyQualifiedKey(String key);
//
//        /**
//         * @param key
//         *            !empty
//         * @return null or raw tamplate
//         */
//        String nonCompletingResolve(String key);
//    }
//
//    /**
//     * Code to simplify the validation and conversion of a E13nResolver into a
//     * NonCompleting for the purposes of chaining E13nResolver(s)
//     *
//     * @param proxied
//     *            !null
//     * @return proxied cast to a NonCompleting
//     */
//    public static NonCompleting validateProxy(E13nResolver proxied) {
//        Assert.notNull("Proxied E13nResolver", proxied);
//        if (proxied instanceof NonCompleting) {
//            return (NonCompleting) proxied;
//        }
//        throw new IllegalArgumentException("Proxied E13nResolver not a NonCompleting instance: " + proxied.getClass().getName());
//    }
//
//    /**
//     * Delegation method for E13nResolver: String resolve(E13nData data);
//     *
//     * @param data
//     *            !null
//     * @param resolver
//     *            !null
//     */
//    public static String resolveDataWith(E13nData data, NonCompleting resolver) {
//        Assert.notNull("E13nData data", data);
//        String key = data.getTemplateIdCode();
//        return processResolvedWithSubKeys(key, resolver.nonCompletingResolve(key), resolver, data.getTemplateSubstitutionNamedValues());
//    }
//
//    /**
//     * Delegation method for E13nResolver: String resolve(Enum<?> key);
//     *
//     * @param key
//     *            !null
//     * @param resolver
//     *            !null
//     */
//    public static String resolveEnumWith(Enum<?> key, NonCompleting resolver) {
//        Assert.notNull("Enum key", key);
//        String keyName = key.name();
//        String prefix = ObjectUtils.getSimpleClassName(key);
//        String value = resolver.nonCompletingResolve(prefix + E13nResolver.PREFIX_SEP + keyName);
//        if (value == null) {
//            value = resolver.nonCompletingResolve(keyName);
//        }
//        return processResolvedWithSubKeys(keyName, value, resolver, Collections.<String, SubstitutionValue> emptyMap());
//    }
//
//    /**
//     * Delegation method for E13nResolver: String resolve(String key);
//     *
//     * @param key
//     *            !null
//     * @param resolver
//     *            !null
//     */
//    public static String resolveStringWith(String key, NonCompleting resolver) {
//        key = Assert.significantOrNull("String key", key);
//        return processResolvedWithSubKeys(key, resolver.nonCompletingResolve(key), resolver, Collections.<String, SubstitutionValue> emptyMap());
//    }
//
//    /**
//     * Common code from all three of the above delegation methods that handles
//     * the primary key resolved value and then delegates to the sub-key
//     * processor.
//     *
//     * When a primary key is not found, it is resolved to a "boxed" form, e.g. a
//     * key of "Type" that is not found will return "[Type]". However, if there
//     * are override substitution values, then the "boxed" form adds the override
//     * values, and attempts resolve the sub-keys, e.g.
//     * "[Type:Name={Name},Why={Why}]".
//     *
//     * Since unresolved sub-keys are left wrapped and the unresolved primary
//     * keys are wrapped with '[' & ']' characters, it is hoped that unresolved
//     * keys will be easy to recognize in the resulting text displayed.
//     *
//     * @param key
//     *            key looked up (!null)
//     * @param value
//     *            value found without completing for the key (null OK)
//     * @param overrides
//     *            key/value pairs to resolve sub-keys from first (before
//     *            delegating to the resolver)
//     *
//     * @return resolved string (!null)
//     */
//    private static String processResolvedWithSubKeys(String key, String value, NonCompleting resolver, Map<String, SubstitutionValue> overrides) {
//        if (value == null) {
//            StringBuilder sb = new StringBuilder().append('[').append(resolver.getFullyQualifiedKey(key));
//            char prefix = ':';
//            for (String name : overrides.keySet()) {
//                sb.append(prefix).append(name).append("='").append(E13nResolver.INIT).append(name).append(E13nResolver.FINI).append("'");
//                prefix = ',';
//            }
//            value = sb.append(']').toString();
//            System.out.println("Unable to resolve: " + value);
//        }
//        return processSubKeys(0, value, resolver, overrides);
//    }
//
//
//
}
