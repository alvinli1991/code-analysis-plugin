package me.alvin.learn.domain.xml;

import java.util.List;

/**
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 2:06 PM
 */
public interface Depends extends com.intellij.util.xml.DomElement {
    List<Depend> getDepends();
}
