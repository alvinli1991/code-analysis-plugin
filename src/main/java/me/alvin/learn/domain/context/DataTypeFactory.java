package me.alvin.learn.domain.context;

import com.intellij.psi.PsiType;
import me.alvin.learn.domain.clazz.DataTypeMeta;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 3:19 PM
 */
public class DataTypeFactory {

    private static final Map<PsiType, DataTypeMeta> DATA_TYPE_HOLDER;

    static {
        DATA_TYPE_HOLDER = new ConcurrentHashMap<>();
    }

    /**
     * 获取类型对应的解析后结果
     *
     * @param psiType
     * @return
     */
    public static synchronized Optional<DataTypeMeta> getDataTypeMeta(PsiType psiType) {
        if (Objects.isNull(psiType)) {
            return Optional.empty();
        }
        if (DATA_TYPE_HOLDER.containsKey(psiType)) {
            return Optional.of(DATA_TYPE_HOLDER.get(psiType));
        }

        DataTypeMeta meta = new DataTypeMeta(psiType);
        DATA_TYPE_HOLDER.put(psiType, meta);
        return Optional.of(meta);
    }
}
