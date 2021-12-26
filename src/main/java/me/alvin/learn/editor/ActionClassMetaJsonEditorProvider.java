package me.alvin.learn.editor;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import me.alvin.learn.service.MetaJsonTempFileCacheService;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: Li Xiang
 * Date: 2021/12/30
 * Time: 5:16 PM
 */
public class ActionClassMetaJsonEditorProvider implements FileEditorProvider {
    private static final Logger LOGGER = Logger.getInstance(ActionClassMetaJsonEditorProvider.class);
    public static final String ACTION_META_JSON = "action-meta-json";

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        if (file.getFileType() != JavaFileType.INSTANCE) {
            return false;
        }
        PsiJavaFile javaFile = (PsiJavaFile) PsiManager.getInstance(project).findFile(file);
        List<PsiClass> matchClasses = Stream.of(javaFile.getClasses())
                .filter(psiClass -> Stream.of(psiClass.getImplementsListTypes())
                        .anyMatch(psiClassType -> {
                            String interfaceClassName = psiClassType.resolve().getQualifiedName();
                            return StringUtils.containsIgnoreCase(interfaceClassName, "action");
                        }))
                .collect(Collectors.toList());
        if (matchClasses.size() != 1) {
            return false;
        }
        PsiClass theClass = matchClasses.get(0);
        MetaJsonTempFileCacheService metaJsonTempFileCacheService = project.getService(MetaJsonTempFileCacheService.class);
        return metaJsonTempFileCacheService.isClassMetaJsonTempFileExist(theClass);
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        PsiJavaFile javaFile = (PsiJavaFile) PsiManager.getInstance(project).findFile(file);
        List<PsiClass> matchClasses = Stream.of(javaFile.getClasses())
                .filter(psiClass -> Stream.of(psiClass.getImplementsListTypes())
                        .anyMatch(psiClassType -> {
                            String interfaceClassName = psiClassType.resolve().getQualifiedName();
                            return StringUtils.containsIgnoreCase(interfaceClassName, "action");
                        }))
                .collect(Collectors.toList());
        PsiClass theClass = matchClasses.get(0);

        MetaJsonTempFileCacheService metaJsonTempFileCacheService = project.getService(MetaJsonTempFileCacheService.class);
        VirtualFile metaJsonVirtualFile = metaJsonTempFileCacheService.getClassMetaJsonTempFile(theClass);
        FileEditor fileEditor = TextEditorProvider.getInstance().createEditor(project, metaJsonVirtualFile);
        return fileEditor;
    }

    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return ACTION_META_JSON;
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }
}
