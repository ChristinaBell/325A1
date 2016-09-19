package nz.ac.auckland.parolee.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateTime;

/**
 * JAXB XML adapter to convert between Joda DateTime instances and Strings.
 * DateTime objects are marshalled as Strings, and unmarshalled back into 
 * DateTime instances.
 *
 */
public class RecipeAdapter extends XmlAdapter<String, Recipe> {

	@Override
	public Recipe unmarshal(String recipeAsString) throws Exception {
		if(recipeAsString == null) {
			return null;
		}
		
		return new Recipe(recipeAsString);
	}

	@Override
	public String marshal(Recipe recipe) throws Exception {
		if(recipe == null) {
			return null;
		}
		
		return recipe.toString();
	}
}