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
package fr.paris.lutece.plugins.webappcontainer.business;

import java.util.ArrayList;
import java.util.Collection;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * This class provides Data Access methods for Sites objects
 */
public final class SiteDAO implements ISiteDAO
{
	// Constants
	private static final String SQL_QUERY_SELECT = "SELECT site_url, site_description, site_encoding, site_hat, site_workgroup_key, site_rebuild_html_page, site_redirect_non_html_content, site_use_proxy FROM webappcontainer_site WHERE site_code = ? ";

	private static final String SQL_QUERY_SELECTALL = "SELECT site_code, site_url, site_description, site_encoding, site_hat, site_workgroup_key, site_rebuild_html_page, site_redirect_non_html_content, site_use_proxy FROM webappcontainer_site ORDER BY site_code ";

	private static final String SQL_QUERY_INSERT = "INSERT INTO webappcontainer_site ( site_code, site_url, site_description, site_encoding, site_hat, site_workgroup_key, site_rebuild_html_page, site_redirect_non_html_content, site_use_proxy )  VALUES ( ? , ? , ? , ? , ? , ? , ? , ? , ? ) ";

	private static final String SQL_QUERY_DELETE = "DELETE FROM webappcontainer_site WHERE site_code = ? ";

	private static final String SQL_QUERY_UPDATE = "UPDATE webappcontainer_site SET site_url = ?, site_description = ?, site_encoding = ?, site_hat = ?, site_workgroup_key = ?, site_rebuild_html_page = ?, site_redirect_non_html_content = ?, site_use_proxy = ? WHERE site_code = ?  ";

	// //////////////////////////////////////////////////////////////////////
	// Methods using a dynamic pool

	/**
	 * Insert a new record in the table.
	 * 
	 * @param site The site object
	 * @param plugin The {@link Plugin}
	 */
	public void insert( Site site, Plugin plugin )
	{
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
		daoUtil.setString( 1, site.getCode() );
		daoUtil.setString( 2, site.getUrl() );
		daoUtil.setString( 3, site.getDescription() );
		daoUtil.setString( 4, site.getEncoding() );
		daoUtil.setString( 5, site.getHat() );
		daoUtil.setString( 6, site.getWorkgroup() );
		daoUtil.setBoolean( 7, site.isRebuildHtmlPage() );
		daoUtil.setBoolean( 8, site.isRedirectNonHtmlContent() );
		daoUtil.setBoolean( 9, site.isUseProxy() );

		daoUtil.executeUpdate();
		daoUtil.free();
	}

	/**
	 * Load the data of Site from the table
	 * 
	 * @param strSiteCode The code of {@link Site}
	 * @param plugin The {@link Plugin}
	 * @return the instance of the {@link Site}
	 */
	public Site load( String strSiteCode, Plugin plugin )
	{
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
		daoUtil.setString( 1, strSiteCode );
		daoUtil.executeQuery();

		Site site = null;

		if ( daoUtil.next() )
		{
			site = new Site();
			site.setCode( strSiteCode );
			site.setUrl( daoUtil.getString( 1 ) );
			site.setDescription( daoUtil.getString( 2 ) );
			site.setEncoding( daoUtil.getString( 3 ) );
			site.setHat( daoUtil.getString( 4 ) );
			site.setWorkgroup( daoUtil.getString( 5 ) );
			site.setRebuildHtmlPage( daoUtil.getBoolean( 6 ) );
			site.setRedirectNonHtmlContent( daoUtil.getBoolean( 7 ) );
			site.setUseProxy( daoUtil.getBoolean( 8 ) );
		}

		daoUtil.free();

		return site;
	}

	/**
	 * Delete a record from the table
	 * 
	 * @param strSiteCode The {@link Site} code
	 * @param plugin The {@link Plugin}
	 */
	public void delete( String strSiteCode, Plugin plugin )
	{
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
		daoUtil.setString( 1, strSiteCode );
		daoUtil.executeUpdate();
		daoUtil.free();
	}

	/**
	 * Update the record in the table
	 * 
	 * @param site The reference of {@link Site}
	 * @param plugin The {@link Plugin}
	 */
	public void store( Site site, Plugin plugin )
	{
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );

		daoUtil.setString( 1, site.getUrl() );
		daoUtil.setString( 2, site.getDescription() );
		daoUtil.setString( 3, site.getEncoding() );
		daoUtil.setString( 4, site.getHat() );
		daoUtil.setString( 5, site.getWorkgroup() );
		daoUtil.setBoolean( 6, site.isRebuildHtmlPage() );
		daoUtil.setBoolean( 7, site.isRedirectNonHtmlContent() );
		daoUtil.setBoolean( 8, site.isUseProxy() );

		daoUtil.setString( 9, site.getCode() );

		daoUtil.executeUpdate();
		daoUtil.free();
	}

	/**
	 * Load the list of sites
	 * 
	 * @param plugin The {@link Plugin}
	 * @return The Collection of the Sites
	 */
	public Collection<Site> selectAll( Plugin plugin )
	{
		Collection<Site> sitesList = new ArrayList<Site>();
		DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECTALL, plugin );
		daoUtil.executeQuery();

		while (daoUtil.next())
		{
			Site site = new Site();
			site.setCode( daoUtil.getString( 1 ) );
			site.setUrl( daoUtil.getString( 2 ) );
			site.setDescription( daoUtil.getString( 3 ) );
			site.setEncoding( daoUtil.getString( 4 ) );
			site.setHat( daoUtil.getString( 5 ) );
			site.setWorkgroup( daoUtil.getString( 6 ) );
			site.setRebuildHtmlPage( daoUtil.getBoolean( 7 ) );
			site.setRedirectNonHtmlContent( daoUtil.getBoolean( 8 ) );
			site.setUseProxy( daoUtil.getBoolean( 9 ) );
			sitesList.add( site );
		}

		daoUtil.free();

		return sitesList;
	}
}
