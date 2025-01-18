package fileHandling.xml;


import fileHandling.xml.jaxb.generated.ex2.STLSheet;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import sheet.base.api.Sheet;

import java.io.File;
import java.io.InputStream;

public class XMLSheetLoader {

   public static Sheet loadXMLFile(String fileName) throws IllegalArgumentException, JAXBException {

      File file = new File(fileName);
      if (!file.exists()) {
         throw new IllegalArgumentException("File does not exist.");
      }
      if (!fileName.endsWith(".xml")) {
         throw new IllegalArgumentException("File is not an XML file.");
      }

      JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

      STLSheet stlSheet = (STLSheet) jaxbUnmarshaller.unmarshal(file);

      DataMapper mapper = new DataMapper();
      return mapper.mapSTLSheetToSheet(stlSheet);
   }

   public static Sheet getSheetFromInputStream(InputStream inputStream) throws IllegalArgumentException, JAXBException {
      if (inputStream == null) {
         throw new IllegalArgumentException("InputStream cannot be null.");
      }

      JAXBContext jaxbContext = JAXBContext.newInstance(STLSheet.class);
      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

      // Unmarshal the input stream directly
      STLSheet stlSheet = (STLSheet) jaxbUnmarshaller.unmarshal(inputStream);

      DataMapper mapper = new DataMapper();
      return mapper.mapSTLSheetToSheet(stlSheet);
   }

}
