package me.alvin.learn.domain.xml;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 2:04 PM
 */
public interface Depend extends com.intellij.util.xml.DomElement {
    @Attribute("id")
    GenericAttributeValue<String> getDependId();
}
