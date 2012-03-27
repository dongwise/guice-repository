/****************************************************************************\
 __FILE..........: FileConfigurationTest.java
 __AUTHOR........: Alexey Krylov
 __COPYRIGHT.....: Copyright (c) 2012 i-free
 _________________All rights reserved.
 __VERSION.......: 1.0
 __DESCRIPTION...:
 __HISTORY.......: DATE       COMMENT
 _____________________________________________________________________________
 ________________:28.02.12 Alexey Krylov AKA LexX: created.
 ****************************************************************************/


package ru.befree;

import junit.framework.Assert;
import org.junit.Test;
import ru.befree.common.service.configuration.ConfigurationService;
import ru.befree.common.service.configuration.PropertiesConfigurationService;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PropertiesConfigurationServiceTest {

    /*===========================================[ CLASS METHODS ]==============*/

    @SuppressWarnings({"MessageMissingOnJUnitAssertion"})
    @Test
    public void testFileConfiguration() throws Exception {
        ConfigurationService cs = new PropertiesConfigurationService();

        final Map<String, Object> changes = new HashMap<String, Object>();
        cs.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println(String.format("name:  [%s] old: [%s] new: [%s]", evt.getPropertyName(), evt.getOldValue(), evt.getNewValue()));
                changes.put(evt.getPropertyName(), evt.getNewValue());
            }
        });

        cs.start();

        Assert.assertEquals("1", cs.getString("one"));
        Assert.assertEquals("2", cs.getString("two"));
        Assert.assertEquals("3", cs.getString("three"));

        cs.setProperty("one", "one");
        Assert.assertEquals("one", changes.get("one"));

        cs.clearProperty("two");
        Assert.assertEquals(null, changes.get("two"));

        cs.setProperty("four", "4");
        Assert.assertEquals("4", changes.get("four"));

        cs.setProperty("five", "5");
        Assert.assertEquals("5", changes.get("five"));

        TimeUnit.SECONDS.sleep(5);
        cs.stop();

/*
        TimeUnit.SECONDS.sleep(10);

        System.out.println("111");
        TimeUnit.SECONDS.sleep(10);
        System.out.println("222");
                cs.stop();
*/

    }
}
