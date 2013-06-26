package org.cyclopsgroup.kaufman.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * A base abstract HTTP filter that verifies request before passing it to next
 * step of chain
 */
public abstract class AbstractRequestAuthorizingFilter
    implements Filter
{
    private Set<String> pathIgnored =
        Collections.unmodifiableSet( new HashSet<String>(
                                                          Arrays.asList( "/ping" ) ) );

    /**
     * @inheritDoc
     */
    @Override
    public void doFilter( ServletRequest request, ServletResponse response,
                          FilterChain chain )
        throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;

        // For a list of path, filter does not apply
        String path =
            StringUtils.stripToEmpty( req.getServletPath() )
                + StringUtils.stripToEmpty( req.getPathInfo() );
        if ( pathIgnored.contains( path ) )
        {
            chain.doFilter( request, response );
            return;
        }

        // Reject request if it's not authorized
        if ( !isRequestAuthorized( (HttpServletRequest) request ) )
        {
            ( (HttpServletResponse) response ).sendError( HttpServletResponse.SC_UNAUTHORIZED,
                                                          "Request is denied by filter "
                                                              + getClass().getSimpleName() );
            return;
        }
        chain.doFilter( request, response );
    }

    /**
     * Implement concrete code logic to evaluate an HTTP request
     *
     * @param request HTTP request to evaluate
     * @return True if request is authorized
     * @throws ServletException Allows internal servlet exception
     * @throws IOException Allows internal IO exception
     */
    protected abstract boolean isRequestAuthorized( HttpServletRequest request )
        throws ServletException, IOException;

    /**
     * Set a set of paths that bypass authorization
     *
     * @param paths Set of paths that don't require authorization
     */
    public void setPathIgnored( Set<String> paths )
    {
        this.pathIgnored = Collections.unmodifiableSet( paths );
    }
}
