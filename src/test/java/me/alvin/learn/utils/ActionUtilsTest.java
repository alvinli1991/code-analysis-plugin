package me.alvin.learn.utils;

import com.intellij.openapi.editor.Document;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.TestDataPath;
import me.alvin.base.MyJavaCodePsiParseTestCase;

/**
 * @author: Li Xiang
 * Date: 2021/12/28
 * Time: 7:45 PM
 */
public class ActionUtilsTest extends MyJavaCodePsiParseTestCase {

    public void testParseInputForConstant() {
        PsiClass theClass = myFixture.findClass("me.alvin.test.TestActionForConstantInput");
        PsiFile containingFile = theClass.getContainingFile();
        FileViewProvider fileViewProvider = containingFile.getViewProvider();
        Document document = fileViewProvider.getDocument();
        System.out.println(JacksonUtils.toJson(ActionUtils.parseConstantInputForActionClass(theClass,document,javaPsiFacade)));
    }

    public void testFindInputReferenceForConstant() {

    }
}