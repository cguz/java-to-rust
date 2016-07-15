package de.aschoerk.javaconv;

/**
 * @author aschoerk
 */
class Import {
    boolean wildcardImport;
    boolean staticImport;
    String importString;

    public Import(final String importString, final boolean staticImport, final boolean wildcardImport) {
        this.importString = importString;
        this.staticImport = staticImport;
        this.wildcardImport = wildcardImport;
    }

    public String getImportString() {
        return importString;
    }

    public boolean isStaticImport() {
        return staticImport;
    }

    public boolean isWildcardImport() {
        return wildcardImport;
    }
}
