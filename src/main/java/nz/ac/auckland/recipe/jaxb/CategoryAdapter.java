package nz.ac.auckland.recipe.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import nz.ac.auckland.recipe.domain.Category;

import org.joda.time.DateTime;

/**
 * JAXB XML adapter to convert between Joda DateTime instances and Strings.
 * DateTime objects are marshalled as Strings, and unmarshalled back into 
 * DateTime instances.
 *
 */
public class CategoryAdapter extends XmlAdapter<String, Category> {

	@Override
	public Category unmarshal(String categoryAsString) throws Exception {
		if(categoryAsString == null) {
			return null;
		}
		
		return new Category(categoryAsString);
	}

	@Override
	public String marshal(Category category) throws Exception {
		if(category == null) {
			return null;
		}
		
		return category.toString();
	}
}