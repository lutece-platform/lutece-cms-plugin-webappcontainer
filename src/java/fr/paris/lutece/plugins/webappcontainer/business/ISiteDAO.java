/*
 * Copyright (c) 2002-2009, Mairie de Marseille
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

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.Collection;


/**
 * Interface for SiteDAO
 * @author ELY
 *
 */
public interface ISiteDAO
{
    /**
     * Insert a new record in the table.
     *
     * @param site The site object
     * @param plugin The {@link Plugin}
     */
    void insert( Site site, Plugin plugin );

    /**
     * Load the data of Site from the table
     *
     * @param strSiteCode The code of {@link Site}
     * @param plugin The {@link Plugin}
     * @return the instance of the {@link Site}
     */
    Site load( String strSiteCode, Plugin plugin );

    /**
     * Delete a record from the table
     *
     * @param strSiteCode The {@link Site} code
     * @param plugin The {@link Plugin}
     */
    void delete( String strSiteCode, Plugin plugin );

    /**
     * Update the record in the table
     *
     * @param site The reference of {@link Site}
     * @param plugin The {@link Plugin}
     */
    void store( Site site, Plugin plugin );

    /**
     * Load the list of sites
     *
     * @param plugin The {@link Plugin}
     * @return The Collection of the Sites
     */
    Collection<Site> selectAll( Plugin plugin );
}
