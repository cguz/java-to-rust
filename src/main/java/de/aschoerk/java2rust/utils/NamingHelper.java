package de.aschoerk.java2rust.utils;

import java.util.stream.IntStream;

public class NamingHelper {
    /**
     * Convert camel-case string to snake-string representation
     *
     * @param input The camel case string to be converted to snake case
     * @return The snake case string representation of the given string
     */
    public static String camelToSnakeCase(String input) {
        // early return
        if (input.isEmpty()) {
            return input;
        }

        StringBuilder snake = new StringBuilder();

        // lowercase first character
        snake.append(Character.toLowerCase(input.charAt(0)));

        // create snake case string
        IntStream.range(1, input.length()).forEach(i -> {
            char c = input.charAt(i);
            if (Character.isUpperCase(c)) {
                snake.append("_");
            }
            snake.append(Character.toLowerCase(c));
        });
        return snake.toString();
    }
}
