package me.alvin.learn.domain.annotation;


import me.alvin.learn.domain.StageType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Li Xiang
 * Date: 2022/4/20
 * Time: 10:26 AM
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActionMeta {
    /**
     * 所属stage
     *
     * @return
     */
    StageType stage();

    /**
     * 用户如果未定义，解析后默认取类名首字母小写的形式
     *
     * @return
     */
    String id() default "";

    /**
     * action的描述
     *
     * @return
     */
    String description();

    /**
     * 依赖的action
     * 如果没有依赖，则它属于所处stage的root action
     *
     * @return
     */
    Class<?>[] depends();

    /**
     * 是否激活
     *
     * @return
     */
    boolean isActive() default true;
}
