package org.litesoft.externalization.shared;

/**
 * Implementation of a E13nResolver that checks the overriding E13nSubstitutionData before checking with the proxied.
 *
 * @author georgs/smitg
 */
public class OverridingE13nResolver extends AbstractE13nResolver {

    public OverridingE13nResolver( E13nSubstitutionData overrides, E13nResolver proxied ) {
        super( new OverridingSubstitutionData( overrides, proxied.getSubstitutionData() ), proxied.getContextualKeyProviders() );
    }
}
