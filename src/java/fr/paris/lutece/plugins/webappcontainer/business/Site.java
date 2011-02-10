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

import fr.paris.lutece.portal.service.workgroup.AdminWorkgroupResource;


/**
 *
 * @author ELY
 *
 */
public class Site implements AdminWorkgroupResource
{
    private String _strCode;
    private String _strUrl;
    private String _strDescription;
    private String _strWorkgroupKey;
    private boolean _bRebuildHtmlPage;
    private boolean _bRedirectNonHtmlContent;
    private boolean _bUseProxy;

    /**
     * @return the code
     */
    public String getCode(  )
    {
        return _strCode;
    }

    /**
     * @param strCode the code to set
     */
    public void setCode( String strCode )
    {
        this._strCode = strCode;
    }

    /**
     * @return the url
     */
    public String getUrl(  )
    {
        return _strUrl;
    }

    /**
     * @param strUrl the url to set
     */
    public void setUrl( String strUrl )
    {
        this._strUrl = strUrl;
    }

    /**
     * @return the description
     */
    public String getDescription(  )
    {
        return _strDescription;
    }

    /**
     * @param strDescription the description to set
     */
    public void setDescription( String strDescription )
    {
        this._strDescription = strDescription;
    }

    /**
     * @param strWorkgroupKey the Workgroup key to set
     */
    public void setWorkgroup( String strWorkgroupKey )
    {
        this._strWorkgroupKey = strWorkgroupKey;
    }

    /**
     * @return the Workgroup key
     */
    public String getWorkgroup(  )
    {
        return _strWorkgroupKey;
    }

    /**
     * Set to true to redirect non-HTML contents (images, CSS, ...), false else
     * @param bRedirectNonHtmlContent The bRedirectNonHtmlContent value to set
     */
    public void setRedirectNonHtmlContent( boolean bRedirectNonHtmlContent )
    {
        this._bRedirectNonHtmlContent = bRedirectNonHtmlContent;
    }

    /**
    * Return true if the process redirect non-HTML content (images, CSS, ...), false else
     * @return true if the process redirect non-HTML content (images, CSS, ...), false else
     */
    public boolean isRedirectNonHtmlContent(  )
    {
        return _bRedirectNonHtmlContent;
    }

    /**
     * Set to true if the process must rebuild the HTML page (merge head and body contents), false else
     * @param bRebuildHtmlPage The bRebuildHtmlPage value to set
     */
    public void setRebuildHtmlPage( boolean bRebuildHtmlPage )
    {
        this._bRebuildHtmlPage = bRebuildHtmlPage;
    }

    /**
     * Return true if the process must rebuild the HTML page (merge head and body contents), false else
     * @return true if the process must rebuild the HTML page (merge head and body contents), false else
     */
    public boolean isRebuildHtmlPage(  )
    {
        return _bRebuildHtmlPage;
    }

    /**
     * Set to true if the process must use the proxy defined in the plugin property file, false else
     * @param bUseProxy The bUseProxy value to set
     */
    public void setUseProxy( boolean bUseProxy )
    {
        this._bUseProxy = bUseProxy;
    }

    /**
     * Return true if the process must use the proxy defined in the plugin property file, false else
     * @return true if the process must use the proxy defined in the plugin property file, false else
     */
    public boolean isUseProxy(  )
    {
        return _bUseProxy;
    }
}
