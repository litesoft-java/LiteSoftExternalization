package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;

public abstract class AbstractE13nResolver implements E13nResolver {
    private static final int MAX_DEPTH_FLAG_AS_CYCLE = 5;

    private final KeyedTextValues substitutionData;
    private final ContextualKeyProvider[] keyProviders;

    protected AbstractE13nResolver( KeyedTextValues substitutionData, ContextualKeyProvider[] keyProviders ) {
        this.substitutionData = Confirm.isNotNull( "substitutionData", substitutionData );
        this.keyProviders = Confirm.isNotNull( "keyProviders", keyProviders );
    }

    protected AbstractE13nResolver( KeyedTextValues pSubstitutionData ) {
        this( pSubstitutionData, ContextualKeyProvider.INSTANCES );
    }

    protected String customWrappedKeys( String wrappedKey ) {
        if ( DONT_SHOW_SUBSTITUTION_ID.equals( wrappedKey ) ) { // Special sub-key for Empty String
            return ""; // Empty String
        }
        if ( SPACE_SUBSTITUTION_ID.equals( wrappedKey ) ) { // Special sub-key for Space
            return " "; // Space
        }
        return null;
    }

    @Override
    public E13nResolver withContext( String requiredContext ) {
        return new ContextualE13nResolver( this, requiredContext );
    }

    @Override
    public E13nResolver withOptionalContext( String context ) {
        return (null == ConstrainTo.significantOrNull( context )) ? this : withContext( context );
    }

    @Override
    public final String getFullyQualifiedKey( String key ) {
        return keyProviders[0].getSearchKey( key );
    }

    @Override
    public final KeyedTextValues getSubstitutionData() {
        return substitutionData;
    }

    @Override
    public final ContextualKeyProvider[] getContextualKeyProviders() {
        return keyProviders;
    }

    @Override
    public final String resolveOptionally( String key ) {
        String results = nonCompletingResolve( Confirm.significant( "key", key ) );
        return (results == null) ? null : resolveResultsWith( results );
    }

    @Override
    public final String resolveOrDefault( String key, String defaultValue ) {
        return ConstrainTo.notNull( resolveOptionally( key ), defaultValue );
    }

    @Override
    public final String resolve( String key ) {
        String zFound = resolveOptionally( key );
        return (zFound != null) ? zFound : keyNotFound( key );
    }

    @Override
    public final String resolve( Externalizable externalizable ) {
        if ( externalizable instanceof CustomizedExternalizable ) {
            return ((CustomizedExternalizable) externalizable).getExternalizedText( this );
        }
        if ( externalizable instanceof ExternalizableByCodeWithData ) {
            E13nData zE13nData = ((ExternalizableByCodeWithData) externalizable).getE13nData();
            return new OverridingE13nResolver( zE13nData.getSubstitutionData(), this )
                    .resolve( zE13nData.getExternalizableCode() );
        }
        if ( externalizable instanceof E13nData ) {
            E13nData zE13nData = (E13nData) externalizable;
            return new OverridingE13nResolver( zE13nData.getSubstitutionData(), this )
                    .resolve( zE13nData.getExternalizableCode() );
        }
        if ( externalizable instanceof ExternalizableByCode ) {
            return resolve( ((ExternalizableByCode) externalizable).getExternalizableCode() );
        }
        if ( externalizable instanceof ExternalizableEnum ) {
            return new ContextualE13nResolver( this, ClassName.simple( externalizable ) )
                    .resolve( ((ExternalizableEnum) externalizable).name() );
        }
        return resolve( externalizable.toString() );
    }

