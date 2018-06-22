package etech.dms;

import etech.dms.util.CabinetUtility;
import etech.dms.util.DataDefinitionUtility;
import etech.dms.util.DocumentUtility;
import etech.dms.util.FolderUtility;

public interface DmsService<Folder, DataDefinition, Field> {

	public abstract FolderUtility<Folder, DataDefinition, Field> getFolderUtility();
	
	public abstract DocumentUtility getDocumentUtility();

	public abstract DataDefinitionUtility<DataDefinition> getDataDefinitionUtility();
	
	public abstract CabinetUtility getCabinetUtility();
	
	public void complete();
}
