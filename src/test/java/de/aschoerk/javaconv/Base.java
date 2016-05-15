package de.aschoerk.javaconv;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by aschoerk on 15.05.16.
 */
@RunWith(JUnit4.class)
public class Base {

    protected String call(String s) {
        return JavaConverter.convert2Rust(s);
    }
}
