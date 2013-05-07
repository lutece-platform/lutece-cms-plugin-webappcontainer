/*
 * Copyright (c) 2002-2013, Mairie de Marseille
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Marseille' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.webappcontainer.web;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import fr.paris.lutece.plugins.webappcontainer.business.Site;
import fr.paris.lutece.plugins.webappcontainer.business.SiteHome;
import fr.paris.lutece.plugins.webappcontainer.business.WebappResponse;
import fr.paris.lutece.plugins.webappcontainer.service.WebappcontainerPlugin;
import fr.paris.lutece.plugins.webappcontainer.util.HtmlDocumentWebappcontainer;
import fr.paris.lutece.plugins.webappcontainer.util.HtmlDocumentWebappcontainerException;
import fr.paris.lutece.plugins.webappcontainer.util.HttpAccessException;
import fr.paris.lutece.plugins.webappcontainer.util.UrlUtils;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.message.SiteMessage;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.message.SiteMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.UserNotSignedException;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.xpages.XPage;
import fr.paris.lutece.portal.web.xpages.XPageApplication;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

/**
 * 
 */
public class WebappcontainerApp implements XPageApplication
{
    // Parameters
    public static final String PARAMETER_CODE = "site_code";
    public static final String PARAMETER_WEBAPP_URL = "webapp_url";
    public static final String PARAMETER_PAGE = "page";
    public static final String PARAMETER_PAGE_HACK = "page_external_site_";

    // Templates
    private static final String TEMPLATE_SITES = "skin/plugins/webappcontainer/list_sites.html";
    private static final String TEMPLATE_SITE_CONTENT = "skin/plugins/webappcontainer/site_content.html";

    // Others
    private static final String EMPTY_STRING = "";
    private static final String SERVLET_WEBAPPCONTAINER = "webappcontainer";

    // Markers
    private static final String MARK_SITES_LIST = "sites_list";
    private static final String MARK_SITE = "site";
    private static final String MARK_WEBAPP_URL = "webapp_url";
    private static final String MARK_EXTERNAL_SITE_BODY = "external_site_body";

    // Properties
    private static final String PROPERTY_INCLUDE_CSS = "webappcontainer.rebuildHtmlPage.includeCSS";

    // I18n
    private static final String PROPERTY_PAGE_TITLE_SITES = "webappcontainer.list_sites.pageTitle";
    private static final String PROPERTY_PAGE_PATH_LABEL = "webappcontainer.list_sites.pagePathLabel";
    private static final String MESSAGE_ERROR_SITE = "webappcontainer.message.errorGettingSite";
    private static final String MESSAGE_ERROR_CONTENT = "webappcontainer.message.errorParsingDom";

    // Logger
    private static Logger _logger;
    private static final String LOGGER_DEBUG_WEBAPPCONTAINER = "lutece.debug.webappcontainer";

    /**
     * Returns the XPage content depending on the request parameters and the current mode.
     * 
     * @param request The HTTP request.
     * @param nMode The current mode.
     * @param plugin The plugin
     * @return The page content.
     * @throws UserNotSignedException If the application ask for an user sign
     * @throws SiteMessageException occurs when a site message need to be displayed
     */
    public XPage getPage( HttpServletRequest request, int nMode, Plugin plugin ) throws UserNotSignedException,
            SiteMessageException
    {
        String strCode = request.getParameter( PARAMETER_CODE );

        if ( _logger == null )
        {
            _logger = Logger.getLogger( LOGGER_DEBUG_WEBAPPCONTAINER );
        }

        if ( (strCode == null) || strCode.equals( EMPTY_STRING ) )
        {
            return getListSites( request, nMode, plugin );
        }
        else
        {
            return getWebappContainerFromSiteCode( strCode, request, nMode, plugin );
        }
    }

