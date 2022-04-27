package me.alvin.learn.domain.xml.dag;

import me.alvin.learn.domain.StageType;
import me.alvin.learn.domain.annotation.ActionMeta;

/**
 * @author: Li Xiang
 * Date: 2022/4/26
 * Time: 8:13 PM
 */
@ActionMeta(stage = StageType.INIT, description = "弹性算力信息的初始化",isActive = false, depends = {})
public class InitElasticComputeAction {
}
