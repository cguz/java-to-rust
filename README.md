# java-converter

This crude web-application can be a small help or not so small help when trying to port Java-Code to Rust.
The safety features in Rust make it feasible, even if there is no GC in Rust. So if trying to provide libraries for Rust, why
not look into the great open source code provided for java.

The author is a beginner in rust, so the generated code will sometimes be kind of "unrusty".
At the moment this server is used in the porting of the apache math3 maven artefact.

## How to use it.

There are two ways:
<a href=https://jrconverter.appspot.com/index.jsp>link</a> leads to a small vm running this war.

or use maven to build a snapshot and deploy the war-file into a J2EE-Container.

The default Web-Side shows two Textfields
The first can be edited and the java-text can be pasted there. After pressing the button
the converted code appears in the second Textfield.

The java-code can be a class, a part of a class or a simple statement.
The code must be (java-)syntactically correct. The result quite certainly will not
 be (rust-)syntactically correct ;-)
``

## functions

The server mainly tries to support in dumb formatting changes which are always the same.

* What has been implemented and might be of use:

    * conversion of **declarations** Java: _"Type name = init"_ to _"let name: Type = init"_
    * **conversion of arrays** type[] to vectors
    * **snake-case** for camelcase-identifiers starting with lower case
    * mapping of **primitive** types
    * **&amp;self** as first parameter in non static methods
    * **new** type becomes type::new
    * **class becomes struct** with its instance-variables
    * class-methods can be found in extra block **impl for** <class-name> { }
    * decide about usage of **mut**
    * conversion of **integer-constants** to float-constants where necessary
    * conversion of **Exceptions** into Results
    * **static methods** are called using ::
    * **@Test** is converted to #[test]
    * **interfaces** become traits
    * Java methods with declared **throws** return Result&lt;_,Rc&lt;Exception&gt;&gt; used
      rust code can be found in directory rust.

* experimental
    * conversion of **throw** to break loop with label

* very experimental certainly wrongly done:
    * **super-classes** become instance-variables

* what does not change
    * javadoc-comments

# License

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
