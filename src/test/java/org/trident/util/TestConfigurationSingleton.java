package org.trident.util;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;

public class TestConfigurationSingleton {

    private String PATH_TEST = "/home/vincent/trident_test";

    @Test
    public void testOne(){
        String prop = ConfiguratorSingleton.getInstance().getPropriety(Constant.THEME);
        TestCase.assertEquals("MATERIAL_BACK", prop);
    }

    @Test
    public void testTwoCreateDirectory(){
        String homeDirectory = PATH_TEST;
        File file = new File(homeDirectory);
        TestCase.assertFalse(file.exists());
        ConfiguratorSingleton.getInstance().createDirectory(homeDirectory);
        TestCase.assertTrue(file.exists());
        TestCase.assertTrue(file.delete());
    }
}
