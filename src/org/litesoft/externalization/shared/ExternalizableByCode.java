package org.litesoft.externalization.shared;

import org.litesoft.commonfoundation.base.*;

public interface ExternalizableByCode extends ExternalizableCodeSupplier {
    String getExternalizableCode();

    public class Of implements ExternalizableByCode {
        private final String mCode;

        public Of( String pCode ) {
            mCode = Confirm.significant( "Code", pCode );
        }

        @Override
        public String getExternalizableCode() {
            return mCode;
        }
    }
}
