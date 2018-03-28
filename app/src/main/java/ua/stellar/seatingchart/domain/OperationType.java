package ua.stellar.seatingchart.domain;

public class OperationType {

	private Long id;
	private String name;
	private Integer color;
	private Integer borderColor;
	private Integer borderSize;
	private Boolean actived;

	public OperationType() {

	}

	public OperationType(final Long id,
						 final String name,
						 final Integer color,
						 final Integer borderColor,
						 final Integer borderSize,
						 final Boolean actived) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.borderColor = borderColor;
		this.borderSize = borderSize;
		this.actived = actived;
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

	public Integer getColor() {
		return color;
	}

	public void setColor(Integer color) {
		this.color = color;
	}

	public Integer getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Integer borderColor) {
		this.borderColor = borderColor;
	}

	public Integer getBorderSize() {
		return borderSize;
	}

	public void setBorderSize(Integer borderSize) {
		this.borderSize = borderSize;
	}

	public Boolean getActived() {
		return actived;
	}

	public void setActived(Boolean actived) {
		this.actived = actived;
	}
}
