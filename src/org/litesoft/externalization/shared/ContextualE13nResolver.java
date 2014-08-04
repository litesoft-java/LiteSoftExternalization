package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.typeutils.*;

/**
 * Implementation of a E13nResolver that Optionally adds some text(s) onto each
 * key as part of the lookup of the underlying template.
 *
 * @author georgs/smitg
 */
public class ContextualE13nResolver extends AbstractE13nResolver {

    public ContextualE13nResolver( E13nResolver proxied, String requiredContext, String... additionalContexts ) {
        super( proxied.getSubstitutionData(),
               createKeyProviders( proxied, requiredContext, additionalContexts ) );
    }

    public ContextualE13nResolver( E13nResolver proxied, KeyedTextValues overrides, String requiredContext, String... additionalContexts ) {
        super( new OverridingKeyedTextValues( overrides, proxied.getSubstitutionData() ),
               createKeyProviders( proxied, requiredContext, additionalContexts ) );
    }

    private static ContextualKeyProvider[] createKeyProviders( E13nResolver proxied, String requiredContext, String... additionalContexts ) {
        requiredContext = Confirm.significant( "requiredContext", requiredContext );
        additionalContexts = Strings.deNull( additionalContexts );
        ContextualKeyProvider[] currentKeyProviders = proxied.getContextualKeyProviders();
        ContextualKeyProvider[] newKeyProviders = new ContextualKeyProvider[(2 + additionalContexts.length) * currentKeyProviders.length];
        int to = 0;
        for ( ContextualKeyProvider zKeyProvider : currentKeyProviders ) {
            newKeyProviders[to++] = zKeyProvider.addContext( requiredContext );
            for ( String additionalContext : additionalContexts ) {
                newKeyProviders[to++] = zKeyProvider.addContext( additionalContext );
            }
            newKeyProviders[to++] = zKeyProvider;
        }
        return newKeyProviders;
    }
}
