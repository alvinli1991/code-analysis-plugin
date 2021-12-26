package me.alvin.learn.domain.xml;

/**
 * @author: Li Xiang
 * Date: 2021/12/7
 * Time: 11:18 AM
 */
public interface DagGraph extends com.intellij.util.xml.DomElement {
    Units getUnits();

    Stages getStages();
}
