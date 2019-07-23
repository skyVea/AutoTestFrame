package com.autotest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.autotest.annotation.Column;
import com.autotest.enumeration.TestResultEnum;
import com.autotest.model.BaseTestPoint;
import com.autotest.model.KVObject;

/**
 * Excel工具，可按对象操作。用例对象需要加注解{@link Column}
 * 
 * @author veaZhao
 *
 */
public class ExcelUtil {
	private String pathname;
	private Workbook WORKBOOK = null;
	// private Map<String, Class<? extends BaseTestCase>> VOCLAZZ = new
	// HashMap<String, Class<? extends BaseTestCase>>();// SHEET对应VOCLAZZ
	private Map<String, Map<Integer, String>> RELATIONMAP = new HashMap<String, Map<Integer, String>>();// SHEET对应列头关系表

	public ExcelUtil(String pathname) {
		this.pathname = pathname;
		initWorkBook();
		initColumnHeadMapping();
	}

	public synchronized Map<String, List> read() throws IOException {
		Map<String, List> workBookMap = new LinkedHashMap<String, List>();
		for (int i = 0; i < WORKBOOK.getNumberOfSheets(); i++) {
			Sheet sheet = WORKBOOK.getSheetAt(i);
			ArrayList<Map<Integer, String>> excelList = new ArrayList<Map<Integer, String>>();
			workBookMap.put(sheet.getSheetName(), excelList);
			for (int j = 0; j < sheet.getPhysicalNumberOfRows() - 1; j++) {
				Map<Integer, String> sheetMap = new LinkedHashMap<Integer, String>();
				for (Cell cell : sheet.getRow(j)) {
					sheetMap.put(Integer.valueOf(cell.getColumnIndex()), cell.getStringCellValue());
				}
				excelList.add(sheetMap);
			}
		}
		return workBookMap;
	}

