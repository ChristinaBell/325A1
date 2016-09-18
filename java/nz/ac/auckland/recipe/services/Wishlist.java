package nz.ac.auckland.recipe.services;

import java.util.Set;

import javax.persistence.*;

import nz.ac.auckland.recipe.domain.*;

// Recipes that the user wishes to make 
@Entity
public class Wishlist {
	
	@Id
	public Long _id; 
	
	@ManyToOne
	private Baker _owner; 
	
	private String _name; 
	private String _description; 
	private Set<Recipe> _wishlistRecipes; 

	public Long getId(){
		return _id; 
	}
	
	public void setId(Long id){
		_id = id; 
	}
	
	public Baker getOwner(){
		return _owner; 
	}
	
	public void setOwner(Baker owner){
		_owner = owner; 
	}
	
	public String getName(){
		return _name; 
	}
	
	public void setName(String name){
		_name = name; 
	}
	
	public String getDescription(){
		return _description; 
	}
	
	public void setDescription(String description){
		_description = description; 
	}
	
	public Set<Recipe> getWishListRecipes(){
		return _wishlistRecipes; 
	}
	
	public void setWishList(Set<Recipe> wishlist){
		_wishlistRecipes = wishlist; 
	}
	
	
}
