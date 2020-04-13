package com.test.demo.demo.jdk8collection;

import java.util.ArrayList;
import java.util.List;

class TestJDK8Collection {
	public static void main(String[] args) {
		testRemoveElement();
	}
	
	public static void testRemoveElement() {
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");
		list.add("e");
		list.removeIf(word -> word.equals("c"));
		
		list.forEach(System.out::println);
	}
}
