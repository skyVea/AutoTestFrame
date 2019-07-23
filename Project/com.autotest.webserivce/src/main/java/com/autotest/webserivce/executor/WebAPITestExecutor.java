package com.autotest.webserivce.executor;

import java.lang.reflect.Method;
import java.util.List;

import com.autotest.adatper.TestCaseAdapter;
import com.autotest.executor.BaseTestExecutor;
import com.autotest.model.TestSheetConfig;
import com.autotest.utils.StringUtils;
import com.autotest.webserivce.adatper.WebAPICaseAdapter;
import com.autotest.webserivce.model.WebAPITestCase;

/**
 * WebAPI测试执行器
 * 
 * @author veaZhao
 *
 */
public class WebAPITestExecutor extends BaseTestExecutor<WebAPITestCase> {

	@Override
	protected WebAPITestCase execute(String region, WebAPITestCase testcase) throws Exception {
		List<TestSheetConfig> testSheetConfigs = testModuleConfig.getTestSheetConfigs();
		String testClass = null;
		WebAPITestCase resultCase = null;
		for (int i = 0; i < testSheetConfigs.size(); i++) {
			if (region.equals(testSheetConfigs.get(i).getId())) {
				testClass = testSheetConfigs.get(i).getTestclass();
			}
		}
		// 类不为空，走类方法
		if (!StringUtils.isEmpty(testClass)) {
			Object object = null;
			Method method = null;
			try {
				object = Class.forName(testClass).newInstance();
				method = Class.forName(testClass).getDeclaredMethod(testcase.getName(), WebAPITestCase.class);
				method.setAccessible(true);
				resultCase = (WebAPITestCase) method.invoke(object, testcase);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("实体测试类" + testClass + "没有这个找到[" + testcase.getName() + "]方法!");
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("没有找到Region:" + region + "配置测试类" + testClass + "对应的实体测试类！");
			}
		} else {
			throw new RuntimeException("测试类为空，请检查TestSuit.xml中，Region:" + region + "配置的测试类");
			// 类为空，走全局调用
			/*BaseTestPoint point = testcase.getTestPoint();
			Map<?, ?> pointMap = point.getParams();
			String wsdlUrl = (String) pointMap.get("wsdlUrl");
			String namespaceURI = (String) pointMap.get("namespaceURI");
			String argstr = (String) pointMap.get("args");
			Object[] objects = null;
			try {
				if (!StringUtils.isEmpty(argstr)) {
					List<?> args = JsonUtils.fromJson((String) pointMap.get("args"), ArrayList.class);
					objects = new WebServiceClient(wsdlUrl, namespaceURI).invoke(testcase.getName(),
							args.toArray(new String[args.size()]));
				} else {
					objects = new WebServiceClient(wsdlUrl, namespaceURI).invoke(testcase.getName());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}*/
		}
		return resultCase;
	}

	@Override
	protected TestCaseAdapter<WebAPITestCase> getTestCaseAdapter() {
		return new WebAPICaseAdapter(this.testModuleConfig);
	}

}
