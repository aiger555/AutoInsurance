package com.ain.insuranceservice.model;


public enum VehicleType {
    PASSENGER_CAR("Легковой автомобиль"),
    TRUCK("Грузовой автомобиль"),
    ELECTRIC_CAR("Электромобиль"),
    MOTORCYCLE("Мотоцикл"),
    TRAILER("Прицеп"),
    SEMI_TRAILER("Полуприцеп"),
    BUS("Автобус"),
    MINIBUS("Микроавтобус"),
    SPECIAL_VEHICLE("Спецтехника");

    private final String displayName;

    VehicleType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
