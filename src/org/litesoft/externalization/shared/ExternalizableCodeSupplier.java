package org.litesoft.externalization.shared;

/**
 * Implementers of this interface are committing to provide support for an "Externalizable Code".
 * <p/>
 * Specifically they must implement ExternalizableByCode OR, implement a toString() method that
 * will return a string that will be the "Externalizable Code"!
 * <p/>
 * This interface is primarily used to limit the scope so as not to include CustomizedExternalizable!
 */
public interface ExternalizableCodeSupplier extends Externalizable {
}
