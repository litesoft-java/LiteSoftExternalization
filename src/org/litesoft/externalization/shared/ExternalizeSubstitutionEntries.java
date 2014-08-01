package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.iterators.*;
import org.litesoft.commonfoundation.typeutils.*;

public class ExternalizeSubstitutionEntries {

    private Object[] mValuesByIndex;

    public ExternalizeSubstitutionEntries( Object... pValuesByIndex ) {
        mValuesByIndex = (pValuesByIndex != null) ? pValuesByIndex : Objects.EMPTY_ARRAY;
    }

    public Externalizable get( E13nResolver pE13nResolver, ExternalizableCodeSupplier pExternalizable ) {
        if ( mValuesByIndex.length == 0 ) {
            return pExternalizable;
        }
        ArrayIterator<Object> zIT = new ArrayIterator<Object>( mValuesByIndex );
        E13nData.BuilderIndexed zBuilder = add( pE13nResolver, E13nData.builder( pExternalizable ), zIT.next() );
        while ( zIT.hasNext() ) {
            zBuilder = add( pE13nResolver, zBuilder, zIT.next() );
        }
        return zBuilder.build();
    }

    private E13nData.BuilderIndexed add( E13nResolver pE13nResolver, E13nData.AbstractIndexBuilder pBuilder, Object pParameter ) {
        Object zParameter = ConstrainTo.notNull( pParameter, "" );
        if ( zParameter instanceof Externalizable ) {
            return pBuilder.addNextIndexedValue( pE13nResolver, (Externalizable) zParameter );
        }
        return pBuilder.addNextIndexedUserData( zParameter.toString() );
    }

    private static final ExternalizeSubstitutionEntries NULL = new ExternalizeSubstitutionEntries();

    public static ExternalizeSubstitutionEntries deNull( ExternalizeSubstitutionEntries pEntries ) {
        return (pEntries != null) ? pEntries : NULL;
    }
}
