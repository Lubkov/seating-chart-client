package ua.stellar.seatingchart.domain;

import java.util.Date;

public class Operation {

	private Long id;
	private Date createDate;
	private Date fromDate;
	private Date toDate;
	private String userName;
	private Long operationType;
	private Long goodsId;
	private Integer goodsNumber;
	private String goodsTypeName;
	private Integer layoutNumber;

	public Operation() {
		
	}

	public Operation(final Long id,
					 final Date createDate,
					 final Date fromDate,
					 final Date toDate,
					 final String userName,
					 final Long operationType,
					 final Long goodsId,
					 final Integer goodsNumber,
					 final String goodsName,
					 final Integer layoutNumber) {
		this.id = id;
		this.createDate = createDate;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.userName = userName;
		this.operationType = operationType;
		this.goodsId = goodsId;
		this.goodsNumber = goodsNumber;
		this.goodsTypeName = goodsName;
		this.layoutNumber = layoutNumber;
	}

	public Operation(final Operation source) {
		this.id = source.id;
		this.createDate = source.createDate;
		this.fromDate = source.fromDate;
		this.toDate = source.toDate;
		this.userName = source.userName;
		this.operationType = source.operationType;
		this.goodsId = source.goodsId;
		this.goodsNumber = source.goodsNumber;
		this.goodsTypeName = source.goodsTypeName;
		this.layoutNumber = source.layoutNumber;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setOperationType(Long operationType) {
		this.operationType = operationType;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public Integer getGoodsNumber() {
		return goodsNumber;
	}

	public void setGoodsNumber(Integer goodsNumber) {
		this.goodsNumber = goodsNumber;
	}

	public Long getOperationType() {
		return operationType;
	}

	public String getGoodsTypeName() {
		return goodsTypeName;
	}

	public void setGoodsTypeName(String goodsTypeName) {
		this.goodsTypeName = goodsTypeName;
	}

	public Integer getLayoutNumber() {
		return layoutNumber;
	}

	public void setLayoutNumber(Integer layoutNumber) {
		this.layoutNumber = layoutNumber;
	}
}
