package me.alvin.learn.domain.xml;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;

/**
 * @author: Li Xiang
 * Date: 2021/12/7
 * Time: 11:18 AM
 */
public interface DagGraph extends com.intellij.util.xml.DomElement {
    @Attribute("id")
    GenericAttributeValue<String> getDagId();
    Units getUnits();

    Stages getStages();
}
