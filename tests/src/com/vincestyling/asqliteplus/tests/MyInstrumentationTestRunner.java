package com.vincestyling.asqliteplus.tests;

import android.os.Bundle;
import android.test.InstrumentationTestRunner;

public class MyInstrumentationTestRunner extends InstrumentationTestRunner {
    @Override
    public void onCreate(Bundle arguments) {
        super.onCreate(arguments);

        MyDBOverseer.init(getTargetContext());
        MyDBOverseer.get().setIsDebug(true);
    }
}
