package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;

/**
 * Implementation of a E13nResolver that depends on a TemplateSource.
 *
 * @author georgs
 */
public class E13nSubstitutionDataBasedResolver implements E13nResolver,
                                                   HelperE13nResolver.NonCompleting {

    private final E13nSubstitutionData mSubstitutionData;

    public E13nSubstitutionDataBasedResolver( E13nSubstitutionData pSubstitutionData ) {
        mSubstitutionData = Confirm.isNotNull( "TemplateSource", pSubstitutionData );
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
        return mSubstitutionData.get( pKey );
    }
}
