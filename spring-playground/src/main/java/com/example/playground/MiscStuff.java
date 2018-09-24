package com.example.playground;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.Lists;

public class MiscStuff {
	
	public static void main(String[] args) {
		List<Integer> source = Arrays.asList(1, 2, 3);
		Stream<Integer> filter = source.stream().filter(n -> n >= 2);
		System.out.println(Lists.newArrayList(filter.toArray()));
	}

}
