package com.example.playground.mapstruct;

import org.junit.Test;

public class MapStructTest {
	
	@Test
	public void test() {
		// check other frameworks:
		// https://www.baeldung.com/java-performance-mapping-frameworks
		
		Car car = new Car();
		car.setName("car-name");
		car.setNumberOfSeats(42);
		
		CarDto carDto = CarMapper.INSTANCE.carToCarDto(car);
		
		System.out.printf("Car: %s%n", car);
		System.out.printf("CarDto: %s%n", carDto);
	}

}
