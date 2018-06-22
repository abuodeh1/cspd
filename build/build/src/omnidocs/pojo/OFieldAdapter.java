package omnidocs.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class OFieldAdapter extends XmlAdapter<OFieldAdapter.Fields, Map<String, Field>>{
	
	public static class Fields {
		@XmlElement(name = "Field")
		public List<Field> fields = new ArrayList<Field>();
	} 
	
	@Override
	public Map<String, Field> unmarshal(OFieldAdapter.Fields value) throws Exception {
		
		Map<String, Field> map = new HashMap<String, Field>();
		for( Field msg : value.fields ) 
			map.put( msg.getIndexName(), msg );
		
		return map;
	}

	@Override
	public OFieldAdapter.Fields marshal(Map<String, Field> map) throws Exception {

		Fields fields = new Fields();
		fields.fields = (List<Field>)map.values();
		
		return fields;
	}

}
