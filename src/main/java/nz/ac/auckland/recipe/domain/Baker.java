package nz.ac.auckland.recipe.domain;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.persistence.*;

import nz.ac.auckland.recipe.domain.*;
import nz.ac.auckland.recipe.jaxb.*;


@SuppressWarnings("serial")
@Entity
@XmlRootElement(name="baker")
@Access(AccessType.FIELD)
public class Baker implements Serializable{
	
	@Id
	@XmlAttribute(name="id")
	private Long _id;
	
	@XmlAttribute(name="username")
	private String _username;
	
	@XmlAttribute(name="recipes")
	@XmlJavaTypeAdapter(value=RecipeAdapter.class)
	@OneToMany(mappedBy = "_author")
	private Set<Recipe> _recipes; 
	
//	@XmlAttribute(name="wishlist")
//	@XmlJavaTypeAdapter(value=WishlistAdapter.class)
//	@OneToOne
//	private Wishlist _wishlist;  
		
	public Baker(String username, String lastname, String firstname, Set<Recipe> recipes) {
		_username = username;
		_recipes = recipes; 
	}
	
	public Baker(String username, String lastname, String firstname) {
		_username = username;
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
	
	
	
	
}
