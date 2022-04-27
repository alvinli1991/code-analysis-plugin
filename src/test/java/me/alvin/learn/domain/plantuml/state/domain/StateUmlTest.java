package me.alvin.learn.domain.plantuml.state.domain;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StateUmlTest {


    private StateUml stateUmlUnderTest;

    @Before
    public void setUp() {
        State s1 = new State("X","", null);
        State s2 = new State("Y","", Lists.newArrayList(s1));
        State s3 = new State("Z", "",null);
        State s4 = new State("L","", null);
        State s5 = new State("M", "",Lists.newArrayList(s3, s4));
        stateUmlUnderTest = StateUml.builder().states(Lists.newArrayList(s2, s5))
                .stateRelation(StateRelation.builder()
                        .from(s2)
                        .to(s5)
                        .build())
                .stateRelation(StateRelation.builder()
                        .from(s3)
                        .to(s4)
                        .build())
                .build();
    }

    @Test
    public void testToPlantuml() {
        String expect = "@startuml\n" +
                "state Y {\n" +
                "state X {\n" +
                "}\n" +
                "}\n" +
                "\n" +
                "state M {\n" +
                "state Z {\n" +
                "}\n" +
                "state L {\n" +
                "}\n" +
                "}\n" +
                "\n" +
                "Y --> M\n" +
                "Z --> L\n" +
                "@enduml\n";
        String result = stateUmlUnderTest.toPlantuml();
        System.out.println(result);
        assertThat(result, equalTo(expect));

    }
}
