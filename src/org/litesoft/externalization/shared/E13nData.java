package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.problems.*;

import java.util.*;

public class E13nData implements ExternalizableByCode {
    public static E13nData from( Problem pProblem ) {
        Throwable zThrowable = pProblem.getThrowable();
        if ( zThrowable != null ) {
            zThrowable.printStackTrace();
        }
        Builder zBuilder = builder( pProblem.getProblemCode() );
        String[] zProblemSupportValues = pProblem.getProblemSupportValues();
        if ( Currently.isNotNullOrEmpty( zProblemSupportValues ) ) {
            return zBuilder.addIndexedUserData( zProblemSupportValues ).build();
        }
        List<NameValuePair> zNamedSupportValues = pProblem.getNamedSupportValues();
        if ( Currently.isNullOrEmpty( zNamedSupportValues ) ) {
            zBuilder.addSubstitutionNamedUserDatas( zNamedSupportValues ).build();
        }
        return zBuilder.build();
    }

    public static Builder builder( String externalizableCode ) {
        return new Builder( externalizableCode );
    }

    public static Builder builder( Enum<?> enumNameAsExternalizableCode ) {
        return builder( Confirm.isNotNull( "enumNameAsExternalizableCode", enumNameAsExternalizableCode ).name() );
    }

    public static Builder builder( ExternalizableCodeSupplier externalizableCodeSupplier ) {
        return new Builder( E13nResolver.Code.get( Confirm.isNotNull( "externalizableCodeSupplier",
                                                                      externalizableCodeSupplier ) ) );
    }

    private abstract static class AbstractBuilder {
        protected final String externalizableCode;
        private Map<String, String> substitutionNamedValues;

        private AbstractBuilder( String externalizableCode, Map<String, String> substitutionNamedValues ) {
            this.externalizableCode = externalizableCode;
            this.substitutionNamedValues = substitutionNamedValues;
        }

        /**
         * Add a list of Substitution Named Values (as plain text, should probably not contain substitution key identifiers).
         *
         * @param name     Not allowed to be empty
         * @param userData null converted to ""
         */
        public BuilderFinal addSubstitutionNamedUserDatas( List<NameValuePair> pNamedValues ) {
            if ( pNamedValues != null ) {
                for ( NameValuePair zNamedValue : pNamedValues ) {
                    if ( zNamedValue != null ) {
                        add( zNamedValue.getName(), zNamedValue.getValue() );
                    }
                }
            }
            return chainToBuilderFinal();
        }

        /**
         * Add a Substitution Named Value (as plain text, should probably not contain substitution key identifiers).
         *
         * @param name     Not allowed to be empty
         * @param userData null converted to ""
         */
        public BuilderFinal addSubstitutionNamedUserData( String name, String userData ) {
            add( name, userData );
            return chainToBuilderFinal();
        }

        /**
         * Add a Substitution Named Value (with the assumption that it will get wrapped by substitution key identifiers).
         *
         * @param name               Not allowed to be empty
         * @param externalizableCode Not allowed to be empty
         */
        public BuilderFinal addSubstitutionNamedExternalizableCode( String name, String externalizableCode ) {
            add( name, toNestedReference( externalizableCode ) );
            return chainToBuilderFinal();
        }

        /**
         * Add a Substitution Named Value (that is resolved immediately).
         *
         * @param name           Not allowed to be empty
         * @param externalizable null converted to ""
         */
        public BuilderFinal addSubstitutionNamedValue( String name, E13nResolver resolver, Externalizable externalizable ) {
            add( name, toString( resolver, externalizable ) );
            return chainToBuilderFinal();
        }

        protected String toNestedReference( String externalizableCode ) {
            return E13nResolver.INIT + Confirm.significant( "externalizableCode", externalizableCode ) + E13nResolver.FINI;
        }

        protected String toString( E13nResolver resolver, Externalizable externalizable ) {
            return (externalizable == null) ? "" : resolver.resolve( externalizable );
        }

        protected void addIndexed( String parameter ) {
            add( Integer.toString( substitutionNamedValues.size() ), parameter );
        }

        protected void addArrayIndexed( String... parameters ) {
            if ( parameters != null ) {
                for ( String parameter : parameters ) {
                    add( Integer.toString( substitutionNamedValues.size() ), parameter );
                }
            }
        }

