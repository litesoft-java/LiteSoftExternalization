package org.litesoft.externalization.shared;

public interface E13nSubstitutionData {
    /**
     * Get the Externalization value for the 'key', or null if it is not found.
     * <p/>
     *
     * @param key !empty
     *
     * @return null or the substitution String.
     */
    String get( String key );

    static final E13nSubstitutionData EMPTY = new E13nSubstitutionData() {
        @Override
        public String get( String key ) {
            return null;
        }
    };
}
