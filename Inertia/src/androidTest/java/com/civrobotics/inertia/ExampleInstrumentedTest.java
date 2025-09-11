package com.civrobotics.inertia;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.ftcciv.inertia.test", appContext.getPackageName());

        // Actual test
        @Savable(elementName = "test")
        int test = 42; // The universe

        Registry registry = new Registry("registry.json", this.getClass());
        registry.saveSavables();

        try {
            assertEquals(42, registry.getSaved("test"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}