package com.space.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;


@Entity
@Table(name = "ship")
public class Ship {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "ship")
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "planet", nullable = false)
    private String planet;

    @Column(name = "shipType", nullable = false)
    @Enumerated(EnumType.STRING)
    private ShipType shipType;

    @Column(name = "prodDate", nullable = false)
    private Date prodDate;

    @Column(name = "isUsed", nullable = false)
    private Boolean isUsed;

    @Column(name = "speed", nullable = false)
    private Double speed;

    @Column(name = "crewSize", nullable = false)
    private Integer crewSize;

    @Column(name = "rating", nullable = false)
    private Double rating;

    public double calcRating(Boolean isUsed, Double speed, Date prodDate){
        double result;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(prodDate);
        int yearForRating = 3019 - calendar.get(Calendar.YEAR);

        BigDecimal tempRating;

        if (isUsed)
            tempRating = BigDecimal.valueOf((80 * this.speed * 0.5) / (yearForRating + 1));
        else
            tempRating = BigDecimal.valueOf((80 * this.speed * 1) / (yearForRating + 1));

        result = tempRating.setScale(2, RoundingMode.HALF_EVEN).doubleValue();

        return result;
    }

    public Ship(Long id, String name, String planet, ShipType shipType, Date prodDate,
                Boolean isUsed, Double speed, Integer crewSize, Double rating) {
        this.id = id;
        this.name = name;
        this.planet = planet;
        this.shipType = shipType;
        this.prodDate = prodDate;
        this.isUsed = isUsed;
        this.speed = speed;
        this.crewSize = crewSize;
        this.rating = calcRating(isUsed, speed, prodDate);
    }

    public Ship() {
    }

    @Override
    public String toString() {
        return "Ship{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", planet='" + planet + '\'' +
                ", shipType=" + shipType +
                ", prodDate=" + prodDate +
                ", isUsed=" + isUsed +
                ", speed=" + speed +
                ", crewSize=" + crewSize +
                ", rating=" + rating +
                '}';
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPlanet() {
        return planet;
    }

    public ShipType getShipType() {
        return shipType;
    }

    public Date getProdDate() {
        return prodDate;
    }

    public Boolean getUsed() {
        return isUsed;
    }

    public Double getSpeed() {
        return speed;
    }

    public Integer getCrewSize() {
        return crewSize;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPlanet(String planet) {
        this.planet = planet;
    }

    public void setShipType(ShipType shipType) {
        this.shipType = shipType;
    }

    public void setProdDate(Date prodDate) {
        this.prodDate = prodDate;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setCrewSize(int crewSize) {
        this.crewSize = crewSize;
    }
}