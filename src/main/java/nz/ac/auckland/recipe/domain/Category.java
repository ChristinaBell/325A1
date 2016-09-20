package nz.ac.auckland.recipe.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author Christina Bell - cbel296 
 * 
 * This class represents the "Category" object in the web server.  
 * 
 * My web server represents a online service where users - called Bakers, can create and post recipes.  
 * Other users can then review these recipes.  Recipes also have categories.  
 * 
 * The category object defines a category of recipes. Categories have an id and a name 
 *
 */

@SuppressWarnings("serial")
@Entity
@Access(AccessType.FIELD)
public class Category implements Serializable {
	
	@Id
	@XmlAttribute(name="id")
	private Long _id; 
	
	@XmlElement(name="category-name")
	private String _categoryName;
	
	@OneToMany(mappedBy="_category")
	@XmlElement(name="recipes")
	private Set<Recipe> _recipes;
	
	protected Category(){
		
	}

	public Category(String categoryAsString) {
		_categoryName = categoryAsString; 
	}

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
	
	@Override 
	public String toString(){
		return _id.toString() + "," + _categoryName ; 
	}
	
}
