/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
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

import org.apache.commons.httpclient.Cookie;


/**
 * This class supply the response informations and data from the external site
 *
 * @author ELY
 *
 */
public class WebappResponse
{
    private byte[] _byteContent;
    private String _strContentType;
    private String _strContentCharset;
    private String _strLocation;
    private Cookie[] _cookies;

    /**
         * @return the _cookies
         */
    public Cookie[] getCookies(  )
    {
        // Return a copy to _cookies to avoid malicious code
        if ( _cookies != null )
        {
            return _cookies.clone(  );
        }

        return null;
    }

    /**
     * @param cookies the cookies to set
     */
    public void setCookies( Cookie[] cookies )
    {
        // Save a copy to cookies to avoid malicious code
        if ( cookies != null )
        {
            this._cookies = cookies.clone(  );
        }
    }

    /**
     * @return the _strContentCharset
     */
    public String getContentCharset(  )
    {
        return _strContentCharset;
    }

    /**
     * @param strContentCharset the _strContentCharset to set
     */
    public void setContentCharset( String strContentCharset )
    {
        _strContentCharset = strContentCharset;
    }

    /**
         * @return the _strLocation
         */
    public String getLocation(  )
    {
        return _strLocation;
    }

    /**
     * @param strLocation the _strLocation to set
     */
    public void setLocation( String strLocation )
    {
        _strLocation = strLocation;
    }

    /**
    * @return the content
    */
    public byte[] getContent(  )
    {
        // Return a copy to _byteContent to avoid malicious code
        if ( _byteContent != null )
        {
            return _byteContent.clone(  );
        }

        return null;
    }

    /**
     * @param byteContent the content to set
     */
    public void setContent( byte[] byteContent )
    {
        // Save a copy to byteContent to avoid malicious code
        if ( byteContent != null )
        {
            this._byteContent = byteContent.clone(  );
        }
    }

    /**
     * @return the contentType
     */
    public String getContentType(  )
    {
        return _strContentType;
    }

    /**
     * @param strContentType the contentType to set
     */
    public void setContentType( String strContentType )
    {
        _strContentType = strContentType;
    }
}
