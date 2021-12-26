package me.alvin.learn.domain.context;

import com.intellij.psi.PsiMethod;
import me.alvin.learn.domain.clazz.MethodMeta;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Li Xiang
 * Date: 2021/12/28
 * Time: 5:39 PM
 */
public class MethodFactory {
    private static final Map<PsiMethod, MethodMeta> METHOD_META_HOLDER;

    static {
        METHOD_META_HOLDER = new ConcurrentHashMap<>();
    }

    public static synchronized Optional<MethodMeta> getMethodMeta(PsiMethod psiMethod) {
        if (Objects.isNull(psiMethod)) {
            return Optional.empty();
        }
        if (METHOD_META_HOLDER.containsKey(psiMethod)) {
            return Optional.of(METHOD_META_HOLDER.get(psiMethod));
        }

        MethodMeta meta = new MethodMeta(psiMethod);
        METHOD_META_HOLDER.put(psiMethod, meta);
        return Optional.of(meta);
    }
}
