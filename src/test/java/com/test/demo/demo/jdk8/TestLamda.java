package com.test.demo.demo.jdk8;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import com.test.demo.demo.vo.StudentVO;


public class TestLamda {
	
	@Test
	public void testListRemoveElement() {
		List<String> list = new ArrayList<String>();
		list.add("a");
		list.add("b");
		list.add("c");
		list.add("d");
		list.add("e");
		list.removeIf(word -> word.equals("c"));
		
		list.forEach(System.out::println);
		
	}
	
	/**
	 * (x,y) -> {}
	 *	左侧是一个小括号，里面是要实现的抽象方法的参数，有几个参数就写几个参数名，无参可写空括号，无需声明参数类型；
	 */
	@Test
	public void testAbstractLamda() {
		//method1
		Comparable<Integer> c1 = new Comparable<Integer>() {
			@Override
			public int compareTo(Integer i) {
				return Integer.compare(i, 100);
			}
		};
		
		c1.compareTo(2);
		//method2
		Comparable<Integer> c2 = (x) -> Integer.compare(x, 100);
		c2.compareTo(2);
	}
	
	/**
	 * 
	 */
	@Test
	public void testLamda() {
		Consumer<String> consumer = (x) -> System.out.println(x);
		consumer.accept("hehe");
		
		Consumer<String> consumer1 = System.out::println;
		consumer1.accept("hehe::");
		
		Supplier<String> supplier = () -> "Hello Jack";
		supplier.get();
		
		Supplier<StudentVO> supplier1 = () -> new StudentVO();
		Supplier<StudentVO> supplier2 = StudentVO::new;
		
		//String入参类型, Integer返回值类型
		Function<String, Integer> function = (x) -> x.length();
		function.apply("length");
		
		//lambda表达式常用方式
		BiFunction<String, String, StudentVO> function1 = (x, y) -> new StudentVO(x, y);
		//构造器引用:通过类型推断，引用有两个String参数的构造器
		BiFunction<String, String, StudentVO> function2 = StudentVO::new;
		
		Predicate<Integer> predicate = (x) -> x > 0;
		predicate.test(3);
		
		BiPredicate<String, String> bp1 = (x, y) -> x.equals(y);
		//方法引用：类::实例方法（方法传入参数是两个参数，且第一个参数作为方法调用对象，第二个参数作为调用的方法的参数）
		BiPredicate<String, String> bp2 = String::equals;
		bp2.test("1", "2");
	}
	
	@Test
	public void test1() {
		Consumer<String> con1 = (x) -> System.out.println(x);
		con1 = System.out::println;
		con1.accept("consumer");
		
		Supplier<String> sup1 = () -> "supplier";
		sup1 = String::new;
		String supString = sup1.get();
		System.out.println(supString);
		
		Function<String, Integer> fun1 = (x) -> x.length();
		fun1.apply("fun1");
		
		Function<String, String> fun2 = String::new;//new String(str);
		fun2.apply("fun2");
		
		Predicate<String> pre1 = (x) -> x.length() > 0;
		pre1.test("pre1");
		
		
	}
	
	@Test
	public void testStream() {
		String[] strings = {"1", "2", "3", "4"};
		Stream<String> stream = Stream.of(strings);
	}
}
