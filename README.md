# AutoTestFrame
# 1 前言
## 1.1 目的
指导初次使用测试框架的测试人员能够快速入门。通过简单的配置，用例代码完成自动的执行测试用例、管理测试流程、收集测试结果、输出日志、报告等。同时也提供了可扩展的执行器、数据适配器、监听器等。
## 1.2 使用范围
测试人员。
# 2 使用流程
## 2.1 执行流程
![img](https://github.com/skyVea/AutoTestFrame/blob/master/Img/follow.png)
## 2.2 结构说明
框架分为三层。第一层为核心组件层，主要负责调用数据接口com.autotest.adatper.TestCaseAdapter，调用用例执行接口com.autotest.executor.BaseTestExecutor，提供了默认测试过程监听器，与注册自定监听器的接口。提供了解析全局配置，维护全局变量，提供一些工具类以及默认测试报告监听器，测试日志等；第二层测试对象层，这个层不是具体的测试用例层。而是将一类用例或一类测试内容，抽象出来。比如：UI测试、Http接口测试等。需要实现核心组件提供的两个接口，与用例Modle对象，根据需要实现监听器；第三层是用例层，包含了，配置文件，描述用例，具体的测试用例类和方法等。
## 2.3 配置目录
### 2.3.1 设置环境变量TEST_HOME
此目录用于存放日志、测试报告、测试结果等。目录与内容都会自动生成。指定测试目	录环境变量如下。</br>
![img](https://github.com/skyVea/AutoTestFrame/blob/master/Img/env.png)
### 2.3.2 TEST_HOME测试目录说明

* <文件夹>
全局配置目录（未启用）：D:\autotest\test_home\global
日志目录：D:\autotest\test_home\log
Excel测试结果目录：D:\autotest\test_home\excel
* <文件>
测试报告：D:\autotest\test_home\Extent.html

## 2.4 测试用例
### 2.4.1 描述型用例
* 描述测试用例保存到Excel，通过框架对数据进行读取，执行相关测试方法，并输出测试结果、日志、报告等，也可扩展其他存储方式
* 用例表头必须为Excel的第一行
* 用例存放在Resources目录：AutotestSample/src/main/resources</br>
![img](https://github.com/skyVea/AutoTestFrame/blob/master/Img/excelcase.png)
### 2.4.2 配置文件
* Testsuit.xml是测试执行的配置文件
* Testmodule path为测试用例文件名 executor为用例执行器类
* Testsheet id为用例表名，testclass为用例测试类
* 配置存放在Resources目录：AutotestSample/src/main/resources
```xml
<testsuit>
	<testmodule id="自动化测试" path="测试用例.xls" executor="com.autotest.webserivce.executor.WebAPITestExecutor">
		<testsheet id="region1" testclass="AutotestSample.TestRegion1"/>
		<testsheet id="region2" testclass="AutotestSample.TestRegion2"/>
	</testmodule>
</testsuit>
```
### 2.4.3 代码用例
具体模板见AutotestSample组件。</br>
```java
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
```
## 2.5 执行及结果
开始执行TestExecutor.getInstance().execute();
执行结果会被回写到描述测试用例内。测试日志与测试报告在TEST_HOME下查看。
* 日志：</br>
![img](https://github.com/skyVea/AutoTestFrame/blob/master/Img/log.png)
* 报告：</br>
![img](https://github.com/skyVea/AutoTestFrame/blob/master/Img/report.png)
## 2.6 扩展及其他
想要做扩展也可以。如SeleniumUI自动化等。用例转换与执行需要继承BaseTestExecutor与TestCaseAdapter；用例Model需要继承BaseTestCase。也可以重写各种监听器如报告监听器。具体实现方式见com.autotest.webserivce组件代码详情。
### 2.6.1 用例Model
用例Model配置注解需与表头一致。如：
```java
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
}
```
# 3 Jenkins集成测试框架使用
## 3.1 构建一个Maven项目
![img](https://github.com/skyVea/AutoTestFrame/blob/master/Img/maven_project.png)
## 3.2 配置全局变量TEST_HOME
![img](https://github.com/skyVea/AutoTestFrame/blob/master/Img/set_env.png)
## 3.3 配置源码管理
![img](https://github.com/skyVea/AutoTestFrame/blob/master/Img/set_source.png)
## 3.4 执行Maven构建触发测试
![img](https://github.com/skyVea/AutoTestFrame/blob/master/Img/build.png)
## 3.5 工作空间查看测试结果目录test_home
![img](https://github.com/skyVea/AutoTestFrame/blob/master/Img/workspace.png)
 # 联系方式88603221@qq.com
