package com.example.playground.mapstruct;

public class Car {
	private String name;
	private Integer numberOfSeats;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNumberOfSeats() {
		return numberOfSeats;
	}

	public void setNumberOfSeats(Integer numberOfSeats) {
		this.numberOfSeats = numberOfSeats;
	}

	@Override
	public String toString() {
		return "Car [name=" + name + ", numberOfSeats=" + numberOfSeats + "]";
	}

}
