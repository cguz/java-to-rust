# Java to Rust Converter

This is a shell version of the [web-application](https://github.com/aschoerk/converter-page). From now, all the credits for the original author of the web-application.

The application is a small help when trying to port Java-Code to Rust.

The author is a beginner in rust, so the generated code will sometimes be kind of "unrusty".

## How to use it.

$ java -jar java-to-rust.jar [path_file.java | path_directory]

``

## Implemented Functions

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
