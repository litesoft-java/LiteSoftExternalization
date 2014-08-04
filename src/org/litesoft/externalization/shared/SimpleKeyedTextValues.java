package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;

import java.util.*;

public class SimpleKeyedTextValues implements KeyedTextValues {
    private final Map<String, String> mMap;

    /* package friendly */ SimpleKeyedTextValues( Map<String, String> pMap ) {
        mMap = pMap;
    }

    public static SimpleKeyedTextValues with( String pKey, String pValue ) {
        return new SimpleKeyedTextValues( new HashMap<String, String>() ).and( pKey, pValue );
    }

    public SimpleKeyedTextValues and( String pKey, String pValue ) {
        mMap.put( Confirm.isNotNull( "Key", pKey ), Confirm.isNotNull( "Value", pValue ) );
        return this;
    }

    @Override
    public String get( String key ) {
        return mMap.get( key );
    }
}
