<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="de.aschoerk.javaconv.JavaConverter" %>
<html>
<body>
<h4>Java/Rust Converter</h4>
The Conversion tries to get rid of some menial editing work, necessary when converting a java program to rust.
Input Java Code, Best are complete Class-Files, Single Methods and Statements should work as well.
<form action="index.jsp" method="POST" >
    <table width="75%">
        <thead>
        <td>The Java Code <input type="submit" name="Submit" value="Convert" /></td>
        <td>The Conversion Result</td>
        </thead>
        <tr>
            <td width="50%">
                <textarea rows="50" cols="100" name="textarea" >
                <% if (!StringUtils.isEmpty(request.getParameter("textarea"))) { %>
                   <%= request.getParameter("textarea") %>
                <% } %>
                </textarea>
            </td>
            <td width="50%">
                <textarea rows="50" cols="100" name="textarea" >
                <% if (!StringUtils.isEmpty(request.getParameter("textarea"))) { %>
                   <%= JavaConverter.convert2Rust(request.getParameter("textarea")) %>
                <% } %>
                </textarea>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
