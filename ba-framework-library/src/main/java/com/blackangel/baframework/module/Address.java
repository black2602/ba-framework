package com.blackangel.baframework.module;

/**
 * Created by Finger-kjh on 2017-07-20.
 */

public class Address {
    private String postalCode;
    private String streetAddress;
    private String legacyAddress;

    public Address(String postalCode, String streetAddress, String legacyAddress) {
        this.postalCode = postalCode;
        this.streetAddress = streetAddress;
        this.legacyAddress = legacyAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getLegacyAddress() {
        return legacyAddress;
    }

    public void setLegacyAddress(String legacyAddress) {
        this.legacyAddress = legacyAddress;
    }
}
