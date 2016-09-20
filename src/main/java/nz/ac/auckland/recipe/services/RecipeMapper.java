package nz.ac.auckland.recipe.services;
import nz.ac.auckland.recipe.domain.*;


/**
 * Helper class to convert between domain-model and DTO objects representing
 * Parolees.
 * 
 * @author Ian Warren
 *
 */
public class RecipeMapper {

	static Recipe toDomainModel(nz.ac.auckland.recipe.dto.Recipe dtoRecipe) {
		Recipe fullRecipe = new Recipe(
				dtoRecipe.getId(), 
				dtoRecipe.getName(),
				dtoRecipe.getContent(),
				dtoRecipe.getAuthor(),
				dtoRecipe.getCategory()
				);
		return fullRecipe;
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
