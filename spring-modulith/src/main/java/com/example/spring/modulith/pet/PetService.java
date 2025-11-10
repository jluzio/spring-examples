package com.example.spring.modulith.pet;

import java.util.Collection;
import java.util.Optional;

public interface PetService {

  Collection<Pet> findAll();

  Optional<Pet> findById(String id);

  void update(Pet pet);

}
