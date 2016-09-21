package nz.ac.auckland.recipe.domain;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import nz.ac.auckland.recipe.dto.Recipe;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 
 * @author Christina Bell - cbel296 
 * 
 * This class represents the "Review" object in the web server.  
 * 
 * My web server represents a online service where users - called Bakers, can create and post recipes.  
 * Other users can then review these recipes.  Recipes also have categories.  
 * 
 * The review object keeps track of a review or comment on a recipe. Reviews have only a content string and 
 * are embeddable - i.e. a value type 
 */

@SuppressWarnings("serial")
@Embeddable 
@Access(AccessType.FIELD)
@XmlRootElement(name="review")
public class Review implements Serializable {
 
	@Column(nullable=false, name="CONTENT")
	@XmlElement(name="content")
	private String _content; 
	
	protected Review(){
		
	}
	
	public Review(String reviewAsString) {
		// TODO Auto-generated constructor stub
	}

	
	public String getContent(){
		return _content; 
	}
	
	public void setContent(String content){
		_content = content; 
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Recipe))
            return false;
        if (obj == this)
            return true;

        Recipe rhs = (Recipe) obj;
        return new EqualsBuilder().
            append(_content, rhs.getContent()).
            isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31). 
	            append(_content).
	            toHashCode();
	}
	
		

}
