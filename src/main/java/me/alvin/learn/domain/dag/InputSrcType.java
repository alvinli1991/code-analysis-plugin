package me.alvin.learn.domain.dag;

/**
 * 输入来源
 *
 * @author: Li Xiang
 * Date: 2021/12/25
 * Time: 9:38 PM
 */
public enum InputSrcType {
    /**
     * 类自己
     */
    CLASS_SELF,
    /**
     * 类有返回值的方法
     */
    CLASS_METHOD,
    /**
     * 类的成员变量
     */
    CLASS_FIELD,
}
