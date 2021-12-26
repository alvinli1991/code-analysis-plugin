package me.alvin.learn.domain.dag;

/**
 * 设置输出的方式
 *
 * @author: Li Xiang
 * Date: 2021/12/25
 * Time: 10:09 PM
 */
public enum OutputAssignType {
    /**
     * 使用set方法
     */
    SET_METHOD,
    /**
     * 通过集合的更新操作，例如add、put之类
     */
    UPDATE_COLLECTION,

}
