import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Processor {
    private CosmosClient client;

    private final String databaseName = "AzureSampleFamilyDB";
    private final String containerName = "FamilyContainer";

    private CosmosDatabase database;
    private CosmosContainer container;

    public void init() throws Exception {

        ArrayList<String> preferredRegions = new ArrayList<String>();
        preferredRegions.add("West US");

        //  Create sync client
        //  <CreateSyncClient>
        client = new CosmosClientBuilder()
                .endpoint("https://testowa-baza.documents.azure.com:443/")
                .key("V1fRypuHbr30JM9wQDA9YXE75STFirVyqgzPO7zOSAI8p3jaWzGu5GFxxUoj1esDEkfOpBB41nS5JRcSQrqkSA")
                .preferredRegions(preferredRegions)
                .consistencyLevel(ConsistencyLevel.EVENTUAL)
                .contentResponseOnWriteEnabled(true)
                .buildClient();
        createDatabaseIfNotExists();
        createContainerIfNotExists();
    }

    private void createDatabaseIfNotExists() throws Exception {

        //  Create database if not exists
        //  <CreateDatabaseIfNotExists>
        CosmosDatabaseResponse databaseResponse = client.createDatabaseIfNotExists(databaseName);
        database = client.getDatabase(databaseResponse.getProperties().getId());

        //  </CreateDatabaseIfNotExists>

        System.out.println("Checking database " + database.getId() + " completed!\n");
    }

    private void createContainerIfNotExists() throws Exception {

        //  Create container if not exists
        //  <CreateContainerIfNotExists>
        CosmosContainerProperties containerProperties =
                new CosmosContainerProperties(containerName, "/id");

        //  Create container with 400 RU/s
        ThroughputProperties throughputProperties = ThroughputProperties.createManualThroughput(400);
        CosmosContainerResponse containerResponse = database.createContainerIfNotExists(containerProperties, throughputProperties);
        container = database.getContainer(containerResponse.getProperties().getId());
        //  </CreateContainerIfNotExists>

        System.out.println("Checking container " + container.getId() + " completed!\n");
    }

    public String addTourist(Tourist tourist) throws Exception {
        CosmosItemRequestOptions cosmosItemRequestOptions = new CosmosItemRequestOptions();
        CosmosItemResponse<Tourist> item = container.createItem(tourist, new PartitionKey(tourist.getId()), cosmosItemRequestOptions);
        return item.getItem().getId();
    }

    public void updateTourist(Tourist tourist) throws Exception {
        container.upsertItem(tourist);
    }

    public void deleteTouristById(String touristId) {
        container.deleteItem(touristId, new PartitionKey(touristId), new CosmosItemRequestOptions());
    }

    public Tourist getById(String id) {
        CosmosItemResponse<Tourist> item = container.readItem(id, new PartitionKey(id), Tourist.class);
        return item.getItem();
    }

    public List<Tourist> getBySurname(String surname) {
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        //queryOptions.setEnableCrossPartitionQuery(true); //No longer necessary in SDK v4
        //  Set populate query metrics to get metrics around query executions
        queryOptions.setQueryMetricsEnabled(true);
        CosmosPagedIterable<Tourist> touristCosmosPagedIterable = container.queryItems(
                "SELECT * FROM Tourist WHERE Tourist.surname="+"'"+surname+"'", queryOptions, Tourist.class);

        return StreamSupport.stream(touristCosmosPagedIterable.iterableByPage(50).spliterator(), false)
                .map(FeedResponse::getResults)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public Integer calculateAvgAge() {
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        //queryOptions.setEnableCrossPartitionQuery(true); //No longer necessary in SDK v4
        //  Set populate query metrics to get metrics around query executions
        CosmosPagedIterable<JsonNode> item = container.queryItems("SELECT AVG(t.age) FROM Tourist t", queryOptions, JsonNode.class);
        if (item.iterator().hasNext()) {
            JsonNode jsonnode = item.iterator().next();
            return jsonnode.get("$1").asInt();
        }
        return null;
    }
/*
    private void readItems(ArrayList<Family> familiesToCreate) {
        //  Using partition key for point read scenarios.
        //  This will help fast look up of items because of partition key
        familiesToCreate.forEach(family -> {
            //  <ReadItem>
            try {
                CosmosItemResponse<Family> item = container.readItem(family.getId(), new PartitionKey(family.getLastName()), Family.class);
                double requestCharge = item.getRequestCharge();
                Duration requestLatency = item.getDuration();
                logger.info(String.format("Item successfully read with id %s with a charge of %.2f and within duration %s",
                        item.getItem().getId(), requestCharge, requestLatency));
            } catch (CosmosException e) {
                e.printStackTrace();
                logger.info(String.format("Read Item failed with %s", e));
            }
            //  </ReadItem>
        });
    }

    private void replaceItems(ArrayList<Family> familiesToCreate) {
        familiesToCreate.forEach(family -> {
            //  <ReadItem>
            try {
                String district = family.getDistrict();
                family.setDistrict(district + "_newDistrict");
                CosmosItemResponse<Family> item = container.replaceItem(family, family.getId(),
                        new PartitionKey(family.getLastName()), new CosmosItemRequestOptions());
                double requestCharge = item.getRequestCharge();
                Duration requestLatency = item.getDuration();
                logger.info("Item successfully replaced with id: {}, district: {}, charge: {}, duration: {}",
                        item.getItem().getId(), item.getItem().getDistrict(), requestCharge, requestLatency);
            } catch (CosmosException e) {
                logger.error(String.format("Replace Item failed with %s", e));
            }
            //  </ReadItem>
        });
    }

    private void queryItems() {
        //  <QueryItems>

        // Set some common query options
        int preferredPageSize = 10;
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        //queryOptions.setEnableCrossPartitionQuery(true); //No longer necessary in SDK v4
        //  Set populate query metrics to get metrics around query executions
        queryOptions.setQueryMetricsEnabled(true);

        CosmosPagedIterable<Family> familiesPagedIterable = container.queryItems(
                "SELECT * FROM Family WHERE Family.lastName IN ('Andersen', 'Wakefield', 'Johnson')", queryOptions, Family.class);

        familiesPagedIterable.iterableByPage(preferredPageSize).forEach(cosmosItemPropertiesFeedResponse -> {
            logger.info("Got a page of query result with " +
                    cosmosItemPropertiesFeedResponse.getResults().size() + " items(s)"
                    + " and request charge of " + cosmosItemPropertiesFeedResponse.getRequestCharge());

            logger.info("Item Ids " + cosmosItemPropertiesFeedResponse
                    .getResults()
                    .stream()
                    .map(Family::getId)
                    .collect(Collectors.toList()));
        });
        //  </QueryItems>
    }

    private void deleteItem(Family item) {
        container.deleteItem(item.getId(), new PartitionKey(item.getLastName()), new CosmosItemRequestOptions());
    }

    private void shutdown() {
        try {
        *//*    //Clean shutdown
            logger.info("Deleting Cosmos DB resources");
            logger.info("-Deleting container...");
            if (container != null)
                container.delete();
            logger.info("-Deleting database...");
            if (database != null)
                database.delete();
            logger.info("-Closing the client...");*//*
        } catch (Exception err) {
            logger.error("Deleting Cosmos DB resources failed, will still attempt to close the client. See stack trace below.");
            err.printStackTrace();
        }
        client.close();
        logger.info("Done.");
    }*/
}
