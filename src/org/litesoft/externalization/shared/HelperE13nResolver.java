package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.externalization.shared.E13nData.SubstitutionValue;
import java.util.*;

/**
 * Since there can be many different implementations of E13nResolver, the
 * ability to chain them is not supported as all three "flavors" of the resolve
 * methods will ALWAYS resolve the request. To overcome this chaining problem an
 * implementation of E13nResolver can also implement the NotCompleting interface
 * listed below, and delegate all of the normal three E13nResolver methods to
 * this class, and all the hard work will be done here, by limiting the actual
 * resolving to the NotCompleting method: resolveWithoutCompleting.
 * <p/>
 * The delegation to this class and the use of resolveWithoutCompleting(),
 * allows for an E13nResolver implementation (that implements NonCompleting) to
 * chain to other E13nResolver implementation(s) (that also implement
 * NonCompleting).
 *
 * @author georgs
 */
public class HelperE13nResolver {
    private static final int MAX_DEPTH_FLAG_AS_CYCLE = 5;

    public interface NonCompleting {
        /**
         * @param pKey !empty
         *
         * @return null or raw template
         */
        String resolveWithoutCompleting( String pKey );
    }

    /**
     * Code to simplify the validation and conversion of a E13nResolver into a
     * NonCompleting for the purposes of chaining E13nResolver(s)
     *
     * @param pProxied !null
     *
     * @return proxied cast to a NonCompleting
     */
    public static NonCompleting validateProxy( E13nResolver pProxied ) {
        Confirm.isNotNull( "Proxied E13nResolver", pProxied );
        if ( pProxied instanceof NonCompleting ) {
            return (NonCompleting) pProxied;
        }
        throw new IllegalArgumentException( "Proxied E13nResolver not a NonCompleting instance: " + pProxied.getClass().getName() );
    }

    /**
     * Delegation method for E13nResolver: String resolve(E13nData data);
     *
     * @param data     !null
     * @param resolver !null
     */
    public static String resolveDataWith( E13nData data, NonCompleting resolver ) {
        Confirm.isNotNull( "E13nData data", data );
        String key = data.getTemplateIdCode();
        return processResolvedWithSubKeys( key, resolver.resolveWithoutCompleting( key ), resolver, data.getSubstitutionNamedValues() );
    }

    /**
     * Delegation method for E13nResolver: String resolve(Enum<?> key);
     *
     * @param key      !null
     * @param resolver !null
     */
    public static String resolveEnumWith( Enum<?> key, NonCompleting resolver ) {
        Confirm.isNotNull( "Enum key", key );
        String keyName = key.name();
        String prefix = ClassName.simple( key );
        String value = resolver.resolveWithoutCompleting( prefix + E13nResolver.PREFIX_SEP + keyName );
        if ( value == null ) {
            value = resolver.resolveWithoutCompleting( keyName );
        }
        return processResolvedWithSubKeys( keyName, value, resolver, Collections.<String, SubstitutionValue>emptyMap() );
    }

    /**
     * Delegation method for E13nResolver: String resolve(String key);
     *
     * @param key      !null
     * @param resolver !null
     */
    public static String resolveStringWith( String key, NonCompleting resolver ) {
        key = Confirm.isNotNull( "String key", key );
        return processResolvedWithSubKeys( key, resolver.resolveWithoutCompleting( key ), resolver, Collections.<String, SubstitutionValue>emptyMap() );
    }

    /**
     * Common code from all three of the above delegation methods that handles
     * the primary key resolved value and then delegates to the sub-key
     * processor.
     * <p/>
     * When a primary key is not found, it is resolved to a "boxed" form, e.g. a
     * key of "Type" that is not found will return "[Type]". However, if there
     * are override substitution values, then the "boxed" form adds the override
     * values, and attempts resolve the sub-keys, e.g.
     * "[Type:Name={Name},Why={Why}]".
     * <p/>
     * Since unresolved sub-keys are left wrapped and the unresolved primary
     * keys are wrapped with '[' & ']' characters, it is hoped that unresolved
     * keys will be easy to recognize in the resulting text displayed.
     *
     * @param key       key looked up (!null)
     * @param value     value found without completing for the key (null OK)
     * @param overrides key/value pairs to resolve sub-keys from first (before
     *                  delegating to the resolver)
     *
     * @return resolved string (!null)
     */
    private static String processResolvedWithSubKeys( String key, String value, NonCompleting resolver, Map<String, SubstitutionValue> overrides ) {
        if ( value == null ) {
            StringBuilder sb = new StringBuilder().append( '[' ).append( key );
            char prefix = ':';
            for ( String name : overrides.keySet() ) {
                sb.append( prefix ).append( name ).append( "='" ).append( E13nResolver.INIT ).append( name ).append( E13nResolver.FINI ).append( "'" );
                prefix = ',';
            }
            value = sb.append( ']' ).toString();
        }
        return processSubKeys( 0, value, resolver, overrides );
    }

