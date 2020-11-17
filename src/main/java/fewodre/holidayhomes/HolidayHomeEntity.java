package fewodre.holidayhomes;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class HolidayHomeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private String id;


	public String getId() {
		return id;
	}
}
