package com.wei.elaslaticsearch.repository;

import com.wei.elaslaticsearch.bo.Person;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

@Component
public interface PersonRepository extends ElasticsearchRepository<Person,String> {
    Person queryAllBy(String id);
}
