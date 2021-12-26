package me.alvin.learn.domain.xml;

import com.intellij.util.xml.DomFileDescription;

/**
 * @author: Li Xiang
 * Date: 2021/12/7
 * Time: 11:25 AM
 */
public class DagGraphDomFileDescription extends DomFileDescription<DagGraph> {

    public DagGraphDomFileDescription() {
        super(DagGraph.class, "DagGraph", "http://adapi.waimai.dev.meituan.com/api/search/lpp-dag.xsd");
    }
}
