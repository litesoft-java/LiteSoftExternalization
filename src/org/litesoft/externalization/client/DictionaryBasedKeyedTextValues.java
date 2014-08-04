package org.litesoft.externalization.client;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.typeutils.*;

import com.google.gwt.i18n.client.Dictionary;

import java.util.*;

public class DictionaryBasedKeyedTextValues implements KeyedTextValues {
    private final Map<String, String> mMap = Maps.newHashMap();

    public DictionaryBasedKeyedTextValues( Dictionary pDictionary ) {
        for ( String zKey : pDictionary.keySet() ) {
            mMap.put( zKey, pDictionary.get( zKey ) );
        }
    }

    public DictionaryBasedKeyedTextValues( String pDictionaryName ) {
        this( Dictionary.getDictionary( pDictionaryName ) );
    }

    @Override
    public String get( String key ) {
        return mMap.get( key );
    }
}
