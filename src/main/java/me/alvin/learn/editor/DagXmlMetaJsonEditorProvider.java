package me.alvin.learn.editor;

import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorPolicy;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.fileEditor.impl.text.TextEditorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import me.alvin.learn.domain.xml.DagGraph;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * @author: Li Xiang
 * Date: 2021/12/30
 * Time: 2:31 PM
 */
public class DagXmlMetaJsonEditorProvider implements FileEditorProvider {
    private static final Logger LOGGER = Logger.getInstance(DagXmlMetaJsonEditorProvider.class);

    public static final String DAG_META_JSON = "dag-meta-json";
    public static final String TEST_JSON = "[{\"input_core\":{\"input_src_type\":\"CLASS_FIELD\",\"src_data_type\":{\"full_type_name\":\"java.util.Map<java.lang.String,java.lang.String>\",\"type_name\":\"Map<String, String>\"},\"src_field\":{\"type\":{\"full_type_name\":\"java.util.Map<java.lang.String,java.lang.String>\",\"type_name\":\"Map<String, String>\"},\"name\":\"MAP_CONSTANT\",\"reference_name\":\"me.alvin.test.TestConstant#MAP_CONSTANT\",\"final\":false},\"src_input_method\":null},\"has_var_holder\":false,\"var_name_holders\":[],\"input_src_types\":null,\"input_src_code_blocks\":[{\"line_number\":23,\"code_block\":\"TestConstant.MAP_CONSTANT\"}],\"input_references\":[{\"input_reference_type\":\"METHOD_CALL\",\"code_block\":{\"line_number\":23,\"code_block\":\"TestConstant.MAP_CONSTANT.get(\\\"hello\\\")\"},\"action_inner\":true,\"inner_ref_level\":0}],\"relate_child_inputs\":[]},{\"input_core\":{\"input_src_type\":\"CLASS_FIELD\",\"src_data_type\":{\"full_type_name\":\"java.lang.String\",\"type_name\":\"String\"},\"src_field\":{\"type\":{\"full_type_name\":\"java.lang.String\",\"type_name\":\"String\"},\"name\":\"A\",\"reference_name\":\"me.alvin.test.TestActionForConstantInput#A\",\"final\":false},\"src_input_method\":null},\"has_var_holder\":false,\"var_name_holders\":[],\"input_src_types\":null,\"input_src_code_blocks\":[{\"line_number\":16,\"code_block\":\"A\"}],\"input_references\":[{\"input_reference_type\":\"METHOD_CALL\",\"code_block\":{\"line_number\":16,\"code_block\":\"System.out.println(A)\"},\"action_inner\":true,\"inner_ref_level\":0}],\"relate_child_inputs\":[]},{\"input_core\":{\"input_src_type\":\"CLASS_FIELD\",\"src_data_type\":{\"full_type_name\":\"java.lang.String\",\"type_name\":\"String\"},\"src_field\":{\"type\":{\"full_type_name\":\"java.lang.String\",\"type_name\":\"String\"},\"name\":\"KEY_1\",\"reference_name\":\"me.alvin.test.TestConstant#KEY_1\",\"final\":false},\"src_input_method\":null},\"has_var_holder\":false,\"var_name_holders\":[],\"input_src_types\":null,\"input_src_code_blocks\":[{\"line_number\":22,\"code_block\":\"TestConstant.KEY_1\"},{\"line_number\":15,\"code_block\":\"TestConstant.KEY_1\"},{\"line_number\":19,\"code_block\":\"TestConstant.KEY_1\"}],\"input_references\":[{\"input_reference_type\":\"BINARY\",\"code_block\":{\"line_number\":19,\"code_block\":\"TestConstant.KEY_1 != \\\"1\\\"\"},\"action_inner\":true,\"inner_ref_level\":0},{\"input_reference_type\":\"CONDITIONAL\",\"code_block\":{\"line_number\":22,\"code_block\":\"TestConstant.KEY_1 != \\\"1\\\"?1:2\"},\"action_inner\":true,\"inner_ref_level\":0},{\"input_reference_type\":\"METHOD_CALL\",\"code_block\":{\"line_number\":15,\"code_block\":\"System.out.println(TestConstant.KEY_1)\"},\"action_inner\":true,\"inner_ref_level\":0},{\"input_reference_type\":\"METHOD_CALL\",\"code_block\":{\"line_number\":19,\"code_block\":\"TestConstant.KEY_1.equals(\\\"2\\\")\"},\"action_inner\":true,\"inner_ref_level\":0},{\"input_reference_type\":\"IF_STATEMENT\",\"code_block\":{\"line_number\":19,\"code_block\":\"if(TestConstant.KEY_1.equals(\\\"2\\\") && TestConstant.KEY_1 != \\\"1\\\")\"},\"action_inner\":true,\"inner_ref_level\":0},{\"input_reference_type\":\"BINARY\",\"code_block\":{\"line_number\":22,\"code_block\":\"TestConstant.KEY_1 != \\\"1\\\"\"},\"action_inner\":true,\"inner_ref_level\":0},{\"input_reference_type\":\"BINARY\",\"code_block\":{\"line_number\":19,\"code_block\":\"TestConstant.KEY_1.equals(\\\"2\\\") && TestConstant.KEY_1 != \\\"1\\\"\"},\"action_inner\":true,\"inner_ref_level\":0}],\"relate_child_inputs\":[]}]";

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile file) {
        if (file.getFileType() != XmlFileType.INSTANCE) {
            return false;
        }
        XmlFile xmlFile = (XmlFile) PsiManager.getInstance(project).findFile(file);
        DomManager domManager = DomManager.getDomManager(project);
        DomFileElement<DagGraph> xmlFileElement = domManager.getFileElement(xmlFile, DagGraph.class);
        return xmlFileElement != null;
    }

    @Override
    public @NotNull FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile file) {
        VirtualFile vfs = createTestVirtualFile();
        if (vfs != null) {
            return TextEditorProvider.getInstance().createEditor(project, vfs);
        }
        return TextEditorProvider.getInstance().createEditor(project, file);
    }

    @Override
    public @NotNull @NonNls String getEditorTypeId() {
        return DAG_META_JSON;
    }

    @Override
    public @NotNull FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }

    private VirtualFile createTestVirtualFile() {
        try {
            File tempFile = FileUtil.createTempFile("action-meta", ".json", true);
            FileUtil.writeToFile(tempFile, TEST_JSON);
            VirtualFile virtualFile = VfsUtil.findFileByIoFile(tempFile, false);
            if (null == virtualFile) {
                throw new IOException("virtual file can't be open");
            }
            return virtualFile;
        } catch (IOException e) {
            LOGGER.error(e);
        }
        return null;
    }
}
