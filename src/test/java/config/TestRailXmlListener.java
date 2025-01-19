package config;

import browserstack.shaded.commons.lang3.exception.ExceptionUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestRailXmlListener implements ITestListener {

    private List<TestResultData> testResults = new ArrayList<>();
    private Document document;

    @BeforeSuite
    public void onStart(ITestContext context) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            document = dBuilder.newDocument();

            Element rootElement = document.createElement("testsuite");
            rootElement.setAttribute("name", context.getSuite().getName());
            rootElement.setAttribute("tests", String.valueOf(context.getAllTestMethods().length));
            rootElement.setAttribute("failures", "0");
            rootElement.setAttribute("errors", "0");
            rootElement.setAttribute("skipped", "0");

            document.appendChild(rootElement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        addTestResult(result, "pass", (double) result.getEndMillis() - result.getStartMillis());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        addTestResult(result, "fail", (double) result.getEndMillis() - result.getStartMillis());
        updateSuiteCounts("failures");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        addTestResult(result, "skipped", (double) result.getEndMillis() - result.getStartMillis());
        updateSuiteCounts("skipped");
    }

    private void updateSuiteCounts(String attribute) {
        Element rootElement = document.getDocumentElement();
        int count = Integer.parseInt(rootElement.getAttribute(attribute));
        rootElement.setAttribute(attribute, String.valueOf(count + 1));
    }


    private void addTestResult(ITestResult result, String status, double executionTime) {
        String testRailId = null;
        if (result.getMethod().getConstructorOrMethod().getMethod().isAnnotationPresent(TestRailCase.class)) {
            TestRailCase annotation = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(TestRailCase.class);
            testRailId = annotation.id();
        }

        testResults.add(new TestResultData(result.getName(), result.getTestClass().getName(), status, executionTime / 1000.0, testRailId, result.getThrowable()));
    }

    @AfterSuite
    public void onFinish(ITestContext context) {
        try {
            Element rootElement = document.getDocumentElement();
            double totalTime = 0;

            for (TestResultData testResult : testResults) {
                Element testcaseElement = document.createElement("testcase");
                testcaseElement.setAttribute("name", testResult.name());
                testcaseElement.setAttribute("classname", testResult.className());
                testcaseElement.setAttribute("time", String.format("%.3f", testResult.executionTime()));
                totalTime += testResult.executionTime();
                if (testResult.status().equals("fail") && testResult.throwable() != null) {
                    String failureMessage = testResult.throwable().getMessage();
                    String stackTrace = ExceptionUtils.getStackTrace(testResult.throwable());

                    Element failureElement = document.createElement("failure");
                    failureElement.setAttribute("message", failureMessage);
                    failureElement.setAttribute("type", testResult.throwable().getClass().getName());
                    failureElement.appendChild(document.createTextNode(stackTrace));
                    testcaseElement.appendChild(failureElement);
                }

                if (testResult.status().equals("skipped") && testResult.throwable() != null) {
                    Element failureElement = document.createElement("skipped");
                    testcaseElement.appendChild(failureElement);
                }

                if (testResult.testRailId() != null) {
                    Element testrailIdElement = document.createElement("property");
                    testrailIdElement.setAttribute("name", "test_id");
                    testrailIdElement.setAttribute("value", testResult.testRailId());
                    testcaseElement.appendChild(testrailIdElement);
                }

                rootElement.appendChild(testcaseElement);
            }
            rootElement.setAttribute("time", String.format("%.3f", totalTime));

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);

            File xmlFile = new File("testrail_results.xml");
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);

            System.out.println("TestRail XML report generated: " + xmlFile.getAbsolutePath());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private record TestResultData(String name, String className, String status, double executionTime, String testRailId,
                                  Throwable throwable) {
    }
}
