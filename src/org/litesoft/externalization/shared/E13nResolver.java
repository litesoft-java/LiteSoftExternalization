package org.litesoft.externalization.shared;

/**
 * Externalization Resolver that takes an E13nData (or Enum key or String key)
 * and resolves it to a String with an Externally Sourced String keyed template
 * system that supports substitutions within any specific template by "named"
 * values.
 * <p/>
 * Since Substitution Keys may NOT have spaces, spaces can be used to force
 * what would appear to be a substitution key (something between the INIT & FINI
 * characters) to be ignored. Unbalanced INIT & FINI characters or an
 * empty substitution key are also ignored.
 */
public interface E13nResolver {
    public static final char PREFIX_SEP = '_';
    public static final char INIT = '{'; // For Substitution Key
    public static final char FINI = '}'; // For Substitution Key
    public static final String DONT_SHOW_SUBSTITUTION_ID = "{DontShow}"; // Replaced by a "" (empty String)
    public static final String SPACE_SUBSTITUTION_ID = "{Space}"; // used to force either leading or trailing spaces from being "trim()" away

    /**
     * Resolves the 'data' to a String by implementing an Externally Sourced
     * String keyed template system that supports substitutions within any
     * specific template by "named" values.
     *
     * @param pData !null
     *
     * @return Resolved String form of the !null 'data';
     */
    public String resolve( E13nData pData );

    /**
     * Resolves the 'key' (by optionally prefixing its name with the enum's
     * simple class name) to a String by implementing an Externally Sourced
     * String keyed template system that supports substitutions within any
     * specific template by "named" values.
     *
     * @param pKey !null
     *
     * @return Resolved String form of the !null 'key';
     */
    public String resolve( Enum<?> pKey );

    /**
     * Resolves the 'pKey' to a String by implementing an Externally Sourced
     * String keyed template system that supports substitutions within any
     * specific template by "named" values.
     *
     * @param pKey !null & !empty (after trimming)
     *
     * @return Resolved String form of the !empty 'Key';
     */
    public String resolve( String pKey );
}
