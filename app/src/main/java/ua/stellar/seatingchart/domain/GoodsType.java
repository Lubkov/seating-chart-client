package ua.stellar.seatingchart.domain;

public class GoodsType {

    private Long id;

    private String name = "";

    private Background background;

    private Font titleFont;

    public GoodsType() {

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

    public Background getBackground() {
        return background;
    }

    public void setBackground(Background background) {
        this.background = background;
    }

    public Font getTitleFont() {
        return titleFont;
    }

    public void setTitleFont(Font titleFont) {
        this.titleFont = titleFont;
    }
}
