package etech.omni.helper;

import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import etech.omni.ngo.NGOAddFolder;
import etech.omni.ngo.NGOResponse;

public class NGOHelper {

	public static synchronized <T extends NGOResponse> T getNGOResponseAsPOJO(Class<T> classType, String xmlResponse) {

		T ngoResponse = null;

		try {
			
			StringReader stringReader = new StringReader(xmlResponse);

			JAXBContext jaxbContext = JAXBContext.newInstance(classType);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			ngoResponse = (T) jaxbUnmarshaller.unmarshal(stringReader);

			ngoResponse.setOriginalXML(xmlResponse);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return ngoResponse;
	}
	
	public static <T> T getResponseAsPOJO(Class<T> classType, String xmlResponse) {

		T ngoResponse = null;

		try {
			
			StringReader stringReader = new StringReader(xmlResponse);

			JAXBContext jaxbContext = JAXBContext.newInstance(classType);

			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			
			ngoResponse = (T) jaxbUnmarshaller.unmarshal(stringReader);
			
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return ngoResponse;
	}

	public static void main(String... args) throws Exception {
		
		//String xmlResponse = "<?xml version=\"1.0\"?><NGOAddDocument_Output><Option>NGOAddDocument</Option><Status>-1000</Status><Error>java.lang.StringIndexOutOfBoundsException: String index out of range: -1</Error></NGOAddDocument_Output>";
		String xmlResponse = new String(Files.readAllBytes(new File("E:\\mywork\\omni-dms-impl\\src\\etech\\omni\\helper\\xmltest.xml").toPath()));
		//System.out.println(xmlResponse);
		NGOAddFolder ngoResponse = new NGOHelper().getResponseAsPOJO(NGOAddFolder.class, xmlResponse);
								
		System.out.println("Name : " + ngoResponse.getFolder().getFolderName());
		System.out.println("DF Name : " + ngoResponse.getFolder().getDataDefinition().getDataDefIndex());
		System.out.println("DF : " + ngoResponse.getFolder().getDataDefinition().getFields().size());
		ngoResponse.getFolder().getDataDefinition().getFields().entrySet().forEach(entry -> System.out.println(entry.getKey() + "  " + entry.getValue().getIndexValue()));
	}

}
