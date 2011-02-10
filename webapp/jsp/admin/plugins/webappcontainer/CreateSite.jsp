<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:include page="../../AdminHeader.jsp" />

<jsp:useBean id="webappcontainerJspBean" scope="session" class="fr.paris.lutece.plugins.webappcontainer.web.WebappcontainerJspBean" />

<% webappcontainerJspBean.init( request, webappcontainerJspBean.RIGHT_MANAGE_WEBAPPCONTAINER ); %>
<%= webappcontainerJspBean.getCreateSite( request ) %>

<%@ include file="../../AdminFooter.jsp" %>