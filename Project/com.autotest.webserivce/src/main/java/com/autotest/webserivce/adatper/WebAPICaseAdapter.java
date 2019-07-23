package com.autotest.webserivce.adatper;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.autotest.adatper.TestCaseAdapter;
import com.autotest.consts.GlobalConst;
import com.autotest.model.TestContext;
import com.autotest.model.TestModuleConfig;
import com.autotest.model.TestResult;
import com.autotest.model.TestSheetConfig;
import com.autotest.utils.ExcelFactory;
import com.autotest.utils.ExcelUtil;
import com.autotest.utils.FileUtil;
import com.autotest.utils.PathUtil;
import com.autotest.utils.StringUtils;
import com.autotest.webserivce.model.WebAPITestCase;

/**
 * WebAPI测试用例适配器
 * 
 * @author veaZhao
 *
 */
public class WebAPICaseAdapter extends TestCaseAdapter<WebAPITestCase> {

	public WebAPICaseAdapter(TestModuleConfig testModuleConfig) {
		super(testModuleConfig);
	}

	/**
	 * @return 返回所有用例对象集合
	 * @throws Exception
	 */
	@Override
	public Map<String, List<WebAPITestCase>> adaptTestCases() {
		Map<String, List<WebAPITestCase>> map = new HashMap<String, List<WebAPITestCase>>();
		List<TestSheetConfig> testSheetConfigs = this.testModuleConfig.getTestSheetConfigs();
		for (int i = 0; i < testSheetConfigs.size(); i++) {
			TestSheetConfig testSheetConfig = testSheetConfigs.get(i);
//			ExcelUtil excelUtil = ExcelFactory.getInstance()
//					.getExcelUtil(PathUtil.getPath("/" + this.testModuleConfig.getPath()));
			ExcelUtil excelUtil = new ExcelUtil(PathUtil.getPath("/" + this.testModuleConfig.getPath()));
			excelUtil.autoCompleByIncrement(testSheetConfig.getId(), WebAPITestCase.class);
			List<WebAPITestCase> sheecaseList = excelUtil.readAllObjs(testSheetConfig.getId(), WebAPITestCase.class);
			if (sheecaseList == null || sheecaseList.isEmpty()) {
				continue;
			}
			map.put(testSheetConfig.getId(), sheecaseList);
		}
		return map;
	}

	public void saveAllCases(Map<String, List<WebAPITestCase>> regionCases) {
		for (Entry<String, List<WebAPITestCase>> regionCase : regionCases.entrySet()) {
			String sheetname = regionCase.getKey();
			Object cases = regionCase.getValue();
			// ExcelUtil excelUtil = ExcelFactory.getInstance()
			// .getExcelUtil(PathUtil.getPath("/" + this.testModuleConfig.getPath()));
			ExcelUtil excelUtil = new ExcelUtil(PathUtil.getPath("/" + this.testModuleConfig.getPath()));
			String path = StringUtils.appendStringForPath(
					GlobalConst.TESTRESULT_EXCEL + File.separator + this.testModuleConfig.getPath(),
					StringUtils.dateString("yyyy-MM-dd-HH-mm-ss", TestContext.getInstance().getStartDate()));
			excelUtil.writeAllObjs(sheetname, (List<Object>) cases, path);
		}
	}

	@Override
	public void saveResult(TestResult<WebAPITestCase> result) {
		// ExcelUtil excelUtil = ExcelFactory.getInstance()
		// .getExcelUtil(PathUtil.getPath("/" + this.testModuleConfig.getPath()));

		result.getTestCase().setResult(result.getTestResult());
		String path = StringUtils.appendStringForPath(
				GlobalConst.TESTRESULT_EXCEL + File.separator + this.testModuleConfig.getPath(),
				StringUtils.dateString("yyyy-MM-dd-HH-mm-ss", TestContext.getInstance().getStartDate()));
		ExcelUtil excelUtil = null;
		if (FileUtil.isExist(new File(path))) {
			excelUtil = new ExcelUtil(path);
		} else {
			excelUtil = new ExcelUtil(PathUtil.getPath("/" + this.testModuleConfig.getPath()));
		}

		excelUtil.writeOneObj(result.getRegionId(), result.getTestCase(), path);
	}
}
