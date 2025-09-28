package grupo12.practico.models;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import grupo12.practico.dtos.User.UserDTO;

public abstract class User {
    private String id;
    private String document;
    private DocumentType documentType;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String email;
    private String phone;
    private LocalDate dateOfBirth;
    private String passwordHash;
    private String passwordSalt;
    private LocalDate passwordUpdatedAt;
    private String imageUrl;
    private String address;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    public User() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public LocalDate getPasswordUpdatedAt() {
        return passwordUpdatedAt;
    }

    public void setPasswordUpdatedAt(LocalDate passwordUpdatedAt) {
        this.passwordUpdatedAt = passwordUpdatedAt;
    }

    /**
     * Sets a plain text password and automatically hashes it with salt.
     * 
     * @param plainPassword The plain text password to hash
     */
    public void setPassword(String plainPassword) {
        if (plainPassword != null && !plainPassword.trim().isEmpty()) {
            this.passwordSalt = grupo12.practico.services.PasswordUtil.generateSalt();
            this.passwordHash = grupo12.practico.services.PasswordUtil.hashPassword(plainPassword, this.passwordSalt);
            this.passwordUpdatedAt = LocalDate.now();
        }
    }

    /**
     * Verifies a plain text password against the stored hash.
     * 
     * @param plainPassword The plain text password to verify
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String plainPassword) {
        if (this.passwordHash == null || this.passwordSalt == null) {
            return false;
        }
        return grupo12.practico.services.PasswordUtil.verifyPassword(plainPassword, this.passwordHash,
                this.passwordSalt);
    }

    // Relationship methods - to be implemented by subclasses
    public void addHealthWorker(HealthWorker healthWorker) {
        // Default implementation - do nothing
    }

    public void addAffiliatedHealthProvider(Clinic clinic) {
        // Default implementation - do nothing
    }

    public ClinicalHistory getClinicalHistory() {
        return null; // Default implementation - no clinical history
    }

    public void setClinicalHistory(ClinicalHistory clinicalHistory) {
        // Default implementation - do nothing
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public UserDTO toDto() {
        UserDTO dto = new UserDTO();
        dto.setId(id);
        dto.setDocument(document);
        dto.setDocumentType(documentType);
        dto.setFirstName(firstName);
        dto.setLastName(lastName);
        dto.setGender(gender);
        dto.setEmail(email);
        dto.setPhone(phone);
        dto.setImageUrl(imageUrl);
        dto.setAddress(address);
        dto.setDateOfBirth(dateOfBirth);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);
        // Note: Password fields are intentionally not exposed in DTO for security
        return dto;
    }
}
