package me.alvin.test;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: Li Xiang
 * Date: 2021/12/27
 * Time: 4:38 PM
 */
@Data
public class TestContext {
    private String id;
    private boolean testFlag;
    private List<Integer> poiIds;
    private List<TestBean1> bean1List;
    private TestBean1 testBean1;
    private TestBean2 testBean2;
    private Map<String,String> testMap;
}
