package me.alvin.learn.domain;

/**
 * @author: Li Xiang
 * Date: 2022/4/20
 * Time: 10:43 AM
 */
public enum StageType {
    /**
     * 检查
     */
    CHECK("check", null),
    /**
     * 初始化
     */
    INIT("init", CHECK),
    /**
     * 召回。还包含粗排截断以及截断后某些依赖信息的召回
     */
    RECALL("recall", INIT),
    /**
     * 预估
     */
    PREDICT("predict", RECALL),
    /**
     * 排序
     */
    SORT("sort", PREDICT),
    /**
     * 出价
     */
    BID("bid", SORT),
    /**
     * 优化
     */
    OPTIMIZE("optimize", BID),
    /**
     * 渲染
     */
    RENDER("render", OPTIMIZE),
    /**
     * 日志记录
     */
    LOG("log", RENDER),
    ;

    private String id;
    private StageType depend;

    StageType(String id, StageType depend) {
        this.id = id;
        this.depend = depend;
    }

    public String getId() {
        return id;
    }

    public StageType getDepend() {
        return depend;
    }
}
