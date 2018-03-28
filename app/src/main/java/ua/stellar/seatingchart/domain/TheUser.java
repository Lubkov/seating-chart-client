package ua.stellar.seatingchart.domain;

public class TheUser {

	private Long id;
	private String name;
	private Integer role;
	
	public TheUser() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getRole() {
		return role;
	}

	public void setRole(Integer role) {
		this.role = role;
	}
}
