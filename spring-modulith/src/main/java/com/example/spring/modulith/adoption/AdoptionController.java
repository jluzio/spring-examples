package com.example.spring.modulith.adoption;

import com.example.spring.modulith.pet.Pet;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pets")
@RequiredArgsConstructor
class AdoptionController {

  private final AdoptionService adoptionService;

  @GetMapping
  Collection<Pet> availablePets() {
    return adoptionService.availablePets();
  }

  @PostMapping("/{petId}/adopt")
  void adopt(@PathVariable String petId, @RequestParam String owner) {
    adoptionService.adopt(petId, owner);
  }

}
