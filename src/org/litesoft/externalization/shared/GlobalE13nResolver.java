package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;

public class GlobalE13nResolver {

    private static E13nResolver sE13nResolver;

    public static synchronized E13nResolver get() {
        if ( sE13nResolver == null ) {
            set( KeyedTextValues.EMPTY );
        }
        return sE13nResolver;
    }

    public static synchronized E13nResolver set( E13nResolver pE13nResolver ) {
        return (sE13nResolver = pE13nResolver);
    }

    public static E13nResolver set( KeyedTextValues pSubstitutionData ) {
        return set( new E13nKeyedTextValuesBasedResolver( pSubstitutionData ) );
    }
}