    /**
     * @param key !empty
     *
     * @return null or raw template
     */
    private String nonCompletingResolve( String key ) {
        String result;
        for ( ContextualKeyProvider zKeyProvider : keyProviders ) {
            if ( null != (result = substitutionData.get( zKeyProvider.getSearchKey( key ) )) ) {
                return result;
            }
        }
        return null;
    }

//    private static String processResultsWith( AbstractE13nResolver resolver,
//                                              String code, E13nSubstitutionData curData, E13nSubstitutionData newData,
//                                              Externalizable externalizable, Class<?> klass,
//                                              String results ) {
//        if ( results != null ) {
//            if ( !newData.isEmpty() ) {
//                resolver = new OverridingE13nResolver( resolver, merge( curData, newData ) );
//            }
//            return resolveResultsWith( resolver, results );
//        }
//        StringBuilder sb = new StringBuilder();
//        sb.append( '[' );
//        sb.append( ClassName.simple( externalizable ) ).append( '-' ).append( ClassName.simple( klass ) );
//        String[] zKeys = Strings.toArray( curData.keys() );
//        if ( (code != null) || (zKeys.length != 0) ) {
//            sb.append( '(' );
//            if ( code != null ) {
//                sb.append( '\'' ).append( code ).append( '\'' );
//            }
//            char prefix = ':';
//            for ( String zKey : zKeys ) {
//                sb.append( prefix ).append( ' ' ).append( zKey ).append( "='" );
//                appendTo( sb, curData.get( zKey ) );
//                sb.append( '\'' );
//                prefix = ',';
//            }
//            sb.append( ')' );
//        }
//        return sb.append( ']' ).toString();
//    }
//
//    private static E13nSubstitutionData merge( E13nSubstitutionData curData, E13nSubstitutionData newData ) {
//        return curData.isEmpty() ? newData : new MapBasedSubstitutionData( curData, newData );
//    }
//
//    private static void appendTo( StringBuilder sb, Object data ) {
//        if ( data instanceof String ) {
//            sb.append( data.toString() );
//            return;
//        }
//        String xtra = (data instanceof Externalizable) ? Code.get( (Externalizable) data ) : data.toString();
//        sb.append( ClassName.simple( data ) ).append( '(' ).append( xtra ).append( ')' );
//    }

    private String keyNotFound( String key ) {
        String zNotFound = "[" + getFullyQualifiedKey( key ) + "]";
        System.out.println( "Unable to Externalize: " + zNotFound );
        return zNotFound;
    }

    /**
     * @param results !null
     */
    private String resolveResultsWith( String results ) {
        return processSubKeys( 0, results );
    }

    /**
     * Process the resolved 'value' for any sub-keys recursively.
     *
     * @param depth used to track levels of recursion for giving up!
     * @param value value found without completing for the key OR the "boxed"
     *              primary key (!null)
     *
     * @return resolved string (!null)
     */
    private String processSubKeys( int depth, String value ) {
        int finiAt = value.indexOf( E13nResolver.FINI );
        if ( finiAt == -1 ) {
            return value; // Happy case, no sub-keys
        }
        StringBuilder sb = new StringBuilder();
        do {
            // extract each sub-key
            String left = value.substring( 0, ++finiAt );
            value = value.substring( finiAt );
            int initAt = left.indexOf( E13nResolver.INIT );
            if ( initAt == -1 ) {
                sb.append( left ); // Dangling "FINI" (No "INIT")
            } else {
                sb.append( left.substring( 0, initAt ) );
                String wrappedKey = left.substring( initAt );
                // process the potential "wrapped" sub-key
                String substitutionText = wrappedResolveWith( depth, wrappedKey );
                if ( substitutionText != null ) {
                    sb.append( substitutionText ); // Success - resolved the key!
                } else {
                    sb.append( wrappedKey ); // Couldn't resolve - simply add the wrapped key (not a key?)
                }
            }
        } while ( -1 != (finiAt = value.indexOf( E13nResolver.FINI )) );
        return sb.append( value ).toString();
    }

    /**
     * Handle the two special case sub-keys, and if not one of them, then unwrap
     * it and try to resolve it.
     *
     * @param depth      used to track levels of recursion for giving up!
     * @param wrappedKey wrapped key to looked up (!null) (wrapped means still
     *                   surrounded by the sub-key indicators - see: E13nResolver INIT
     *                   & FINI)
     *
     * @return resolved string (!null)
     */
    private String wrappedResolveWith( int depth, String wrappedKey ) {
        String results = customWrappedKeys( wrappedKey );
        return (results != null) ? results : // Otherwise Unwrap & attempt to resolve
               unwrappedResolveWith( depth, wrappedKey.substring( 1, wrappedKey.length() - 1 ) );
    }

    /**
     * Resolve the unwrapped sub-key.
     *
     * @param depth used to track levels of recursion for giving up!
     * @param key   key to looked up (!null but may be "")
     *
     * @return resolved string (!null)
     */
    private String unwrappedResolveWith( int depth, String key ) {
        if ( key.length() == 0 ) {
            return "[?Empty?]";
        }
        if ( depth > MAX_DEPTH_FLAG_AS_CYCLE ) {
            return "[?'" + key + "' - Cycle?]";
        }
        String results = nonCompletingResolve( key );
        return (results == null) ? keyNotFound( key ) : processSubKeys( depth + 1, results ); // Recurse
    }
}
