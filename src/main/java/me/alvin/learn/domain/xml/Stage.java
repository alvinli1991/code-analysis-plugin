package me.alvin.learn.domain.xml;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 2:03 PM
 */
public interface Stage extends com.intellij.util.xml.DomElement {

    @Attribute("id")
    GenericAttributeValue<String> getStageId();

    Flows getFlows();

    Depends getDepends();
}
