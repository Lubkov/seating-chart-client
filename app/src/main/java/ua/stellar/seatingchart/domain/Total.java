package ua.stellar.seatingchart.domain;

public class Total {

	private Long id;
	private String name;
	private Integer amount;
	private Integer amountAll;
	private Integer color;
	private Integer borderColor;
	private Integer borderSize;

	public Total() {
		
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


	public Integer getAmount() {
		return amount;
	}


	public void setAmount(Integer amount) {
		this.amount = amount;
	}


	public Integer getAmountAll() {
		return amountAll;
	}


	public void setAmountAll(Integer amountAll) {
		this.amountAll = amountAll;
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
}
