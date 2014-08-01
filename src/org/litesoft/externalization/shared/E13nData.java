package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;
import org.litesoft.commonfoundation.typeutils.*;

import java.util.*;

/**
 * Externalization Data to be used with an Externally Sourced String keyed
 * template system that supports substitutions within any specific template by
 * "named" values.
 */
public class E13nData {

    /**
     * Substitution Value w/ a flag indicating if it is User Data, or another
     * Template Id Code to be used as a Sub-Template.
     */
    public static class SubstitutionValue {
        private final boolean mUserData;
        private final String mValue;

        public SubstitutionValue( boolean userData, String value ) {
            this.mUserData = userData;
            this.mValue = value;
        }

        /**
         * Flag indicating if the "value" is User Data, or another Template Id
         * Code to be used as a Sub-Template.
         */
        public boolean isUserData() {
            return mUserData;
        }

        /**
         * User Data, or another Template Id Code to be used as a Sub-Template.
         */
        public String getValue() {
            return mValue;
        }

        @Override
        public String toString() {
            return mUserData ? mValue : "{" + mValue + "}";
        }
    }

    private final String mTemplateIdCode;
    private Map<String, SubstitutionValue> mSubstitutionNamedValues;

    /**
     * Convert the templateIdCode (parameter) into an acceptable string form for
     * the templateIdCode.
     *
     * @param pTemplateIdCode Template Identifying Code
     */
    private E13nData( Object pTemplateIdCode ) {
        this.mTemplateIdCode = Confirm.significantOfToStringOf( "templateIdCode", pTemplateIdCode );
    }

    public static E13nData builder( String externalizableCode ) {
        return new E13nData( externalizableCode );
    }

    public static E13nData builder( Enum<?> pEnum ) {
        return new E13nData( pEnum );
    }

    public E13nData build() {
        return this;
    }

    /**
     * @return Not null map of Template Substitution Named Values
     */
    public synchronized Map<String, SubstitutionValue> getSubstitutionNamedValues() {
        if ( mSubstitutionNamedValues == null ) {
            return Collections.emptyMap();
        }
        return Maps.newHashMap( mSubstitutionNamedValues ); // defensive copy for Thread Safety
    }

    private synchronized E13nData addPair( boolean pUserData, String pName, String pValue ) {
        pName = Confirm.significant( "name", pName );
        if ( mSubstitutionNamedValues == null ) {
            mSubstitutionNamedValues = Maps.newHashMap();
        }
        mSubstitutionNamedValues.put( pName, new SubstitutionValue( pUserData, pValue ) );
        return this;
    }

    /**
     * Add a User Substitution Named Value.
     *
     * @param pName     Not allowed to be empty
     * @param pUserData null converted to ""
     */
    public E13nData addSubstitutionNamedUserData( String pName, String pUserData ) {
        return addPair( true, pName, ConstrainTo.notNull( pUserData ) );
    }

    /**
     * Add a User Substitution Named Value.
     *
     * @param pName              Not allowed to be empty
     * @param pSubTemplateIdCode Not allowed to be empty
     */
    public E13nData addSubstitutionNamedExternalizableCode( String pName, String pSubTemplateIdCode ) {
        return addPair( false, pName, Confirm.significant( "subTemplateIdCode", pSubTemplateIdCode ) );
    }

    /**
     * @return Not Empty TemplateIdCode
     */
    public String getTemplateIdCode() {
        return mTemplateIdCode;
    }

    @Override
    public String toString() {
        return getTemplateIdCode() + getSubstitutionNamedValues();
    }
}
