package AutotestSample;

import com.autotest.enumeration.TestResultEnum;
import com.autotest.webserivce.model.WebAPITestCase;

public class TestRegion1 {

	public Object testCase1(WebAPITestCase testCase) {
		testCase.setResult(TestResultEnum.PASS.value());
		return testCase;
	}

	public Object testCase2(WebAPITestCase testCase) {
		System.out.println(testCase.getTestPoint().getParams().toString());
		testCase.setResult(TestResultEnum.FAIL.value());
		return testCase;
	}

	public Object testCase3(WebAPITestCase testCase) {
		testCase.setResult(TestResultEnum.FAIL.value());
		return testCase;
	}

}