        protected void add( String name, String parameter ) {
            if ( null != substitutionNamedValues.put( name = Confirm.significant( "name", name ), ConstrainTo.notNull( parameter ) ) ) {
                throw new IllegalStateException( "Duplicate Substitution added for '" + name + "'" );
            }
        }

        protected abstract BuilderFinal chainToBuilderFinal();

        protected Map<String, String> snagSubstitutionNamedValues() {
            Map<String, String> snagged = substitutionNamedValues;
            substitutionNamedValues = null;
            return snagged;
        }
    }

    public abstract static class AbstractIndexBuilder extends AbstractBuilder {
        private AbstractIndexBuilder( String externalizableCode, Map<String, String> substitutionNamedValues ) {
            super( externalizableCode, substitutionNamedValues );
        }

        /**
         * Add 'next' Indexed Substitution Value (as plain text, should probably not contain substitution key identifiers).
         *
         * @param userData null converted to ""
         */
        public BuilderIndexed addIndexedUserData( String... userData ) {
            addArrayIndexed( userData );
            return chainToBuilderIndexed();
        }

        /**
         * Add 'next' Indexed Substitution Value (as plain text, should probably not contain substitution key identifiers).
         *
         * @param userData null converted to ""
         */
        public BuilderIndexed addNextIndexedUserData( String userData ) {
            addIndexed( userData );
            return chainToBuilderIndexed();
        }

        /**
         * Add 'next' Indexed Substitution Value (with the assumption that it will get wrapped by substitution key identifiers).
         *
         * @param externalizableCode Not allowed to be empty
         */
        public BuilderIndexed addNextIndexedExternalizableCode( String externalizableCode ) {
            addIndexed( toNestedReference( externalizableCode ) );
            return chainToBuilderIndexed();
        }

        /**
         * Add 'next' Indexed Substitution Value (that is resolved immediately).
         *
         * @param externalizable null converted to ""
         */
        public BuilderIndexed addNextIndexedValue( E13nResolver resolver, Externalizable externalizable ) {
            addIndexed( toString( resolver, externalizable ) );
            return chainToBuilderIndexed();
        }

        @Override
        protected BuilderFinal chainToBuilderFinal() {
            return new BuilderFinal( externalizableCode, snagSubstitutionNamedValues() );
        }

        protected abstract BuilderIndexed chainToBuilderIndexed();
    }

    public static class Builder extends AbstractIndexBuilder {
        private Builder( String externalizableCode ) {
            super( Confirm.significant( "externalizableCode", externalizableCode ), new HashMap<String, String>() );
        }

        @Override
        protected BuilderIndexed chainToBuilderIndexed() {
            return new BuilderIndexed( externalizableCode, snagSubstitutionNamedValues() );
        }

        public E13nData build() {
            return new E13nData( externalizableCode, snagSubstitutionNamedValues() );
        }
    }

    public static class BuilderIndexed extends AbstractIndexBuilder {
        private BuilderIndexed( String externalizableCode, Map<String, String> substitutionNamedValues ) {
            super( externalizableCode, substitutionNamedValues );
        }

        @Override
        protected BuilderIndexed chainToBuilderIndexed() {
            return this;
        }

        public E13nData build() {
            return new E13nData( externalizableCode, snagSubstitutionNamedValues() );
        }
    }

    public static class BuilderFinal extends AbstractBuilder {
        private BuilderFinal( String externalizableCode, Map<String, String> substitutionNamedValues ) {
            super( externalizableCode, substitutionNamedValues );
        }

        @Override
        protected BuilderFinal chainToBuilderFinal() {
            return this;
        }

        public E13nData build() {
            return new E13nData( externalizableCode, snagSubstitutionNamedValues() );
        }
    }

    private final String externalizableCode;
    private final SimpleKeyedTextValues substitutionNamedValues;

    private E13nData( String externalizableCode, Map<String, String> substitutionNamedValues ) {
        this.externalizableCode = externalizableCode;
        this.substitutionNamedValues = new SimpleKeyedTextValues( substitutionNamedValues );
    }

    @Override
    public String getExternalizableCode() {
        return externalizableCode;
    }

    public KeyedTextValues getSubstitutionData() {
        return substitutionNamedValues;
    }
}
