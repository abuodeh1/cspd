package test;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import etech.dms.DmsService;
import etech.dms.exception.FolderException;
import etech.omni.OmniService;
import omnidocs.pojo.DataDefinition;
import omnidocs.pojo.Field;
import omnidocs.pojo.Folder;

public class DmsTest {

	DmsService<Folder, DataDefinition, Field> omniService;
	
	@Before
	public void setUp() throws Exception {
		omniService = new OmniService("192.168.60.148", 3333, true);
	}

	@After
	public void tearDown() throws Exception {
		omniService.complete();
	}
	
	public void addFolder() {
		try {
			Folder oFolder = new Folder();
			oFolder.setFolderName("Hamzah1");
			Folder f = omniService.getFolderUtility().addFolder("61325", oFolder);
		} catch (FolderException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getFolder() {
		try {
			
			//OFolder oFolder = omniService.getFolderUtility().getFolder("2014");
			assertNotNull( omniService.getFolderUtility().getFolder("2014") );
			
			//System.out.println(oFolder.getFolderName());
			
			
			
		} catch (FolderException e) {
			e.printStackTrace();
			
			
			
			
		}
	}

}
