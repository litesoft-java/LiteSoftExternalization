package org.litesoft.externalization.shared;

/**
 * Implementers of this interface are committing to provide support for Externalization.
 * <p/>
 * Specifically they must either implement one of this interfaces sub-interfaces:
 * <li>CustomizedExternalizable</li>
 * <li>ExternalizableCodeSupplier</li>
 * OR, implement a toString() method that will return a string that will be the "Externalizable Code"!
 */
public interface Externalizable {
}
