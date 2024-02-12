# Java 2 Rust

[![Maintenance](https://img.shields.io/badge/Maintained%3F-yes-green.svg)](https://gitHub.com/cguz/)
[![Eclipse](https://img.shields.io/badge/-Eclipse-blueviolet)](https://eclipse.org/)
[![Java](https://img.shields.io/badge/Java-ED8B00?&logo=java&logoColor=white)](https://java.org/)

Author: Cesar Augusto Guzman Alvarez [@cguz](https://github.com/cguz/)

Co-Author: Jonas Cir [@JonasCir](https://github.com/JonasCir)

## Description

This is a command line tool based on the version [web-application](https://github.com/aschoerk/converter-page). Thus,
all the credits for the original author of the web-application.

The application is a small help when trying to port Java-Code to Rust.

The author is a beginner in rust, so the generated code will sometimes be kind of "unrusty".

## How to build it.

Run `mvn package` and find your `java2rust.jar` in the project's `target` folder.

## How to use it.

$ java -jar java2rust.jar -d [path_file.java | path_directory]

The converted files will be saved in the folder: "output"

Other options are:
 - -o: Specify the output directory path (default: output)
 - -i: Ignore existing files in the output directory (default: false)
 - -v: Specify the verbosity level (default: 2)
 - -cp: Copy other non-java files to the output directory (default: false)

## Implemented funcionality

- might be of use:

    - conversion of declarations Java: "Type name = init" to "let name: Type = init"
    - conversion of arrays type[] to vectors
    - snake-case for camelcase-identifiers starting with lower case
    - mapping of primitive types
    - &self as first parameter in non static methods
    - new type becomes type::new
    - class becomes struct with its instance-variables
    - class-methods can be found in extra block impl for { }
    - decide about usage of mut
    - conversion of integer-constants to float-constants where necessary
    - conversion of Exceptions into Results
    - static methods are called using ::
    - @Test is converted to #[test]
    - interfaces become traits
    - Java methods with declared throws return Result<_,Rc<Exception>> used rust code can be found in directory rust.

- experimental

    - conversion of throw to break loop with label
    - mvn-assembly-plugin
    - convert camel-cased file names to snake-cased names 

- very experimental certainly wrongly done:

    - super-classes become instance-variables

- what does not change

    - javadoc-comments
