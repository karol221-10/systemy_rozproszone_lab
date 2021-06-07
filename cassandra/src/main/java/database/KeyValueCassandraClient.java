package database;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.Update;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.InsertInto;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;
import com.datastax.oss.driver.api.querybuilder.schema.CreateTable;
import com.datastax.oss.driver.api.querybuilder.schema.CreateType;
import com.datastax.oss.driver.api.querybuilder.update.UpdateStart;
import com.datastax.oss.driver.api.querybuilder.update.UpdateWithAssignments;
import database.cassandra.KeyspaceManager;
import lombok.val;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class KeyValueCassandraClient implements KeyValueDatabaseConnector, KeyValueDatabaseInstance, KeyValueDatabaseProcessor{

    private CqlSession session;

    @Override
    public void connect(String hostname, Integer port) throws IOException {
        session = CqlSession.builder().build();
        createNeededTables();
    }

    public void createNeededTables() {
        KeyspaceManager keyspaceManager = new KeyspaceManager(session, "TravelAgency");
        keyspaceManager.dropKeyspace();
        keyspaceManager.selectKeyspaces();
        keyspaceManager.createKeyspace();
        keyspaceManager.useKeyspace();

        CreateTable touristTable = SchemaBuilder.createTable("tourist")
                .withPartitionKey("id", DataTypes.TEXT)
                .withColumn("name", DataTypes.TEXT)
                .withColumn("surname", DataTypes.TEXT)
                .withColumn("age", DataTypes.INT);

        CreateTable countTable = SchemaBuilder.createTable("count")
                .withPartitionKey("id", DataTypes.TEXT)
                .withColumn("value", DataTypes.INT);
        session.execute(touristTable.build());
        session.execute(countTable.build());
    }

    @Override
    public <T> T getValue(String collection, String key, Class<T> className) {
        val statement = "SELECT * from " +collection + " WHERE id='"+key+"';";
        val resultSet = session.execute(statement);
        val row = resultSet.iterator().next();
       return mapClass(row, className);
    }

    private <T> T mapClass(Row row, Class<T> className) {
        Field[] fields = className.getDeclaredFields();
        try {
            T object = className.getDeclaredConstructor().newInstance();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                if(String.class.equals(fields[i].getType())) {
                    fields[i].set(object, row.getString(fields[i].getName()));
                }
                else {
                    fields[i].set(object, row.getInt(fields[i].getName()));
                }
                fields[i].setAccessible(false);
            }
            return object;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getObjectCount(String keyName) {
        val statement = "SELECT * from count WHERE id='"+keyName+"';";
        val resultSet = session.execute(statement);
        if(!resultSet.iterator().hasNext()) {
            return 0;
        }
        val row = resultSet.iterator().next();
        return row.getInt("value");
    }

    @Override
    public void updateObjectCount(String keyName, int newValue) {
        String statement;
        if (newValue > 1) {
            statement = "UPDATE count SET value=" + newValue + " WHERE id='"+keyName+"';";
        }
        else {
            statement = "INSERT INTO count(id,value) VALUES('"+keyName+"',"+newValue+");";
        }
        session.execute(statement);
    }

    @Override
    public <T> List<T> getAll(String collection, Class<T> className) {
        List<T> objects = new ArrayList<>();
        val query = QueryBuilder.selectFrom(collection).all();
        val result = session.execute(query.build());
        val iterator = result.iterator();
        while(iterator.hasNext()) {
            objects.add(mapClass(iterator.next(), className));
        }
        return objects;
    }

    @Override
    public <T> List<T> findBy(String collection, String fieldName, String fieldValue, Class<T> className) {
        List<T> resultList = new ArrayList<>();
        try {
            val field = className.getDeclaredField(fieldName);
            String convertedFieldValue;
            if(String.class.equals(field.getType())){
                convertedFieldValue = "'" + fieldValue + "'";
            }
            else convertedFieldValue = fieldValue;
            val query = QueryBuilder.selectFrom(collection).all().allowFiltering().whereColumn(fieldName).isEqualTo(QueryBuilder.raw(convertedFieldValue));
            val result = session.execute(query.build());
            val iterator = result.iterator();
            while(iterator.hasNext()) {
                resultList.add(mapClass(iterator.next(), className));
            }
            return resultList;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean containsKey(String collection, String key) {
        val statement = "SELECT * from " + collection + " WHERE id='" + key + "';";
        val resultSet = session.execute(statement);
        if (!resultSet.iterator().hasNext()) {
            return false;
        }
        return true;
    }

    @Override
    public <T> void setValue(String collection, String key, T value) {
        Field[] fields = value.getClass().getDeclaredFields();
        InsertInto insertInto = QueryBuilder.insertInto("TravelAgency", collection);
        RegularInsert insert = null;
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            try {
                String text;
                if (String.class.equals(fields[i].getType())) {
                    text = "'" + fields[i].get(value).toString() + "'";
                }
                else {
                    text = fields[i].get(value).toString();
                }
                if (insert == null) {
                   insert = insertInto.value(fields[i].getName(), QueryBuilder.raw(text));
                }
                else {
                    insert = insert.value(fields[i].getName(), QueryBuilder.raw(text));
                }
                fields[i].setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        session.execute(insert.build());
    }

    @Override
    public <T> void updateValue(String collection, String key, T value) {
        Field[] fields = value.getClass().getDeclaredFields();
        UpdateStart update = QueryBuilder.update(collection);
        UpdateWithAssignments updateWithAssignments = null;
        for (int i = 0; i < fields.length; i++) {
            if(fields[i].getName().equals("id")) {
                continue;
            }
            fields[i].setAccessible(true);
            try {
                String text;
                if (String.class.equals(fields[i].getType())) {
                    text = "'" + fields[i].get(value).toString() + "'";
                }
                else {
                    text = fields[i].get(value).toString();
                }
                if (updateWithAssignments == null) {
                    updateWithAssignments = update.setColumn(fields[i].getName(), QueryBuilder.raw(text));
                }
                else {
                    updateWithAssignments = updateWithAssignments.setColumn(fields[i].getName(), QueryBuilder.raw(text));
                }
                fields[i].setAccessible(false);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        val completeUpdate = updateWithAssignments.whereColumn("id").isEqualTo(QueryBuilder.raw("'"+key+"'"));
        session.execute(completeUpdate.build());
    }

    @Override
    public void removeValue(String collection, String key) {
        val delete = QueryBuilder.deleteFrom(collection).whereColumn("id").isEqualTo(QueryBuilder.raw("'"+key+"'"));
        session.execute(delete.build());
    }

    @Override
    public <T> double calcAvgByField(String collection, String fieldName, Class<T> objectName) {
        val allObjects = getAll(collection, objectName);
        double sum = 0;
        for(int i = 0; i < allObjects.size(); i++) {
            try {
                val field = objectName.getDeclaredField(fieldName);
                field.setAccessible(true);
                sum += Double.parseDouble(field.get(allObjects.get(i)).toString());
                field.setAccessible(false);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return sum / allObjects.size();
    }
}
