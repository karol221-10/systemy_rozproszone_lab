package application.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

abstract class GenericService<T> implements Service<T> {

    protected RestHighLevelClient session;
    protected ObjectMapper objectMapper = new ObjectMapper();

    public GenericService(RestHighLevelClient session){
        this.session = session;
    }

    @Override
    public T read(String id) throws IOException {
        GetRequest getRequest = new GetRequest(getEntityType().getSimpleName().toLowerCase(Locale.ROOT));
        getRequest.id(id);

        GetResponse getResponse = session.get(getRequest, RequestOptions.DEFAULT);
        var response = getResponse.getSourceAsString();
        if(response == null) {
            return null;
        }
        return objectMapper.readValue(response, getEntityType());
    }

    @Override
    public Iterable<T> readAll() throws IOException {
        SearchRequest searchRequest = new SearchRequest(getEntityType().getSimpleName().toLowerCase(Locale.ROOT));
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        SearchResponse getResponse = session.search(searchRequest, RequestOptions.DEFAULT);
        return Arrays.stream(getResponse.getHits().getHits())
                .map(hit -> {
                    try {
                        return objectMapper.readValue(hit.getSourceAsString(), getEntityType());
                    } catch (JsonProcessingException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest(getEntityType().getSimpleName().toLowerCase(Locale.ROOT));
        deleteRequest.id(id);
        DeleteResponse deleteResponse = session.delete(deleteRequest, RequestOptions.DEFAULT);
    }

    @Override
    public void deleteAll() {
       /* session.deleteAll(getEntityType());*/
    }

    @Override
    public void createOrUpdate(T entity, String id) throws IOException {
        var stringObjectValue = objectMapper.writeValueAsString(entity);
        IndexRequest indexRequest = new IndexRequest(getEntityType().getSimpleName().toLowerCase(Locale.ROOT));
        indexRequest.id(id);
        var request = indexRequest.source(stringObjectValue, XContentType.JSON);
        IndexResponse response = session.index(request, RequestOptions.DEFAULT);
        System.out.println("Created object with id: " + response.getId());
    }

    abstract Class<T> getEntityType();
}