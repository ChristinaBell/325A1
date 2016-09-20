package nz.ac.auckland.recipe.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;

@SuppressWarnings("serial")
@Entity
@Access(AccessType.FIELD)
public class Category implements Serializable {
	
	@Id
	@XmlAttribute(name="id")
	private Long _id; 
	
	@XmlAttribute(name="category-name")
	private String _categoryName;
	
	@OneToMany(mappedBy="_category")
	@XmlAttribute(name="recipes")
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
	
//	@Override 
//	public String toString(){
//		return _id.toString() + "-" + _categoryName ; 
//	}
	
}
