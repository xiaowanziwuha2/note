package com.test.demo.demo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.ClassPathResource;

public class ExcelTemplateUtils {
	public static byte[] process(Object data, String templatePath) throws Exception {
		if (data == null || StringUtils.isEmpty(templatePath)) {
			return null;
		}
		ClassPathResource resource = new ClassPathResource(templatePath);
		InputStream inputStream = resource.getInputStream();
		Workbook wb = WorkbookFactory.create(inputStream);

		/*
		 * Iterator<Sheet> iterable = wb.sheetIterator(); while (iterable.hasNext()) {
		 * processSheet(data, iterable.next()); }
		 */
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		wb.write(out);
		return out.toByteArray();
	}

	private static void processSheet(Object data, Sheet sheet) throws Exception {
		Map<Integer, Map<Integer, Cell>> listRecord = new LinkedHashMap<>();
		int lastRowNum = sheet.getLastRowNum();
		for (int i = lastRowNum; i >= 0; i--) {
			Row row = sheet.getRow(i);
			if (row == null) {
				continue;
			}
			int lastCellNum = row.getLastCellNum();
			for (int j = 0; j < lastCellNum; j++) {
				Cell cell = row.getCell(j);
				if (cell == null) {
					continue;
				}
				try {
					String cellValue = cell.getStringCellValue();
					if (cellValue.matches(".*\\$\\{[\\w.()]+}.*")) {// ${cls.headmaster}
						fillCell(cell, cellValue, data);
					} else if (cellValue.matches(".*\\$\\{[\\w.]+\\[#][\\w.]+}.*")) {// ${cls.students[#].name}
						Map<Integer, Cell> rowRecord = listRecord.computeIfAbsent(i, k -> new HashMap<>());
						rowRecord.put(j, cell);
					}
				} catch (Exception e) {
					System.out.println("Error Log...");
				}
			}
		}

		Map<String, List> listInData = new HashMap<>();
		Map<String, CellStyle> listCellStyle = new HashMap<>();
		Map<Cell, String> listCellPath = new HashMap<>();
		listRecord.forEach((rowNum, colMap) -> {
			Pattern p = Pattern.compile("\\$\\{[\\w.\\[#\\]]+}");
			Set<String> listPath = new HashSet<>();
			colMap.forEach((colNum, cell) -> {
				String cellValue = cell.getStringCellValue();
				Matcher m = p.matcher(cellValue);
				if (m.find()) {
					String reg = m.group();
					String regPre = reg.substring(2, reg.indexOf("["));
					String regSuf = reg.substring(reg.lastIndexOf("].") + 2, reg.length() - 1);
					listPath.add(regPre);
					listCellStyle.put(String.format("%s.%s", regPre, regSuf), cell.getCellStyle());
					listCellPath.put(cell, String.format("%s#%s", regPre, regSuf));
				}
			});

			int maxRow = 0;
			for (String path : listPath) {
				Object list = null;
				try {
					list = getAttributeByPath(data, path);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new RuntimeException(e);
				}

				if (list == null) {
					list = new ArrayList<>();
				}

				if (list instanceof List) {
					int len = ((List) list).size();
					maxRow = maxRow > len ? maxRow : len;
					listInData.put(path, ((List) list));
				} else {
					throw new IllegalArgumentException(
							String.format("%s is not a list but a %s", path, list.getClass().getSimpleName()));
				}
			}
			if (maxRow > 1) {
				int endRow = sheet.getLastRowNum();
				sheet.shiftRows(rowNum + 1, endRow + 1, maxRow - 1);
			}
		});

		listRecord.forEach((rowNum, colMap) -> {
			colMap.forEach((colNum, cell) -> {
				String path = listCellPath.get(cell);
				String[] pathData = path.split("#");
				List list = listInData.get(pathData[0]);
				int baseRowIndex = cell.getRowIndex();
				int colIndex = cell.getColumnIndex();
				CellStyle style = listCellStyle.get(String.format("%s.%s", pathData[0], pathData[1]));
				for (int i = 0; i < list.size(); i++) {
					int rowIndex = baseRowIndex + i;
					Row row = sheet.getRow(rowIndex);
					if (row == null) {
						row = sheet.createRow(rowIndex);
					}
					Cell cellToFill = row.getCell(colIndex);
					if (cellToFill == null) {
						cellToFill = row.createCell(colIndex);
					}
					cellToFill.setCellStyle(style);
					try {
						setCellValue(cellToFill, getAttribute(list.get(i), pathData[1]));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			});
		});
	}

	private static void fillCell(Cell cell, String expression, Object data)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Pattern p = Pattern.compile("\\$\\{[\\w.\\[\\]()]+}");
		Matcher m = p.matcher(expression);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String exp = m.group();
			String path = exp.substring(2, exp.length() - 1);
			Object value = getAttributeByPath(data, path);
			m.appendReplacement(sb, value == null ? "" : value.toString());
		}
		setCellValue(cell, sb.toString());
	}

	private static void setCellValue(Cell cell, Object value) {
		if (value == null) {
			cell.setCellValue("");
		} else if (value instanceof Date) {
			cell.setCellValue((Date) value);
		} else if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Long) {
			cell.setCellValue((Long) value);
		} else if (value instanceof Double) {
			cell.setCellValue((Double) value);
		} else if (value instanceof Float) {
			cell.setCellValue((Float) value);
		} else if (value instanceof Character) {
			cell.setCellValue((Character) value);
		} else if (value instanceof BigDecimal) {
			cell.setCellValue(((BigDecimal) value).doubleValue());
		} else {
			cell.setCellValue(value.toString());
		}
	}

	private static Object getAttributeByPath(Object obj, String path)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		String[] paths = path.split("\\.");
		Object o = obj;
		for (String s : paths) {
			o = PropertyUtils.getNestedProperty(o, s);
		}
		return o;
	}

	private static Object getAttribute(Object obj, String member)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return PropertyUtils.getNestedProperty(obj, member);
	}

}
