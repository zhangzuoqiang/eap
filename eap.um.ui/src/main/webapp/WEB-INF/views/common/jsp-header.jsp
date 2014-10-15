<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" trimDirectiveWhitespaces="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="un" uri="http://jakarta.apache.org/taglibs/unstandard-1.0"%>
<%@taglib prefix="decorator" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<%@taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page" %>
<%@taglib prefix="e" uri="eap/web/tags" %>
<%@page import="eap.Env" %>
<%@page import="eap.EapContext" %>
<%@page import="eap.WebEnv" %>
<%@page import="eap.um.ui.common.P"%>
<%WebEnv env = (WebEnv) EapContext.getEnv(); %>
<c:set var="env" value="<%=env %>" scope="request" />
<un:useConstants var="Env" className="eap.WebEnv" />
<un:useConstants var="P" className="eap.um.ui.common.P" />