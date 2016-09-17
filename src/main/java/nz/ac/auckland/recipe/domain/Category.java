package nz.ac.auckland.recipe.domain;

import java.util.Set;
import javax.persistence.OneToMany;

public class Category {
	
	private String _categoryName;
	
	@OneToMany(mappedBy="category") 
	private Set<Recipe> _recipes;

	public void setCategoryName(String name){
		_categoryName = name; 
	}
	
}
