package de.aschoerk.java2rust;

/**
 * @author aschoerk
 */
public class TypeDescription {
    Class clazz;
    int arrayCount;

    public TypeDescription(final int arrayCount, final Class clazz) {
        this.arrayCount = arrayCount;
        this.clazz = clazz;
    }

    public int getArrayCount() {
        return arrayCount;
    }

    public Class getClazz() {
        return clazz;
    }
}
