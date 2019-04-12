package com.wei.elaslaticsearch.controller;

import com.wei.elaslaticsearch.bo.Person;
import com.wei.elaslaticsearch.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController

@RequestMapping("es")
public class PersonController {

    @Autowired
    private PersonRepository personRepository;

    @RequestMapping("add")
    public Object add() {
        Person person = new Person();
        String id = UUID.randomUUID() + "";
        person.setId(id);
        person.setAge(23);
        person.setIsStudent(false);
        person.setName("hu");
        personRepository.save(person);
        return "success";
    }

    @RequestMapping("delete/{id}")
    public Object delete(@PathVariable String id) {
        Person person = new Person();
        person.setId(id);
        personRepository.delete(person);
        return "success";
    }

    @RequestMapping("get/{id}")
    public Object get(@PathVariable String id) {
        Person person = new Person();
        person.setId(id);
        Person personIndex = personRepository.findById(id).orElse(null);
        return personIndex;
    }

    @RequestMapping("update")
    public Object update(@RequestBody Person person) {
        Person personIndex = personRepository.save(person);
        return personIndex;
    }

}
