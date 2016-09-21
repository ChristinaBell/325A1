
package nz.ac.auckland.recipe.domain;

import java.util.Set;

import nz.ac.auckland.recipe.jaxb.*; 

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nz.ac.auckland.recipe.domain.*;

/**
 * 
 * @author Christina Bell - cbel296 
 * 
 * This class represents the "WishList" object in the web server.  
 * 
 * My web server represents a online service where users - called Bakers, can create and post recipes.  
 * Other users can then review these recipes.  Recipes also have categories.  
 * 
 * The WishList object keeps track of the recipes a user wishes to bake. Wishlists have an owner, a name and a set of recipes  
 *
 */

// Recipes that the user wishes to make 
@Entity
@XmlRootElement(name="wishlist")
@Access(AccessType.FIELD)
public class Wishlist {
	
	@Id
	@XmlAttribute(name="id")
	public Long _id; 
	
	@OneToOne
	@XmlAttribute(name="owner")
	@XmlJavaTypeAdapter(value=BakerAdapter.class)
	private Baker _owner; 
	
	@XmlAttribute(name="name")
	private String _name;
	
	@XmlAttribute(name="description")
	private String _description;
	
	@XmlAttribute(name="wishlist-recipes")
	@OneToMany
	private Set<Recipe> _wishlistRecipes; 

	public Wishlist(String wishlistAsString) {
		_name=wishlistAsString; 
	}

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
