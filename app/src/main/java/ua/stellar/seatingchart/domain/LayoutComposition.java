package ua.stellar.seatingchart.domain;

public class LayoutComposition {
	
	private Long id;
	private Long layoutID;
	private Long goodID;
	private Long goodsTypeID;
	private String goodsTypeName;
	private String goodName;
	private Integer goodNumber;
	private Operation lastOper;
	private Integer positionX;
	private Integer positionY;
	private Integer height;	
	private Integer width;	
	private Integer backgroundAngle;
	private Integer titleAlign;
	private Integer titleAlignment;
	
	public LayoutComposition() {
		
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLayoutID() {
		return layoutID;
	}

	public void setLayoutID(Long layoutID) {
		this.layoutID = layoutID;
	}

	public Long getGoodID() {
		return goodID;
	}

	public void setGoodID(Long goodID) {
		this.goodID = goodID;
	}

	public Long getGoodsTypeID() {
		return goodsTypeID;
	}

	public void setGoodsTypeID(Long goodsTypeID) {
		this.goodsTypeID = goodsTypeID;
	}

	public String getGoodName() {
		return goodName;
	}

	public void setGoodName(String goodName) {
		this.goodName = goodName;
	}

	public Integer getGoodNumber() {
		return goodNumber;
	}

	public void setGoodNumber(Integer goodNumber) {
		this.goodNumber = goodNumber;
	}

	public Operation getLastOper() {
		return lastOper;
	}

	public void setLastOper(Operation lastOper) {
		this.lastOper = lastOper;
	}

	public Integer getPositionX() {
		return positionX;
	}

	public void setPositionX(Integer positionX) {
		this.positionX = positionX;
	}

	public Integer getPositionY() {
		return positionY;
	}

	public void setPositionY(Integer positionY) {
		this.positionY = positionY;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getBackgroundAngle() {
		return backgroundAngle;
	}

	public void setBackgroundAngle(Integer backgroundAngle) {
		this.backgroundAngle = backgroundAngle;
	}

	public Integer getTitleAlign() {
		return titleAlign;
	}

	public void setTitleAlign(Integer titleAlign) {
		this.titleAlign = titleAlign;
	}

	public Integer getTitleAlignment() {
		return titleAlignment;
	}

	public void setTitleAlignment(Integer titleAlignment) {
		this.titleAlignment = titleAlignment;
	}

	public String getGoodsTypeName() {
		return goodsTypeName;
	}

	public void setGoodsTypeName(String goodsTypeName) {
		this.goodsTypeName = goodsTypeName;
	}
}
