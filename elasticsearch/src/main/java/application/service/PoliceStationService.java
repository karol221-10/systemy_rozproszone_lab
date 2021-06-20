package application.service;

import application.model.PoliceStation;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PoliceStationService extends GenericService<PoliceStation> {

    public PoliceStationService(RestHighLevelClient session) {
        super(session);
    }

    @Override
    Class<PoliceStation> getEntityType() {
        return PoliceStation.class;
    }

    public List<PoliceStation> getAllPoliceStationsFromTown(String townName) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders
                .matchQuery("city", townName));
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

    public Map<String, Long> countPolicemansByCity() throws IOException {
        var result = StreamSupport.stream(this.readAll().spliterator(), false)
                .collect(Collectors.groupingBy(PoliceStation::getCity));
        return result.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                    .map(PoliceStation::getPolicemans)
                    .count()));
    }
}
