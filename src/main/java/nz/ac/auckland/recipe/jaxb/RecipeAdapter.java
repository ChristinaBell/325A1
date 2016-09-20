package nz.ac.auckland.recipe.jaxb;

import javax.persistence.EntityManager;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import nz.ac.auckland.recipe.PersistenceManager;
import nz.ac.auckland.recipe.domain.Baker;
import nz.ac.auckland.recipe.domain.Category;
import nz.ac.auckland.recipe.domain.Recipe;
import nz.ac.auckland.recipe.services.RecipeResource;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		
		Logger _logger = LoggerFactory
				.getLogger(RecipeResource.class);
		
		
		String[] args = recipeAsString.split("["); 
		Long id =  Long.parseLong(args[1]); 
		args = recipeAsString.split(";");
		
		String name = args[1]; 
		String content = args[2];
		
		String authorString = args[3];
		String categoryString = args[4];
//		String reviews = args[5]; 
		
		EntityManager em = PersistenceManager.instance().createEntityManager(); 
		
		Baker author = em.find(Baker.class, Long.parseLong(authorString)); 
		
		Category category = em.find(Category.class, Long.parseLong(categoryString)); 
						
		_logger.debug("id = " + id);
		_logger.debug("name = " + name);
		_logger.debug("content = " + content);
		_logger.debug("authorString = " + authorString);
		_logger.debug("id = " + id);
		
		return new Recipe(id, name, content, author, category );
	}

	@Override
	public String marshal(Recipe recipe) throws Exception {
		if(recipe == null) {
			return null;
		}
		
		return recipe.toString();
	}
}