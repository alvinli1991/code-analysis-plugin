package me.alvin.learn.domain.xml;

/**
 * @author: Li Xiang
 * Date: 2021/12/7
 * Time: 11:21 AM
 */
public interface Unit extends com.intellij.util.xml.DomElement {
    Id getId();

    Description getDescription();

    Clz getClz();
}
