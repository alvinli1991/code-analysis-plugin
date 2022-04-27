package me.alvin.learn.domain.plantuml.state.domain;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class StateTest {

    private State stateUnderTest;

    @Before
    public void setUp() {
        stateUnderTest = new State("X", "hello", Lists.newArrayList(new State("Y", "", Collections.emptyList()), new State("Z", "", Collections.emptyList())));
    }


    @Test
    public void testToPlantuml() {
        String expect = "state X {\n" +
                "X:hello\n" +
                "state Y {\n" +
                "}\n" +
                "state Z {\n" +
                "}\n" +
                "}\n";
        System.out.println(stateUnderTest.toPlantuml());
        assertThat(stateUnderTest.toPlantuml(), equalTo(expect));

    }
}
