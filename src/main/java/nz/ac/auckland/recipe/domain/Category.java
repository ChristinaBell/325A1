package nz.ac.auckland.recipe.domain;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;

/**
 * 
 * @author Christina Bell - cbel296 
 * 
 * This class represents the "Category" object in the web server.  
 * 
 * My web server represents a online service where users - called Bakers, can create and post recipes.  
 * Other users can then review these recipes.  Recipes also have categories.  
 * 
 * The category object defines a category of recipes. Categories are an enum type 
 *
 */

@XmlEnum(String.class)
public enum Category {
	
	CAKES, BISCUITS, BREAD, MUFFINS, SAVORY, SLICE; 
	
	/**
	 * Return a Gender value that corresponds to a String value.
	 */
	public static Category fromString(String text) {
	    if (text != null) {
	      for (Category c : Category.values()) {
	        if (text.equalsIgnoreCase(c.toString())) {
	          return c;
	        }
	      }
	    }
	    return null;
	  }
	
	
}
