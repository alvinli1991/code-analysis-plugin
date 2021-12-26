package me.alvin.learn.domain.xml;

import java.util.List;

/**
 * @author: Li Xiang
 * Date: 2021/12/7
 * Time: 11:22 AM
 */
public interface Units extends com.intellij.util.xml.DomElement {
    List<Unit> getUnits();
}
