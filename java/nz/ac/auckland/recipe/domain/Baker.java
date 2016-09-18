package nz.ac.auckland.recipe.domain;

import java.util.Set;

import javax.persistence.*;

import nz.ac.auckland.recipe.services.Wishlist;

@Entity
public class Baker {

	@Id
	private Long _id;
	
	private String _username;
	private String _lastname;
	private String _firstname;
	
	@OneToMany(mappedBy = "_author")
	private Set<Recipe> _recipes; 
	
	@OneToMany(mappedBy = "_owner")
	private Set<Wishlist> _wishlists;  
	
	public Baker(String username, String lastname, String firstname, Set<Recipe> recipes) {
		_username = username;
		_lastname = lastname;
		_firstname = firstname;
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
