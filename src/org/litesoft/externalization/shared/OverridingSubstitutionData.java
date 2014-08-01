package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;

public class OverridingSubstitutionData implements E13nSubstitutionData {
    private final E13nSubstitutionData overrides;
    private final E13nSubstitutionData proxied;

    /* package friendly */ OverridingSubstitutionData( E13nSubstitutionData overrides, E13nSubstitutionData proxied ) {
        this.overrides = Confirm.isNotNull( "overrides", overrides );
        this.proxied = Confirm.isNotNull( "proxied", proxied );
    }

    @Override
    public String get( String key ) {
        String results = overrides.get( key );
        return (results != null) ? results : proxied.get( key );
    }
}
