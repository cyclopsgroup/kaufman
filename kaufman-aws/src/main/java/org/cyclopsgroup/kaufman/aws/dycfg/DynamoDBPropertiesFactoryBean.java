package org.cyclopsgroup.kaufman.aws.dycfg;

import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;

import com.amazonaws.services.dynamodb.AmazonDynamoDB;

public class DynamoDBPropertiesFactoryBean
    implements FactoryBean<Properties>
{
    private final Properties props;

    public DynamoDBPropertiesFactoryBean( DynamoDBPropertiesBuilder builder, String category )
    {
        props = builder.fetchProperties( category );
    }

    public DynamoDBPropertiesFactoryBean( AmazonDynamoDB dynamo, String tableName, String category )
    {
        this( new DynamoDBPropertiesBuilder().withDynamo( dynamo ).withTableName( tableName ), category );
    }

    /**
     * @inheritDoc
     */
    @Override
    public Properties getObject()
    {
        return props;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Class<Properties> getObjectType()
    {
        return Properties.class;
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isSingleton()
    {
        return true;
    }
}
