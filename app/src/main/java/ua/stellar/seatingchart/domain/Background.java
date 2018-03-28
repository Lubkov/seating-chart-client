package ua.stellar.seatingchart.domain;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;

public class Background implements Parcelable {

	private Long id;
	private Long number;
	private String name = "";
	private Integer width;
	private Integer height;
	//private Integer color;

	public Background() {
		
	}
	
	public Background(String name, 
			          Integer width, 
			          Integer height) {
		this.name = name;
		this.width = width;
		this.height = height;
	}

	public Background(Parcel in) {
		this.id = in.readLong();
		this.name = in.readString();
		this.height = in.readInt();
		this.width = in.readInt();
		this.number = in.readLong();
		//this.color = in.readInt();
	}

	public static int getRGBColor(Integer color) {

		if ((color != null) && (color >= 0)) {
			int a = (color >> 24) & 0xff;
			int r = (color >> 16) & 0xff;
			int g = (color >> 8) & 0xff;
			int b = (color) & 0xff;

			return Color.rgb(b, g, r);
		} else {
			return 0;
		}
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

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

//	public Integer getColor() {
//		return getRGBColor(color);
//	}
//
//	public void setColor(Integer color) {
//		this.color = color;
//	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(name);
		dest.writeInt(height);
		dest.writeInt(width);
		dest.writeLong(number);
		//dest.writeInt(color);
	}

	public static final Parcelable.Creator<Background> CREATOR = new Parcelable.Creator<Background>() {

		@Override
		public Background createFromParcel(Parcel in) {
			return new Background(in);
		}

		@Override
		public Background[] newArray(int size) {
			return new Background[size];
		}
	};
}
