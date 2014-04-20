package org.cyclopsgroup.kaufman.web;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.LinkTool;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class WebLinkTool
    extends LinkTool
{
    private WebLinkToolConfig config;

    private boolean externalResourceActivated;

    private String externalResourceBase;

    private String externalResourcePath;

    @Override
    public LinkTool absolute( String uri )
    {
        boolean external = false;
        for ( Pattern p : config.getExternalResourcePatterns() )
        {
            if ( p.matcher( uri ).find() )
            {
                external = true;
                externalResourcePath = uri;
                break;
            }
        }
        if ( external )
        {
            externalResourceActivated = true;
            return this;
        }
        return super.absolute( uri );
    }

    @Override
    public void init( Object obj )
    {
        super.init( obj );
        ViewContext vc = (ViewContext) obj;

        ApplicationContext context =
            WebApplicationContextUtils.getRequiredWebApplicationContext( vc.getServletContext() );
        config = context.getBean( WebLinkToolConfig.class );

        externalResourceBase = config.getExternalResourceUrl();
        if ( StringUtils.isBlank( externalResourceBase ) )
        {
            externalResourceBase = vc.getServletContext().getContextPath();
        }
    }

    @Override
    public String toString()
    {
        if ( !externalResourceActivated )
        {
            return super.toString();
        }
        externalResourceActivated = false;
        return externalResourceBase + externalResourcePath;
    }
}
