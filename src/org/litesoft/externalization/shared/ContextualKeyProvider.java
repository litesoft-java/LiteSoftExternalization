package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;

public class ContextualKeyProvider {
    public static final ContextualKeyProvider[] INSTANCES = {new ContextualKeyProvider()};

    private ContextualKeyProvider() {
    }

    /**
     * @param key !empty
     *
     * @return Possibly prefixed Key
     */
    public String getSearchKey( String key ) {
        return Confirm.significant( "key", key );
    }

    /**
     * @return either a new Provider if context is NOT empty or "this" if context IS empty
     */
    public ContextualKeyProvider addContext( String context ) {
        return (null == ConstrainTo.significantOrNull( context )) ? this : new Prefixing( context );
    }

    private static class Prefixing extends ContextualKeyProvider {
        private final String prefix;

        private Prefixing( String prefix ) {
            this.prefix = prefix;
        }

        @Override
        public String getSearchKey( String key ) {
            return prefix + E13nResolver.CONTEXT_KEY_SEP + super.getSearchKey( key );
        }

        @Override
        public ContextualKeyProvider addContext( String context ) {
            return (null == ConstrainTo.significantOrNull( context )) ? this : new Prefixing( prefix + E13nResolver.CONTEXT_KEY_SEP + context );
        }
    }
}
