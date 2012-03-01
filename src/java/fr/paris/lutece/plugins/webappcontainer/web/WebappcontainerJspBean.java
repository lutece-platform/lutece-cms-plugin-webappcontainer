/*
 * Copyright (c) 2002-2012, Mairie de Marseille
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

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.webappcontainer.business.Site;
import fr.paris.lutece.plugins.webappcontainer.business.SiteHome;
import fr.paris.lutece.portal.service.admin.AccessDeniedException;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupService;
import fr.paris.lutece.portal.web.admin.PluginAdminPageJspBean;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.string.StringUtil;
import fr.paris.lutece.util.url.UrlItem;

/**
 * This class provides the user interface to manage webapps (Sites) features ( manage, create, modify, remove )
 */
public class WebappcontainerJspBean extends PluginAdminPageJspBean
{
	// //////////////////////////////////////////////////////////////////////////
	// Constants

	// Right
	public static final String RIGHT_MANAGE_WEBAPPCONTAINER = "WEBAPPCONTAINER_MANAGEMENT";

	// Jsp
	private static final String JSP_URL_SITES_LIST = "ManageSites.jsp";

	private static final String JSP_URL_DO_REMOVE_SITE = "DoRemoveSite.jsp";

	private static final String JSP_URL_PREFIX = "jsp/admin/plugins/webappcontainer/";

	// properties for page titles
	private static final String PROPERTY_PAGE_TITLE_SITES = "webappcontainer.manage_sites.pageTitle";

	private static final String PROPERTY_PAGE_TITLE_CREATE = "webappcontainer.create_site.pageTitle";

	private static final String PROPERTY_PAGE_TITLE_MODIFY = "webappcontainer.modify_site.pageTitle";

	// Properties for proxy
	private static final String PROPERTY_PROXY_HOST = "webappcontainer.httpAccess.proxyHost";

	private static final String PROPERTY_PROXY_PORT = "webappcontainer.httpAccess.proxyPort";

	private static final String PROPERTY_SITE_DEFAULT_ENCODING = "webappcontainer.site.default.encoding";

	// Markers
	private static final String MARK_SITES_LIST = "sites_list";

	private static final String MARK_WORKGROUPS_LIST = "workgroup_keys_list";

	private static final String MARK_SITE = "site";

	private static final String MARK_WORKGROUP_KEY_DEFAULT_VALUE = "workgroup_key_default_value";

	private static final String MARK_PROXY_HOST = "proxy_host";

	private static final String MARK_PROXY_PORT = "proxy_port";

	private static final String MARK_WEBAPP_URL = "webapp_url";

	private static final String MARK_LOCALE = "locale";

	// templates
	private static final String TEMPLATE_SITES = "/admin/plugins/webappcontainer/manage_sites.html";

	private static final String TEMPLATE_CREATE_SITE = "/admin/plugins/webappcontainer/create_site.html";

	private static final String TEMPLATE_MODIFY_SITE = "/admin/plugins/webappcontainer/modify_site.html";

	// I18n messages
	private static final String MESSAGE_ERROR_SITE_CODE = "webappcontainer.message.errorSiteCode";

	private static final String MESSAGE_ERROR_SITE_CODE_ALREADY_EXISTS = "webappcontainer.message.errorSiteCodeAlreadyExists";

	private static final String MESSAGE_CONFIRM_REMOVE_SITE = "webappcontainer.message.confirmRemoveSite";

	// Parameters
	private static final String PARAMETER_CODE = "site_code";

	private static final String PARAMETER_OLD_CODE = "site_old_code";

	private static final String PARAMETER_URL = "site_url";

	private static final String PARAMETER_DESCRIPTION = "site_description";

	private static final String PARAMETER_ENCODING = "site_encoding";

	private static final String PARAMETER_HAT = "site_hat";

	private static final String PARAMETER_WORKGROUP_KEY = "site_workgroup_key";

