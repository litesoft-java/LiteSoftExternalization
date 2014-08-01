package org.litesoft.externalization.shared;

/**
 * When this type is resolved, if it does NOT implement ExternalizableByCode, the
 * toString() "code" is optionally prefixed with the enum's simple class name.
 */
public interface ExternalizableEnum extends ExternalizableCodeSupplier {
    String name();
}
