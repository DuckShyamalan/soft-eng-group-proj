package org.group15;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.nio.file.Paths;
//import java.awt.*;

public class FileSelect {

    private String dir = null;



    // Window title does NOT display on Mac. Switched over to JFileChooser.
    /*
    public String selectFile(String windowPrompt){

        FileDialog dialog = new FileDialog((Frame)null, windowPrompt);
        dialog.setFilenameFilter((dir, file) -> file.toLowerCase().endsWith(".csv"));

        dialog.setMode(FileDialog.LOAD);
        dialog.setVisible(true);
        String file = dialog.getDirectory() + dialog.getFile();
        if (file.equals("nullnull")) {
            return null;
        } else {
            return file;
        }
    }
    */

    // File selection functionality.
    public String selectCSVFile(String windowPrompt){
        JFileChooser chooser;
        if(dir != null) chooser = new JFileChooser(dir);
        else chooser = new JFileChooser();
        chooser.setDialogTitle(windowPrompt);

        // Limit choice to CSV files.
        FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            dir = chooser.getSelectedFile().getParentFile().getAbsolutePath();


            return chooser.getSelectedFile().getAbsoluteFile().toString();
        }
        return null;
    }

    public String selectADCFile(String windowPrompt){
        JFileChooser chooser;
        if(dir != null) chooser = new JFileChooser(dir);
        else chooser = new JFileChooser();
        chooser.setDialogTitle(windowPrompt);

        // Limit choice to ADC files.
        FileNameExtensionFilter filter = new FileNameExtensionFilter("ADC files", "adc");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            dir = chooser.getSelectedFile().getParentFile().getAbsolutePath();


            return chooser.getSelectedFile().getAbsoluteFile().toString();
        }
        return null;
    }

    public String setADCFile(String windowPrompt) {
        JFileChooser chooser;
        if(dir != null) chooser = new JFileChooser(dir);
        else chooser = new JFileChooser();
        chooser.setDialogTitle(windowPrompt);

        // Limit choice to ADC files.
        FileNameExtensionFilter filter = new FileNameExtensionFilter("ADC files", "adc");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
            dir = chooser.getSelectedFile().getParentFile().getAbsolutePath();

            return chooser.getSelectedFile().getAbsoluteFile().toString();
        }
        return null;
    }

}