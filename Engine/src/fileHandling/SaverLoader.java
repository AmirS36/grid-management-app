package fileHandling;

import sheet.management.api.SheetManager;
import java.io.*;

public class SaverLoader {

    public static void saveSheetToFile(SheetManager sheetManager, String filePath) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(filePath);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(sheetManager);
            System.out.println("Sheet saved successfully to: " + filePath);
        }
    }

    public static SheetManager loadSheetFromFile(String filePath) throws IOException, ClassNotFoundException {
        try (FileInputStream fileIn = new FileInputStream(filePath);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            SheetManager sheetManager = (SheetManager) in.readObject();
            System.out.println("Sheet loaded successfully from: " + filePath);
            return sheetManager;
        }
    }
}
