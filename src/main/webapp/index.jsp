<%@ page import="org.apache.commons.lang3.StringUtils" %>
<%@ page import="de.aschoerk.javaconv.JavaConverter" %>
<%@ page import="de.aschoerk.doxiaconv.DoxiaConverter" %>
<html>
<body>
<h4>TWiki/Confluence Converter</h4>
The Conversion tries to get rid of some menial editing work, necessary when converting a twiki pages to confluence.
Input TWiki-Code.
<form action="index.jsp" method="POST" >
    <table width="75%">
        <thead>
        <td>The Java Code <input type="submit" name="Submit" value="Convert" /></td>
        <td>The Conversion Result</td>
        </thead>
        <tr>
            <td width="50%"><textarea rows="50" cols="100" name="textarea" ><%= request.getParameter("textarea") == null ? "" : request.getParameter("textarea") %></textarea></td>
            <td width="50%">
                <textarea rows="50" cols="100" name="textarea" ><%= new DoxiaConverter().convert(request.getParameter("textarea"),"twiki","confluence") %></textarea>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
