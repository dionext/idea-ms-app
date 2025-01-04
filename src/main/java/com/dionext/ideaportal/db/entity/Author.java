package com.dionext.ideaportal.db.entity;


import com.dionext.ideaportal.db.HistoricalDate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "author")
public class Author {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "cannot be empty")
    @Size(min = 3, max = 50, message = "Artist name can be minimum 3 and maximum 30 characters long")
    @Column(nullable = false, length = 50)
    private String names;

    @NotEmpty
    @Column(nullable = false, length = 50)
    private String namep;

    @NotBlank
    @Column(columnDefinition="TEXT")
    private String info;

    @Column(name = "is_abstract", nullable = false)
    private boolean isAbstract;

    @Column(name = "year_left", nullable = false, length = 6)
    private String yearLeft;

    @Column(name = "year_right", nullable = false, length = 6)
    private String yearRight;
    @Column(length = 250)
    private String photo;
    @Column(name = "view_count")
    private Integer viewCount;

    public String getYearLeftStr() {
        if (getYearLeft() == null) return null;
        else {
            HistoricalDate d = new HistoricalDate();
            try {
                d.parseHistDate(getYearLeft());
                return d.formatHistDateForView();
            } catch (Exception ex) {
                return "Error parsing date: " + getYearLeft();
            }
        }
    }

    public String getYearFullStr() {
        StringBuilder str = new StringBuilder();
        if (getYearLeft() != null)
            str.append(getYearLeftStr());
        if (!(getYearLeft() == null && getYearRight() == null))
            str.append(" - ");
        if (getYearRight() != null)
            str.append(getYearRightStr());
        return str.toString();
    }

    public String getYearRightStr() {
        if (getYearRight() == null) return null;
        else {
            HistoricalDate d = new HistoricalDate();
            try {
                d.parseHistDate(getYearRight());
                return d.formatHistDateForView();
            } catch (Exception ex) {
                return "Error parsing date: " + getYearRight();
            }
        }
    }

    public String getName() {
        return getNames();
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getNamep() {
        return namep;
    }

    public void setNamep(String namep) {
        this.namep = namep;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean anAbstract) {
        isAbstract = anAbstract;
    }

    public String getYearLeft() {
        return yearLeft;
    }

    public void setYearLeft(String yearLeft) {
        this.yearLeft = yearLeft;
    }

    public String getYearRight() {
        return yearRight;
    }

    public void setYearRight(String yearRight) {
        this.yearRight = yearRight;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}