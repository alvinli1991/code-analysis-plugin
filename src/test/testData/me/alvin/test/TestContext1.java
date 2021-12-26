package me.alvin.test;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: Li Xiang
 * Date: 2021/12/27
 * Time: 7:50 PM
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestContext1 {
    private TestContext testContext;
    private String tc1;

    public TestContext1(TestContext context) {
        this(context,null);
    }
}
