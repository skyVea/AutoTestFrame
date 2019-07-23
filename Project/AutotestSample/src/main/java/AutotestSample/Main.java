package AutotestSample;

import com.autotest.consts.GlobalConst;
import com.autotest.executor.TestExecutor;
import com.autotest.listener.TestListenerFactory;
import com.autotest.webservice.listener.WebAPITestReporterListener;

/**
 * 测试框架使用Demo
 * @author veaZhao
 *
 */
public class Main {
	public static void main(String[] args) {
		//查看环境变量是否生效
		System.out.println(GlobalConst.TEST_HOME);
		System.out.println(GlobalConst.TESTSUIT);
		//注册测试报告监听
		TestListenerFactory.registerTestListener(TestExecutor.getInstance(), new WebAPITestReporterListener());
		//执行测试
		TestExecutor.getInstance().execute();
	}
}
