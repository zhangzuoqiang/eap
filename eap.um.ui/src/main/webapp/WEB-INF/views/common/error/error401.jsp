<%@page contentType="text/html; charset=utf-8" pageEncoding="utf-8" isErrorPage="true" %>
<%
	response.setStatus(401);
%>
<!DOCTYPE html>
<html>
<head>
	<title>未授权的</title>
</head>
<body>
	未授权的
	<br />
	<%
	if (exception != null) {
		out.write(exception.getMessage());
	} else {
		
	}
	%>
</body>
</html>