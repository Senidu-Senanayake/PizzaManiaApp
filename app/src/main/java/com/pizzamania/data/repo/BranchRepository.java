package com.pizzamania.data.repo;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.pizzamania.data.db.AppDbHelper;
import com.pizzamania.data.model.Branch;

import java.util.ArrayList;
import java.util.List;

public class BranchRepository {
    private static final String TAG = "BranchRepository";
    private static final String PREFS_NAME = "pizza_mania_prefs";
    private static final String KEY_SELECTED_BRANCH = "selected_branch_id";
    private static final String KEY_DATA_INITIALIZED = "data_initialized";

    private final AppDbHelper dbHelper;
    private final SharedPreferences sharedPreferences;
    private final Context context;

    public BranchRepository(Context context) {
        this.context = context;
        this.dbHelper = new AppDbHelper(context);
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Initialize sample data if not already done
        if (!sharedPreferences.getBoolean(KEY_DATA_INITIALIZED, false)) {
            initializeSampleData();
        }
    }

    /**
     * Get all branches from database
     */
    public List<Branch> getAllBranches() {
        List<Branch> branches = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query("Branch",
                    null, null, null, null, null, "name ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Branch branch = createBranchFromCursor(cursor);
                    if (branch != null) {
                        branches.add(branch);
                    }
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting all branches", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return branches;
    }

    /**
     * Get active branches only
     */
    public List<Branch> getActiveBranches() {
        List<Branch> branches = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query("Branch",
                    null, "is_active = ?", new String[]{"1"},
                    null, null, "name ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Branch branch = createBranchFromCursor(cursor);
                    if (branch != null) {
                        branches.add(branch);
                    }
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting active branches", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return branches;
    }

    /**
     * Get branch by ID
     */
    public Branch getBranchById(int branchId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        Branch branch = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query("Branch",
                    null, "branch_id = ?", new String[]{String.valueOf(branchId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                branch = createBranchFromCursor(cursor);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting branch by ID: " + branchId, e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return branch;
    }

    /**
     * Search branches by name, address, or phone
     */
    public List<Branch> searchBranches(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllBranches();
        }

        List<Branch> branches = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String searchQuery = "%" + query.trim() + "%";
            cursor = db.query("Branch",
                    null,
                    "name LIKE ? OR address LIKE ? OR phone LIKE ?",
                    new String[]{searchQuery, searchQuery, searchQuery},
                    null, null, "name ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Branch branch = createBranchFromCursor(cursor);
                    if (branch != null) {
                        branches.add(branch);
                    }
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error searching branches with query: " + query, e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return branches;
    }

    /**
     * Get branches by city or area
     */
    public List<Branch> getBranchesByCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            return getAllBranches();
        }

        List<Branch> branches = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            String cityQuery = "%" + city.trim() + "%";
            cursor = db.query("Branch",
                    null,
                    "address LIKE ? OR name LIKE ?",
                    new String[]{cityQuery, cityQuery},
                    null, null, "name ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Branch branch = createBranchFromCursor(cursor);
                    if (branch != null) {
                        branches.add(branch);
                    }
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting branches by city: " + city, e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return branches;
    }

    /**
     * Get nearest branches based on coordinates
     */
    public List<Branch> getNearestBranches(double userLat, double userLng, int limit) {
        List<Branch> allBranches = getActiveBranches();
        List<BranchDistance> branchDistances = new ArrayList<>();

        for (Branch branch : allBranches) {
            double distance = calculateDistance(userLat, userLng, branch.getLat(), branch.getLng());
            branchDistances.add(new BranchDistance(branch, distance));
        }

        // Sort by distance
        branchDistances.sort((a, b) -> Double.compare(a.distance, b.distance));

        List<Branch> nearestBranches = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, branchDistances.size()); i++) {
            nearestBranches.add(branchDistances.get(i).branch);
        }

        return nearestBranches;
    }

    /**
     * Insert a new branch
     */
    public long insertBranch(Branch branch) {
        SQLiteDatabase db = null;
        long result = -1;

        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = createContentValuesFromBranch(branch);
            result = db.insert("Branch", null, values);

            if (result > 0) {
                Log.d(TAG, "Branch inserted successfully: " + branch.getName());
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error inserting branch: " + branch.getName(), e);
        } finally {
            if (db != null) db.close();
        }

        return result;
    }

    /**
     * Update an existing branch
     */
    public int updateBranch(Branch branch) {
        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = createContentValuesFromBranch(branch);
            rowsAffected = db.update("Branch", values,
                    "branch_id = ?", new String[]{String.valueOf(branch.getBranchId())});

            if (rowsAffected > 0) {
                Log.d(TAG, "Branch updated successfully: " + branch.getName());
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error updating branch: " + branch.getName(), e);
        } finally {
            if (db != null) db.close();
        }

        return rowsAffected;
    }

    /**
     * Delete a branch
     */
    public int deleteBranch(int branchId) {
        SQLiteDatabase db = null;
        int rowsAffected = 0;

        try {
            db = dbHelper.getWritableDatabase();
            rowsAffected = db.delete("Branch",
                    "branch_id = ?", new String[]{String.valueOf(branchId)});

            if (rowsAffected > 0) {
                Log.d(TAG, "Branch deleted successfully: ID " + branchId);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting branch: ID " + branchId, e);
        } finally {
            if (db != null) db.close();
        }

        return rowsAffected;
    }

    /**
     * Save selected branch preference
     */
    public void setSelectedBranch(int branchId) {
        sharedPreferences.edit()
                .putInt(KEY_SELECTED_BRANCH, branchId)
                .apply();
        Log.d(TAG, "Selected branch saved: ID " + branchId);
    }

    /**
     * Get selected branch
     */
    public Branch getSelectedBranch() {
        int branchId = sharedPreferences.getInt(KEY_SELECTED_BRANCH, -1);
        if (branchId != -1) {
            return getBranchById(branchId);
        }
        return null;
    }

    /**
     * Check if branch is currently open (simplified implementation)
     */
    public boolean isBranchOpen(Branch branch) {
        if (branch == null || !branch.isActive()) {
            return false;
        }

        // For now, return active status
        // In a real implementation, you'd parse working hours and compare with current time
        return true;
    }

    /**
     * Get branch count
     */
    public int getBranchCount() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        int count = 0;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT COUNT(*) FROM Branch", null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting branch count", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return count;
    }

    /**
     * Create Branch object from cursor
     */
    private Branch createBranchFromCursor(Cursor cursor) {
        try {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("branch_id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String address = cursor.getString(cursor.getColumnIndexOrThrow("address"));
            double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("lat"));
            double lng = cursor.getDouble(cursor.getColumnIndexOrThrow("lng"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));

            // Handle optional columns with defaults
            String email = getStringFromCursor(cursor, "email", "");
            String workingHours = getStringFromCursor(cursor, "working_hours", "10:00 AM - 11:00 PM");
            boolean isActive = getBooleanFromCursor(cursor, "is_active", true);

            return new Branch(id, name, address, lat, lng, phone, email, workingHours, isActive);
        } catch (Exception e) {
            Log.e(TAG, "Error creating branch from cursor", e);
            return null;
        }
    }

    /**
     * Create ContentValues from Branch object
     */
    private ContentValues createContentValuesFromBranch(Branch branch) {
        ContentValues values = new ContentValues();

        if (branch.getBranchId() > 0) {
            values.put("branch_id", branch.getBranchId());
        }
        values.put("name", branch.getName());
        values.put("address", branch.getAddress());
        values.put("lat", branch.getLat());
        values.put("lng", branch.getLng());
        values.put("phone", branch.getPhone());
        values.put("email", branch.getEmail());
        values.put("working_hours", branch.getWorkingHours());
        values.put("is_active", branch.isActive() ? 1 : 0);

        return values;
    }

    /**
     * Safely get string from cursor
     */
    private String getStringFromCursor(Cursor cursor, String columnName, String defaultValue) {
        try {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (columnIndex >= 0) {
                String value = cursor.getString(columnIndex);
                return value != null ? value : defaultValue;
            }
        } catch (Exception e) {
            Log.w(TAG, "Column not found: " + columnName);
        }
        return defaultValue;
    }

    /**
     * Safely get boolean from cursor
     */
    private boolean getBooleanFromCursor(Cursor cursor, String columnName, boolean defaultValue) {
        try {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (columnIndex >= 0) {
                return cursor.getInt(columnIndex) == 1;
            }
        } catch (Exception e) {
            Log.w(TAG, "Column not found: " + columnName);
        }
        return defaultValue;
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distance in km
    }

    /**
     * Initialize sample data for testing
     */
    private void initializeSampleData() {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();

            // Check if data already exists
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Branch", null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();

            if (count == 0) {
                // Insert sample branches
                insertSampleBranches(db);

                // Mark as initialized
                sharedPreferences.edit()
                        .putBoolean(KEY_DATA_INITIALIZED, true)
                        .apply();

                Log.d(TAG, "Sample data initialized successfully");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error initializing sample data", e);
        } finally {
            if (db != null) db.close();
        }
    }

    /**
     * Insert sample branches for testing
     */
    private void insertSampleBranches(SQLiteDatabase db) {
        String[] sampleBranches = {
                "INSERT INTO Branch (branch_id, name, address, lat, lng, phone, email, working_hours, is_active) VALUES " +
                        "(1, 'Pizza Mania Colombo City Centre', 'Level 2, Colombo City Centre, 137 Sir Chittampalam A Gardiner Mawatha, Colombo 02', 6.9270786, 79.861243, '+94 11 234 5678', 'colombo@pizzamania.lk', '10:00 AM - 11:00 PM', 1)",

                "INSERT INTO Branch (branch_id, name, address, lat, lng, phone, email, working_hours, is_active) VALUES " +
                        "(2, 'Pizza Mania Kandy City', 'No. 12, Dalada Veediya, Kandy', 7.2955773, 80.6369011, '+94 81 222 3456', 'kandy@pizzamania.lk', '10:00 AM - 10:30 PM', 1)",

                "INSERT INTO Branch (branch_id, name, address, lat, lng, phone, email, working_hours, is_active) VALUES " +
                        "(3, 'Pizza Mania Galle', 'No. 45, Wakwella Road, Galle', 6.0367342, 80.2170410, '+94 91 222 7890', 'galle@pizzamania.lk', '11:00 AM - 10:00 PM', 1)",

                "INSERT INTO Branch (branch_id, name, address, lat, lng, phone, email, working_hours, is_active) VALUES " +
                        "(4, 'Pizza Mania Negombo', 'No. 78, Main Street, Negombo', 7.2083912, 79.8358160, '+94 31 222 4567', 'negombo@pizzamania.lk', '10:30 AM - 10:30 PM', 1)",

                "INSERT INTO Branch (branch_id, name, address, lat, lng, phone, email, working_hours, is_active) VALUES " +
                        "(5, 'Pizza Mania Matara', 'No. 23, Anagarika Dharmapala Mawatha, Matara', 5.9486833, 80.5353210, '+94 41 222 8901', 'matara@pizzamania.lk', '11:00 AM - 9:30 PM', 0)",

                "INSERT INTO Branch (branch_id, name, address, lat, lng, phone, email, working_hours, is_active) VALUES " +
                        "(6, 'Pizza Mania Jaffna', 'No. 67, Hospital Road, Jaffna', 9.6615434, 80.0255232, '+94 21 222 3457', 'jaffna@pizzamania.lk', '10:00 AM - 10:00 PM', 1)"
        };

        for (String sql : sampleBranches) {
            try {
                db.execSQL(sql);
            } catch (SQLException e) {
                Log.e(TAG, "Error inserting sample branch", e);
            }
        }
    }

    /**
     * Helper class to store branch with distance for sorting
     */
    private static class BranchDistance {
        Branch branch;
        double distance;

        BranchDistance(Branch branch, double distance) {
            this.branch = branch;
            this.distance = distance;
        }
    }

    /**
     * Clean up resources
     */
    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}