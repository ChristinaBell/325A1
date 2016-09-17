package nz.ac.auckland.recipe.domain;

import java.util.Set;
import javax.persistence.*;

@Entity
public class Category {
	
	@Id
	private Long _id; 
	private String _categoryName;
	
	@OneToMany(mappedBy="_category") 
	private Set<Recipe> _recipes;

	
	public void setId(Long id){
		_id = id; 
	}
	
	public Long getId(){
		return _id; 
	}
	
	public void setCategoryName(String name){
		_categoryName = name; 
	}
	
	public String getCategoryName(){
		return _categoryName; 
	}
	
	public void setRecipes(Set<Recipe> recipes){
		_recipes = recipes; 
	}
	
	public Set<Recipe> getRecipes(){
		return _recipes; 
	}
	
}
