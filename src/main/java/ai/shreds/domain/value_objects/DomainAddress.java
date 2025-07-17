package ai.shreds.domain.value_objects;

import java.util.Objects;

public class DomainAddress {
    private final String street;
    private final String city;
    private final String postalCode;
    private final String country;

    public DomainAddress(String street, String city, String postalCode, String country) {
        if (street == null || street.trim().isEmpty()) {
            throw new IllegalArgumentException("Street must not be empty");
        }
        if (city == null || city.trim().isEmpty()) {
            throw new IllegalArgumentException("City must not be empty");
        }
        if (postalCode == null || postalCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Postal code must not be empty");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Country must not be empty");
        }
        this.street = street;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }

    public boolean isComplete() {
        return street != null && !street.isEmpty()
                && city != null && !city.isEmpty()
                && postalCode != null && !postalCode.isEmpty()
                && country != null && !country.isEmpty();
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainAddress)) return false;
        DomainAddress that = (DomainAddress) o;
        return street.equals(that.street) && city.equals(that.city)
                && postalCode.equals(that.postalCode) && country.equals(that.country);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, postalCode, country);
    }

    @Override
    public String toString() {
        return "DomainAddress{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}