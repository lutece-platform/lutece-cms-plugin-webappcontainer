<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="webappcontainerJspBean" scope="session" class="fr.paris.lutece.plugins.webappcontainer.web.WebappcontainerJspBean" />

<%
	webappcontainerJspBean.init( request , webappcontainerJspBean.RIGHT_MANAGE_WEBAPPCONTAINER );
    response.sendRedirect( webappcontainerJspBean.doCreateSite( request ) );
%>