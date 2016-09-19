package nz.ac.auckland.recipe.domain;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import nz.ac.auckland.recipe.domain.XmlRootElement;

@Embeddable 
@Access(AccessType.FIELD)
@XmlRootElement(name="review")
public class Review {
	
	@Id
	private Long _id; 

	@Column(nullable=false, name="CONTENT")
	private String _content; 
	
	protected Review(){
		
	}
	
	public void setId(Long id){
		_id = id; 
	}
	
	public Long getId(){
		return _id; 
	}
	
	public String getContent(){
		return _content; 
	}
	
	public void setContent(String content){
		return _content; 
	}
	
		

}
