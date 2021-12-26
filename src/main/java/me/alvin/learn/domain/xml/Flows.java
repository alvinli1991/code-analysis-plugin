package me.alvin.learn.domain.xml;

import java.util.List;

/**
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 2:02 PM
 */
public interface Flows extends com.intellij.util.xml.DomElement {

    List<Flow> getFlows();
}
