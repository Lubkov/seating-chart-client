package ua.stellar.seatingchart.domain;

public class Font {

	private Long id;
	private String name;
	private Integer color;
	private Integer size;
	private Integer style;
	
	public Font() {
		
	}

	public Font(final Integer color, 
			    final Integer size, 
			    final String name, 
			    final Integer style) {
		super();
		
		this.color = color;
		this.size = size;
		this.name = name;
		this.style = style;
	}
	
	public Long getId() { 
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getColor() {
		return color;
	}

	public void setColor(Integer color) {
		this.color = color;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getStyle() {
		return style;
	}

	public void setStyle(Integer style) {
		this.style = style;
	}
}
