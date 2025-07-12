package com.mercadolibre.product_api.performance;

import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("performance")
public class ProductAPIPerformanceTest {

    @LocalServerPort
    private int port;

    private static final int NUM_THREADS = 100;
    private static final int RAMP_UP_PERIOD = 10;
    private static final int LOOP_COUNT = 10;

    @BeforeAll
    public static void setupJMeter() {
        // Configurar JMeter
        File jmeterHome = new File("target/jmeter");
        jmeterHome.mkdirs();
        JMeterUtils.setJMeterHome(jmeterHome.getAbsolutePath());
        JMeterUtils.loadJMeterProperties("src/test/resources/jmeter.properties");
        JMeterUtils.initLocale();
    }

    @Test
    void performanceTest() throws Exception {
        // Crear el plan de pruebas
        TestPlan testPlan = new TestPlan("Product API Performance Test Plan");
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, ArgumentsPanel.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        // Crear grupo de hilos
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Product API Thread Group");
        threadGroup.setNumThreads(NUM_THREADS);
        threadGroup.setRampUp(RAMP_UP_PERIOD);
        threadGroup.setSamplerController(new LoopController());
        ((LoopController) threadGroup.getSamplerController()).setLoops(LOOP_COUNT);
        threadGroup.setSamplerController((LoopController) threadGroup.getSamplerController());

        // Crear petición HTTP
        HTTPSamplerProxy httpSampler = new HTTPSamplerProxy();
        httpSampler.setProtocol("http");
        httpSampler.setDomain("localhost");
        httpSampler.setPort(port);
        httpSampler.setPath("/api/products");
        httpSampler.setMethod("GET");
        httpSampler.setName("Get Products Request");
        httpSampler.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSampler.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        // Crear árbol de pruebas
        HashTree testPlanTree = new HashTree();
        HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
        threadGroupHashTree.add(httpSampler);

        // Agregar listener para resultados
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String resultFile = "target/jmeter/results_" + timestamp + ".jtl";
        ResultCollector resultCollector = new ResultCollector(new Summariser());
        resultCollector.setFilename(resultFile);
        testPlanTree.add(testPlanTree.getArray()[0], resultCollector);

        // Guardar el plan de pruebas
        SaveService.saveTree(testPlanTree, new FileOutputStream("target/jmeter/testplan.jmx"));

        // Ejecutar pruebas
        StandardJMeterEngine jmeter = new StandardJMeterEngine();
        jmeter.configure(testPlanTree);
        jmeter.run();
    }
} 