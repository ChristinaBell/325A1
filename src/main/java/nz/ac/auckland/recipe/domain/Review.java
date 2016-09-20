package nz.ac.auckland.recipe.domain;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import javax.xml.bind.annotation.XmlRootElement;


@SuppressWarnings("serial")
@Embeddable 
@Access(AccessType.FIELD)
@XmlRootElement(name="review")
public class Review implements Serializable {
 

	@Column(nullable=false, name="CONTENT")
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
	
		

}
