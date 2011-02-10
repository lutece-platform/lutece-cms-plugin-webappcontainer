<%@page import="fr.paris.lutece.portal.service.util.AppPathService"%>
<%@page import="fr.paris.lutece.portal.service.message.SiteMessageException"%>
<jsp:useBean id="webappcontainerApp" scope="page" class="fr.paris.lutece.plugins.webappcontainer.web.WebappcontainerApp" />

<%
	/* This method is used to catch the front messages */
    try
	{
		String strContent = webappcontainerApp.getWebappContent( request );
		out.print( strContent );
		out.flush(  );
	}
	catch( SiteMessageException lme )
	{
		response.sendRedirect( AppPathService.getBaseUrl( request ) );
	}
%>
