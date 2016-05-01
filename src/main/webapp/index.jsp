<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="de.aschoerk.javaconv.JavaConverter" %>
<html>
<body>
<h1>Java/Rust Converter</h1>
Input Java Pieces. A conversion will be tried.
<form action="index.jsp" method="POST" >
    <table width="75%">
        <tr>
            <td width="20%">What is your java code?</td>
            <td width="40%">
                <textarea rows="50" cols="60" name="textarea" >
                <% if (!StringUtils.isEmpty(request.getParameter("textarea"))) { %>
                   <%= request.getParameter("textarea") %>
                <% } %>
                </textarea>
            </td>
            <td width="40%">
                <textarea rows="50" cols="120" name="textarea" >
                <% if (!StringUtils.isEmpty(request.getParameter("textarea"))) { %>
                   <%= JavaConverter.convert2Rust(request.getParameter("textarea")) %>
                <% } %>
                </textarea>
            </td>
        </tr>
    </table>
    <p>
        <input type="submit" name="Submit" value="Submit name" />
        <input type="reset" name="Reset" value="Reset form" />
    </p>
</form>
</body>
</html>
