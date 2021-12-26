package me.alvin.learn.action;

import com.intellij.lang.xml.XMLLanguage;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.Query;
import com.intellij.util.xml.DomManager;
import me.alvin.learn.domain.DagActionClassMeta;
import me.alvin.learn.domain.DependService;
import me.alvin.learn.domain.xml.DagGraph;
import me.alvin.learn.domain.xml.Unit;
import me.alvin.learn.utils.JacksonUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: Li Xiang
 * Date: 2021/12/6
 * Time: 9:59 AM
 */
public class XmlExtractAction extends AnAction {
    private static final Logger LOG =
            Logger.getInstance(XmlExtractAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        final GlobalSearchScope allScope = GlobalSearchScope.allScope(project);

        //获取所有Action类名
        DomManager domManager = DomManager.getDomManager(project);
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);
        XmlFile xmlFile = (XmlFile) psiFile;
        DagGraph dagGraph = domManager.getFileElement(xmlFile, DagGraph.class).getRootElement();
        List<Unit> units = dagGraph.getUnits().getUnits();

        List<DagActionClassMeta> classMetas = units.stream().map(unit -> {
            DagActionClassMeta dagClassMeta = new DagActionClassMeta();
            dagClassMeta.setId(unit.getId().getValue());
            dagClassMeta.setFullClassName(unit.getClz().getValue());
            dagClassMeta.setDescription(unit.getDescription().getValue());
            return dagClassMeta;
        }).collect(Collectors.toList());

        //获取所有类名对应的真实类的psi
        classMetas.stream().forEach(
                classMeta -> {
                    PsiClass theClass = JavaPsiFacade.getInstance(project).findClass(classMeta.getFullClassName(), allScope);
                    if (Objects.nonNull(theClass)) {
                        classMeta.setPsiClass(theClass);
                    } else {
                        LOG.warn("can't find classPsi:" + classMeta.getFullClassName());
                    }
                }
        );

        //获取Action所依赖的service信息
        classMetas.stream()
                .filter(metaClass -> Objects.nonNull(metaClass.getPsiClass()))
                .forEach(
                        metaClass -> {
                            //过滤掉不需要分析的field
                            List<PsiField> psiServiceFields = Stream.of(metaClass.getPsiClass().getFields())
                                    .filter(psiField -> {
                                        PsiType type = psiField.getType();
                                        if ("org.slf4j.Logger".equals(type.getCanonicalText())) {
                                            return false;
                                        }
                                        if (psiField.hasAnnotation("javax.annotation.Resource")) {
                                            return true;
                                        }
                                        if (psiField.hasAnnotation("org.springframework.beans.factory.annotation.Autowired")) {
                                            return true;
                                        }
                                        return false;
                                    }).collect(Collectors.toList());
                            metaClass.setDependServiceFields(psiServiceFields);

                            List<DependService> dependServices = psiServiceFields.stream()
                                    .map(psiServiceField -> {
                                        //获取field字段所属类的全名
                                        String serviceFieldClass = psiServiceField.getType().getCanonicalText();
                                        String fieldName = psiServiceField.getName();
                                        //搜索类中对该service方法的调用
                                        Query<PsiReference> fieldQuery = ReferencesSearch.search(psiServiceField);
                                        List<String> refMethods = fieldQuery.findAll().stream()
                                                .filter(psiRef -> {
                                                            if (psiRef.getElement().getParent() instanceof PsiReturnStatement) {
                                                                return false;
                                                            }
                                                            return true;
                                                        }
                                                )
                                                //获取调用的方法名。由于是xxx.yyy()的模式，因此获取lastChild即为方法名
                                                .map(psiRef -> {
                                                    PsiReferenceExpression psiReferenceExpression = PsiTreeUtil.getTopmostParentOfType(psiRef.getElement(), PsiReferenceExpression.class);
                                                    String referenceStr = "";
                                                    if (Objects.nonNull(psiReferenceExpression)) {
                                                        referenceStr = psiReferenceExpression.getText();
                                                    }
                                                    return StringUtils.remove(referenceStr, fieldName + ".");
                                                })
                                                .filter(StringUtils::isNotBlank)
                                                .distinct()
                                                .collect(Collectors.toList());
                                        DependService theDependService = new DependService();
                                        theDependService.setService(serviceFieldClass);
                                        theDependService.setMethods(refMethods);
                                        return theDependService;
                                    })
                                    .collect(Collectors.toList());

                            metaClass.setDependServices(dependServices);
                        });
        System.out.println(JacksonUtils.toJson(classMetas));
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        super.update(event);
        Project project = event.getProject();

        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);

        boolean isEnable = project != null && psiFile != null && (psiFile.getLanguage().is(XMLLanguage.INSTANCE));


        event.getPresentation().setEnabledAndVisible(isEnable);
    }
}
