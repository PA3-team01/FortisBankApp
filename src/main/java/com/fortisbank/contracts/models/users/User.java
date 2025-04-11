package com.fortisbank.contracts.models.users;

    import com.fortisbank.contracts.models.others.Notification;

    import java.io.Serializable;
    import java.util.ArrayList;
    import java.util.List;

    /**
     * Abstract class representing a user.
     */
    public abstract class User implements Serializable {

        /**
         * The user ID.
         */
        protected String userId;

        /**
         * The email of the user.
         */
        protected String email;

        /**
         * The hashed password of the user.
         */
        protected String hashedPassword;

        /**
         * The hashed PIN of the user.
         */
        protected String PINHash;

        /**
         * The first name of the user.
         */
        protected String firstName;

        /**
         * The last name of the user.
         */
        protected String lastName;

        /**
         * The role of the user.
         */
        protected Role role;

        /**
         * The inbox containing notifications for the user.
         */

        protected transient List<Notification> inbox;

        /**
         * Constructor initializing a user with specified values.
         *
         * @param userId the user ID
         * @param firstName the first name of the user
         * @param lastName the last name of the user
         * @param email the email of the user
         * @param hashedPassword the hashed password of the user
         * @param pinHash the hashed PIN of the user
         * @param role the role of the user
         */
        protected User(String userId, String firstName, String lastName, String email,
                       String hashedPassword, String pinHash, Role role) {
            this.userId = userId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.hashedPassword = hashedPassword;
            this.PINHash = pinHash;
            this.role = role;
            this.inbox = new ArrayList<>(); // Initialize inbox as an empty list
        }

        /**
         * No-arg constructor for deserialization.
         */
        protected User() {
            // No-arg constructor for deserialization
        }

        /**
         * Returns the user ID.
         *
         * @return the user ID
         */
        public String getUserId() {
            return userId;
        }

        /**
         * Returns the email of the user.
         *
         * @return the email of the user
         */
        public String getEmail() {
            return email;
        }

        /**
         * Returns the hashed password of the user.
         *
         * @return the hashed password of the user
         */
        public String getHashedPassword() {
            return hashedPassword;
        }

        /**
         * Returns the hashed PIN of the user.
         *
         * @return the hashed PIN of the user
         */
        public String getPINHash() {
            return PINHash;
        }

        /**
         * Returns the first name of the user.
         *
         * @return the first name of the user
         */
        public String getFirstName() {
            return firstName;
        }

        /**
         * Returns the last name of the user.
         *
         * @return the last name of the user
         */
        public String getLastName() {
            return lastName;
        }

        /**
         * Returns the role of the user.
         *
         * @return the role of the user
         */
        public Role getRole() {
            return role;
        }

        /**
         * Returns the full name of the user.
         *
         * @return the full name of the user
         */
        public String getFullName() {
            return firstName + " " + lastName;
        }

        /**
         * Sets the user ID.
         *
         * @param userId the user ID to set
         */
        public void setUserId(String userId) {
            this.userId = userId;
        }

        /**
         * Sets the email of the user.
         *
         * @param email the email to set
         */
        public void setEmail(String email) {
            this.email = email;
        }

        /**
         * Sets the hashed password of the user.
         *
         * @param hashedPassword the hashed password to set
         */
        public void setHashedPassword(String hashedPassword) {
            this.hashedPassword = hashedPassword;
        }

        /**
         * Sets the hashed PIN of the user.
         *
         * @param PINHash the hashed PIN to set
         */
        public void setPINHash(String PINHash) {
            this.PINHash = PINHash;
        }

        /**
         * Sets the first name of the user.
         *
         * @param firstName the first name to set
         */
        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        /**
         * Sets the last name of the user.
         *
         * @param lastName the last name to set
         */
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        /**
         * Sets the role of the user.
         *
         * @param role the role to set
         */
        public void setRole(Role role) {
            this.role = role;
        }

        /**
         * Returns the inbox containing notifications for the user.
         *
         * @return the inbox
         */
        public List<Notification> getInbox() {
            return inbox;
        }

        /**
         * Sets the inbox containing notifications for the user.
         *
         * @param inbox the inbox to set
         */
        public void setInbox(List<Notification> inbox) {
            this.inbox = inbox;
        }

        /**
         * Returns a string representation of the user.
         *
         * @return a string representation of the user
         */
        @Override
        public String toString() {
            return "User{" +
                    "userId='" + userId + '\'' +
                    ", fullName='" + getFullName() + '\'' +
                    ", email='" + email + '\'' +
                    ", role=" + role +
                    '}';
        }
    }