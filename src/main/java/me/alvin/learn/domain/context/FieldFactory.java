package me.alvin.learn.domain.context;

import com.intellij.psi.PsiField;
import me.alvin.learn.domain.clazz.FieldMeta;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Li Xiang
 * Date: 2021/12/28
 * Time: 3:38 PM
 */
public class FieldFactory {
    private static final Map<PsiField, FieldMeta> FIELD_META_HOLDER;

    static {
        FIELD_META_HOLDER = new ConcurrentHashMap<>();
    }

    public static synchronized Optional<FieldMeta> getFieldMeta(PsiField psiField) {
        if (Objects.isNull(psiField)) {
            return Optional.empty();
        }
        if (FIELD_META_HOLDER.containsKey(psiField)) {
            return Optional.of(FIELD_META_HOLDER.get(psiField));
        }
        FieldMeta meta = new FieldMeta(psiField);
        FIELD_META_HOLDER.put(psiField, meta);
        return Optional.of(meta);
    }

}
