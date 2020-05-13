package com.unitn.disi.lpsmt.racer.api.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * User mapping
 *
 * @author Carlo Corradini
 */
public class User {
    /**
     * Password minimum length in chars
     */
    public static final short PASSWORD_MIN_LENGTH = 8;
    /**
     * Favorite number minimum inclusive value
     */
    public static final int FAVORITE_NUMBER_MIN = 1;
    /**
     * Favorite number maximum inclusive value
     */
    public static final int FAVORITE_NUMBER_MAX = 99;

    /**
     * User Genders
     */
    public enum Gender {
        /**
         * Male Gender
         */
        @SerializedName("male")
        MALE("male"),

        /**
         * Female Gender
         */
        @SerializedName("female")
        FEMALE("female"),

        /**
         * Unknown Gender
         */
        @SerializedName("unknown")
        UNKNOWN("unknown");

        /**
         * Value of the Gender
         */
        private final String value;

        /**
         * Construct a Gender enum
         *
         * @param value Value of the Gender
         */
        Gender(String value) {
            this.value = value;
        }

        /**
         * Return the value of the current Gender
         *
         * @return Gender value
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * User Roles
     */
    public enum Role {
        /**
         * Admin Role
         */
        @SerializedName("admin")
        ADMIN("admin"),

        /**
         * Standard Role
         */
        @SerializedName("standard")
        STANDARD("standard");

        /**
         * Value of the Role
         */
        private final String value;

        /**
         * Construct a Role enum
         *
         * @param value Value of the Role
         */
        Role(String value) {
            this.value = value;
        }

        /**
         * Return the value of the current Role
         *
         * @return Role value
         */
        public String getValue() {
            return value;
        }
    }

    /**
     * Id of the User
     */
    @SerializedName("id")
    @Expose
    public UUID id;

    /**
     * Username of the User
     */
    @SerializedName("username")
    @Expose
    public String username;

    /**
     * Password of the User
     */
    @SerializedName("password")
    @Expose
    public String password;

    /**
     * Role of the User
     *
     * @see Role
     */
    @SerializedName("role")
    @Expose
    public Role role;

    /**
     * Name of the User
     */
    @SerializedName("name")
    @Expose
    public String name;

    /**
     * Surname of the User
     */
    @SerializedName("surname")
    @Expose
    public String surname;

    /**
     * Gender of the User
     *
     * @see Gender
     */
    @SerializedName("gender")
    @Expose
    public Gender gender;

    /**
     * Date of Birth of the User
     */
    @SerializedName("date_of_birth")
    @Expose
    public LocalDate dateOfBirth;

    /**
     * Residence of the User
     */
    @SerializedName("residence")
    @Expose
    public String residence;

    /**
     * Email of the User
     */
    @SerializedName("email")
    @Expose
    public String email;

    /**
     * Avatar image url of the User
     */
    @SerializedName("avatar")
    @Expose
    public URL avatar;

    /**
     * Favorite Number of the User
     */
    @SerializedName("favorite_number")
    @Expose
    public Short favoriteNumber;

    /**
     * Favorite Car id of the User
     */
    @SerializedName("favorite_car")
    @Expose
    public Long favoriteCar;

    /**
     * Favorite Circuit id of the User
     */
    @SerializedName("favorite_circuit")
    @Expose
    public Long favoriteCircuit;

    /**
     * Hated Circuit id of the User
     */
    @SerializedName("hated_circuit")
    @Expose
    public Long hatedCircuit;

    /**
     * List of Championship ids that the User is subscribed to
     */
    @SerializedName("championships")
    @Expose
    public List<Long> championships;

    /**
     * Creation {@link LocalDateTime} of the User
     */
    @SerializedName("created_at")
    @Expose
    public LocalDateTime createdAt;

    /**
     * Update {@link LocalDateTime} of the User
     */
    @SerializedName("updated_at")
    @Expose
    public LocalDateTime updatedAt;

    /**
     * Construct an User class
     *
     * @param id              Id of the User
     * @param username        Username of the User
     * @param password        Password of the User
     * @param role            Role of the User
     * @param name            Name of the User
     * @param surname         Surname of the User
     * @param gender          Gender of the User
     * @param dateOfBirth     Date of Birth of the User
     * @param residence       Residence of the User
     * @param email           Email of the User
     * @param avatar          Avatar of the User
     * @param favoriteNumber  Favorite Number of the User
     * @param favoriteCar     Favorite Car id of the User
     * @param favoriteCircuit Favorite Circuit id of the User
     * @param hatedCircuit    Hated Circuit id of the User
     * @param championships   List of Championship ids that the User is subscribed to
     * @param createdAt       Creation {@link LocalDateTime} of the User
     * @param updatedAt       Update {@link LocalDateTime} of the User
     */
    public User(UUID id, String username, String password, Role role, String name, String surname, Gender gender, LocalDate dateOfBirth, String residence, String email, URL avatar, Short favoriteNumber, Long favoriteCar, Long favoriteCircuit, Long hatedCircuit, List<Long> championships, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.surname = surname;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.residence = residence;
        this.email = email;
        this.avatar = avatar;
        this.favoriteNumber = favoriteNumber;
        this.favoriteCar = favoriteCar;
        this.favoriteCircuit = favoriteCircuit;
        this.hatedCircuit = hatedCircuit;
        this.championships = championships;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Construct an empty User class
     */
    public User() {
    }
}