    /**
     * Get the {@link Site} object in Session or create it if not present
     * 
     * @param request The {@link HttpServletRequest}
     * @return The {@link Site} object
     */
    public static Site getSite( HttpServletRequest request )
    {
        Site site = null;
        String strCode = request.getParameter( PARAMETER_CODE );

        if ( strCode != null )
        {
            site = SiteHome.findByPrimaryKey( strCode, PluginService.getPlugin( WebappcontainerPlugin.PLUGIN_NAME ) );
        }

        return site;
    }

    /**
     * Get the requested url in Session or create it if not present
     * 
     * @param request The {@link HttpServletRequest}
     * @return The requested url
     */
    public static String getRequestUrl( HttpServletRequest request )
    {
        String strUrl = request.getParameter( PARAMETER_WEBAPP_URL );

        return UrlUtils.decodeUrl( strUrl );
    }

    /**
     * Get the list of external sites
     * 
     * @param request The {@link HttpServletRequest}
     * @param nMode The mode
     * @param plugin The {@link Plugin}
     * @return The Xpage with the list of accessible externals sites
     * @throws UserNotSignedException If used is not signed
     * @throws SiteMessageException Display a Site message
     */
    private XPage getListSites( HttpServletRequest request, int nMode, Plugin plugin ) throws UserNotSignedException,
            SiteMessageException
    {
        XPage page = new XPage( );
        page.setTitle( I18nService.getLocalizedString( PROPERTY_PAGE_TITLE_SITES, request.getLocale( ) ) );
        page.setPathLabel( I18nService.getLocalizedString( PROPERTY_PAGE_PATH_LABEL, request.getLocale( ) ) );

        Collection<Site> listSite = SiteHome.findAll( plugin );
        Map<String, Object> model = new HashMap<String, Object>( );
        model.put( MARK_SITES_LIST, listSite );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_SITES, request.getLocale( ), model );
        page.setContent( templateList.getHtml( ) );

