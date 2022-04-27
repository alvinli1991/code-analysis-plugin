package me.alvin.learn.domain.xml.dag;

import me.alvin.learn.domain.StageType;
import me.alvin.learn.domain.annotation.ActionMeta;

/**
 * @author: Li Xiang
 * Date: 2022/4/26
 * Time: 8:13 PM
 */
@ActionMeta(stage = StageType.CHECK, description = "空白流量控制", depends = {InitElasticComputeAction.class})
public class ControlBlankFlowAction {
}