	public synchronized Map<String, List> read(String... sheetName) throws IOException {
		Map<String, List> workBookMap = new LinkedHashMap<String, List>();
		for (int i = 0; i < WORKBOOK.getNumberOfSheets(); i++) {
			Sheet sheet = WORKBOOK.getSheetAt(i);
			for (int k = 0; k < sheetName.length; k++) {
				if (sheetName[k].equals(sheet.getSheetName())) {
					ArrayList<Map<Integer, String>> excelList = new ArrayList<Map<Integer, String>>();
					workBookMap.put(sheet.getSheetName(), excelList);
					for (int j = 0; j < sheet.getPhysicalNumberOfRows(); j++) {
						Map<Integer, String> sheetMap = new LinkedHashMap<Integer, String>();
						for (Cell cell : sheet.getRow(j)) {
							if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
								sheetMap.put(Integer.valueOf(cell.getColumnIndex()),
										String.valueOf(cell.getNumericCellValue()));
							} else {
								sheetMap.put(Integer.valueOf(cell.getColumnIndex()), cell.getStringCellValue());
							}
						}
						excelList.add(sheetMap);
					}
				}
			}
		}
		return workBookMap;
	}

	public synchronized void write(Map<String, List<Map<Integer, String>>> sheetMap, String targetpathname)
			throws IOException {
		for (int k = 0; k < sheetMap.size(); k++) {
			Sheet sheet = WORKBOOK.getSheetAt(k);
			List<Map<Integer, String>> rowList = sheetMap.get(sheet.getSheetName());
			for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
				Map<Integer, String> rowMap = (Map<Integer, String>) rowList.get(i);
				for (int j = 0; j < sheet.getRow(i).getPhysicalNumberOfCells(); j++) {
					sheet.getRow(i).getCell(j).setCellValue(rowMap.get(j));
				}
			}
		}
		commit(targetpathname);
	}

	public synchronized void write(String sheetName, ArrayList<Map<Integer, String>> rowList, String targetpathname)
			throws IOException {
		Sheet sheet = WORKBOOK.getSheet(sheetName);
		for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
			Map<Integer, String> rowMap = rowList.get(i);
			for (int j = 0; j < sheet.getRow(i).getPhysicalNumberOfCells(); j++) {
				sheet.getRow(i).getCell(j).setCellValue(rowMap.get(j));
			}
		}
		commit(targetpathname);
	}

	public synchronized void writeOneRow(String sheetName, Integer columnIndex, Integer rowIndex, String Cellvalue,
			boolean commit, String targetpathname) throws IOException {
		if (columnIndex == null || StringUtils.isEmpty(this.pathname) || StringUtils.isEmpty(sheetName)
				|| rowIndex == null) {
			TestLog.getInstance(this.getClass()).info("PathName或SheeName或ColumnIdex或RowIndex为空");
			return;
		}
		Sheet sheet = WORKBOOK.getSheet(sheetName);
		sheet.getRow(rowIndex).getCell(columnIndex).setCellValue(Cellvalue);
		if (commit)
			commit(targetpathname);
	}

	/**
	 * @param pathName
	 * @param sheetName
	 * @param primaryObj
	 *            Key-主键列名，Value-主键列下指定行Cell的值（唯一值）
	 * @param TargetObj
	 *            Key-目标列名，Value-目标列下指定行Cell的值
	 * @param Cellvalue
	 * @throws IOException
	 */
	public synchronized void writeOneRow(String sheetName, KVObject<String, String> primaryObj,
			KVObject<String, String> TargetObj, String targetpathname) throws IOException {
		Integer rowIndex = getRowIndexByName(sheetName, primaryObj);
		Integer columnIndex = getColumnIndexByName(sheetName, TargetObj.getKey());
		writeOneRow(sheetName, columnIndex, rowIndex, TargetObj.getValue(), true, targetpathname);
	}

	/**
	 * @param pathName
	 * @param sheetName
	 * @param primaryObj
	 *            Key-主键列名，Value-主键列下指定行Cell的值（唯一值）
	 * @param TargetObj
	 *            Key-目标列名，Value-目标列下指定行Cell的值
	 * @param Cellvalue
	 * @param Boolean
	 *            是否马上提交保存
	 * @throws IOException
	 */
	public synchronized void writeOneRow(String sheetName, KVObject<String, String> primaryObj,
			KVObject<String, String> TargetObj, boolean commit, String targetpathname) throws IOException {
		Integer rowIndex = getRowIndexByName(sheetName, primaryObj);
		Integer columnIndex = getColumnIndexByName(sheetName, TargetObj.getKey());
		writeOneRow(sheetName, columnIndex, rowIndex, TargetObj.getValue(), commit, targetpathname);
	}

	/**
	 * @param pathName
	 *            路径名称
	 * @param sheetName
	 *            表名
	 * @param columnName
	 *            列名
	 * @return
	 */
	public Integer getColumnIndexByName(String sheetname, String columnname) {
		Sheet sheet = WORKBOOK.getSheet(sheetname);
		if (sheet == null) {
			return null;
		}
		Row columnRow = sheet.getRow(0);
		if (columnRow == null)
			return null;
		for (int i = 0; i < sheet.getRow(0).getPhysicalNumberOfCells(); i++) {
			Cell cell = columnRow.getCell(i);
			if (cell == null)
				continue;
			String cellValue = cell.getStringCellValue();
			if (StringUtils.isEmpty(cellValue))
				continue;
			if (columnname.equals(cellValue)) {
				return cell.getColumnIndex();
			}
		}
		return null;
	}

	/**
	 * @param pathName
	 * @param columnObj
	 *            Key-目标列名，Value-目标列下指定行Cell的值（唯一值）
	 * @return
	 */
	public Integer getRowIndexByName(String sheetName, KVObject<String, String> columnObj) {
		Sheet sheet = WORKBOOK.getSheet(sheetName);
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			Row row = sheet.getRow(i);
			if (row == null)
				continue;
			Integer columnIndex = getColumnIndexByName(sheetName, columnObj.getKey());
			Cell cell = row.getCell(columnIndex);
			if (cell == null)
				continue;
			String value = cell.getStringCellValue();
			if (StringUtils.isEmpty(value))
				continue;
			if (value.equals(columnObj.getValue())) {
				return cell.getRowIndex();
			}
		}
		return null;
	}

	// 初始化WorkBook，需要指定每个Sheet的主键
	private void initWorkBook() {
		try {
			InputStream is = new FileInputStream(this.pathname);
			if (pathname.endsWith(".xls")) {
				this.WORKBOOK = new HSSFWorkbook(is);
			} else if (pathname.endsWith(".xlsx")) {
				this.WORKBOOK = new XSSFWorkbook(is);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param sheetname
	 * @param clazz
	 *            对象类型
	 * @return 读取多条对象
	 */
	public <T> List<T> readAllObjs(String sheetname, Class<T> clazz) {
		Sheet sheet = WORKBOOK.getSheet(sheetname);
		if (sheet == null) {
			TestLog.getInstance(this.getClass()).info(this.pathname + "没有这个Sheet:" + sheetname);
			return null;
		}
		List<T> list = new ArrayList<T>();
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			T object = null;
			try {
				object = clazz.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			Field[] fields = clazz.getDeclaredFields();
			for (int j = 0; j < fields.length; j++) {
				Integer columnindex = null;
				Column column = fields[j].getAnnotation(Column.class);
				if (column == null) {
					continue;
				}
				String name = column.columName();
				if (name == null) {
					continue;
				}
				for (Entry<Integer, String> rEntry : RELATIONMAP.get(sheetname).entrySet()) {
					if (name.equals(rEntry.getValue())) {
						columnindex = rEntry.getKey();
						break;
					}
				}
				Object cellvalue = getCellValue(sheet.getRow(i).getCell(columnindex));
				// if (column.isPrimary() && StringUtils.isEmpty(value)) {
				// throw new RuntimeException("primaryvalue为空，请设置表格内主键值");
				// }
				if (sheet.getRow(i).getCell(columnindex).getCellType() == CellType.BLANK.getCode()
						&& column.isPrimary()) {
					throw new RuntimeException("primaryvalue为空，请设置表格内主键值");
				}

				try {
					fields[j].setAccessible(true);
					if (cellvalue.getClass() == fields[j].getType() && StringUtils.isJavaClass(fields[j].getType())) {
						fields[j].set(object, cellvalue);
					} else {
						// 不是String类型的需要具体解析
						if (name.equals("参数") && (fields[j].getType() == BaseTestPoint.class)) {
							fields[j].set(object, splitStringToList(String.valueOf(cellvalue)));
						}
						if (name.equals("测试结果")) {
							// fields[j].set(object, TestResultEnum.getValue(String.valueOf(cellvalue)));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			list.add(object);
		}
		return list;
	}

	/**
	 * @param pathname
	 * @param sheetname
	 * @param primaryvalue
	 * @return 获取一条对象
	 */
	public <T> T readOneObj(String primaryvalue, String sheetname, Class<T> clazz) {
		if (StringUtils.isEmpty(primaryvalue)) {
			throw new RuntimeException("primaryvalue为空，请设置表格内主键值");
		}
		T object = null;
		Sheet sheet = WORKBOOK.getSheet(sheetname);
		try {
			object = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Field[] fields = clazz.getDeclaredFields();
		String pcolumnname = null;
		for (int j = 0; j < fields.length; j++) {
			Column column = fields[j].getAnnotation(Column.class);
			if (column == null) {
				continue;
			}
			pcolumnname = column.columName();
			if (!StringUtils.isEmpty(pcolumnname)) {
				break;
			}
		}
		Integer pcolumnindex = getPrimaryColumnIndex(sheet, pcolumnname);
		Integer rowindex = getPrimaryRowIndex(sheet, pcolumnindex, primaryvalue);

		for (int k = 0; k < fields.length; k++) {
			Integer columnindex = null;
			Column column = fields[k].getAnnotation(Column.class);
			if (column == null) {
				continue;
			}
			String columnname = column.columName();
			if (columnname == null) {
				continue;
			}
			for (Entry<Integer, String> rEntry : RELATIONMAP.get(sheetname).entrySet()) {
				if (columnname.equals(rEntry.getValue())) {
					columnindex = rEntry.getKey();
					break;
				}
			}

			Object cellvalue = getCellValue(sheet.getRow(rowindex).getCell(columnindex));
			if (sheet.getRow(rowindex).getCell(columnindex).getCellType() == CellType.BLANK.getCode()) {
				throw new RuntimeException("primaryvalue为空，请设置表格内主键值");
			}
			try {
				if (cellvalue.getClass() == fields[k].getType() && StringUtils.isJavaClass(clazz)) {
					fields[k].set(object, cellvalue);
				} else {
					// 不是String类型的需要具体解析
					if (columnname.equals("参数") && (fields[k].getType() == BaseTestPoint.class)) {
						fields[k].set(object, splitStringToList(String.valueOf(cellvalue)));
					}
					if (columnname.equals("测试结果")) {
						// fields[k].set(object, TestResultEnum.getValue(String.valueOf(cellvalue)));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return object;
	}

	/**
	 * @param sheet
	 * @param objects
	 */
	public void writeAllObjs(String sheetname, List<Object> objects, String targetpathname) {
		for (int i = 0; i < objects.size(); i++) {
			writeOneObj0(sheetname, objects.get(i), false, targetpathname);
		}
		commit(targetpathname);
	}

	// 保存某个Cell

	/**
	 * 保存一个对象到表
	 * 
	 * @param sheet
	 * @param object
	 */
	public void writeOneObj(String sheetname, Object object, String targetpathname) {
		writeOneObj0(sheetname, object, true, targetpathname);
	}

	/**
	 * 保存一个对象到表
	 * 
	 * @param pathname
	 * @param sheetname
	 * @param object
	 * @param isCommit
	 *            是否立即提交
	 */
	private void writeOneObj0(String sheetname, Object object, boolean isCommit, String targetpathname) {
		Sheet sheet = WORKBOOK.getSheet(sheetname);
		Integer pcolumnindex = null;
		Integer prowindex = null;
		KVObject<String, String> pKvObject = getPrimaryColumnKV(object);
		// 获取主键columnindex
		pcolumnindex = getPrimaryColumnIndex(sheet, pKvObject.getKey());

		// 获取主键rowindex
		prowindex = getPrimaryRowIndex(sheet, pcolumnindex, pKvObject.getValue());

		// 保存到CELL
		Field[] fields = object.getClass().getDeclaredFields();
		for (int j = 0; j < fields.length; j++) {
			Integer columnindex = null;
			Column column = fields[j].getAnnotation(Column.class);
			if (column == null || StringUtils.isEmpty(column.columName()) || column.isReadOnly()) {
				continue;
			}
			String name = column.columName();
			for (Entry<Integer, String> rEntry : RELATIONMAP.get(sheetname).entrySet()) {
				if (name.equals(rEntry.getValue())) {
					columnindex = rEntry.getKey();
					break;
				}
			}

			try {
				fields[j].setAccessible(true);
				Cell cell = sheet.getRow(prowindex).getCell(columnindex);
				Object cellvalue = getCellValue(cell);
				if (cellvalue.getClass() == fields[j].getType() && StringUtils.isJavaClass(fields[j].getType())) {
					setCellValue(cell, fields[j].get(object));
				} else {
					// 不是String类型的需要具体解析
					if (("参数").equals(name) && (fields[j].getType() == BaseTestPoint.class)) {

					}
					if (("测试结果").equals(name) && (fields[j].getType() == Integer.class)) {
						String result = TestResultEnum.getDesc((Integer) fields[j].get(object));
						cell.setCellValue(result);
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("保存CELL值失败" + e.getStackTrace());
			}
		}
		if (isCommit) {
			commit(targetpathname);
		}
	}

	/**
	 * 获取注解对象主键
	 * 
	 * @param object
	 *            注解对象
	 * @return KVObject 主键的key-列名 value-列对应的值
	 */
	private KVObject<String, String> getPrimaryColumnKV(Object object) {
		String primaryvalue = null;
		String columnname = null;
		Field[] fields = object.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Column column = fields[i].getAnnotation(Column.class);
			if (column.isPrimary()) {
				columnname = column.columName();
				try {
					fields[i].setAccessible(true);
					primaryvalue = String.valueOf(fields[i].get(object));
					if (StringUtils.isEmpty(primaryvalue)) {
						throw new RuntimeException("对象内主键值primaryvalue为空获取失败");
					}
					break;
				} catch (Exception e) {
					throw new RuntimeException("对象内主键值primaryvalue获取失败，请检查表格内主键是否有值，且不重复" + e.getStackTrace());
				}
			}
		}
		return new KVObject<String, String>(columnname, primaryvalue);
	}

	/**
	 * @param sheet
	 * @param primaryvalue
	 * @param clazz
	 * @return 获取主键值对应的行下标
	 */
	private Integer getPrimaryRowIndex(Sheet sheet, String primaryvalue, Class<?> clazz) {
		Field[] fields = clazz.getClass().getDeclaredFields();
		String columnname = null;
		for (int i = 0; i < fields.length; i++) {
			Column column = fields[i].getAnnotation(Column.class);
			if (column.isPrimary()) {
				columnname = column.columName();
				break;
			}
		}
		Integer columnindex = getPrimaryColumnIndex(sheet, columnname);
		return getPrimaryRowIndex(sheet, columnindex, primaryvalue);
	}

	/**
	 * @param sheet
	 * @param columnname
	 * @return 获取主键列下标
	 */
	private Integer getPrimaryColumnIndex(Sheet sheet, String columnname) {
		// 获取主键columnindex
		Integer pcolumnindex = null;
		Row columnrow = sheet.getRow(0);
		for (int j = 0; j < columnrow.getPhysicalNumberOfCells(); j++) {
			Cell cell = columnrow.getCell(j);
			String value = cell.getStringCellValue();
			if (!StringUtils.isEmpty(value)) {
				if (value.equals(columnname)) {
					pcolumnindex = cell.getColumnIndex();
				}
			}
		}
		return pcolumnindex;
	}

	/**
	 * @param sheet
	 * @param columnindex
	 * @param primaryvalue
	 * @return 获取主键行下标
	 */
	private Integer getPrimaryRowIndex(Sheet sheet, Integer columnindex, String primaryvalue) {
		Integer prowindex = null;
		for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
			Cell cell = sheet.getRow(i).getCell(columnindex);
			String value = cell.getStringCellValue();
			if (!StringUtils.isEmpty(value)) {
				if (value.equals(primaryvalue)) {
					prowindex = cell.getRowIndex();
				}
			}
		}
		return prowindex;
	}

	// 获取columnindex与columnname的关系
	private void initColumnHeadMapping() {
		for (int j = 0; j < WORKBOOK.getNumberOfSheets(); j++) {
			Sheet sheet = WORKBOOK.getSheetAt(j);
			Row row = sheet.getRow(0);
			if (row == null) {
				continue;
			}

			Map<Integer, String> sheetRelationMap = new HashMap<Integer, String>();
			if (!RELATIONMAP.containsKey(sheet.getSheetName())) {
				RELATIONMAP.put(sheet.getSheetName(), sheetRelationMap);
			}
			for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
				Cell cell = sheet.getRow(0).getCell(i);
				String value = cell.getStringCellValue();
				if (StringUtils.isEmpty(value)) {
					continue;
				}
				if (!sheetRelationMap.containsKey(i)) {
					sheetRelationMap.put(i, value);
				}
			}

		}
	}

	// 保存到表
	public synchronized void commit(String pathname) {
		try {
			WORKBOOK.write(new FileOutputStream(new File(pathname)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param sheetname
	 * @param clazz
	 * @param incrementPrimaryKey
	 *            自动生成主键值
	 */
	public void autoCompleByIncrement(String sheetname, Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Column column = fields[i].getAnnotation(Column.class);
			if (column == null || StringUtils.isEmpty(column.columName()) || !column.isIncrement()) {
				continue;
			}
			Integer columnindex = getColumnIndexByName(sheetname, column.columName());
			IDGenerator idGenerator = new IDGenerator();
			Sheet sheet = WORKBOOK.getSheet(sheetname);
			if (sheet == null) {
				return;
			}
			for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
				Cell cell = WORKBOOK.getSheet(sheetname).getRow(j).getCell(columnindex);
				if (cell != null && StringUtils.isEmpty(cell.getStringCellValue())) {
					cell.setCellValue(idGenerator.nextId());
				}
			}
		}
		commit(this.pathname);
	}

	private BaseTestPoint splitStringToList(String source) {
		BaseTestPoint baseTestPoint = new BaseTestPoint();
		if (StringUtils.isEmpty(source)) {
			return baseTestPoint;
		}
		int startIndex = source.lastIndexOf("#");
		String sub = source.substring(startIndex + 1);
		sub.trim();
		Map params = JsonUtils.fromJson(sub, Map.class);
		baseTestPoint.setParams(params);
		return baseTestPoint;
	}

	/**
	 * @param clazz
	 *            对象成员变量类型
	 * @param cell
	 * @return
	 */
	private Object getCellValue(Cell cell) {
		switch (cell.getCellTypeEnum()) {
		case NUMERIC:
			return cell.getNumericCellValue();
		case STRING:
			return cell.getStringCellValue().trim();
		case BLANK:
			return cell.getStringCellValue();
		case BOOLEAN:
			return cell.getBooleanCellValue();
		case _NONE:
			return cell.getStringCellValue();
		case FORMULA:
			return cell.getStringCellValue();
		case ERROR:
			return cell.getStringCellValue();
		}
		return cell.getStringCellValue();
	}

	private void setCellValue(Cell cell, Object value) {
		switch (cell.getCellTypeEnum()) {
		case NUMERIC:
			cell.setCellValue((Double) value);
			break;
		case STRING:
			cell.setCellValue((String) value);
			break;
		case BLANK:
			cell.setCellValue((String) value);
			break;
		case BOOLEAN:
			cell.setCellValue((Boolean) value);
			break;
		case _NONE:
			cell.setCellValue((String) value);
			break;
		case FORMULA:
			cell.setCellValue((String) value);
			break;
		case ERROR:
			cell.setCellValue((String) value);
			break;
		}
	}

	private Object readCellValue(Class<?> clazz, Object cellvalue) {
		if (cellvalue.getClass() != clazz && StringUtils.isJavaClass(clazz)) {
			return String.valueOf(cellvalue);
		}
		return cellvalue;
	}

	// 保存Row一条
	// 保存Row批量

	// 保存Cell
	// 保存Cell批量

	// 查询对象一条
	// 查询对象批量

	// 查询Row一条
	// 查询Row批量

	// 查询Cell
	// 查询Cell批量
}
