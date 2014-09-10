package org.cyclopsgroup.kaufman.aws.dycfg;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.QueryRequest;
import com.amazonaws.services.dynamodb.model.QueryResult;

public class DynamoDBPropertiesBuilderTest
{
    private DynamoDBPropertiesBuilder builder;

    private AmazonDynamoDB dynamo;

    private Mockery mock;

    @After
    public void assertMocks()
    {
        mock.assertIsSatisfied();
    }

    @Before
    public void setUpMocks()
    {
        mock = new Mockery();
        dynamo = mock.mock( AmazonDynamoDB.class );
        builder = new DynamoDBPropertiesBuilder().withDynamo( dynamo ).withTableName( "test-table" );
    }

    @Test
    public void testFetchProperties()
    {
        final QueryRequest query =
            new QueryRequest().withTableName( "test-table" ).withHashKeyValue( new AttributeValue( "test-category" ) ).withConsistentRead( false );
        final QueryResult result = new QueryResult();
        Map<String, AttributeValue> entry = new HashMap<String, AttributeValue>();
        entry.put( "propertyName", new AttributeValue( "prop1" ) );
        entry.put( "propertyValue", new AttributeValue( "a-value" ) );
        List<Map<String, AttributeValue>> items = new ArrayList<Map<String, AttributeValue>>();
        items.add( entry );
        result.setItems( items );
        mock.checking( new Expectations()
        {
            {
                one( dynamo ).query( query );
                will( returnValue( result ) );
            }
        } );

        Properties props = builder.fetchProperties( "test-category" );
        assertEquals( 1, props.size() );
        assertEquals( "a-value", props.getProperty( "prop1" ) );
    }
}
