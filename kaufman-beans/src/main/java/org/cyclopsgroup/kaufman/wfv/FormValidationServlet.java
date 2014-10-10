package org.cyclopsgroup.kaufman.wfv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This servlet validates request with {@link Validator} from spring context and return a JSON pojo for validation
 * results. It is called by Javascript in order validate form with AJAX before submitting the form.
 */
@SuppressWarnings( "serial" )
public class FormValidationServlet
    extends HttpServlet
{
    private static final Log LOG = LogFactory.getLog( FormValidationServlet.class );

    private static Object newInstance( String typeName )
        throws ServletException
    {
        try
        {
            return Class.forName( typeName ).newInstance();
        }
        catch ( Exception e )
        {
            throw new ServletException( "Invalid form bean " + typeName, e );

        }
    }

    private FormValidationResult buildResult( BindingResult from )
    {
        FormValidationResult result = new FormValidationResult();

        Map<String, FieldValidationResult> fieldMap = new HashMap<String, FieldValidationResult>();
        for ( FieldError f : from.getFieldErrors() )
        {
            FieldValidationResult fr = fieldMap.get( f.getField() );
            if ( fr == null )
            {
                fr = new FieldValidationResult();
                fr.setFieldName( f.getField() );
                fr.setFailureMessages( new ArrayList<String>() );
                fieldMap.put( f.getField(), fr );
            }
            fr.getFailureMessages().add( f.getDefaultMessage() );
        }

        if ( !from.getGlobalErrors().isEmpty() )
        {
            // To keep client easy, global error is considered as error of a
            // field named "form"
            FieldValidationResult fr = new FieldValidationResult();
            fr.setFieldName( "form" );
            fr.setFailureMessages( new ArrayList<String>() );
            for ( ObjectError e : from.getGlobalErrors() )
            {
                fr.getFailureMessages().add( e.getDefaultMessage() );
            }
            fieldMap.put( fr.getFieldName(), fr );
        }

        result.setFields( new ArrayList<FieldValidationResult>( fieldMap.values() ) );
        result.setSuccessful( result.getFields().isEmpty() );
        return result;
    }

    /**
     * @inheritDoc
     */
    @Override
    protected void doPost( HttpServletRequest req, HttpServletResponse resp )
        throws ServletException, IOException
    {
        validateForm( req, resp );
    }

    private void validateForm( HttpServletRequest request, HttpServletResponse response )
        throws ServletException, IOException
    {
        String formBeanType = request.getParameter( "formBean" );
        if ( formBeanType == null )
        {
            throw new ServletException( "formBean must be specified" );
        }

        Object formBean = newInstance( formBeanType );

        ServletRequestDataBinder binder = new ServletRequestDataBinder( formBean );
        binder.setValidator( WebApplicationContextUtils.getRequiredWebApplicationContext( getServletContext() ).getBean( Validator.class ) );
        binder.bind( request );
        LOG.info( "Validating form " + ToStringBuilder.reflectionToString( formBean ) );
        binder.validate();

        FormValidationResult result = buildResult( binder.getBindingResult() );
        LOG.info( "Returning validation result in JSON: " + result );
        response.setContentType( "application/json" );
        new ObjectMapper().writeValue( response.getWriter(), result );
        response.getWriter().flush();
    }
}
