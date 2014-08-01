package org.litesoft.externalization.shared;

public class Tooltip {
    /**
     * @return Either text found or ""
     */
    public static String text( E13nResolver pE13nResolver, Externalizable pExternalizable ) {
        return E13nResolver.Code.resolveWithSuffix( pE13nResolver, pExternalizable, E13nResolverCodeSuffix.TOOLTIP );
    }

    /**
     * @return Either text found or ""
     */
    public static String text( E13nResolver pE13nResolver, String pCodeForTitle ) {
        return E13nResolver.Code.resolveWithSuffix( pE13nResolver, pCodeForTitle, E13nResolverCodeSuffix.TOOLTIP );
    }
}
