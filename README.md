# AutoTestFrame
# 前言
## 目的
指导初次使用测试框架的测试人员能够快速入门。通过简单的配置，用例代码完成自动的执行测试用例、管理测试流程、收集测试结果、输出日志等。同时也提供了可扩展的执行器、数据适配器、监听器等。
## 使用范围
测试人员。
# 使用流程
## 执行流程
![follow](https://github.com/skyVea/AutoTestFrame/blob/master/Img/follow.png)
## 结构说明
框架分为三层。第一层为核心组件层，主要负责调用数据接口com.autotest.adatper.TestCaseAdapter，调用用例执行接口com.autotest.executor.BaseTestExecutor，注册监听器，解析全局配置，维护全局变量，提供一些工具类；第二层测试对象层，这个层不是具体的测试用例层。而是将一类用例或一类测试内容，抽象出来。比如：UI测试、Http接口测试等。需要实现核心组件提供的两个接口，与用例Modle对象；第三层是具体用例层，包含了具体的测试用例类和方法。
## 配置文件
### 设置环境变量TEST_HOME
此目录用于存放日志、报告、配置文件等。指定测试目录路径如下。</br>
![follow](https://github.com/skyVea/AutoTestFrame/blob/master/Img/env.png)
### 创建test_home测试目录结构
具体模板见AutotestSample/src/main/resources目录。</br>
* <文件夹></br>
D:\autotest\test_home\global</br>
D:\autotest\test_home\log</br>
* <文件></br>
D:\autotest\test_home\global\TestSuit.xml</br>
<pre>
<testsuit>
	<testmodule id="自动化测试" path="D:\autotest\测试用例.xls" executor="com.autotest.webserivce.executor.WebAPITestExecutor">
		<testsheet id="region1" testclass="AutotestSample.TestRegion1"/>
		<testsheet id="region2" testclass="AutotestSample.TestRegion2"/>
	</testmodule>
</testsuit>
</pre>
## 测试用例
### 描述型用例
描述测试用例保存到Excel，通过框架对数据进行读取，并执行对应方法，回写测试结	果，输入日志、报告等，也可扩展其他存储方式。Sheet名称与用例测试类对应关系，	以及路径等信息都在TestSuit.xml内配置。用例表头必须为Excel的第一行。具体模板	见AutotestSample/src/main/resources目录。</br>
![follow](https://github.com/skyVea/AutoTestFrame/blob/master/Img/excelcase.png)
### 代码用例
具体模板见AutotestSample组件。</br>
<pre>
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
</pre>
## 执行及结果
开始执行TestExecutor.getInstance().execute();
执行结果会被回写到描述测试用例内。测试日志与测试报告在TEST_HOME下查看。
* 日志：</br>
![follow](https://github.com/skyVea/AutoTestFrame/blob/master/Img/log.png)
* 报告：</br>
![follow](https://github.com/skyVea/AutoTestFrame/blob/master/Img/report.png)
## 扩展及其他
想要做扩展也可以。如SeleniumUI自动化等。用例转换与执行需要继承BaseTestExecutor与TestCaseAdapter；用例Model需要继承BaseTestCase。也可以重写各种监听器如报告监听器。具体实现方式见com.autotest.webserivce组件代码详情。
### 用例Model
用例Model配置注解需与表头一致。如：
<pre>
public class WebAPITestCase extends BaseTestCase {
	@Column(columName = "用例编码", isPrimary = true, isIncrement = true)
	String code;
	@Column(columName = "功能模块")
	String module;
	@Column(columName = "接口方法", isRequired = true)
	String name;
	@Column(columName = "描述")
	String detail;
	@Column(columName = "优先级")
	String priority;
	@Column(columName = "测试目的")
	String order;
	@Column(columName = "参数", isRequired = true)
	BaseTestPoint testPoint;
	@Column(columName = "期望结果", isRequired = true)
	String expected;
	@Column(columName = "测试结果", isReadOnly = false)
	Integer result;
	@Column(columName = "备注")
	String comment;
  </pre>
 # 联系方式88603221@qq.com
