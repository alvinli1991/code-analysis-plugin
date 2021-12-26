package me.alvin.learn.action;

import com.intellij.lang.java.JavaLanguage;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import me.alvin.learn.domain.dag.Input;
import me.alvin.learn.service.MetaJsonTempFileCacheService;
import me.alvin.learn.utils.ActionUtils;
import me.alvin.learn.utils.JacksonUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: Li Xiang
 * Date: 2021/12/30
 * Time: 4:38 PM
 */
public class ShowActionMetaAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (null == project) {
            return;
        }

        PsiClass theClass = DumbService.getInstance(project).runReadActionInSmartMode(() -> {
            PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
            List<PsiClass> matchClasses = Stream.of(((PsiJavaFile) psiFile).getClasses())
                    .filter(psiClass -> Stream.of(psiClass.getImplementsListTypes())
                            .anyMatch(psiClassType -> {
                                String interfaceClassName = psiClassType.resolve().getQualifiedName();
                                return StringUtils.containsIgnoreCase(interfaceClassName, "action");
                            }))
                    .collect(Collectors.toList());
            return matchClasses.get(0);
        });

        Set<Input> constantInputs = DumbService.getInstance(project).runReadActionInSmartMode(() -> {
            JavaPsiFacade javaPsiFacade = JavaPsiFacade.getInstance(project);
            PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
            FileViewProvider fileViewProvider = psiFile.getViewProvider();
            Document document = fileViewProvider.getDocument();
            return ActionUtils.parseConstantInputForActionClass(theClass, document, javaPsiFacade);
        });

//        Document document = ApplicationManager.getApplication().runWriteAction((Computable<Document>) () -> {
//            MetaJsonTempFileCacheService metaJsonTempFileCacheService = project.getService(MetaJsonTempFileCacheService.class);
//            File tempFile = metaJsonTempFileCacheService.getTempFile();
//            VirtualFile virtualFile = VirtualFileManager.getInstance().refreshAndFindFileByNioPath(tempFile.toPath());
//            return FileDocumentManager.getInstance().getDocument(virtualFile);
//        });
//
//        CommandProcessor.getInstance().executeCommand(project, () -> {
//            document.setText(JacksonUtils.toJson(constantInputs, true));
//        }, "", null, document);


        ApplicationManager.getApplication().runWriteAction(() -> {
            String inputJson = JacksonUtils.toJson(constantInputs, true);
            MetaJsonTempFileCacheService metaJsonTempFileCacheService = project.getService(MetaJsonTempFileCacheService.class);
            File tempFile = metaJsonTempFileCacheService.getTempFile();
            VirtualFile virtualFile = VirtualFileManager.getInstance().refreshAndFindFileByNioPath(tempFile.toPath());
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            document.setText(inputJson);
            boolean saveResult = metaJsonTempFileCacheService.writeToFile(tempFile, inputJson);
            if (saveResult) {
                metaJsonTempFileCacheService.put(theClass, tempFile);
            }
        });

    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (null == project) {
            event.getPresentation().setEnabledAndVisible(false);
            return;
        }
        boolean show = DumbService.getInstance(project).runReadActionInSmartMode(() -> {
            PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
            boolean fileExist = project != null && psiFile != null;
            boolean hasTargetClass = false;
            if (fileExist) {
                if (psiFile.getLanguage().is(JavaLanguage.INSTANCE)) {
                    List<PsiClass> matchClasses = Stream.of(((PsiJavaFile) psiFile).getClasses())
                            .filter(psiClass -> Stream.of(psiClass.getImplementsListTypes())
                                    .anyMatch(psiClassType -> {
                                        String interfaceClassName = psiClassType.resolve().getQualifiedName();
                                        return StringUtils.containsIgnoreCase(interfaceClassName, "action");
                                    }))
                            .collect(Collectors.toList());
                    hasTargetClass = matchClasses.size() == 1;
                }
            }
            return hasTargetClass;
        });


        event.getPresentation().setEnabledAndVisible(show);
    }
}
