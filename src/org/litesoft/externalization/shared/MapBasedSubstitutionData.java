package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;

import java.util.*;

public class MapBasedSubstitutionData implements E13nSubstitutionData {
    private final Map<String, String> mMap;

    /* package friendly */ MapBasedSubstitutionData( Map<String, String> pMap ) {
        mMap = pMap;
    }

    public static MapBasedSubstitutionData with( String pKey, String pValue ) {
        return new MapBasedSubstitutionData( new HashMap<String, String>() ).and( pKey, pValue );
    }

    public MapBasedSubstitutionData and( String pKey, String pValue ) {
        mMap.put( Confirm.isNotNull( "Key", pKey ), Confirm.isNotNull( "Value", pValue ) );
        return this;
    }

    @Override
    public String get( String key ) {
        return mMap.get( key );
    }
}
