package de.aschoerk.java2rust.utils;

import org.junit.Test;

import static de.aschoerk.java2rust.utils.NamingHelper.camelToSnakeCase;
import static org.junit.Assert.assertEquals;

public class NamingHelperTests {

    @Test
    public void testCamelToSnake() {
        assertEq("hello_world", "HelloWorld");
        assertEq("hello_world", "helloWorld");
        assertEq("my_awesome_stub_resolver_facade_ejb", "MyAwesomeStubResolverFacadeEjb");
        assertEq("a_very_long_variable_name", "aVeryLongVariableName");
        assertEq("", "");
        assertEq("nothing", "nothing");
        assertEq("snake", "Snake");
    }

    private void assertEq(String expected, String actual) {
        assertEquals(expected, camelToSnakeCase(actual));
    }
}
