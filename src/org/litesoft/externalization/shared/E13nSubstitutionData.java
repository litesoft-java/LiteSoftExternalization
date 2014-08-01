package org.litesoft.externalization.shared;

public interface E13nSubstitutionData {
    /**
     * Get the Template value associated with the key
     *
     * @param pKey Leading and trailing spaces are ignored
     *
     * @return null or the Template value associated with the key
     */
    String get( String pKey );
}
