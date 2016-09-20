
package nz.ac.auckland.recipe.services;
import nz.ac.auckland.recipe.domain.Recipe;
import nz.ac.auckland.recipe.dto.*;

/**
 * Helper class to convert between domain-model and DTO objects representing
 * Recipes.
 * 
 * @author Christina Bell 
 *
 */
public class BakerMapper {

	static Recipe toDomainModel(nz.ac.auckland.recipe.dto.Recipe dtoRecipe) {
		Recipe fullParolee = new Recipe(
				dtoRecipe.getId(), 
				dtoRecipe.getName(),
				dtoRecipe.getContent(),
				dtoRecipe.getAuthor(),
				dtoRecipe.getCategory()
				);
		return fullParolee;
	}
	
	static nz.ac.auckland.recipe.dto.Recipe toDto(Recipe recipe) {
		nz.ac.auckland.recipe.dto.Recipe dtoParolee = 
				new nz.ac.auckland.recipe.dto.Recipe(
						recipe.getId(),
						recipe.getName(),
						recipe.getContent(),
						recipe.getAuthor(),
						recipe.getCategory(),
						recipe.getMostRecentReview()
						);
		return dtoParolee;
		
	}
}
