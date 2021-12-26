package me.alvin.learn.domain.context;

import com.intellij.psi.PsiClass;
import me.alvin.learn.domain.clazz.ClassMeta;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Li Xiang
 * Date: 2021/12/24
 * Time: 4:04 PM
 */
public class ClassFactory {
    private static final Map<PsiClass, ClassMeta> CLASS_META_HOLDER;

    public static PsiClass objectClass;

    static {
        CLASS_META_HOLDER = new ConcurrentHashMap<>();
    }

    public static synchronized Optional<ClassMeta> getClassMeta(PsiClass psiClass) {
        if (Objects.isNull(psiClass)) {
            return Optional.empty();
        }
        if (CLASS_META_HOLDER.containsKey(psiClass)) {
            return Optional.of(CLASS_META_HOLDER.get(psiClass));
        }

        ClassMeta meta = new ClassMeta(psiClass);
        CLASS_META_HOLDER.put(psiClass, meta);
        return Optional.of(meta);
    }

    public static PsiClass getObjectClass() {
        return ClassFactory.objectClass;
    }

    public static void setObjectClass(PsiClass objectClass) {
        ClassFactory.objectClass = objectClass;
    }
}
