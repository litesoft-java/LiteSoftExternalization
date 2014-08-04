package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;

/**
 * Implementation of a E13nResolver that checks the overriding E13nSubstitutionData before checking with the proxied.
 *
 * @author georgs/smitg
 */
public class OverridingE13nResolver extends AbstractE13nResolver {

    public OverridingE13nResolver( KeyedTextValues overrides, E13nResolver proxied ) {
        super( new OverridingKeyedTextValues( overrides, proxied.getSubstitutionData() ), proxied.getContextualKeyProviders() );
    }
}