	private static final String PARAMETER_REBUILD_HTML_PAGE = "site_rebuild_html_page";

	private static final String PARAMETER_REDIRECT_NON_HTML_CONTENT = "site_redirect_non_html_content";

	private static final String PARAMETER_USE_PROXY = "site_use_proxy";

	/**
	 * Returns the list of sites
	 * 
	 * @param request The Http request
	 * @return the sites list
	 */
	public String getManageSites( HttpServletRequest request )
	{
		setPageTitleProperty( PROPERTY_PAGE_TITLE_SITES );

		Collection<Site> listSites = SiteHome.findAll( getPlugin() );
		listSites = AdminWorkgroupService.getAuthorizedCollection( listSites, getUser() );

		Map<String, Object> rootModel = new HashMap<String, Object>();
		rootModel.put( MARK_SITES_LIST, listSites );

		HtmlTemplate templateList = AppTemplateService.getTemplate( TEMPLATE_SITES, getLocale(), rootModel );

		return getAdminPage( templateList.getHtml() );
	}

	/**
	 * Returns the form to create a {@link Site}
	 * 
	 * @param request The Http request
	 * @return the html code of the {@link Site} form
	 */
	public String getCreateSite( HttpServletRequest request )
	{
		setPageTitleProperty( PROPERTY_PAGE_TITLE_CREATE );

		Map<String, Object> model = new HashMap<String, Object>();
		ReferenceList workgroupsList = AdminWorkgroupService.getUserWorkgroups( getUser(), getLocale() );
		model.put( MARK_WORKGROUPS_LIST, workgroupsList );
		model.put( MARK_WORKGROUP_KEY_DEFAULT_VALUE, AdminWorkgroupService.ALL_GROUPS );
		model.put( MARK_PROXY_HOST, AppPropertiesService.getProperty( PROPERTY_PROXY_HOST ) );
		model.put( MARK_PROXY_PORT, AppPropertiesService.getProperty( PROPERTY_PROXY_PORT ) );
		model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
		model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage() );

		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CREATE_SITE, getLocale(), model );

		return getAdminPage( template.getHtml() );
	}

	/**
	 * Process the data capture form of a new contact
	 * 
	 * @param request The Http Request
	 * @return The Jsp URL of the process result
	 */
	public String doCreateSite( HttpServletRequest request )
	{
		String strCode = request.getParameter( PARAMETER_CODE );
		String strUrl = request.getParameter( PARAMETER_URL );
		String strDescription = request.getParameter( PARAMETER_DESCRIPTION );
		String strEncoding = request.getParameter( PARAMETER_ENCODING );
		String strHat = request.getParameter( PARAMETER_HAT );
		String strWorkgroupKey = request.getParameter( PARAMETER_WORKGROUP_KEY );
		String strRebuildHtmlPage = request.getParameter( PARAMETER_REBUILD_HTML_PAGE );
		String strRedirectNonHtmlContent = request.getParameter( PARAMETER_REDIRECT_NON_HTML_CONTENT );
		String strUseProxy = request.getParameter( PARAMETER_USE_PROXY );

		// Mandatory field
		if ( ( strCode == null ) || strCode.equals( "" ) || ( strUrl == null ) || strUrl.equals( "" ) || ( strDescription == null ) || strDescription.equals( "" ) || ( strWorkgroupKey == null )
				|| !StringUtil.checkCodeKey( strWorkgroupKey ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
		}

		if ( !StringUtil.checkCodeKey( strCode ) )
		{
			return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_SITE_CODE, AdminMessage.TYPE_STOP );
		}

		if ( SiteHome.findByPrimaryKey( strCode, getPlugin() ) != null )
		{
			return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_SITE_CODE_ALREADY_EXISTS, AdminMessage.TYPE_STOP );
		}

		if ( StringUtils.isBlank( strEncoding ) )
		{
			strEncoding = AppPropertiesService.getProperty( PROPERTY_SITE_DEFAULT_ENCODING );
		}

		Site site = new Site();
		site.setCode( strCode );
		site.setUrl( strUrl );
		site.setDescription( strDescription );
		site.setEncoding( strEncoding );
		site.setHat( strHat );
		site.setWorkgroup( strWorkgroupKey );

		if ( ( strRebuildHtmlPage != null ) && strRebuildHtmlPage.equals( Boolean.TRUE.toString() ) )
		{
			site.setRebuildHtmlPage( true );
		}
		else
		{
			site.setRebuildHtmlPage( false );
		}

		if ( ( strRedirectNonHtmlContent != null ) && strRedirectNonHtmlContent.equals( Boolean.TRUE.toString() ) )
		{
			site.setRedirectNonHtmlContent( true );
		}
		else
		{
			site.setRedirectNonHtmlContent( false );
		}

		if ( ( strUseProxy != null ) && strUseProxy.equals( Boolean.TRUE.toString() ) )
		{
			site.setUseProxy( true );
		}
		else
		{
			site.setUseProxy( false );
		}

		SiteHome.create( site, getPlugin() );

		// If the operation is successful, redirect towards the list of sites
		UrlItem url = new UrlItem( JSP_URL_SITES_LIST );

		return url.getUrl();
	}

	/**
	 * Returns the form to update info about a {@link Site}
	 * 
	 * @param request The Http request
	 * @return The HTML form to update info
	 */
	public String getModifySite( HttpServletRequest request )
	{
		setPageTitleProperty( PROPERTY_PAGE_TITLE_MODIFY );

		String strCode = request.getParameter( PARAMETER_CODE );
		Site site = SiteHome.findByPrimaryKey( strCode, getPlugin() );

		Map<String, Object> model = new HashMap<String, Object>();
		ReferenceList workgroupsList = AdminWorkgroupService.getUserWorkgroups( getUser(), getLocale() );
		model.put( MARK_WORKGROUPS_LIST, workgroupsList );
		model.put( MARK_SITE, site );
		model.put( MARK_PROXY_HOST, AppPropertiesService.getProperty( PROPERTY_PROXY_HOST ) );
		model.put( MARK_PROXY_PORT, AppPropertiesService.getProperty( PROPERTY_PROXY_PORT ) );
		model.put( MARK_WEBAPP_URL, AppPathService.getBaseUrl( request ) );
		model.put( MARK_LOCALE, AdminUserService.getLocale( request ).getLanguage() );

		HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_MODIFY_SITE, getLocale(), model );

		return getAdminPage( template.getHtml() );
	}

	/**
	 * Process the change form of a {@link Site}
	 * 
	 * @param request The Http request
	 * @return The Jsp URL of the process result
	 * @throws AccessDeniedException if used is not authorized to modify {@link Site}
	 */
	public String doModifySite( HttpServletRequest request ) throws AccessDeniedException
	{
		String strCode = request.getParameter( PARAMETER_CODE );
		String strOldCode = request.getParameter( PARAMETER_OLD_CODE );
		String strUrl = request.getParameter( PARAMETER_URL );
		String strDescription = request.getParameter( PARAMETER_DESCRIPTION );
		String strEncoding = request.getParameter( PARAMETER_ENCODING );
		String strHat = request.getParameter( PARAMETER_HAT );
		String strWorkgroupKey = request.getParameter( PARAMETER_WORKGROUP_KEY );
		String strRebuildHtmlPage = request.getParameter( PARAMETER_REBUILD_HTML_PAGE );
		String strRedirectNonHtmlContent = request.getParameter( PARAMETER_REDIRECT_NON_HTML_CONTENT );
		String strUseProxy = request.getParameter( PARAMETER_USE_PROXY );

		// Mandatory field
		if ( ( strOldCode == null ) || strOldCode.equals( "" ) || ( strCode == null ) || strCode.equals( "" ) || ( strUrl == null ) || strUrl.equals( "" ) || ( strDescription == null )
				|| strDescription.equals( "" ) || ( strWorkgroupKey == null ) || !StringUtil.checkCodeKey( strWorkgroupKey ) )
		{
			return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
		}

		Site oldSite = SiteHome.findByPrimaryKey( strOldCode, getPlugin() );

		if ( !AdminWorkgroupService.isAuthorized( oldSite, getUser() ) )
		{
			throw new AccessDeniedException();
		}

		if ( oldSite == null )
		{
			return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
		}

		if ( !StringUtil.checkCodeKey( strCode ) )
		{
			return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_SITE_CODE, AdminMessage.TYPE_STOP );
		}

		if ( !strOldCode.equals( strCode ) && ( SiteHome.findByPrimaryKey( strCode, getPlugin() ) != null ) )
		{
			return AdminMessageService.getMessageUrl( request, MESSAGE_ERROR_SITE_CODE_ALREADY_EXISTS, AdminMessage.TYPE_STOP );
		}

		if ( StringUtils.isBlank( strEncoding ) )
		{
			strEncoding = AppPropertiesService.getProperty( PROPERTY_SITE_DEFAULT_ENCODING );
		}

		Site site = oldSite;
		site.setCode( strCode );
		site.setUrl( strUrl );
		site.setDescription( strDescription );
		site.setEncoding( strEncoding );
		site.setHat( strHat );
		site.setWorkgroup( strWorkgroupKey );

		if ( ( strRebuildHtmlPage != null ) && strRebuildHtmlPage.equals( Boolean.TRUE.toString() ) )
		{
			site.setRebuildHtmlPage( true );
		}
		else
		{
			site.setRebuildHtmlPage( false );
		}

		if ( ( strRedirectNonHtmlContent != null ) && strRedirectNonHtmlContent.equals( Boolean.TRUE.toString() ) )
		{
			site.setRedirectNonHtmlContent( true );
		}
		else
		{
			site.setRedirectNonHtmlContent( false );
		}

		if ( ( strUseProxy != null ) && strUseProxy.equals( Boolean.TRUE.toString() ) )
		{
			site.setUseProxy( true );
		}
		else
		{
			site.setUseProxy( false );
		}

		if ( !strCode.equals( strOldCode ) )
		{
			SiteHome.remove( strOldCode, getPlugin() );
			SiteHome.create( site, getPlugin() );
		}
		else
		{
			SiteHome.update( site, getPlugin() );
		}

		// If the operation is successful, redirect towards the list of sites
		UrlItem url = new UrlItem( JSP_URL_SITES_LIST );

		return url.getUrl();
	}

	/**
	 * Return the {@link Site} removal form
	 * @param request The Http request
	 * @return The Html template
	 */
	public String getConfirmRemoveSite( HttpServletRequest request )
	{
		UrlItem url = new UrlItem( JSP_URL_PREFIX + JSP_URL_DO_REMOVE_SITE );
		url.addParameter( PARAMETER_CODE, request.getParameter( PARAMETER_CODE ) );

		return AdminMessageService.getMessageUrl( request, MESSAGE_CONFIRM_REMOVE_SITE, url.getUrl(), AdminMessage.TYPE_CONFIRMATION );
	}

	/**
	 * Processes {@link Site} removal
	 * @param request The Http request
	 * @return The URL to redirect to
	 */
	public String doRemoveSite( HttpServletRequest request )
	{
		String strCode = request.getParameter( PARAMETER_CODE );
		Site site = SiteHome.findByPrimaryKey( strCode, getPlugin() );

		if ( site == null )
		{
			return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
		}

		SiteHome.remove( site.getCode(), getPlugin() );

		// If the operation is successful, redirect towards the list of sites
		return JSP_URL_SITES_LIST;
	}
}
