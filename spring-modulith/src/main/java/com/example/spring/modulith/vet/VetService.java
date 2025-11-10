package com.example.spring.modulith.vet;

import com.example.spring.modulith.adoption.PetAdoptedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
class VetService {

  @EventListener
  void checkup(PetAdoptedEvent petAdoptedEvent) {
    IO.println("Checkup: %s".formatted(petAdoptedEvent));
  }

}
