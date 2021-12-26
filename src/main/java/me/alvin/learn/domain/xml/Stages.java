package me.alvin.learn.domain.xml;

import java.util.List;

/**
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 2:07 PM
 */
public interface Stages extends com.intellij.util.xml.DomElement {
    List<Stage> getStages();
}
