package me.alvin.test;

/**
 * 测试提取常量输入的Action代码
 *
 * @author: Li Xiang
 * Date: 2021/12/28
 * Time: 7:52 PM
 */
public class TestActionForConstantInput {
    public static final String A = "hh";

    public void inputUseStatic(TestContext context,final String b){
        final String a = "1";
        System.out.println(TestConstant.KEY_1);
        System.out.println(A);
        System.out.println(a);
        System.out.println(b);
        if(TestConstant.KEY_1.equals("2") && TestConstant.KEY_1 != "1"){

        }
        int holder = TestConstant.KEY_1 != "1"?1:2;
        TestConstant.MAP_CONSTANT.get("hello");
    }
}
