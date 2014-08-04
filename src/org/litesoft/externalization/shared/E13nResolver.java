package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;

public interface E13nResolver {
    public static final char CONTEXT_KEY_SEP = '_';  // Need to maintain == LocaleFileConstants.COMPOUND_KEY_SEP
    public static final char INIT = '{'; // For Substitution Key
    public static final char FINI = '}'; // For Substitution Key
    public static final String DONT_SHOW_SUBSTITUTION_ID = "{DontShow}"; // Replaced by a "" (empty String)
    public static final String SPACE_SUBSTITUTION_ID = "{Space}"; // used to force either leading or trailing spaces from being "trim()" away

    /**
     * @param key !empty
     *
     * @return Fully Qualified Key
     */
    String getFullyQualifiedKey( String key );

    /**
     * Get the backing substitution data (a form of limited API String-String Map).
     * <p/>
     * This method is primarily used to generate a chain of responsibility pattern for overriding the substitution data.
     */
    KeyedTextValues getSubstitutionData();

    /**
     * Get the underlying ContextualKeyProvider(s) that are used to create the search keys.
     * <p/>
     * The first entry is used to produce the FullyQualifiedKey.
     * <p/>
     * This method is primarily used to generate a chain of responsibility behavior for adding optional Context to the search keys.
     */
    ContextualKeyProvider[] getContextualKeyProviders();

    /**
     * Get a Contextual E13nResolver by adding "requiredContext".
     * <p/>
     * @param requiredContext must be significant.
     */
    E13nResolver withContext(String requiredContext);

    /**
     * Get a Contextual E13nResolver by adding "context" if it is significant.
     */
    E13nResolver withOptionalContext(String context);

    /**
     * Resolves the 'key' to a String by implementing an Externally Sourced
     * String keyed template system that supports substitutions within any
     * specific template by "named" values.
     *
     * @param key !null & !empty (after trimming)
     *
     * @return Resolved String form of the 'key' or a null if not found!
     */
    String resolveOptionally( String key );

    /**
     * Resolves the 'key' to a String by implementing an Externally Sourced
     * String keyed template system that supports substitutions within any
     * specific template by "named" values.
     *
     * @param key !null & !empty (after trimming)
     *
     * @return Resolved String form of the 'key' or the 'defaultValue' if not
     * found!
     */
    String resolveOrDefault( String key, String defaultValue );

    /**
     * Resolves the 'key' to a String by implementing an Externally Sourced
     * String keyed template system that supports substitutions within any
     * specific template by "named" values.
     *
     * @param key !null & !empty (after trimming)
     *
     * @return Resolved String form of the 'key' or the 'key' in square
     * brackets ("[]") if not found!
     */
    String resolve( String key );

    /**
     * Resolves the 'externalizable' to a String by implementing an Externally Sourced
     * String keyed template system that supports substitutions within any
     * specific template by "named" values.
     * <p/>
     * The order of evaluation is:
     * <li>CustomizedExternalizable interface: getExternalizedText(E13nResolver pE13nResolver)</li>
     * <li>ExternalizableByCode interface: resolve({externalizable}.getExternalizableCode())</li>
     * <li>resolve(externalizable.toString())</li>
     *
     * @param externalizable !null (& if a String key is generated is !empty - after trimming)
     *
     * @return Resolved String form of the 'externalizable' or ... (if the externalizable is
     * actually a CustomizedExternalizable).
     */
    String resolve( Externalizable externalizable );

    public static class Code {
        public static String get( Externalizable externalizable ) {
            if ( externalizable != null ) {
                if ( externalizable instanceof ExternalizableByCode ) {
                    return ((ExternalizableByCode) externalizable).getExternalizableCode();
                }
                if ( externalizable instanceof ExternalizableByCodeWithData ) {
                    return ((ExternalizableByCodeWithData) externalizable).getE13nData().getExternalizableCode();
                }
                if ( (externalizable instanceof ExternalizableCodeSupplier) || !(externalizable instanceof CustomizedExternalizable) ) {
                    return externalizable.toString();
                }
            }
            return "";
        }

        public static String extendedCode( ExternalizableEnum externalizable, String code ) {
            return ClassName.simple( externalizable ) + CONTEXT_KEY_SEP + ConstrainTo.significantOrNull( code, externalizable.name() );
        }

        /**
         * This method treats the 'externalizable' similar to E13nResolver.resolve(Externalizable) to find a 'code'
         * (an implementation of CustomizedExternalizable w/o ExternalizableCodeSupplier is ignored!) that is then
         * suffixed w/ 'suffix'.
         *
         * @param suffix !null
         *
         * @return null or the combination of the 'externalizable code' + 'suffix'.
         */
        public static String suffixedCode( Externalizable externalizable, String suffix ) {
            String zCode = ConstrainTo.significantOrNull( get( externalizable ) );
            return (zCode == null) ? null : zCode + suffix;
        }

        /**
         * This method treats the 'externalizable' similar to E13nResolver.resolve(Externalizable) to find a 'code'
         * (an implementation of CustomizedExternalizable w/o ExternalizableCodeSupplier is ignored!) that is then
         * suffixed w/ 'suffix'.
         *
         * @param pE13nResolver !null
         * @param suffix        !null assumed
         *
         * @return "" or the resolved combination of the 'externalizable code' + 'suffix'.
         */
        public static String resolveWithSuffix( E13nResolver pE13nResolver, Externalizable externalizable, String suffix ) {
            return resolveWithSuffix( pE13nResolver, get( externalizable ), suffix );
        }

        /**
         * @param pE13nResolver !null
         * @param suffix        !null assumed
         *
         * @return "" or the resolved combination of the 'code' + 'suffix'.
         */
        public static String resolveWithSuffix( E13nResolver pE13nResolver, String code, String suffix ) {
            return (null == (code = ConstrainTo.significantOrNull( code ))) ?
                   "" : pE13nResolver.resolveOrDefault( code + suffix, "" );
        }
    }
}
