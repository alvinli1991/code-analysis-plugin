package me.alvin.learn.domain.xml.dag;

import me.alvin.learn.domain.StageType;
import me.alvin.learn.domain.xml.dagMeta.*;
import me.alvin.learn.utils.JacksonXmlUtils;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class DagTest {

    private Dag dagUnderTest;


    private String expect = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<DagGraph id=\"hello\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://xxx.xsd\">\n" +
            "  <units>\n" +
            "    <unit>\n" +
            "      <id>initElasticComputeAction</id>\n" +
            "      <clz>me.alvin.learn.domain.xml.dag.InitElasticComputeAction</clz>\n" +
            "      <description>description1</description>\n" +
            "    </unit>\n" +
            "    <unit>\n" +
            "      <id>controlBlankFlowAction</id>\n" +
            "      <clz>me.alvin.learn.domain.xml.dag.ControlBlankFlowAction</clz>\n" +
            "      <description>description2</description>\n" +
            "    </unit>\n" +
            "  </units>\n" +
            "  <stages>\n" +
            "    <stage id=\"check\">\n" +
            "      <flows>\n" +
            "        <flow from=\"initElasticComputeAction\" to=\"controlBlankFlowAction\"/>\n" +
            "      </flows>\n" +
            "    </stage>\n" +
            "    <stage id=\"s2\">\n" +
            "      <depends>\n" +
            "        <depend id=\"check\"/>\n" +
            "      </depends>\n" +
            "      <flows>\n" +
            "        <flow from=\"initElasticComputeAction\" to=\"controlBlankFlowAction\"/>\n" +
            "      </flows>\n" +
            "    </stage>\n" +
            "  </stages>\n" +
            "</DagGraph>\n";

    @Before
    public void setUp() {
        Action action1 = Action.builder()
                .clazz(InitElasticComputeAction.class)
                .description("description1")
                .build();
        Action action2 = Action.builder()
                .clazz(ControlBlankFlowAction.class)
                .description("description2")
                .build();
        Stage stage1 = Stage.builder()
                .stageType(StageType.CHECK)
                .flow(Flow.builder().fromAction(action1)
                        .toAction(action2)
                        .build())
                .build();
        Stage stage2 = Stage.builder()
                .id("s2")
                .flow(Flow.builder().fromAction(action1)
                        .toAction(action2)
                        .build())
                .depend(StageDepend.builder()
                        .dependStage(stage1).build())
                .build();

        dagUnderTest = Dag.builder()
                .xmlns("http://www.w3.org/2001/XMLSchema-instance")
                .schemaLocation("http://xxx.xsd")
                .id("hello")
                .unit(action1)
                .unit(action2)
                .stage(stage1)
                .stage(stage2)
                .build();
    }

    @Test
    public void testGenerateXml() {
        String result = JacksonXmlUtils.toXml(dagUnderTest);
        System.out.println(result);
        assertThat(result, equalTo(expect));
    }

    @Test
    public void testDeserialize() {
        Dag result = JacksonXmlUtils.parse(expect, Dag.class);
        assertThat(result, equalTo(dagUnderTest));
    }
}
