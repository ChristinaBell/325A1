package nz.ac.auckland.recipe.domain;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.persistence.*;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import nz.ac.auckland.recipe.domain.*;
import nz.ac.auckland.recipe.jaxb.*;

/**
 * 
 * @author Christina Bell - cbel296 
 * 
 * This class represents the "Baker" object in the web server.  
 * 
 * My web server represents a online service where users - called Bakers, can create and post recipes.  
 * Other users can then review these recipes.  Recipes also have categories.  
 * 
 * The baker object keeps track of a user. Bakers have an id and a user name, as well as the set of recipes they have authored. 
 *
 */

@SuppressWarnings("serial")
@Entity
@XmlRootElement(name="baker")
@Access(AccessType.FIELD)
public class Baker implements Serializable{
	
	@Id
	@XmlAttribute(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long _id;
	
	@XmlElement(name="username")
	private String _username;
	
	@XmlElement(name="baker-recipes")
	@OneToMany(mappedBy = "_author")
	private Set<Recipe> _recipes;  
		
	//Constructors 
	
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
	
	public Baker() {}
	
	
	// Getters and setters  
	
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
	
	@Override
	public boolean equals(Object obj){
		Baker other = (Baker) obj; 
		if ((this.getId().equals(other.getId())) && this.getUsername().equals(other.getUsername()) ){
			return true; 
		} else {
			return false; 
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_id).
	            append(_username).
	            toHashCode();
	}
	
	
	
	
}
