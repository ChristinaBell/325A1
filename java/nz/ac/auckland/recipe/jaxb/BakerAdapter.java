package nz.ac.auckland.recipe.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateTime;

/**
 * JAXB XML adapter to convert between Joda DateTime instances and Strings.
 * DateTime objects are marshalled as Strings, and unmarshalled back into 
 * DateTime instances.
 *
 */
public class BakerAdapter extends XmlAdapter<String, Baker> {

	@Override
	public Baker unmarshal(String bakerAsString) throws Exception {
		if(bakerAsString == null) {
			return null;
		}
		
		return new Baker(bakerAsString);
	}

	@Override
	public String marshal(Baker baker) throws Exception {
		if(baker == null) {
			return null;
		}
		
		return baker.toString();
	}
}