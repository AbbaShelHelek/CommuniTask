package com.example.communitask.model;

/**
 * Firestore user profile document for a registered CommuniTask member.
 */
public class UserProfile {

    private String uid;
    private String displayName;
    private String email;
    private long createdAtMillis;

    public UserProfile() {
    }

    public UserProfile(String uid, String displayName, String email, long createdAtMillis) {
        this.uid = uid;
        this.displayName = displayName;
        this.email = email;
        this.createdAtMillis = createdAtMillis;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getCreatedAtMillis() {
        return createdAtMillis;
    }

    public void setCreatedAtMillis(long createdAtMillis) {
        this.createdAtMillis = createdAtMillis;
    }
}
