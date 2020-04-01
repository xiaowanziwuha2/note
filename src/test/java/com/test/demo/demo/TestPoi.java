package com.test.demo.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TestPoi {

	@Test
	void test1() throws Exception {
		Map<String, Object> data = new HashMap<>();
        Map<String, Object> cls = new HashMap<>();
        data.put("cls", cls);
        cls.put("headmaster", "李景文");
        cls.put("type", "文科班");
        List<Stu> stus = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Stu stu = new Stu();
            stu.setCode("code" + i);
            stu.setName("name" + i);
            stus.add(stu);
        }
        cls.put("students", stus);

        String templatePath = "/templates/excel/test.xlsx";
        
        String resultFileName = File.createTempFile("test1", ".xlsx").getAbsolutePath();
		System.out.println(resultFileName);
		FileOutputStream fos = new FileOutputStream(new File(resultFileName));
		
        //根据模板 templatePath 和数据 data 生成 excel 文件，写入 fos 流
        ExcelTemplateUtils.process(data, templatePath, fos);
	}
}
