package com.pizzamania.data.repo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.pizzamania.data.db.AppDbHelper;
import com.pizzamania.data.model.Branch;

import java.util.ArrayList;
import java.util.List;

public class BranchRepository {
    private final AppDbHelper dbHelper;

    public BranchRepository(Context context) {
        dbHelper = new AppDbHelper(context);
    }

    public List<Branch> getAllBranches() {
        List<Branch> branches = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query("Branch",
                null, null, null, null, null, null);

        while (c.moveToNext()) {
            int id = c.getInt(c.getColumnIndexOrThrow("branch_id"));
            String name = c.getString(c.getColumnIndexOrThrow("name"));
            String address = c.getString(c.getColumnIndexOrThrow("address"));
            double lat = c.getDouble(c.getColumnIndexOrThrow("lat"));
            double lng = c.getDouble(c.getColumnIndexOrThrow("lng"));
            String phone = c.getString(c.getColumnIndexOrThrow("phone"));

            branches.add(new Branch(id, name, address, lat, lng, phone));
        }
        c.close();
        return branches;
    }
}
