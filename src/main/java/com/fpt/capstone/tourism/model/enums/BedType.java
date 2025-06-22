package com.fpt.capstone.tourism.model.enums;

public enum BedType {
    _1_GIUONG_DOI("1 giường đôi"),
    _2_GIUONG_DON("2 giường đơn"),
    _1_GIUONG_DON("1 giường đơn"),
    _2_GIUONG_DOI("2 giường đôi"),
    _1_GIUONG_KING("1 giường King");

    private final String label;

    BedType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
