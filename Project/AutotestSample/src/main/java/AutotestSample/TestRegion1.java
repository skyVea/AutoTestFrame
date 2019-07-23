package AutotestSample;

import java.util.Map;

import com.autotest.enumeration.TestResultEnum;
import com.autotest.webserivce.model.WebAPITestCase;

public class TestRegion1 {

	public Object testCase1(WebAPITestCase testCase) {
		Map map =testCase.getTestPoint().getParams();
		System.out.println("参数"+map.toString());
		testCase.setResult(TestResultEnum.PASS.value());
		return testCase;
	}

	public Object testCase2(WebAPITestCase testCase) {
		testCase.setResult(TestResultEnum.PASS.value());
		return testCase;
	}

	public Object testCase3(WebAPITestCase testCase) {
		testCase.setResult(TestResultEnum.FAIL.value());
		return testCase;
	}

}
