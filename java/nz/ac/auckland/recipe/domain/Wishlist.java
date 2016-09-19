package nz.ac.auckland.recipe.services;

import java.util.Set;

import javax.persistence.*;

import nz.ac.auckland.recipe.domain.*;

// Recipes that the user wishes to make 
@Entity
@XmlRootElement(name="wishlist")
@Access(AccessType.FIELD)
public class Wishlist {
	
	@Id
	@XmlAttribute("id")
	public Long _id; 
	
	@ManyToOne
	@XmlAttribute("owner")
	@XmlJavaTypeAdapter(value=BakerAdapter.class)
	private Baker _owner; 
	
	@XmlAttribute("name")
	private String _name;
	
	@XmlAttribute("description")
	private String _description;
	
	@XmlAttribute("wishlist-recipes")
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
