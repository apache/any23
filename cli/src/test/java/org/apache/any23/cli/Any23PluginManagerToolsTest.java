package org.apache.any23.cli;

import org.apache.any23.plugin.Any23PluginManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;

import static org.junit.Assert.assertTrue;

public class Any23PluginManagerToolsTest {

    private Any23PluginManager manager;

    @Before
    public void before() {
        manager = Any23PluginManager.getInstance();
    }

    @After
    public void after() {
        manager = null;
    }

    @Test
    public void testGetTools() throws IOException {
        Iterator<Tool> tools = manager.getTools();
        assertTrue(tools.hasNext()); // NOTE: Punctual tool detection verification done by ToolRunnerTest.java
    }
}
