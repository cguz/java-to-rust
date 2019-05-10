package de.aschoerk.javaconv;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author aschoerk
 */
@RunWith(JUnit4.class)
public class StackoverflowTest extends Base {

    @Test
    public void test1() {
        String result = call("class A { "
                             + "   public String toString() { "
                             + "     if (!signalDetected()) { "
                             + "       return \"IR Seeker: --% signal at ---.- degrees\"; "
                             + "     }"
                             + "     return String.format(\"IR Seeker: %3.0f%% signal at %6.1f degrees\", getStrength() * 100.0d, getAngle()); "
                             + "   }"
                             + "   public static void throwIfModernRoboticsI2cAddressIsInvalid(int newAddress) { "
                             + "     if ((newAddress < MIN_NEW_I2C_ADDRESS) ||"
                             + "         (newAddress > MAX_NEW_I2C_ADDRESS)) { "
                             + "       throw new IllegalArgumentException(String.format(\"New I2C address %d is invalid; "
                             + "                     valid range is: %d..%d\", newAddress, MIN_NEW_I2C_ADDRESS, MAX_NEW_I2C_ADDRESS)); "
                             + "     } "
                             + "     else if ((newAddress % 2) != 0) "
                             + "     { "
                             + "       throw new IllegalArgumentException(String.format(\"New I2C address %d is invalid; the address must be even.\", newAddress)); "
                             + "     } "
                             + "   } ; "
                             + "}");
        assert(result.contains("get_strength()"));
    }
}
