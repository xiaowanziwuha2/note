package com.test.demo.demo;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.test.demo.demo.excel.ExcelTemplate;

@SpringBootTest
class TestPoi {

	@Test
	void ExcelTemplate_test1() throws IOException {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource resource = resolver.getResource("/templates/excel/Disposal_Report_ExcelTemplate.xls");
		
		// 加载模板表格
		ExcelTemplate excel = new ExcelTemplate(resource.getInputStream());
		
		// 验证是否通过
		if (!excel.examine())
			return;
		try {
			// 第一个参数，需要操作的sheet的索引
	        // 第二个参数，需要复制的区域的第一行索引
	        // 第三个参数，需要复制的区域的最后一行索引
	        // 第四个参数，需要插入的位置的索引
	        // 第五个参数，填充行区域中${}的值
	        // 第六个参数，是否需要删除原来的区域
	        // 需要注意的是，行的索引一般要减一
			LinkedHashMap<Integer, LinkedList<String>> rows = ExcelTemplate_test1_getData();
			excel.addRowByExist(0, 5, 5, 6, rows, true);
			// 保存到指定路径
			String resultFileName = File.createTempFile("ExcelTemplate_test1", ".xls").getAbsolutePath();
			System.out.println(resultFileName);
			excel.save(resultFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private LinkedHashMap<Integer, LinkedList<String>> ExcelTemplate_test1_getData() {
		// 使用一个Map来存储所有的行区域，
        // 每个行区域对应Map的一个键
        LinkedHashMap<Integer, LinkedList<String>> rows = new LinkedHashMap<>();
        // 创建第一个行区域里面填充的值，ExcelTemplate会按从左至右，
        // 从上往下的顺序，挨个填充区域里面的${}，所以创建的时候注意顺序就好
        LinkedList<String> row1 = new LinkedList<>();
        row1.add("1");
        row1.add("123");
        row1.add("张三");
        row1.add("2019/9/10");
        row1.add("2019/9/10");
        row1.add("2019/9/10");
        row1.add("5");
        row1.add("项目上线");
        // 把第一个行区域row1添加进入rows
        rows.put(1,row1);
        // 创建第二个行区域里面填充的值
        LinkedList<String> row2 = new LinkedList<>();
        row2.add("2");
        row2.add("1234");
        row2.add("李四");
        row2.add("2019/9/11");
        row2.add("2019/9/11");
        row2.add("2019/9/11");
        row2.add("6");
        row2.add("临时突发状况");
        // 把第二个行区域row2添加进入rows
        rows.put(2,row2);

        // 创建需要填充替换的值
		/*
		 * Map<String,String> fill = new HashMap<>(); fill.put("总加班时长","11");
		 * fill.put("公司名称","xxxx有限公司"); fill.put("创建人","王麻子");
		 * fill.put("日期","2019-9-12");
		 */
        
		return rows;
	}
}