        return page;
    }

    /**
     * Get the {@link Site} Content
     * 
     * @param strCode The {@link Site} code
     * @param request The {@link HttpServletRequest}
     * @param nMode The mode
     * @param plugin The {@link Plugin}
     * @return The Xpage containing the {@link Site} content
     * @throws UserNotSignedException If used is not signed
     * @throws SiteMessageException Display a Site message
     */
    private XPage getWebappContainerFromSiteCode( String strCode, HttpServletRequest request, int nMode, Plugin plugin )
            throws UserNotSignedException, SiteMessageException
    {
        XPage page = new XPage( );
        Map<String, Object> model = new HashMap<String, Object>( );
        StringBuffer strWebappTextContent = null;
        WebappResponse webappResponse = null;
        String strRequestedUrl = WebappcontainerApp.getRequestUrl( request );
        Site site = WebappcontainerApp.getSite( request );

        if ( (strRequestedUrl == null) || strRequestedUrl.equals( EMPTY_STRING ) )
        {
            strRequestedUrl = site.getUrl( );
        }

        // Get the webapp content with HTTPAccess
        try
        {
            webappResponse = WebappcontainerResourceServlet.getWebappResponse( request, strRequestedUrl, site );
        }
        catch ( HttpAccessException e )
        {
            SiteMessageService.setMessage( request, MESSAGE_ERROR_SITE, new String[] { e.getMessage( ) },
                    SiteMessage.TYPE_ERROR );
            AppLogService.error( "Error when retrieving external site content (site : " + site.getCode( ) + ", url : "
                    + strRequestedUrl + ")", e );
        }

        // Proces the webapp content modifications
        if ( webappResponse != null )
        {
            strRequestedUrl = webappResponse.getLocation( );

            strWebappTextContent = getWebappBody( request, webappResponse.getContent( ), webappResponse
                    .getContentCharset( ), strRequestedUrl, site );
        }

        // Set the template model, set the XPage
        page.setTitle( site.getDescription( ) );
        page.setPathLabel( site.getCode( ) );
        // _logger.debug( strWebappTextContent );
        model.put( MARK_EXTERNAL_SITE_BODY, strWebappTextContent );
        model.put( MARK_SITE, site );
        model.put( MARK_WEBAPP_URL, strRequestedUrl );

        HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_SITE_CONTENT, request.getLocale( ), model );
        page.setContent( templateList.getHtml( ) );

        return page;
    }

    /**
     * Process the modifications of webapp HTML code (rewrite Url, get only BODY and HEAD tags)
     * 
     * @param request The {@link HttpServletRequest}
     * @param bytesWebappTextContent The source webapp text content
     * @param strEncoding The encoding used to get the webapp body
     * @param strRequestedUrl The requested URL
     * @param site The site
     * @return a {@link StringBuffer} with the converted HTML code
     * @throws UserNotSignedException If used is not signed
     * @throws SiteMessageException Display a Site message
     */
    private static StringBuffer getWebappBody( HttpServletRequest request, byte[] bytesWebappTextContent,
            String strEncoding, String strRequestedUrl, Site site ) throws UserNotSignedException, SiteMessageException
    {
        HtmlDocumentWebappcontainer doc = null;

        try
        {
            doc = new HtmlDocumentWebappcontainer( bytesWebappTextContent, strEncoding );
        }
        catch ( HtmlDocumentWebappcontainerException e )
        {
            AppLogService.error( "Error when parsing DOM HTML (site : " + site.getCode( ) + ", url : "
                    + strRequestedUrl + ")", e );
            SiteMessageService.setMessage( request, MESSAGE_ERROR_SITE, new String[] { I18nService.getLocalizedString(
                    MESSAGE_ERROR_CONTENT, request.getLocale( ) ) }, SiteMessage.TYPE_ERROR );
        }

        // Get the base Url
        String strBaseUrl = getBaseUrl( doc );
        String strBaseUrlWebapp = strRequestedUrl;

        if ( (strBaseUrl != null) && !strBaseUrl.equals( EMPTY_STRING ) )
        {
            strBaseUrlWebapp = strBaseUrl;

            removeBaseUrl( doc );
        }

        rewriteUrls( doc, getXpageUrl( site.getCode( ) ), getServletUrl( site.getCode( ) ), strBaseUrlWebapp, site );

        StringBuffer sbHTMLContent;

        if ( site.isRebuildHtmlPage( ) )
        {
            sbHTMLContent = getHead( doc );
            sbHTMLContent.append( getBody( doc ) );
        }
        else
        {
            sbHTMLContent = doc.getContent( );
        }

        return sbHTMLContent;
    }

    /**
     * Get the webappcontainer XPage url
     * 
     * @param strCode The site code
     * @return The XPage Url
     */
    private static String getXpageUrl( String strCode )
    {
        UrlItem url = null;

        url = new UrlItem( AppPathService.getPortalUrl( ) );
        url.addParameter( "page", "webappcontainer" );

        url.addParameter( PARAMETER_CODE, strCode );
        url.addParameter( PARAMETER_WEBAPP_URL, EMPTY_STRING );

        return url.getUrl( );
    }

    /**
     * Get the webappcontainer servlet
     * 
     * @param strCode The site code
     * @return The servlet Url
     */
    private static String getServletUrl( String strCode )
    {
        UrlItem prefixUrl = new UrlItem( SERVLET_WEBAPPCONTAINER );
        prefixUrl.addParameter( PARAMETER_CODE, strCode );
        prefixUrl.addParameter( PARAMETER_WEBAPP_URL, EMPTY_STRING );

        return prefixUrl.getUrl( );
    }

    /**
     * Rewrite the webapp urls : Replace all url by :
     * <ul>
     * <li>for img, css, javascript, rss tags : a link to webappcontainer servlet (if option enabled)</li>
     * <li>for a, form tags : a link to webappcontainer XPage</li>
     * </ul>
     * The old url will be set :
     * <ul>
     * <li>for simple links : in GET parameter (in url)
     * <li>for forms : as input type "hidden"
     * </ul>
     * 
     * @param doc HtmlDocumentWebappcontainer document
     * @param strXpageUrl The XPage url
     * @param strServletUrl The webappcontainer servlet Url
     * @param strBaseUrlWebapp The base url of webapp
     * @param site The {@link Site} object
     */
    private static void rewriteUrls( HtmlDocumentWebappcontainer doc, String strXpageUrl, String strServletUrl,
            String strBaseUrlWebapp, Site site )
    {
        boolean bEncodeUrlFormNonHtmlContent = true;
        String strBaseUrlWebappForHtmlContent = strServletUrl;

        if ( !site.isRedirectNonHtmlContent( ) )
        {
            // If no redirection, the non HTML content urls must not be encoded. They just have to be convert to absolute urls.
            bEncodeUrlFormNonHtmlContent = false;
            strBaseUrlWebappForHtmlContent = EMPTY_STRING;
        }

        doc.convertUrls( HtmlDocumentWebappcontainer.ELEMENT_IMG, strBaseUrlWebapp, site,
                strBaseUrlWebappForHtmlContent, bEncodeUrlFormNonHtmlContent );
        doc.convertUrls( HtmlDocumentWebappcontainer.ELEMENT_CSS_LINK, strBaseUrlWebapp, site,
                strBaseUrlWebappForHtmlContent, bEncodeUrlFormNonHtmlContent );
        doc.convertUrls( HtmlDocumentWebappcontainer.ELEMENT_ALTERNATE, strBaseUrlWebapp, site,
                strBaseUrlWebappForHtmlContent, bEncodeUrlFormNonHtmlContent );
        doc.convertUrls( HtmlDocumentWebappcontainer.ELEMENT_JAVASCRIPT, strBaseUrlWebapp, site,
                strBaseUrlWebappForHtmlContent, bEncodeUrlFormNonHtmlContent );

        doc.convertUrls( HtmlDocumentWebappcontainer.ELEMENT_A, strBaseUrlWebapp, site, strXpageUrl, true );
        doc.convertUrls( HtmlDocumentWebappcontainer.ELEMENT_FORM, strBaseUrlWebapp, site, strXpageUrl, true );
    }

    /**
     * Get the content of "head" HTML tag
     * 
     * @param doc The HtmlDocumentWebappcontainer document
     * @return a {@link StringBuffer} with the head content
     */
    private static StringBuffer getHead( HtmlDocumentWebappcontainer doc )
    {
        StringBuffer sbHeadContent = new StringBuffer( );

        // Get scripts
        sbHeadContent.append( doc.getElements( HtmlDocumentWebappcontainer.ELEMENT_JAVASCRIPT ) );

        boolean bIncludeCSS = Boolean.parseBoolean( AppPropertiesService.getProperty( PROPERTY_INCLUDE_CSS, Boolean
                .toString( false ) ) );

        if ( bIncludeCSS )
        {
            // Get styles
            sbHeadContent.append( doc.getElements( HtmlDocumentWebappcontainer.ELEMENT_CSS_LINK ) );
            sbHeadContent.append( doc.getElements( HtmlDocumentWebappcontainer.ELEMENT_CSS_STYLE ) );
        }

        return sbHeadContent;
    }

    /**
     * Get the content of "body" HTML tag
     * 
     * @param doc The HtmlDocumentWebappcontainer document
     * @return a {@link StringBuffer} with the body content
     */
    private static StringBuffer getBody( HtmlDocumentWebappcontainer doc )
    {
        return doc.getFirstElement( HtmlDocumentWebappcontainer.ELEMENT_BODY );
    }

    /**
     * Get the content of "href" attribute of "base" HTML tag
     * 
     * @param doc The HtmlDocumentWebappcontainer document
     * @return the site base url or null
     */
    private static String getBaseUrl( HtmlDocumentWebappcontainer doc )
    {
        return doc.getFirstElementAttribute( HtmlDocumentWebappcontainer.ELEMENT_BASE );
    }

    /**
     * Remove the "base" tag
     * 
     * @param doc The HtmlDocumentWebappcontainer document
     */
    private static void removeBaseUrl( HtmlDocumentWebappcontainer doc )
    {
        doc.removeFirstElement( HtmlDocumentWebappcontainer.ELEMENT_BASE );
    }
}
