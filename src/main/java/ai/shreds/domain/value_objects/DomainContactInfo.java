package ai.shreds.domain.value_objects;

import java.util.Objects;
import java.util.regex.Pattern;

public class DomainContactInfo {
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9 .-]{7,20}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final DomainAddress address;
    private final String phoneNumber;
    private final String email;
    private final String contactPerson;

    public DomainContactInfo(DomainAddress address, String phoneNumber, String email, String contactPerson) {
        if (address == null || !address.isComplete()) {
            throw new IllegalArgumentException("Address must be complete");
        }
        if (phoneNumber == null || !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalArgumentException("Phone number must be a valid international format");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email must be a valid format");
        }
        if (contactPerson == null || contactPerson.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact person must have name and role");
        }
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.contactPerson = contactPerson;
    }

    public DomainAddress getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainContactInfo)) return false;
        DomainContactInfo that = (DomainContactInfo) o;
        return address.equals(that.address) && phoneNumber.equals(that.phoneNumber)
                && email.equals(that.email) && contactPerson.equals(that.contactPerson);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, phoneNumber, email, contactPerson);
    }

    @Override
    public String toString() {
        return "DomainContactInfo{" +
                "address=" + address +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", contactPerson='" + contactPerson + '\'' +
                '}';
    }
}