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

import fr.paris.lutece.plugins.webappcontainer.service.WebappcontainerPlugin;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;


/**
 * This class provides instances management methods (create, find, ...) for {@link Site} objects
 */
public final class SiteHome
{
    // Static variable pointed at the DAO instance
    private static ISiteDAO _dao = (ISiteDAO) SpringContextService.getPluginBean( WebappcontainerPlugin.PLUGIN_NAME,
            "siteDAO" );

    /**
     * Private constructor - this class does not need to be instantiated
     */
    private SiteHome(  )
    {
    }

    /**
     * Creation of an instance of {@link Site}
     *
     * @param site The instance of the {@link Site} which contains the informations to store
     * @param plugin The {@link Plugin} object
     * @return The  instance of {@link Site} which has been created with its primary key.
     */
    public static Site create( Site site, Plugin plugin )
    {
        _dao.insert( site, plugin );

        return site;
    }

    /**
     * Update of the {@link Site} which is specified in parameter
     *
     * @param site The instance of the {@link Site} which contains the data to store
     * @param plugin The {@link Plugin} object
     * @return The instance of the  {@link Site} which has been updated
     */
    public static Site update( Site site, Plugin plugin )
    {
        _dao.store( site, plugin );

        return site;
    }

    /**
     * Remove the {@link Site} whose identifier is specified in parameter
     *
     * @param strSiteCode The {@link Site} code to remove
     * @param plugin The {@link Plugin} object
     */
    public static void remove( String strSiteCode, Plugin plugin )
    {
        _dao.delete( strSiteCode, plugin );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Finders

    /**
     * Returns an instance of a {@link Site} whose identifier is specified in parameter
     *
     * @param strSiteCode The Primary key of the {@link Site} (site code)
     * @param plugin The {@link Plugin} object
     * @return An instance of {@link Site}
     */
    public static Site findByPrimaryKey( String strSiteCode, Plugin plugin )
    {
        Site site = _dao.load( strSiteCode, plugin );

        return site;
    }

    /**
     * Returns a collection of {@link Site} objects
     * @param plugin The {@link Plugin} object
     * @return A collection of {@link Site}
     */
    public static Collection<Site> findAll( Plugin plugin )
    {
        Collection<Site> listSite = _dao.selectAll( plugin );

        return listSite;
    }
}
