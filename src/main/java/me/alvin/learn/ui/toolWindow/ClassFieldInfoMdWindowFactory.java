package me.alvin.learn.ui.toolWindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * @author: Li Xiang
 * Date: 2022/1/7
 * Time: 10:39 AM
 */
public class ClassFieldInfoMdWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ClassFieldInfoMdWindow fieldInfoWindow = new ClassFieldInfoMdWindow(toolWindow);

        ProjectService projectService = project.getService(ProjectService.class);
        projectService.setFieldInfoWindow(fieldInfoWindow);

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(fieldInfoWindow.getFieldMdContent(), "", false);
        toolWindow.getContentManager().addContent(content);
    }

    public static class ProjectService{
        private ClassFieldInfoMdWindow fieldInfoWindow;

        public ClassFieldInfoMdWindow getFieldInfoWindow() {
            return fieldInfoWindow;
        }

        public void setFieldInfoWindow(ClassFieldInfoMdWindow fieldInfoWindow) {
            this.fieldInfoWindow = fieldInfoWindow;
        }
    }
}
