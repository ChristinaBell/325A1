package nz.ac.auckland.recipe.domain;

import java.util.Set;

import javax.xml.bind.annotation.*;
import javax.persistence.*;

import nz.ac.auckland.recipe.services.Wishlist;

@Entity
@XmlRootElement(name="baker")
@Access(AccessType.FIELD)
public class Baker {
	
	@Id
	@XmlAttribute(name="id")
	private Long _id;
	
	@XmlAttribute(name="username")
	private String _username;

	
	@XmlAttribute(name="recipes")
	@XmlJavaTypeAdapter(value=RecipeAdapter.class)
	@OneToMany(mappedBy = "_author")
	private Set<Recipe> _recipes; 
	
	@XmlAttribute(name="wishlists")
	@XmlJavaTypeAdapter(value=WishlistAdapter.class)
	@OneToMany(mappedBy = "_owner")
	private Set<Wishlist> _wishlists;  
	
	public Baker(String username, String lastname, String firstname, Set<Recipe> recipes) {
		_username = username;
		_recipes = recipes; 
	}
	
	public Baker(String username, String lastname, String firstname) {
		_username = username;
		_lastname = lastname;
		_firstname = firstname;
	}
	
	public Baker(String username) {
		this(username, null, null);
	}
	
	protected Baker() {}
	
	public Long getId() {
		return _id; 
	}
	
	public void setId(Long id){
		_id = id; 
	}
	
	public String getUsername() {
		return _username;
	}
	
	public void setUsername(String name){
		_username = name; 
	}
	
	public String getLastname() {
		return _lastname;
	}
	
	public void setLastname(String name){
		_lastname = name; 
	}
	
	public String getFirstname() {
		return _firstname;
	}
	
	public void setFirstname(String name){
		_firstname = name; 
	}
}
