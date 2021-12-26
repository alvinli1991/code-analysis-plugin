package me.alvin.base;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;


/**
 * @author: Li Xiang
 * Date: 2021/12/28
 * Time: 7:47 PM
 */
public class MyJavaCodePsiParseTestCase extends LightJavaCodeInsightFixtureTestCase {
    protected JavaPsiFacade javaPsiFacade;

    public void setUp() throws Exception {
        super.setUp();
        myFixture.copyDirectoryToProject("me", "me");
        javaPsiFacade = JavaPsiFacade.getInstance(myFixture.getProject());
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

}
