package com.devhub.util;

public final class AppConstants {

    // Instantiation ko rokne ke liye private constructor (Utility Class standard)
    private AppConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // --- Pagination and Sorting Defaults ---
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "new";
    
    // --- Post Sorting Properties ---
    public static final String SORT_BY_NEW = "new";
    public static final String SORT_BY_TOP = "top";
    public static final String SORT_BY_COMMENTS = "comments";

    // --- Validation Rules & Constraints ---
    public static final int USERNAME_MIN_LENGTH = 3;
    public static final int USERNAME_MAX_LENGTH = 50;
    public static final int PASSWORD_MIN_LENGTH = 8;
    
    // --- Success/Error Alert Flash Messages ---
    public static final String MSG_REGISTRATION_SUCCESS = "Account created successfully! Please log in.";
    public static final String MSG_POST_CREATE_SUCCESS = "Your post has been published successfully.";
    public static final String MSG_UNAUTHORIZED_ACTION = "You do not have permission to modify this content.";
    
    // --- Dynamic Karma System Formula Weights ---
    public static final int KARMA_POST_UPVOTE_WEIGHT = 10;
    public static final int KARMA_COMMENT_UPVOTE_WEIGHT = 5;
}