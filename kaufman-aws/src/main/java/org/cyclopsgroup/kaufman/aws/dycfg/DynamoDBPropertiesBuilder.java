package org.cyclopsgroup.kaufman.aws.dycfg;

import java.util.Map;
import java.util.Properties;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;
import com.amazonaws.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.dynamodb.model.QueryRequest;
import com.amazonaws.services.dynamodb.model.QueryResult;

public class DynamoDBPropertiesBuilder
{
    private static final String DEFAULT_TABLE_NAME = "kaufman-app-properties";

    private boolean consistentRead = false;

    private AmazonDynamoDB dynamo;

    private String tableName = DEFAULT_TABLE_NAME;

    public Properties fetchProperties( String category )
    {
        QueryRequest query =
            new QueryRequest().withTableName( tableName ).withHashKeyValue( new AttributeValue( category ) ).withConsistentRead( consistentRead );
        QueryResult result = dynamo.query( query );
        Properties props = new Properties();
        for ( Map<String, AttributeValue> prop : result.getItems() )
        {
            AttributeValue key = prop.get( "propertyName" );
            AttributeValue value = prop.get( "propertyValue" );

            if ( key == null || value == null )
            {
                continue;
            }
            props.setProperty( key.getS(), value.getS() );
        }
        return props;
    }

    public final void setConsistentRead( boolean consistentRead )
    {
        this.consistentRead = consistentRead;
    }

    public final void setDynamo( AmazonDynamoDB dynamo )
    {
        this.dynamo = dynamo;
    }

    public final void setTableName( String tableName )
    {
        this.tableName = tableName;
    }

    public DynamoDBPropertiesBuilder withConsistentRead( boolean consistentRead )
    {
        setConsistentRead( consistentRead );
        return this;
    }

    public DynamoDBPropertiesBuilder withDynamo( AmazonDynamoDB dynamo )
    {
        setDynamo( dynamo );
        return this;
    }

    public DynamoDBPropertiesBuilder withTableName( String tableName )
    {
        setTableName( tableName );
        return this;
    }
}
