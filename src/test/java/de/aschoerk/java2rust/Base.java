package de.aschoerk.java2rust;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import de.aschoerk.java2rust.JavaConverter;

/**
 * Created by aschoerk on 15.05.16.
 */
@RunWith(JUnit4.class)
public class Base {

    protected String call(String s) {
        return JavaConverter.convert2Rust(s);
    }
}
