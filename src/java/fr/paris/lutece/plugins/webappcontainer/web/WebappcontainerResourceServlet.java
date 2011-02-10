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
package fr.paris.lutece.plugins.webappcontainer.web;

import fr.paris.lutece.plugins.webappcontainer.business.Site;
import fr.paris.lutece.plugins.webappcontainer.business.WebappResponse;
import fr.paris.lutece.plugins.webappcontainer.util.HttpAccess;
import fr.paris.lutece.plugins.webappcontainer.util.HttpAccessException;
import fr.paris.lutece.plugins.webappcontainer.util.UrlUtils;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.httpclient.URI;

import java.io.IOException;
import java.io.OutputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet serving document file resources
 */
@SuppressWarnings( "serial" )
public class WebappcontainerResourceServlet extends HttpServlet
{
    private static final String METHOD_POST = "POST";
    private static final String SESSION_ATTRIBUTE_COOKIES = "HttpWrapper_cookies_";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException the servlet Exception
     * @throws IOException the io exception
     */
    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        OutputStream out = response.getOutputStream(  );
        String strRequestedUrl = WebappcontainerApp.getRequestUrl( request );
        Site site = WebappcontainerApp.getSite( request );

        if ( ( strRequestedUrl == null ) || strRequestedUrl.equals( "" ) )
        {
            strRequestedUrl = site.getUrl(  );
        }

        WebappResponse webappResponse = null;

        try
        {
            webappResponse = getWebappResponse( request, strRequestedUrl, site );
        }
        catch ( HttpAccessException e )
        {
            out.write( ( "Error : " + e ).getBytes(  ) );
            AppLogService.error( "Error when retrieving external site content (site : " + site.getCode(  ) +
                ", url : " + strRequestedUrl + ")", e );
        }

        if ( webappResponse != null )
        {
            out.write( webappResponse.getContent(  ) );
        }

        out.flush(  );
        out.close(  );
    }

    /**
     * Get the webapp response
     *
     * @param request The {@link HttpServletRequest} object
     * @param strRequestedUrl The requested url
     * @param site The site requested
     * @return The {@link WebappResponse} object (contain response content, headers, cookies, ...)
     * @throws HttpAccessException if process cannot access to external site
     */
    public static WebappResponse getWebappResponse( HttpServletRequest request, String strRequestedUrl, Site site )
        throws HttpAccessException
    {
        // Get the site content
        HttpAccess httpAccess = new HttpAccess(  );
        WebappResponse webappResponse = null;
        RequestContext requestContext = new ServletRequestContext( request );
        Map<String, FileItem> fileMap = null;
        Map<String, String[]> parametersMap = null;

        if ( ServletFileUpload.isMultipartContent( requestContext ) )
        {
            //Multipart
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            fileMap = multipartRequest.getFileMap(  );

            parametersMap = getParameterMap( multipartRequest );
        }
        else
        {
            parametersMap = getParameterMap( request );
        }

        // Get the cookies for the specified site
        org.apache.commons.httpclient.Cookie[] cookies = (org.apache.commons.httpclient.Cookie[]) request.getSession(  )
                                                                                                         .getAttribute( SESSION_ATTRIBUTE_COOKIES +
                site.getCode(  ) );

        httpAccess.initializeState( cookies, site );

        if ( !UrlUtils.hostsEquals( site.getUrl(  ), strRequestedUrl ) )
        {
            throw new HttpAccessException( "Error : The requested Url does not corresponding with the specified external site.",
                null );
        }

        try
        {
            if ( request.getMethod(  ).equals( METHOD_POST ) )
            {
                URI uri = new URI( strRequestedUrl, false );
                webappResponse = httpAccess.doPost( uri.getEscapedURI(  ), parametersMap, fileMap );
            }
            else
            {
                // Set the parameters
                UrlItem requestedUrl = new UrlItem( strRequestedUrl );

                for ( Entry<String, String[]> entry : parametersMap.entrySet(  ) )
                {
                    requestedUrl.addParameter( entry.getKey(  ), entry.getValue(  )[0] );
                }

                URI uri = new URI( requestedUrl.getUrl(  ), false );

                webappResponse = httpAccess.doGet( uri.getEscapedURI(  ) );
            }
        }
        catch ( Exception e )
        {
            AppLogService.error( e.getMessage(  ), e );
            throw new HttpAccessException( e.getMessage(  ), e );
        }

        // Set the cookies for the specified site
        request.getSession(  ).setAttribute( SESSION_ATTRIBUTE_COOKIES + site.getCode(  ), webappResponse.getCookies(  ) );

        return webappResponse;
    }

    /**
     * Delete the webappcontainer specifics informations (siteCode and requested url) in the parameter map request
     * @param request The request
     * @return The map parameters
     */
    private static Map<String, String[]> getParameterMap( HttpServletRequest request )
    {
        // Set the parameters
        Map<String, String[]> parametersMap = request.getParameterMap(  );
        Map<String, String[]> parametersWithoutContainerInformations = new HashMap<String, String[]>(  );

        for ( Entry<String, String[]> entry : parametersMap.entrySet(  ) )
        {
            if ( !entry.getKey(  ).equals( WebappcontainerApp.PARAMETER_CODE ) &&
                    !entry.getKey(  ).equals( WebappcontainerApp.PARAMETER_PAGE ) &&
                    !entry.getKey(  ).equals( WebappcontainerApp.PARAMETER_WEBAPP_URL ) )
            {
                if ( entry.getKey(  ).startsWith( WebappcontainerApp.PARAMETER_PAGE_HACK ) )
                {
                    parametersWithoutContainerInformations.put( entry.getKey(  )
                                                                     .substring( WebappcontainerApp.PARAMETER_PAGE_HACK.length(  ) ),
                        entry.getValue(  ) );
                }
                else
                {
                    parametersWithoutContainerInformations.put( entry.getKey(  ), entry.getValue(  ) );
                }
            }
        }

        return parametersWithoutContainerInformations;
    }

    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException the servlet Exception
     * @throws IOException the io exception
     */
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        processRequest( request, response );
    }

    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException the servlet Exception
     * @throws IOException the io exception
     */
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        processRequest( request, response );
    }

    /** Returns a short description of the servlet.
     * @return message
     */
    public String getServletInfo(  )
    {
        return "Servlet serving file resources of webappcontainer";
    }
}
