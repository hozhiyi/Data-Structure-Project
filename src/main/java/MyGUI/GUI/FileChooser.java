package MyGUI.GUI;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class FileChooser {
    private static final String DEFAULT_DIR = "./Sample Input";

    public static File chooseFile() {
        JFileChooser fileChooser = new JFileChooser();

        // set default directory
        fileChooser.setCurrentDirectory(new File(DEFAULT_DIR));

        // Filter the text files
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TXT files", "txt");
        fileChooser.setFileFilter(filter);

        // only show files, not the directories
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // select file to open
        int response = fileChooser.showOpenDialog(null);

        // select file to save
        //int response = fileChooser.showSaveDialog(null);

        // successfully select item
        if (response == JFileChooser.APPROVE_OPTION) {
            // get path of selected file
            File file = new File(fileChooser.getSelectedFile().getAbsolutePath());

            System.out.println(file);
            return file;
        }
        return null;
    }
}
