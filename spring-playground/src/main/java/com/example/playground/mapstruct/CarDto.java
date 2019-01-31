package com.example.playground.mapstruct;

public class CarDto {
	private String name;
	private Integer seatCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSeatCount() {
		return seatCount;
	}

	public void setSeatCount(Integer seatCount) {
		this.seatCount = seatCount;
	}

	@Override
	public String toString() {
		return "Car [name=" + name + ", seatCount=" + seatCount + "]";
	}

}
