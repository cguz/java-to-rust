package de.aschoerk.javaconv;

import org.junit.Test;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

/**
 * Created by aschoerk on 15.05.16.
 */
public class ElseTest extends Base {
    @Test
    public void canConvertNewToStaticCallNew() {
        new Integer(10);
        assertThat(call("new Class()"), containsString("Class::new()"));
        assertThat(call("new Class(i)"), containsString("Class::new(i)"));
    }

}