    /**
     * Process the resolved 'value' for any sub-keys recursively.
     *
     * @param depth     used to track levels of recursion for giving up!
     * @param value     value found without completing for the key OR the "boxed"
     *                  primary key (!null)
     * @param overrides key/value pairs to resolve sub-keys from first (before
     *                  delegating to the resolver)
     *
     * @return resolved string (!null)
     */
    private static String processSubKeys( int depth, String value, NonCompleting resolver, Map<String, SubstitutionValue> overrides ) {
        int finiAt = value.indexOf( E13nResolver.FINI );
        if ( finiAt == -1 ) {
            return value; // Happy case, no sub-keys
        }
        StringBuilder sb = new StringBuilder();
        do {
            // extract each sub-key
            String left = value.substring( 0, ++finiAt );
            value = value.substring( finiAt );
            int initAt = left.indexOf( E13nResolver.INIT );
            if ( initAt == -1 ) {
                sb.append( left ); // Dangling "FINI" (No "INIT")
            } else {
                sb.append( left.substring( 0, initAt ) );
                String wrappedKey = left.substring( initAt );
                // process the potential "wrapped" sub-key
                String substitutionText = wrappedResolveWith( depth, wrappedKey, resolver, overrides );
                if ( substitutionText != null ) {
                    sb.append( substitutionText ); // Success - resolved the key!
                } else {
                    sb.append( wrappedKey ); // Couldn't resolve - simply add the wrapped key (not a key?)
                }
            }
        }
        while ( -1 != (finiAt = value.indexOf( E13nResolver.FINI )) );
        return sb.append( value ).toString();
    }

    /**
     * Handle the two special case sub-keys, and if not one of them, then unwrap
     * it and try to resolve it.
     *
     * @param depth      used to track levels of recursion for giving up!
     * @param wrappedKey wrapped key to looked up (!null) (wrapped means still
     *                   surrounded by the sub-key indicators - see: E13nResolver INIT
     *                   & FINI)
     * @param overrides  key/value pairs to resolve sub-keys from first (before
     *                   delegating to the resolver)
     *
     * @return resolved string (!null)
     */
    private static String wrappedResolveWith( int depth, String wrappedKey, NonCompleting resolver, Map<String, SubstitutionValue> overrides ) {
        if ( E13nResolver.DONT_SHOW_SUBSTITUTION_ID.equals( wrappedKey ) ) { // Special sub-key for Empty String
            return ""; // Empty String
        }
        if ( E13nResolver.SPACE_SUBSTITUTION_ID.equals( wrappedKey ) ) { // Special sub-key for Space
            return " "; // Space
        }
        // Unwrap & attempt to resolve
        return unwrappedResolveWith( depth, wrappedKey.substring( 1, wrappedKey.length() - 1 ), resolver, overrides );
    }

    /**
     * Resolve the unwrapped sub-key using one of the following three kay/value
     * types:
     * <p/>
     * <li>override of User Data - just return the override 'value'</li>
     * <p/>
     * <li>override of non-User Data (possible key) - treat the override 'value'
     * as an unwrapped key and resolve</li>
     * <p/>
     * <li>non-override key - revolve (w/o completing) the key, if !null then
     * recursively process for sub-keys</li>
     *
     * @param depth     used to track levels of recursion for giving up!
     * @param key       key to looked up (!null but may be "")
     * @param overrides key/value pairs to resolve sub-keys from first (before
     *                  delegating to the resolver)
     *
     * @return resolved string (!null)
     */
    private static String unwrappedResolveWith( int depth, String key, NonCompleting resolver, Map<String, SubstitutionValue> overrides ) {
        if ( depth > MAX_DEPTH_FLAG_AS_CYCLE ) {
            return "[?" + key + " - Cycle?]";
        }
        SubstitutionValue substitutionValue = overrides.get( key );
        if ( substitutionValue != null ) {
            String value = substitutionValue.getValue();
            if ( substitutionValue.isUserData() ) {
                return value; // User Value : No further processing
            }
            // !User Data : Assume Value is a Key
            return unwrappedResolveWith( depth + 1, value, resolver, overrides ); // Recurse
        }
        String value = resolver.resolveWithoutCompleting( key ); // No Override! value may have Sub-Keys
        return (value == null) ? null : processSubKeys( depth + 1, value, resolver, overrides ); // Recurse
    }
}
