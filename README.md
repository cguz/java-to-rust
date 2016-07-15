# java-converter

This crude web-application can be a small help or not so small help when trying to port Java-Code to Rust.
The safety features in Rust make it feasible, even if there is no GC in Rust. So if trying to provide libraries for Rust, why
not look into the great open source code provided for java.

The author is a beginner in rust, so the generated code will sometimes be kind of "unrusty".
At the moment this server is used in the porting of the apache math3 maven artefact.

## functions

The server mainly tries to support in dumb formatting changes which are always the same.

* What has been implemented yet and might be of use:

    * conversion of declarations Java: Type name = init to name: Type = init
    * conversion of arrays type[] to vectors: with dimensions [type; size] = new Type[size],
   type[][] = new type[size1][size2] to [[type;size2]; size1]
    * snake-case for camelcase-identifiers starting with lower case
    * mapping of primitive types
    * &self as first parameter in non static methods
    * new type becomes type::new
    * class becomes struct with its instance-variables
    * methods will be found in extra block impl struct { }
    * decide about usage of mut
    * conversion of integer-constants to float-constants
    * conversion of Exceptions into Results

* very experimental possibly wrongly done:

    * interfaces become traits
    * super-classes become instance-variables
    * conversion of throw to break loop with label



## How to use it.

There are two ways:
<a href=http://46.182.19.221:8080/java-converter/index.jsp>link</a>

or build and deploy the war-file into a J2EE-Container. The default Web-Side shows two Textfields
The first can be edited and the java-text can be pasted there. After pressing the button
the converted code appears in the second Textfield.

The java-code can be a class, a part of a class or a simple statement.
The code must be (java-)syntactically correct. The result quite certainly will not
 be (rust-)syntactically correct ;-)
``