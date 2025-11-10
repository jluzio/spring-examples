package com.example.spring.modulith.pet.internal;

import com.example.spring.modulith.pet.Pet;
import com.example.spring.modulith.pet.PetService;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
class DefaultPetService implements PetService {

  private final Map<String, Pet> pets = Stream.of(
      new Pet("1", "Gojira", "Quite big", null),
      new Pet("2", "Mr Miffles", "Very fightning", null),
      new Pet("3", "Jigglefuf", "Very fightning", null)
  ).collect(Collectors.toMap(
      Pet::id, Function.identity(), (pet, pet2) -> pet, LinkedHashMap::new));

  @Override
  public Collection<Pet> findAll() {
    return pets.values();
  }

  @Override
  public Optional<Pet> findById(String id) {
    return Optional.ofNullable(pets.get(id));
  }

  @Override
  public void update(Pet pet) {
    pets.put(pet.id(), pet);
  }
}
