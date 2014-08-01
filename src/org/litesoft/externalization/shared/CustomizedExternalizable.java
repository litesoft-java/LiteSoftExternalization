package org.litesoft.externalization.shared;

public interface CustomizedExternalizable extends Externalizable {
    String getExternalizedText( E13nResolver pE13nResolver );
}
