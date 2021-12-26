package me.alvin.learn.domain.xml;


import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 1:56 PM
 */
public interface Flow extends com.intellij.util.xml.DomElement {

    @Attribute("from")
    GenericAttributeValue<String> getFrom();

    @Attribute("to")
    GenericAttributeValue<String> getTo();

}
