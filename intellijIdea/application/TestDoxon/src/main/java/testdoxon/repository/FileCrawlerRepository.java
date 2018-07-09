package testdoxon.repository;

import testdoxon.gui.ClassComboBox;
import testdoxon.log.TDLog;
import testdoxon.model.TestFile;
import testdoxon.utils.DoxonUtils;

import java.io.File;
import java.util.ArrayList;

public class FileCrawlerRepository {
    private ArrayList<TestFile> testFiles;
    private ArrayList<String> foldersToCheck;

    /**
     * Constructor
     */
    public FileCrawlerRepository() {
        this.testFiles = new ArrayList<>();
        this.foldersToCheck = new ArrayList<>();
    }

    /**
     * Recursively walk through folders
     *
     * @param path
     */
    public void checkFolderHierarchy(String path, ClassComboBox testClassesComboBox) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                testFiles.clear();
                foldersToCheck.clear();

                listFolder(path);

                while (foldersToCheck.size() != 0) {
                    for (String f : new ArrayList<String>(foldersToCheck)) {
                        listFolder(f);
                        foldersToCheck.remove(f);
                    }
                }

                TDLog.info("Testclasses in memory: " + testFiles.size(), TDLog.INFORMATION);
                DoxonUtils.setComboBoxItems(testClassesComboBox, testFiles.toArray(new TestFile[testFiles.size()]));
            }
        });
        thread.start();

    }

    /**
     * Checks content of folders, saves all TestXx...x.java classes
     *
     * @param path
     */
    private void listFolder(String path) {
        File file = new File(path);
        if (file != null && file.isDirectory()) {
            File[] files = file.listFiles();

            for (File f : files) {
                if (f.isFile()) {
                    if (f.getName().matches("^Test.*\\.java") || f.getName().matches(".*Test\\.java")) {
                        this.testFiles.add(new TestFile(f.getName(), f.getAbsolutePath()));
                    }
                } else if (f.isDirectory()) {
                    this.foldersToCheck.add(f.getAbsolutePath());
                }
            }
        }
    }

    public boolean listContains(String path) {
        for (TestFile testfile : testFiles) {
            if (testfile.getFilepath().equals(path)) {
                return true;
            }
        }
        return false;
    }

    public void addToList(TestFile testFile) {
        this.testFiles.add(testFile);
    }

    /**
     * @return ArrayList<TestFile>
     */
    public ArrayList<TestFile> getAllTestFiles() {
        return this.testFiles;
    }
}
