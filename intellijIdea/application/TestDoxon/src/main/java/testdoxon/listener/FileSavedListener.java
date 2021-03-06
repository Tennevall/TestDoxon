package testdoxon.listener;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.ui.components.JBList;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import testdoxon.model.TDFile;
import testdoxon.utils.DoxonUtils;
import testdoxon.utils.TDStatics;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class FileSavedListener implements ApplicationComponent, BulkFileListener {

    private final MessageBusConnection messageBusConnection;
    private JBList testMethodList;
    private JTextArea header;

    public FileSavedListener(JBList testMethodList, JTextArea header) {
        this.testMethodList = testMethodList;
        this.header = header;
        this.messageBusConnection = ApplicationManager.getApplication().getMessageBus().connect();
    }

    public void startToListen() {
        this.messageBusConnection.subscribe(VirtualFileManager.VFS_CHANGES, this);
    }

    @Override
    public void after(@NotNull List<? extends VFileEvent> events) {
        if(events != null) {
            Project[] projects = ProjectManager.getInstance().getOpenProjects();

            if (projects != null && projects.length > 0) {
                VirtualFile[] files = FileEditorManager.getInstance(projects[0]).getSelectedFiles();

                if (files != null && files.length > 0) {
                    for (VFileEvent event : events) {
                        if (files[0] != null && files[0].exists() && event.getFile().getCanonicalPath().equals(files[0].getCanonicalPath()) &&
                                (event.getFile().getName().matches("^Test.*\\.java") || event.getFile().getName().matches(".*Test\\.java"))) {

                            // The same file as the opened file and its a test class
                            TDStatics.currentTestFile = new TDFile(new File(event.getFile().getPath()));
                            DoxonUtils.setListItems(this.testMethodList, this.header);
                        }
                    }
                }
            }
        }
    }
}
