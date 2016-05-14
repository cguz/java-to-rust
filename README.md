# java-converter

This becomes a small helper in the conversion of Java-Code to Rust-Code.
Why that, I am trying to look into the porting of java-code to Rust.
Something that would nobody think to be possible since there is no GC in Rust.
 So let's see, how far the possibilities of the compiler (mainly the borrow-checker)
 help there.
It tries to support in dumb formatting changes which are always the same.

* What has been implemented yet and might be of use:

    * conversion of declarations Java: Type name = init to name: Type = init
    * conversion of arrays type[] to [type; size] = new Type[size],
   type[][] = new type[size1][size2] to [[type;size2]; size1]
    * snake-case for camelcase-identifiers starting with lower case
    * mapping of primitive types
    * &self as first parameter in non static methods
    * new type becomes type::new
    * class becomes struct with its instance-variables
    * methods will be found in extra block impl struct { }

* experimental possibly wrongly done:

    * interfaces become traits
    * super-classes become instance-variables

* next developments:
    * mut in declarations for changed variables
    * prepend "self." when instance-variables and methods in the same class
    are called



How to use it.

Deploy the war-file into a J2EE-Container. The default Web-Side shows two Textfields
The first can be edited and the java-text can be pasted there. After pressing the button
the converted code appears in the second Textfield.

The java-code can be a class, a part of a class or a simple statement.
The code must be (java-)syntactically correct. The result quite certainly will not
 be (rust-)syntactically correct ;-)
 