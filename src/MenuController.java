import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.swing.JFileChooser;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


/**
 * <p>The controller for the menu</p>
 *
 * @author Ian F. Darwin, ian@darwinsys.com, Gert Florijn, Sylvia Stuurman
 * @version 1.6 2014/05/16 Sylvia Stuurman
 */
public class MenuController extends MenuBar {

    protected static final String ABOUT = "About";
    protected static final String FILE = "File";
    protected static final String EXIT = "Exit";
    protected static final String GOTO = "Go to";
    protected static final String HELP = "Help";
    protected static final String NEW = "New";
    protected static final String NEXT = "Next";
    protected static final String OPEN = "Open";
    protected static final String PAGENR = "Page number?";
    protected static final String PREV = "Prev";
    protected static final String SAVE = "Save";
    protected static final String VIEW = "View";
    protected static String TESTFILE = "testPresentation.xml";
    protected static String SAVEFILE = "savedPresentation.xml";
    protected static final String IOEX = "IO Exception: ";
    protected static final String LOADERR = "Load Error";
    protected static final String SAVEERR = "Save Error";
    private static final long serialVersionUID = 227L;
    private final Frame parent; //The frame, only used as parent for the Dialogs
    private final Presentation presentation; //Commands are given to the presentation


    public MenuController(Frame frame, Presentation pres) {
        parent = frame;
        presentation = pres;
        MenuItem menuItem;
        Menu fileMenu = new Menu(FILE);
        fileMenu.add(menuItem = mkMenuItem(OPEN));
        menuItem.addActionListener(actionEvent -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML File", "xml"));
            fileChooser.setAcceptAllFileFilterUsed(false);
            int result = fileChooser.showOpenDialog(parent);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                File fileToCopy = new File(selectedFile.toString());
                File newPresentationFile = new File("openPresentation.xml");
                try {
                    Files.copy(fileToCopy.toPath(), newPresentationFile.toPath(), REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                TESTFILE = "openPresentation.xml";
                presentation.clear();
            }
            Accessor xmlAccessor = new XMLAccessor();
            try {
                xmlAccessor.loadFile(presentation, TESTFILE);
                presentation.setSlideNumber(0);
            } catch (IOException exc) {
                JOptionPane.showMessageDialog(parent, IOEX + exc,
                        LOADERR, JOptionPane.ERROR_MESSAGE);
            }
            parent.repaint();
        });
        fileMenu.add(menuItem = mkMenuItem(NEW));

        menuItem.addActionListener(actionEvent -> {
                String saveFile = saveFile();
                File samplePresentation = new File("testPresentation.xml");
                File newPresentation = new File(saveFile);
                try {
                    Files.copy(samplePresentation.toPath(), newPresentation.toPath());
                    java.awt.Desktop.getDesktop().edit(newPresentation);
                } catch (IOException e) {
                    e.printStackTrace();
                }

        });


        fileMenu.add(menuItem = mkMenuItem(SAVE));
        menuItem.addActionListener(e -> {

            Accessor xmlAccessor = new XMLAccessor();
            SAVEFILE = saveFile();

            try {
                xmlAccessor.saveFile(presentation, SAVEFILE);
            } catch (IOException exc) {
                JOptionPane.showMessageDialog(parent, IOEX + exc,
                        SAVEERR, JOptionPane.ERROR_MESSAGE);
            }
        });
        fileMenu.addSeparator();
        fileMenu.add(menuItem = mkMenuItem(EXIT));
        menuItem.addActionListener(actionEvent -> presentation.exit(0));
        add(fileMenu);
        Menu viewMenu = new Menu(VIEW);
        viewMenu.add(menuItem = mkMenuItem(NEXT));
        menuItem.addActionListener(actionEvent -> presentation.nextSlide());
        viewMenu.add(menuItem = mkMenuItem(PREV));
        menuItem.addActionListener(actionEvent -> presentation.prevSlide());
        viewMenu.add(menuItem = mkMenuItem(GOTO));
        menuItem.addActionListener(actionEvent -> {
            String pageNumberStr = JOptionPane.showInputDialog(PAGENR);
            if (pageNumberStr.isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Page number cannot be empty");
            }
            int pageNumber = Integer.parseInt(pageNumberStr);
            if (pageNumber <= 0) {
                JOptionPane.showMessageDialog(parent, "Page number cannot be smaller than 0");
            } else if (pageNumber >= presentation.getSize() + 1) {
                JOptionPane.showMessageDialog(parent, "Page number cannot be bigger than number of slides available. \n There are " + presentation.getSize() + " slides available");
            } else {
                presentation.setSlideNumber(pageNumber - 1);
            }
        });
        add(viewMenu);
        Menu helpMenu = new Menu(HELP);
        helpMenu.add(menuItem = mkMenuItem(ABOUT));
        menuItem.addActionListener(actionEvent -> JOptionPane.showMessageDialog(parent,
                """
                        JabberPoint is a primitive slide-show program in Java(tm). It
                        is freely copyable as long as you keep this notice and
                        the splash screen intact.
                        Copyright (c) 1995-1997 by Ian F. Darwin, ian@darwinsys.com.
                        Adapted by Gert Florijn (version 1.1) and Sylvia Stuurman (version 1.2 and higher) for the OpenUniversity of the Netherlands, 2002 -- now.
                        Author's version available from https://www.darwinsys.com/""",
                "About JabberPoint",
                JOptionPane.INFORMATION_MESSAGE
        ));
        setHelpMenu(helpMenu);        //Needed for portability (Motif, etc.).
    }

    //Creating a menu-item
    public MenuItem mkMenuItem(String name) {
        return new MenuItem(name, new MenuShortcut(name.charAt(0)));
    }

    public String saveFile(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save JabberPoint presentation");
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("XML File", "xml"));
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setAcceptAllFileFilterUsed(false);
        int userSelection = fileChooser.showSaveDialog(parent);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            return fileToSave + ".xml";
        }
        return null;
    }
}
