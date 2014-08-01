package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;

/**
 * Implementation of a E13nResolver that Optionally Prefixes some text onto each
 * key as part of the lookup of the underlying template.
 *
 * @author georgs
 */
public class ContextualE13nResolver implements E13nResolver,
                                               HelperE13nResolver.NonCompleting {

    private final String prefix;
    private final HelperE13nResolver.NonCompleting proxied;

    public ContextualE13nResolver( E13nResolver pProxied, String pContext ) {
        this.prefix = Confirm.significant( "prefix", pContext );
        this.proxied = HelperE13nResolver.validateProxy( pProxied );
    }

    @Override
    public String resolve( E13nData pData ) {
        return HelperE13nResolver.resolveDataWith( pData, this );
    }

    @Override
    public String resolve( Enum<?> pKey ) {
        return HelperE13nResolver.resolveEnumWith( pKey, this );
    }

    @Override
    public String resolve( String pKey ) {
        return HelperE13nResolver.resolveStringWith( pKey, this );
    }

    @Override
    public String resolveWithoutCompleting( String pKey ) {
        String value = proxied.resolveWithoutCompleting( prefix + PREFIX_SEP + pKey );
        return (value != null) ? value : proxied.resolveWithoutCompleting( pKey );
    }
}
