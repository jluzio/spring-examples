package com.example.spring.modulith.adoption;

import com.example.spring.modulith.pet.Pet;
import com.example.spring.modulith.pet.PetService;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class AdoptionService {

  private final PetService petService;
  private final ApplicationEventPublisher eventPublisher;

  public Collection<Pet> availablePets() {
    return petService.findAll();
  }

  public void adopt(String petId, String owner) {
    petService.findById(petId).ifPresent(pet -> {
      var updatedPet = pet.toBuilder()
          .owner(owner)
          .build();
      petService.update(updatedPet);
      eventPublisher.publishEvent(new PetAdoptedEvent(updatedPet.id()));
    });
  }

}
