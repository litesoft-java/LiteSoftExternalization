package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;

public class OverridingKeyedTextValues implements KeyedTextValues {
    private final KeyedTextValues overrides;
    private final KeyedTextValues proxied;

    /* package friendly */ OverridingKeyedTextValues( KeyedTextValues overrides, KeyedTextValues proxied ) {
        this.overrides = Confirm.isNotNull( "overrides", overrides );
        this.proxied = Confirm.isNotNull( "proxied", proxied );
    }

    @Override
    public String get( String key ) {
        String results = overrides.get( key );
        return (results != null) ? results : proxied.get( key );
    }
}
