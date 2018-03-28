package ua.stellar.seatingchart.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Layout implements Parcelable {

    private Long id;

    private String name = "";

    private Integer number = 0;

    private Background background;

    public Layout() {

    }

    public Layout(Long id, String name, Integer position, Background background) {
        this.id = id;
        this.name = name;
        this.number = position;
        this.background = background;
    }

    protected Layout(Parcel in) {
        id = in.readLong();
        name = in.readString();
        number = in.readInt();
        background = in.readParcelable(getClass().getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeInt(number);
        dest.writeParcelable(background, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Layout> CREATOR = new Creator<Layout>() {
        @Override
        public Layout createFromParcel(Parcel in) {
            return new Layout(in);
        }

        @Override
        public Layout[] newArray(int size) {
            return new Layout[size];
        }
    };

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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Background getBackground() {
        return background;
    }

    public void setBackground(Background background) {
        this.background = background;
    }
}
