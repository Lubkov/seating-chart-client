package ua.stellar.seatingchart.domain;

import java.util.Date;

public class LockedGoods {

	private Long id;
	private Long layoutID;
	private Long goodsID;
	private Long userID;
	private String deviceId;
	private Date createDate;

	public LockedGoods() {
		
	}

	public LockedGoods(final Long layoutID, 
			           final Long goodsID, 
			           final Long userID, 
			           final String deviceId,
			           final Date createDate) {
		super();
		
		this.layoutID = layoutID;
		this.goodsID = goodsID;
		this.userID = userID;
		this.deviceId = deviceId;
		this.createDate = createDate;
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

	public Long getGoodsID() {
		return goodsID;
	}

	public void setGoodsID(Long goodsID) {
		goodsID = goodsID;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}	
	
}

