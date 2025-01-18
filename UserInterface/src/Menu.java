import fileHandling.SaverLoader;
import jakarta.xml.bind.JAXBException;
import sheet.base.api.Sheet;
import sheet.cell.Cell;
import sheet.base.impl.SheetImpl;
import sheet.management.api.SheetManager;
import sheet.management.impl.SheetManagerImpl;
import fileHandling.xml.XMLSheetLoader;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class Menu {

    private Scanner scanner;
    private SheetManager sheetManager;

    public Menu() {
        scanner = new Scanner(System.in);
        sheetManager = null;
    }

    private void setSheetManager(Sheet importedSheet) {
        this.sheetManager = new SheetManagerImpl(importedSheet);
    }

    private void setSheetManager(SheetManager importedSheetManager) {
        this.sheetManager = new SheetManagerImpl(importedSheetManager);
    }

    private SheetManager getSheetManager() {return sheetManager;}

    public void resetSheetManager() {this.sheetManager = null;}

    private void showMenu(){
        System.out.println(" ");
        System.out.println("----- Shticel Main Menu -----");
        System.out.println("1. Load sheet from XML");
        System.out.println("2. Show the sheet");
        System.out.println("3. Show a single cell details");
        System.out.println("4. Update a single cell details");
        System.out.println("5. Show versions");
        System.out.println("6. Save sheet to File");
        System.out.println("7. Load sheet from File");
        System.out.println("8. Exit the program");
        System.out.println(" ");
        System.out.print("Please select an option: ");
    }

    public void runMenu() {
        while (true) {
            showMenu();
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                MenuOption option = MenuOption.fromValue(choice);
                handleSelection(option);
            } catch (IllegalArgumentException | IllegalStateException e) {
                printException(e);
            }
        }
    }




    private void handleSelection(MenuOption option) {
        switch (option) {
            case LOAD_SHEET:
                handleLoadSheetXML();
                break;
            case SHOW_SHEET:
                if(getSheetManager() == null)
                    throw new IllegalStateException("You must load a sheet first!");
                 SheetLayout.displaySheet(sheetManager.getCurrentSheet());
                break;
            case SHOW_CELL_VALUE:
                if(getSheetManager() == null)
                    throw new IllegalStateException("You must load a sheet first!");
                handleShowCell();
                break;
            case UPDATE_CELL_VALUE:
                if(getSheetManager() == null)
                    throw new IllegalStateException("You must load a sheet first!");
                HandleUpdateCell();
                break;
            case SHOW_VERSIONS:
                if(getSheetManager() == null)
                    throw new IllegalStateException("You must load a sheet first!");
              //  showVersionHistory();
                break;
            case SAVE_SHEET_TO_FILE:
                if(getSheetManager() == null)
                    throw new IllegalStateException("You must load a sheet first!");
                handleSaveSheet();
                    break;
            case LOAD_SHEET_FROM_FILE:
                handleLoadSheet();
                break;
            case EXIT:
                System.out.println("Exiting the system...");
                System.exit(0);
                break;
            default:
                throw new IllegalStateException("Invalid selection!");
        }
    }

    private void handleLoadSheetXML(){
        System.out.print("Please enter the path to your XML file (Type 'b'/'B' to go back): ");
        String xmlFilePath = scanner.nextLine();
        if(xmlFilePath.equalsIgnoreCase("b"))
            return;
        try {
            setSheetManager(XMLSheetLoader.loadXMLFile(xmlFilePath));
            getSheetManager().getCurrentSheet().updateAllValues();
            getSheetManager().getVersionManager().saveSheetVersion(getSheetManager().getCurrentSheet());
            System.out.println("Sheet: " + getSheetManager().getCurrentSheet().getName() + " has been loaded successfully!");
        }
        catch (IllegalArgumentException | IllegalStateException | JAXBException e) {
            resetSheetManager();
            printException(e);
            handleLoadSheetXML();
        }
    }

    private void handleShowCell() {
        // Step 1: Get the cell coordinates (e.g., "B3")
        System.out.println(" ");
        System.out.print("Enter the cell you would like to view (e.g., B3). (Type 'b'/'B' to go back): ");
        String cellPosition = scanner.nextLine().toUpperCase().trim();
        if(cellPosition.equalsIgnoreCase("b"))
            return;
        // Step 2: Retrieve and display current cell details (before update)
        try {
            Optional<Cell> maybeCell = Optional.ofNullable(sheetManager.getCurrentSheet().getCell(cellPosition));
            if (maybeCell.isPresent()) {
                Cell currentCell = maybeCell.get();
                System.out.println("Current details of the cell:");
                System.out.println("Cell ID: " + cellPosition);
                System.out.println("Original Value: " + currentCell.getOriginalValue());
                System.out.println("Effective Value: " + currentCell.getEffectiveValue().getValue());
                System.out.println("Last updated in version: " + currentCell.getVersion());
                printCellDependencies(currentCell);
            } else {
                System.out.println("Cell " + cellPosition + " is currently empty.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            printException(e);
            handleShowCell();
        }
    }

    private void printCellDependencies(Cell cell) {
        System.out.println("Cell: " + cell.getCoordinate().toString());

        // Print dependsOn list
        System.out.println("Depends on:");
        if (cell.getDependsOn().isEmpty()) {
            System.out.println("  None");
        } else {
            for (Cell dependentCell : cell.getDependsOn()) {
                System.out.println(" " + dependentCell.getCoordinate().toString());
            }
        }
        // Print influencingOn list
        System.out.println("Influencing on:");
        if (cell.getInfluencingOn().isEmpty()) {
            System.out.println("  None");
        } else {
            for (Cell influencingCell : cell.getInfluencingOn()) {
                System.out.println(" " + influencingCell.getCoordinate().toString());
            }
        }
    }

    private void HandleUpdateCell() {
        // Step 1: Get the cell coordinates (e.g., "B3")
        System.out.println(" ");
        System.out.print("Enter the cell to update (e.g., B3). (Type 'b'/'B' to go back): ");
        String cellPosition = scanner.nextLine().toUpperCase().trim();
        if(cellPosition.equalsIgnoreCase("b"))
            return;
        // Step 2: Retrieve and display current cell details (before update)
        try {
            Optional<Cell> maybeCell = Optional.ofNullable(sheetManager.getCurrentSheet().getCell(cellPosition));
            if (maybeCell.isPresent()) {
                Cell currentCell = maybeCell.get();
                System.out.println("Current details of the cell:");
                System.out.println("Cell ID: " + cellPosition);
                System.out.println("Original Value: " + currentCell.getOriginalValue());
                System.out.println("Effective Value: " + currentCell.getEffectiveValue().getValue());
            } else {
                System.out.println("Cell " + cellPosition + " is currently empty.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            printException(e);
            HandleUpdateCell();
            return;
        }

        // Step 3: Get the new value
        System.out.print("Enter the new value. (Type 'b'/'B' to go back) : ");
        String value = scanner.nextLine().trim();
        if(cellPosition.equalsIgnoreCase("b"))
            return;
        // Step 4: Update the cell
        try {
            sheetManager.updateCell(cellPosition, value, " ","Final");
        } catch (IllegalArgumentException | IllegalStateException e) {
            printException(e);
            innerUpdateCell(cellPosition);
        }
        SheetLayout.displaySheet(sheetManager.getCurrentSheet());
    }

    private void innerUpdateCell(String cellID){
        System.out.println(" ");
        System.out.print("Enter the new value: ");
        String value = scanner.nextLine().trim();

        try {
            sheetManager.getCurrentSheet().setCell(cellID, value, " ","Final");
        } catch (IllegalArgumentException e) {
            printException(e);
            innerUpdateCell(cellID);
        }
    }

    private void handleSaveSheet() {
        System.out.print("Enter the file path to save the sheet. (Type 'b'/'B' to go back): ");
        String filePath = scanner.nextLine();
        if(filePath.equalsIgnoreCase("b"))
            return;
        try {
            SaverLoader.saveSheetToFile(this.sheetManager, filePath);
        } catch (IOException e) {
            System.out.println("----------------ERROR----------------");
            System.out.println("Failed to save the sheet: " + e.getMessage());
            System.out.println("Make sure it include the full fileName and ending (e.g. C:\\temp\\write_file_name_here.txt)");
            System.out.println("-------------------------------------");
            handleSaveSheet();
        }
    }

    private void handleLoadSheet() {
        System.out.print("Enter the file path to load the sheet. (Type 'b'/'B' to go back): ");
        String filePath = scanner.nextLine();
        if(filePath.equalsIgnoreCase("b"))
            return;
        try {
            setSheetManager(SaverLoader.loadSheetFromFile(filePath)); // Replace current sheet with loaded one
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("----------------ERROR----------------");
            System.out.println("Failed to load the sheet: " + e.getMessage());
            System.out.println("Make sure it include the full fileName and ending (e.g. C:\\temp\\write_file_name_here.txt)");
            System.out.println("-------------------------------------");
            handleLoadSheet();
        }
    }

    private void printException(Exception e) {
        System.out.println("----------------ERROR----------------");
        System.out.println(e.getMessage());
        System.out.println("Please try again.");
        System.out.println("-------------------------------------");
    }
}
