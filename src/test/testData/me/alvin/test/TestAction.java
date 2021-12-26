package me.alvin.test;

/**
 * @author: Li Xiang
 * Date: 2021/12/27
 * Time: 4:38 PM
 */
public class TestAction {

    public static final String A = "hh";

    private TestService testService;

    //region input
    //region 直接使用

    /**
     * input是context自身，被外部方法调用
     *
     * @param context
     */
    public void inputContextSelf(TestContext context) {
        testService.execute(context);
    }

    /**
     * input是context方法的返回值
     *
     * @param context
     */
    public void inputContextGetMethod(TestContext context) {
        testService.hello(context.getId());
    }

    /**
     * input是context本身，且作为构造函数
     *
     * @param context
     */
    public void inputContextAsConstructor(TestContext context) {
        new TestContext1(context);
    }

    /**
     * context方法返回值作为if条件
     *
     * @param context
     */
    public void inputIfContextChain(TestContext context) {
        if (context.isTestFlag()) {
            System.out.println("hello");
        }
    }

    /**
     * 直接使用静态变量
     * @param context
     */
    public void inputUseStatic(TestContext context,final String b){
        final String a = "1";
        System.out.println(TestConstant.KEY_1);
        System.out.println(A);
        System.out.println(a);
        System.out.println(b);
        if(TestConstant.KEY_1.equals("2") && TestConstant.KEY_1 != "1"){

        }
    }
    //endregion


    //region 间接使用

    /**
     * context的内部成员通过foreach
     *
     * @param context
     */
    public void inputContextForeach(TestContext context) {
        for (TestBean1 b1Item : context.getBean1List()) {
            System.out.println(b1Item.getB1());
            System.out.println(b1Item.isBoolB1());
        }
    }

    /**
     * context map作为输入
     *
     * @param context
     */
    public void inputContextMap(TestContext context) {
        String value = context.getTestMap().get("hello");
    }

    //region 链式

    /**
     * context链式方法作为input
     *
     * @param context
     */
    public void inputContextChainMethod(TestContext context) {
        System.out.println(context.getTestBean2().getB2());
    }

    /**
     * context链式方法返回值作为if条件
     *
     * @param context
     */
    public void inputIfContextChainMethod(TestContext context) {
        if (context.getTestBean1().isBoolB1()) {
            System.out.println("hello");
        }
    }
    //endregion


    //endregion

    //region 组合使用

    /**
     * 中转了几次
     *
     * @param context
     */
    public void inputCompositionConstructor(TestContext context) {
        String t1 = TestService.trans1(context.getTestBean1());

        TestContext1 tc1 = new TestContext1(context, t1);

        //TODO 此处的getTestBean2不是input
        context.getTestBean2().setB2(tc1.getTc1());
    }

    //endregion


    //endregion

    //region output

    /**
     * 调用了context有入参的方法
     *
     * @param context
     */
    public void outputDirect(TestContext context) {
        context.setId("hello");
    }

    /**
     * 调用了action外部，且无返回值的方法
     *
     * @param context
     */
    public void outputUnknown(TestContext context) {
        testService.execute(context);
        TestService.trans2(context.getTestBean1());
    }

    /**
     * 获取内部变量再赋值
     * <p>
     * 调用了context内是集合成员的添加修改方法
     *
     * @param context
     */
    public void outputContextCollection(TestContext context) {
        context.getTestMap().put("1", "2");
        context.getPoiIds().add(1);
    }

    /**
     * 获取内部变量再赋值
     * <p>
     * 调用内部变量的赋值方法
     *
     * @param context
     */
    public void outputChain(TestContext context) {
        context.getTestBean2().setB2("t1");
    }

    /**
     * 获取内部变量再赋值
     * <p>
     * 1. 先将context内成员获取出来，声明为内部变量
     * 2. 通过此内部变量获取内容
     *
     * @param context
     */
    public void outputIndirect(TestContext context) {
        TestBean1 tb1 = context.getTestBean1();

        tb1.setB1("hello");
    }
    //endregion

}
